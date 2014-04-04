/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.warp.jcontroller;

import java.net.InetSocketAddress;
import java.util.List;

import org.flowforwarding.warp.jcontroller.session.SwitchNurse;
import org.flowforwarding.warp.jcontroller.session.TellToSendFlowMod;
import org.flowforwarding.warp.jcontroller.supply.OFCTellController;
import org.flowforwarding.warp.ofswitch.SwitchState.SwitchRef;
import org.flowforwarding.warp.protocol.ofmessages.OFMessageFlowMod.OFMessageFlowModRef;

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
 * @author Infoblox Inc.
 * @doc.desc Handles incoming tcp connections. Launches and dispatches Switch nurses which are handling connections with switches
 * 
 */
public class Controller extends UntypedActor{
   
   //TODO Improvs We should make it as plugins of something.
   final ActorRef manager;
   static ActorRef controller;
   static ActorRef ofEventHandler;
   static ActorRef handler;
   static ActorRef restApi;
   
   private Controller(ActorRef manager) {
      this.manager = manager;
   }
   
   private static Configuration configuration;
   private static Class<?> ofEventHandlerClass;

   @Override
   public void preStart() throws Exception {
      
      final ActorRef tcp = Tcp.get(getContext().system()).manager();
      tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("192.168.56.101", this.configuration.getTcpPort()), 100), getSelf());
      //tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("localhost", this.configuration.getTcpPort()), 100), getSelf());
//      tcp.tell(TcpMessage.bind(getSelf(), new InetSocketAddress("localhost", this.configuration.getTcpPort()), 100), getSelf());
   }
   
   @Override
   public void onReceive(Object msg) throws Exception {
      if (msg instanceof Bound) {
         manager.tell(msg, getSelf());
         ofEventHandler = getContext().actorOf(Props.create(ofEventHandlerClass));
//         restApi = getContext().actorOf(Props.create(RestApiServer.class));
      } else if (msg instanceof CommandFailed) {
         getContext().stop(getSelf());
      } else if (msg instanceof Connected) {
         final Connected conn = (Connected) msg;
         manager.tell(conn, getSelf());
         System.out.println("[INFO] Getting Switch connection \n");
         
         //final ActorRef ofEventHandler = getContext().actorOf(Props.create(ofEventHandlerClass));
         handler = getContext().actorOf(Props.create(SwitchNurse.class));
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
   * The user-defined OpenFlow events handler, e.g. {@link org.flowforwarding.warp.jcontroller.session.OFSessionHandler}
   */
   public static ControllerRef launch (Configuration config, Class<? extends org.flowforwarding.warp.jcontroller.session.OFActor> handler) {
      
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
   * The user-defined OpenFlow events handler, e.g. {@link org.flowforwarding.warp.jcontroller.session.OFSessionHandler}
   */
   
   public static ControllerRef launch (Class<? extends org.flowforwarding.warp.jcontroller.session.OFActor> handler) {
      
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
         tellController.tell(ofEventHandler, new org.flowforwarding.warp.jcontroller.session.EventGetSwitches());
         
         return null;
      }
      
      public SwitchRef getSwitch (String dpid) {
         return null;
      }
      
      public void sendFlowMod (SwitchRef swRef, OFMessageFlowModRef fmRef) {
         tellController.tell(handler, new TellToSendFlowMod());
      }
   }
}
