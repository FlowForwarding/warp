/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers.sal

import java.math.BigInteger

import scala.util.Try

import spire.math.{UInt, ULong}

trait Node[I]{
  def id: I
  def idStr: String
  def protocol: String
  override def toString = Node.toString(this)
}

trait NodeConnector[I, N <: Node[_]]{
  def id: I
  def node: N
  def `type`: String
  // TODO: correct implementation
  def idStr: String = id.toString
  override def toString = NodeConnector.toString(this)
}

case class OFNode(id: ULong) extends Node[ULong]{
  def protocol = "OF"
  def idStr = longToHexString(id.toLong)

  private def longToHexString(value: Long): String = {
    val arr = value.toHexString.toCharArray
    val padded = Seq.fill(16 - arr.length)('0') ++ arr
    padded.grouped(2).map(_ mkString "").mkString(":")
  }
}

// Ensure type of id
case class OFNodeConnector(id: UInt, `type`: String, node: OFNode) extends NodeConnector[UInt, OFNode]

object Node {
  def toString(n: Node[_]) = s"${n.protocol}|${n.idStr}"
  // nodeType|nodeId
  def parse(s: String): scala.util.Try[Node[_]] = Try {
    val Array(nodeType, nodeId) = s split '|'
    create(nodeType, nodeId).get
  }

  def create(protocol: String, id: String): scala.util.Try[Node[_]] = scala.util.Try {
    protocol match {
      case "OF" => OFNode(ULong(ofStringToLong(id)))
    }
  }

  private def ofStringToLong(values: String): Long =
    new BigInteger(values.replaceAll(":", ""), 16).longValue
}

object NodeConnector {
  def toString(nc: NodeConnector[_, _ <: Node[_]]) = s"${nc.`type`}|${nc.idStr}@${nc.node.toString}"
  def toString(n: Node[_], port: String) = s"${n.protocol}|$port@${n.toString}"
  // connectorType|connectorId@nodeType|nodeId
  def parse(s: String): scala.util.Try[NodeConnector[_, _ <: Node[_]]] = Try {
    val Array(connector, node) = s split '@'
    val Array(connectorType, connectorId) = connector split '|'
    create(Node.parse(node).get, connectorId, connectorType).get
  }

  def create(protocol: String, nodeId: String, connectorId: String, connectorType: String): scala.util.Try[NodeConnector[_, _]] =
    Node.create(protocol, nodeId) flatMap { create(_, connectorType, nodeId) }

  def create(node: Node[_], nodeId: String, connectorType: String): scala.util.Try[NodeConnector[_, _ <: Node[_]]] = scala.util.Try {
    node match {
      case of: OFNode => OFNodeConnector(UInt(nodeId.toInt), connectorType, of)
    }
  }
}
