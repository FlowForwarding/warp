/**
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 */

package org.flowforwarding.of.controller;

import java.net.InetSocketAddress;

import org.flowforwarding.of.controller.session.OFActor;
import org.flowforwarding.of.controller.session.SwitchNurse;
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
      } else if (msg instanceof CommandFailed) {
         getContext().stop(getSelf());
      } else if (msg instanceof Connected) {
         final Connected conn = (Connected) msg;
         manager.tell(conn, getSelf());
         System.out.println("[INFO] Getting Switch connection \n");
         
         final ActorRef ofEventHandler = getContext().actorOf(Props.create(ofEventHandlerClass));
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
   public static void launch (Configuration config, Class<? extends OFActor> handler) {
      
      configuration = config;
      ofEventHandlerClass = handler;
      
      final ActorSystem system = ActorSystem.create("OfController");
      final ActorRef manager = Tcp.get(system).manager();
      controller = system.actorOf(Props.create(Controller.class, manager), "Controller-Dispatcher");

   }
   
  /**
   * 
   * Launches default-configuration Controller (tcp port = 6633)
   *   
   * @param handler
   * The user-defined OpenFlow events handler, e.g. {@link org.flowforwarding.of.controller.session.OFSessionHandler}
   */
   
   public static void launch (Class<? extends OFActor> handler) {
      
      configuration = new Configuration();
      ofEventHandlerClass = handler;
      
      final ActorSystem system = ActorSystem.create("OfController");
      final ActorRef manager = Tcp.get(system).manager();
      controller = system.actorOf(Props.create(Controller.class, manager), "Controller-Dispatcher");
   }
}
