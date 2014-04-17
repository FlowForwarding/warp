/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Infoblox Inc.
 * @deprecated
 *
 */
public class JController {

   /**
    * @param args
    */
   public enum State {
      STARTED,
      CONNECTED,
      HANDSHAKED,
      CONFIG_READY,
      READY
   }
   
   IOFMessageProviderFactory factory = new OFMessageProviderFactoryAvroProtocol();
   IOFMessageProvider provider = factory.getMessageProvider("1.3");
   
   private State state = State.STARTED;
   
   protected Map<String, Map<String, Object>> entries;
   //protected Map<String, Object> entries;
   protected ForkJoinPool pool;
   protected ObserverTask<Integer, org.flowforwarding.warp.jcontroller.restapi.RestApiTask> observerTask;
   protected ChannelHandler handlerTask;
   protected Channel channel;
   
   public static class Occured {
      private static boolean occured = false;
            
      private static volatile Occured instance;
      
      public static void switchOff() {
         occured = false;
      }
      
      public static void switchOn() {
         occured = true;
      }
      
      public static Occured getInstance() {
      Occured localInstance = instance;
      if (localInstance == null) {
          synchronized (Occured.class) {
              localInstance = instance;
              if (localInstance == null) {
                  instance = localInstance = new Occured();
              }
          }
      }
      return localInstance;
  }
      
      public static boolean isOccured () {
         return occured;
      }
   }

   public class ObserverTask <V, Event> extends RecursiveTask<V> {
      
      /**
       * 
       */
      private static final long serialVersionUID = 1882619201643785938L;
      private final Logger log =  LoggerFactory.getLogger(ChannelHandler.class);
      private boolean exitState = false;
      private Event event = null;
      
      public void update (Event event) {
         log.info("REST Service EVENT");
         Occured.getInstance().switchOn();
         this.event = event;
      }

      @Override
      protected V compute() {
         while (! this.exitState) {
            try {
               Thread.sleep(250);
            } catch (InterruptedException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
            if (Occured.getInstance().isOccured()) {
               log.info("REST Service INCOMING request");
               entries = ((org.flowforwarding.warp.jcontroller.restapi.RestApiTask)this.event).join();
               handlerTask.write();
               Occured.getInstance().switchOff();
            }
         }
         return null;
      }
      
   }
   
   /**
    * @param args
    */
   public static void main(String[] args) {
     new JController().run();
/*      try {
         new RestServerApplication().startServer();
      } catch (Exception e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }*/
   }
   
   public void run () {
      
      provider.init();
      
      InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
      
      this.entries = new ConcurrentHashMap<String, Map<String, Object>>();
      //this.entries = new ConcurrentHashMap<String, Object>();
      this.pool = new ForkJoinPool();
      this.observerTask = new ObserverTask<Integer, org.flowforwarding.warp.jcontroller.restapi.RestApiTask>();
      
      Timer timer = new HashedWheelTimer();
      
       this.handlerTask = new ChannelHandler(timer, 0, 0, 20);
      //this.handlerTask = new ChannelHandler();
            
      this.pool.execute(observerTask);
      
      
      ServerBootstrap bootstrap = new ServerBootstrap( new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(),
            Executors.newCachedThreadPool()));

      bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
                       public ChannelPipeline getPipeline() throws Exception {
                           return Channels.pipeline(new LoggingHandler(InternalLogLevel.INFO),
                                                    handlerTask,
                                                    new LoggingHandler(InternalLogLevel.INFO));
                       }
                   });
      
      bootstrap.bind(new InetSocketAddress(6633));
      
      org.flowforwarding.warp.jcontroller.restapi.RestApiServer restApi =  new org.flowforwarding.warp.jcontroller.restapi.RestApiServer(pool, observerTask);
      restApi.run();
   }
   
   public class ChannelHandler extends IdleStateHandler{
//   public class ChannelHandler extends IdleStateAwareChannelHandler{
      
      private final Logger log =  LoggerFactory.getLogger(ChannelHandler.class);
      
      @Override
      protected void channelIdle(ChannelHandlerContext ctx, IdleState state,
            long lastActivityTimeMillis) throws Exception {

/*         if (ofMessageFactory == null) // lazy init
            ofMessageFactory = new BasicFactory();

         OFEchoRequest echoReq = (OFEchoRequest) ofMessageFactory.getMessage(OFType.ECHO_REQUEST);
         
         ChannelBuffer buf = ChannelBuffers.buffer(echoReq.getLengthU());
         echoReq.writeTo(buf);*/
         
         BigEndianHeapChannelBuffer buf = new BigEndianHeapChannelBuffer(provider.encodeEchoRequest());
         channel.write(buf);
         buf.clear();
         
         super.channelIdle(ctx, state, lastActivityTimeMillis);
      }
      
      public ChannelHandler(Timer timer, int readerIdleTimeSeconds,
            int writerIdleTimeSeconds, int allIdleTimeSeconds) {
         super(timer, readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
         // TODO Auto-generated constructor stub
      }

      private final AtomicLong transferredBytes = new AtomicLong();

      @Override
      public void channelConnected(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
         channel = e.getChannel();
         
         super.channelConnected(ctx, e);
      }
      @Override
      public void messageReceived( ChannelHandlerContext ctx, MessageEvent e) {
         transferredBytes.addAndGet(((ChannelBuffer) e.getMessage()).readableBytes());
          
         switch (state) {
         case STARTED:
             
             /*BasicFactory ofMessageFactory = null;
             if (ofMessageFactory == null) // lazy init
                ofMessageFactory = new BasicFactory();

             OFHello helloMsg = (OFHello) ofMessageFactory
                    .getMessage(OFType.HELLO);
             
             ChannelBuffer buf = ChannelBuffers.buffer(helloMsg.getLengthU());
             helloMsg.writeTo(buf);
             e.getChannel().write(buf);
             buf.clear();*/
            
            log.info("OUTGOING Message: HELLO");
            BigEndianHeapChannelBuffer x = new BigEndianHeapChannelBuffer(provider.getHello(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(x);
            x.clear();
            state = State.CONNECTED;
            log.info("OUTGOING Message: FEATURES_REQUEST");
            BigEndianHeapChannelBuffer y = new BigEndianHeapChannelBuffer(provider.getSwitchFeaturesRequest(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(y);
            state = State.HANDSHAKED;
            break;
         case HANDSHAKED:
            log.info("OUTGOING Message: SET_CONFIG");
            BigEndianHeapChannelBuffer z = new BigEndianHeapChannelBuffer(provider.getSetSwitchConfig(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(z);
            z.clear();
            log.info("OUTGOING Message: GET_CONFIG_REQUEST");
            BigEndianHeapChannelBuffer a = new BigEndianHeapChannelBuffer(provider.getSwitchConfigRequest(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(a);
            state = State.CONFIG_READY;
            
            /*BigEndianHeapChannelBuffer b = new BigEndianHeapChannelBuffer(provider.getFlowModTest(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(b);*/ 

            break;
         case CONFIG_READY:
            
/*            BasicFactory ofMessageFactory = null;
            if (ofMessageFactory == null) // lazy init
               ofMessageFactory = new BasicFactory();

            OFFlowMod emptyFlowMod = (OFFlowMod) ofMessageFactory
                   .getMessage(OFType.FLOW_MOD);

            initDefaultFlowMod(emptyFlowMod);
            
            ChannelBuffer buf = ChannelBuffers.buffer(emptyFlowMod.getLengthU());
            emptyFlowMod.writeTo(buf);
            e.getChannel().write(buf);
 //           buf.clear();*/
            state = State.READY;            

            break;
         default:
             break;        
         }
      }
      
      public void write() {
         //System.out.println(">>>> write: entering");
         Set<String> dpids = entries.keySet();
         
         for (String dpid : dpids) {
            log.info("OUTGOING Message: FLOW_MOD");
            BigEndianHeapChannelBuffer b = new BigEndianHeapChannelBuffer(provider.getFlowMod(entries.get(dpid), new ByteArrayOutputStream()).toByteArray());
            channel.write(b);
         }
        
//         while (it.hasNext()) {
           // System.out.println(">>>> write: 1");
//            Map<String, OFFlowMod> flows = entries.get(it.next());
//            Set<String> flowNames = flows.keySet();
//            Iterator<String> flowIt = flowNames.iterator();
            
//            while (flowIt.hasNext()) {
             //  System.out.println(">>>> write: 2");
//               OFFlowMod flow = flows.get(flowIt.next());
//               ChannelBuffer buf = ChannelBuffers.buffer(flow.getLengthU());
//               flow.writeTo(buf);
               //System.out.println(">>>> write: " + buf.toString());
//               channel.write(buf);
//               buf.clear();
//            }
//         }
      }
   }
   
/*   public static void initDefaultFlowMod(OFFlowMod fm) {
      fm.setIdleTimeout((short) 0);   // infinite
      fm.setHardTimeout((short) 0);   // infinite
      //fm.setBufferId(OFPacketOut.BUFFER_ID_NONE);
      fm.setBufferId(0);
      fm.setCommand((byte) OFFlowMod.OFPFC_DELETE);
      fm.setFlags((short) 0);
      fm.setOutPort(OFPort.OFPP_NONE.getValue());
      // TODO DO StaticFlowEntryPusher - setCookie
      //fm.setCookie(computeEntryCookie(fm, 0, entryName));
      fm.setCookie(0);
      //fm.setPriority(Short.MIN_VALUE);
      fm.setPriority(Short.valueOf("0"));
      
      OFMatch2 ofMatch2 = new OFMatch2();
      fm.setMatch2(ofMatch2);
      
  }*/
}
