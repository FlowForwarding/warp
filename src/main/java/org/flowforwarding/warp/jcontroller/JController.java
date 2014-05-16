/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */
package org.flowforwarding.warp.jcontroller;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;

import org.flowforwarding.warp.protocol.common.OFMessageRef;
import org.flowforwarding.warp.protocol.common.OFMessageRef.OFMessageBuilder;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProvider;
import org.flowforwarding.warp.protocol.ofmessages.IOFMessageProviderFactory;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;
import org.flowforwarding.warp.util.Convert;
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
   protected byte[] DPID = null;
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
         log.info("WARP REST EVENT: ");
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
               log.info("WARP REST API: INCOMING request");
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
                           return Channels.pipeline(handlerTask);
                          /*return Channels.pipeline(new LoggingHandler(InternalLogLevel.INFO),
                                                   handlerTask,
                                                   new LoggingHandler(InternalLogLevel.INFO));*/
                       }
                   });
      
      bootstrap.bind(new InetSocketAddress(6633));
      
      org.flowforwarding.warp.jcontroller.restapi.RestApiServer restApi =  new org.flowforwarding.warp.jcontroller.restapi.RestApiServer(pool, observerTask);
      restApi.run();
   }
   
   public class ChannelHandler extends IdleStateHandler{
//   public class ChannelHandler extends IdleStateAwareChannelHandler{
      OFMessageBuilder builder = null;
      private OFMessageRef inMsg = null;
      private Map<byte[], Channel> DPIDs = new HashMap<>();
      private final Logger log =  LoggerFactory.getLogger(ChannelHandler.class);
      
      @Override
      protected void channelIdle(ChannelHandlerContext ctx, IdleState state,
            long lastActivityTimeMillis) throws Exception {

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
         
         byte[] in = ((ChannelBuffer) e.getMessage()).array();
          
         switch (state) {
         case STARTED:
            builder = new OFMessageBuilder("avro", in);
            log.info("WARP OUT: HELLO");
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
            synchronized (this) {
            inMsg = builder.value(in).build();
            if (inMsg.type().equals("OFPT_FEATURES_REPLY")) {
               DPIDs.put(inMsg.field("datapath_id"), e.getChannel());
               DPID = inMsg.field("datapath_id");
               log.info("WARP INFO: Switch DPID is " + Long.toHexString(Convert.toLong(inMsg.field("datapath_id"))).toUpperCase());
            }
            }
            log.info("WARP OUT: SET_CONFIG");
            BigEndianHeapChannelBuffer z = new BigEndianHeapChannelBuffer(provider.getSetSwitchConfig(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(z);
            z.clear();
            log.info("WARP OUT: GET_CONFIG_REQUEST");
            BigEndianHeapChannelBuffer a = new BigEndianHeapChannelBuffer(provider.getSwitchConfigRequest(new ByteArrayOutputStream()).toByteArray());
            e.getChannel().write(a);
            state = State.CONFIG_READY;
 
            break;
         case CONFIG_READY:
            
            state = State.READY;            

            break;
         default:
             break;        
         }
      }
      
      public void write() {
         Set<String> commands = entries.keySet();
         for (String command : commands) {
            if (command.contains("show")) {
               Set<byte[]> dpids = DPIDs.keySet();
               log.info("WARP INFO: Switches connected ");
               for (byte[] dpid : dpids)
                  log.info("          | " + Long.toHexString(Convert.toLong(dpid)).toUpperCase());
               break;
            }
            
            if (entries.get(command).containsKey("switch_id")) {
               byte [] t = Convert.dpidToBytes((String) entries.get(command).get("switch_id"));               
               log.info("WARP OUT: FLOW_MOD, DPID = " + entries.get(command).get("switch_id"));

               BigEndianHeapChannelBuffer b = new BigEndianHeapChannelBuffer(provider.getFlowMod(entries.get(command), new ByteArrayOutputStream()).toByteArray());
               
               Set<byte[]> dpids = DPIDs.keySet();
               log.info("WARP INFO: Switches connected ");
               for (byte[] dpid : dpids) {
                  if (Convert.toLong(dpid) == Convert.toLong(t)) 
                     DPIDs.get(dpid).write(b);
               }
//               channel.write(b);
            }
         }
      }
   }
}
