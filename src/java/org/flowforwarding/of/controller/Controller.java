/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicLong;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

import org.flowforwarding.of.controller.restapi.RestApiServer;
import org.flowforwarding.of.controller.restapi.RestApiTask;
import org.flowforwarding.of.controller.session.EventGetSwitches;
import org.flowforwarding.of.controller.session.OFActor;
import org.flowforwarding.of.controller.session.SwitchNurse;
import org.flowforwarding.of.controller.supply.OFCTellController;
import org.flowforwarding.of.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.of.protocol.ofmessages.OFMessageProvider;
import org.flowforwarding.of.protocol.ofmessages.OFMessageProviderFactory;
import org.flowforwarding.of.protocol.ofmessages.OFMessageProviderFactoryAvroProtocol;
import org.jboss.netty.buffer.BigEndianHeapChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.io.Tcp.Bound;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;


/**
 * Class Controller
 * @author Infoblox Inc.
 * 
 * Handles incoming tcp connections. Launches and dispatches Switch nurses which are handling connections with switches
 * 
 */
public class Controller extends UntypedActor{
   
   final ActorRef manager;
   static ActorRef controller;
   static ActorRef ofEventHandler;
   
   private Controller(ActorRef manager) {
      this.manager = manager;
   }
   
   private static Configuration configuration;
   private static Class<?> ofEventHandlerClass;

   @Override
   public void preStart() throws Exception {
      
      final ActorRef tcp = Tcp.get(getContext().system()).manager();
      tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("localhost", this.configuration.getTcpPort()), 100), getSelf());
   }
   
   @Override
   public void onReceive(Object msg) throws Exception {
      if (msg instanceof Bound) {
         manager.tell(msg, getSelf());
         ofEventHandler = getContext().actorOf(Props.create(ofEventHandlerClass));
      } else if (msg instanceof CommandFailed) {
         getContext().stop(getSelf());
      } else if (msg instanceof Connected) {
         final Connected conn = (Connected) msg;
         manager.tell(conn, getSelf());
         System.out.println("[INFO] Getting Switch connection \n");
         
         //final ActorRef ofEventHandler = getContext().actorOf(Props.create(ofEventHandlerClass));
         final ActorRef handler = getContext().actorOf(Props.create(SwitchNurse.class));
         getSender().tell(TcpMessage.register(handler), getSelf());
         
         handler.tell(ofEventHandler, controller);
      }
   }   
   
  /**
   * 
   * @param config
   * The {@link Configuration} of Controller. 
   *   
   * @param handler
   * The user-defined OpenFlow events handler, e.g. {@link org.flowforwarding.of.controller.session.OFSessionHandler}
   */
   public static ControllerRef launch (Configuration config, Class<? extends OFActor> handler) {
      
      configuration = config;
      ofEventHandlerClass = handler;
      
      final ActorSystem system = ActorSystem.create("OfController");
      final ActorRef manager = Tcp.get(system).manager();
      controller = system.actorOf(Props.create(Controller.class, manager), "Controller-Dispatcher");

      return ControllerRef.create(controller);
   }
   
  /**
   * 
   * Launches default-configuration Controller (tcp port = 6633)
   *   
   * @param handler
   * The user-defined OpenFlow events handler, e.g. {@link org.flowforwarding.of.controller.session.OFSessionHandler}
   */
   
   public static ControllerRef launch (Class<? extends OFActor> handler) {
      
      configuration = new Configuration();
      ofEventHandlerClass = handler;
      
      final ActorSystem system = ActorSystem.create("OfController");
      final ActorRef manager = Tcp.get(system).manager();
      controller = system.actorOf(Props.create(Controller.class, manager), "Controller-Dispatcher");
      
      return ControllerRef.create(controller);
   }
   
   public static class ControllerRef {
      
      protected ActorRef controller = null;
      
      protected OFCTellController tellController = null;
      
      protected ControllerRef () {}
      protected ControllerRef (ActorRef c) {
         controller = c;
         tellController = new OFCTellController(controller);
      }
      
      public static ControllerRef create (ActorRef c) {
         return new ControllerRef (c);
      }
      
      public List<SwitchRef> getSwitches () {
         tellController.tell(ofEventHandler, new EventGetSwitches());
         
         return null;
      }
   }
}
