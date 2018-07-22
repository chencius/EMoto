package com.emoto.network.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.ClientLoginReq;
import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.protocol.fields.Header;
import com.emoto.server.Server;
import com.emoto.server.Server.HWMapping;
import com.emoto.statemachine.ChargePoint;
import com.emoto.statemachine.ChargePort;

public class AsyncServerHandler {
	public AsynchronousChannelGroup group;
	public AsynchronousServerSocketChannel serverChannel;
	private static final int threadPoolNum = 100;
	private String ip;
	private int port;
	private boolean exit = false;
	public Server server;
	private Object callback;
	
	private static final int BUF_SIZE = 1024;
	
	private static Logger logger = Logger.getLogger(AsyncServerHandler.class.getName());
	
	public AsyncServerHandler( String ip, int port, Server server ) throws IOException {
		if (ip == null || ip.isEmpty() || port <= 0) {
			logger.log(Level.WARNING, "Invalid ip/port to create a TCP server(ip={0}, port={1})", new Object[]{ip, port});
			this.ip = null;
			this.port = -1;
			this.group = null;
			this.serverChannel = null;
			this.server = null;
		} else {
			this.ip = ip;
			this.port = port;
			this.server = server;
			this.group = AsynchronousChannelGroup.withCachedThreadPool(Executors.newCachedThreadPool(), threadPoolNum);
			this.serverChannel = AsynchronousServerSocketChannel.open(group);
			
			this.serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
			this.serverChannel.bind(new InetSocketAddress(ip, port));
			
			logger.log(Level.INFO, "Listening on " + ip + " : " + port);
		}	
	}
	
	public void start() {
		this.serverChannel.accept(this, new AcceptCompletionHandler());
		while (!exit) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {

		public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
			logger.log(Level.INFO, "Server accept a connection from chargePoint");
			serverHandler.serverChannel.accept(serverHandler, this);
			ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
			try {
				channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
				channel.setOption(StandardSocketOptions.TCP_NODELAY, true);
				channel.setOption(StandardSocketOptions.SO_RCVBUF, 16*BUF_SIZE);
				channel.setOption(StandardSocketOptions.SO_SNDBUF, 16*BUF_SIZE);
				channel.read(buffer, buffer, new ReadCompletionHandler(channel));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void failed(Throwable exc, AsyncServerHandler serverHandler) {
			logger.log(Level.WARNING, "Failed to accept a connection");
			exc.printStackTrace();
		}
		
	}
	
	private class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
		private AsynchronousSocketChannel channel;
		
		public ReadCompletionHandler(AsynchronousSocketChannel channel) {
			if (this.channel == null) {
				this.channel = channel;
			}
		}

		public void completed(Integer result, ByteBuffer buf) {
			buf.flip();
			Object commands[] = null;
			try {
				commands = CmdFactory.decCommand(buf, Header.CLIENT_STX);
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | NoSuchFieldException
					| SecurityException e) {
				logger.log(Level.WARNING, "Error decoding command from chargeＰoint");
				return;
			}
			
			if (commands.length == 0) {
				logger.log(Level.WARNING, "No message can be decoded from incoming data");
				return;
			}
			
			for (Object c : commands) {
				CmdBase cmd = (CmdBase)c;
				long chargeId = cmd.getChargeId();
				logger.log(Level.INFO, "About to process " + cmd);
				ChargePoint cp;
				if ( cmd instanceof ClientLoginReq) {
					ClientLoginReq req = (ClientLoginReq)cmd;
					HWMapping chargePointInfo = server.hwId2ChargePoint.get(req.getHwId());
					if (chargePointInfo != null) {
						cp = server.chargePoints.get(chargePointInfo.chargeId);
						if (cp == null) {
							server.chargePoints.put(chargePointInfo.chargeId, cp = new ChargePoint(server, channel, chargePointInfo.chargeId));
							cp.setCallback(callback);
							logger.log(Level.INFO, "Receive registration for chargeId {0} with {1}",
								new Object[]{chargePointInfo.chargeId, req});
						} else {
							boolean allInactive = true;
							ChargePort[] ports = cp.getPorts();
							for (int i = 0; i<cp.PORT_NUM; i++) {
								if (ports[i].getActive() == true) {
									allInactive = false;
									break;
								}
							}
							if (allInactive != true) {
								logger.log(Level.INFO, "Receive {0} when ports on chargeId {1} are not all inactive",
										new Object[]{req, chargeId});
							} else {
								logger.log(Level.INFO, "Receive {0} when ports on chargeId {1} are all inactive",
										new Object[]{req, chargeId});
							}
							
							server.chargePoints.put(chargePointInfo.chargeId, cp = new ChargePoint(server, channel, chargePointInfo.chargeId));
							cp.setCallback(callback);
							logger.log(Level.INFO, "Receive registration for chargeId(reactivate) {0} with {1}",
								new Object[]{chargePointInfo.chargeId, req});
						}
					} else {
						logger.log(Level.WARNING, "chargePoint loginReq failed due to no corresponding hwId "
									+ req.getHwId() + " with " + req);
						return;
					}
				} else {
					cp = server.chargePoints.get(chargeId);
					if (cp != null) {
						logger.log(Level.INFO, "Retrieve existing chargePoint whose chargeId = " + chargeId);
					} else {
						logger.log(Level.WARNING, "Can't find chargePoint whose chargeId = ", chargeId);
						return;
					}
				}
				CmdBase[] resp = cp.execCmd(cmd);
				if (resp != null) {
					try {
						for (CmdBase cc : resp) {
							logger.log(Level.INFO, "Send response " + cc);
						}
						doWrite(CmdFactory.encCommand(resp, Header.SERVER_STX));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						logger.log(Level.WARNING, "Failed to encode or send response commands");
					}
				}
			}
			//It is important to clear here. Otherwise there will be an infinite loop
			buf.clear();
			channel.read(buf, buf, this);
		}

		public void failed(Throwable exc, ByteBuffer attachment) {
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void doWrite(ByteBuffer buf) {
			this.channel.write(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
				public void completed(Integer result, ByteBuffer buf) {  
                    if (buf.hasRemaining()) {  
                    	channel.write(buf, buf, this);  
                    }
                }  
  
                public void failed(Throwable exc, ByteBuffer attachment) {  
                    try {  
                    	channel.close();  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }  
                      
                }  
			});
		}
	}

	public void setCallback(Object callback) {
		this.callback = callback;
	}
}
