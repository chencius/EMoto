package com.emoto.server;

import java.util.Iterator;
import java.util.List;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.Constants;

public class Client2 {
	private static EndpointReference targetEPR = new EndpointReference(  
            "http://localhost:8888/axis2/services/ServerControl");  
  
    public static OMElement getBody(String personToGreet) {  
          
        /* 注意：这里根节点必须是sayHello,其他可以任意组装xml报文： 
         * <example1:sayHello xmlns:example1="http://example1.org/example1"> 
         *     <example1:personToGreet>John</example1:personToGreet> 
         * </example1:sayHello> 
         */  
        OMFactory fac = OMAbstractFactory.getOMFactory();  
        OMNamespace omNs = fac.createOMNamespace("http://example1.org/example1", "example1");  
        OMElement method = fac.createOMElement("stopCharging", omNs);  
        OMElement value = fac.createOMElement("personToGreet", omNs);  
        value.addChild(fac.createOMText(value, personToGreet));  
        method.addChild(value);  
        return method;  
    }  
  
    public static void main(String[] args) {  
        try {  
            Options options = new Options();  
            options.setTo(targetEPR);  
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);  
  
            ServiceClient sender = new ServiceClient();  
            sender.setOptions(options);  
  
            System.out.println("req:" + getBody("John"));  
            OMElement result = sender.sendReceive(getBody("John"));  
            System.out.println("res:" + result);  
              
            /* 这是服务端还回的报文，根节点一定是sayHelloResponse： 
             * <example1:sayHelloResponse xmlns:example1="http://example1.org/example1"> 
             *     <example1:greeting>Hello, John</example1:greeting> 
             * </example1:sayHelloResponse> 
             */  
            String response = result.getFirstElement().getText();  
            System.out.println(response);  
  
        } catch (Exception e) {  
            System.out.println(e.toString());  
        }  
    }  

}
