package com.emoto.network.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.emoto.protocol.command.CmdBase;
import com.emoto.protocol.command.CmdFactory;
import com.emoto.server.Server;
import com.emoto.statemachine.ChargePoint;
import com.emoto.statemachine.State;

public class AsyncServerHandler {
	public AsynchronousChannelGroup group;
	public AsynchronousServerSocketChannel serverChannel;
	private static final int threadPoolNum = 100;
	private String ip;
	private int port;
	private boolean exit = false;
	public Server server;
	
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
			this.serverChannel.setOption(StandardSocketOptions.SO_RCVBUF, 16*1024);
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
			serverHandler.serverChannel.accept(serverHandler, this);
			ByteBuffer buffer = ByteBuffer.allocate(BUF_SIZE);
			channel.read(buffer, buffer, new ReadCompletionHandler(channel));
			
		}

		public void failed(Throwable exc, AsyncServerHandler serverHandler) {
			logger.log(Level.WARNING, "Failed to accept a connection");
			exc.printStackTrace();
		}
		
	}
	
	private class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
		private AsynchronousSocketChannel channel;
		
		public ReadCompletionHandler(AsynchronousSocketChannel channel) {
			//super();
			if (this.channel == null) {
				this.channel = channel;
			}
		}

		public void completed(Integer result, ByteBuffer buf) {
			buf.flip();
			CmdBase cmd = null;
			try {
				cmd = CmdFactory.decCommand(buf);
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | NoSuchFieldException
					| SecurityException e) {
				logger.log(Level.WARNING, "Error decoding command from chargepoint");
				return;
			}
			long chargeId = cmd.getChargerId();
			ChargePoint cp;
			if ( server.chargePoints.get(chargeId) == null) {
				server.chargePoints.put(chargeId, cp = new ChargePoint(server));
			} else {
				cp = server.chargePoints.get(chargeId);
			}
			CmdBase[] resp = cp.execCmd(cmd);
			if (resp != null) {
				try {
					doWrite(CmdFactory.encCommand(resp));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					logger.log(Level.WARNING, "Failed to encode command");
				}
			}
		}

		public void failed(Throwable exc, ByteBuffer attachment) {
			try {
				this.channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void doWrite(ByteBuffer buf) {
			buf.flip();
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
	
	
	
	
	
	
	
	
	
	
}
