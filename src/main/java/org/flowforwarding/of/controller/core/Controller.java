package org.flowforwarding.of.controller.core;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import org.flowforwarding.of.controller.protocol.Protocol;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;

public class Controller {
	
   private enum State {
      STARTED,
      CONNECTED,
      HANDSHAKED,
      READY
   }
	
   private State state = State.STARTED;
   Protocol protocol = new Protocol();

   /**
    * @param args
    */
   public static void main(String[] args) {
      new Controller().run();
   }
	
	public void run () {
		
		protocol.init();
		
		ServerBootstrap bootstrap = new ServerBootstrap( new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			              public ChannelPipeline getPipeline() throws Exception {
			                  return Channels.pipeline(new ChannelHandler());
			              }
			          });
		
		bootstrap.bind(new InetSocketAddress(6633));
	}
	
	protected class ChannelHandler extends IdleStateAwareChannelUpstreamHandler {
		
		private final AtomicLong transferredBytes = new AtomicLong();

		
		 @Override
		 public void messageReceived( ChannelHandlerContext ctx, MessageEvent e) {
		    transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
		    
			 switch (state) {
			 case STARTED:
			    BigEndianHeapChannelBuffer x = new BigEndianHeapChannelBuffer(protocol.getHello(new ByteArrayOutputStream()).toByteArray());
				 e.getChannel().write(x);
				 x.clear();
				 state = State.CONNECTED;
				 BigEndianHeapChannelBuffer y = new BigEndianHeapChannelBuffer(protocol.getSwitchFeaturesRequest(new ByteArrayOutputStream()).toByteArray());
				 e.getChannel().write(y);
				 state = State.HANDSHAKED;
				 break;
			 case HANDSHAKED:
			    BigEndianHeapChannelBuffer z = new BigEndianHeapChannelBuffer(protocol.getSwitchConfigRequest(new ByteArrayOutputStream()).toByteArray());
			    e.getChannel().write(z);
			    state = State.READY;
			    break;
			 default:
			    break;			
			 }
			 

//			 BigEndianHeapChannelBuffer x = (BigEndianHeapChannelBuffer) e.getMessage();
//		     e.getChannel().write(e.getMessage());
		 }
				
	}
}
