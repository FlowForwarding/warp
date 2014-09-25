/*
* © 2013 FlowForwarding.Org
* All Rights Reserved.  Use is subject to license terms.
*
* @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
*/
package org.flowforwarding.warp.controller.modules.managers.sal

import java.net.InetAddress

import spire.math.{UByte, UInt, UShort}

import scala.util.Try

sealed trait Action{
  def name: String
  def stringValue: Option[String] = None
  override def toString = stringValue.fold(name) { name + "=" + _ }
}

object Action {
  val DropName = "DROP"
  val LoopbackName = "LOOPBACK"
  val FloodName = "FLOOD"
  val FloodAllName = "FLOOD_ALL"
  val ControllerName = "CONTROLLER"
  val InterfaceName = "INTERFACE"
  val SoftwarePathName = "SW_PATH"
  val HarwarePathName = "HW_PATH"
  val OutputName = "OUTPUT"
  val EnqueueName = "ENQUEUE"
  val SetDlSrcName = "SET_DL_SRC"
  val SetDlDstName = "SET_DL_DST"
  val SetVlanName = "SET_VLAN_ID"
  val SetVlanPcpName = "SET_VLAN_PCP"
  val SetVlanCifName = "SET_VLAN_CFI"
  val PopVlanName = "POP_VLAN"
  val PushVlanName = "PUSH_VLAN"
  val SetDlTypeName = "SET_DL_TYPE"
  val SetNwSrcName = "SET_NW_SRC"
  val SetNwDstName = "SET_NW_DST"
  val SetNwTosName = "SET_NW_TOS"
  val SetTpSrcName = "SET_TP_SRC"
  val SetTpDstName = "SET_TP_DST"
  val SetNextHopName = "SET_NEXT_HOP"

  def apply(name: String, value: Option[String]): Try[Action] = Try {
    import java.lang.Byte.{decode => toByte}
    import java.lang.Short.{decode => toShort}
    import java.lang.Integer.{decode => toInt}
    import java.lang.Long.{decode => toLong}
    // TODO: check ranges
    value match {
      case Some(v) => name match {
        case `OutputName`     => Output(NodeConnector.parse(v).get)
        case `EnqueueName`    => Enqueue(UInt(toLong(v)))
        case `SetDlSrcName`   => SetDlSrc(MAC.parse(v).get.value)
        case `SetDlDstName`   => SetDlDst(MAC.parse(v).get.value)
        case `SetVlanName`    => SetVlan(UShort(toInt(v)))
        case `SetVlanPcpName` => SetVlanPcp(UByte(toByte(v)))
        case `PushVlanName`   => PushVlan(UShort(toInt(v)))
        case `SetDlTypeName`  => SetDlType(UShort(toInt(v)))
        case `SetNwSrcName`   => SetNwSrc(InetAddress.getByName(v))
        case `SetNwDstName`   => SetNwDst(InetAddress.getByName(v))
        case `SetNwTosName`   => SetNwTos(UByte(toByte(v)))
        case `SetTpSrcName`   => SetTpSrc(UShort(toInt(v)))
        case `SetTpDstName`   => SetTpDst(UShort(toInt(v)))
      }
      case None => name match {
        case `DropName`         => Drop()
        case `LoopbackName`     => Loopback()
        case `FloodName`        => Flood()
        case `FloodAllName`     => FloodAll()
        case `ControllerName`   => Controller()
        case `InterfaceName`    => Interface()
        case `SoftwarePathName` => SoftwarePath()
        case `HarwarePathName`  => HarwarePath()
        case `SetVlanCifName`   => SetVlanCif()
        case `PopVlanName`      => PopVlan()
        case `SetNextHopName`   => SetNextHop()
      }
    }
  }
}

import Action._

case class Output[T <: NodeConnector[_, _ <: Node[_]]](nc: T)
                                            extends Action { def name = OutputName;     override val stringValue = Some(nc.idStr) }
case class Enqueue      (qid: UInt)         extends Action { def name = EnqueueName;    override val stringValue = Some(qid.toString)      }
case class SetDlSrc     (data: Array[Byte]) extends Action { def name = SetDlSrcName;   override val stringValue = Some(data.toString)     }
case class SetDlDst     (data: Array[Byte]) extends Action { def name = SetDlDstName;   override val stringValue = Some(data.toString)     }
case class SetVlan      (id: UShort)        extends Action { def name = SetVlanName;    override val stringValue = Some(id.toString)       }
case class SetVlanPcp   (pcp: UByte)        extends Action { def name = SetVlanPcpName; override val stringValue = Some(pcp.toString)      }
case class PushVlan     (`type`: UShort)    extends Action { def name = PushVlanName;   override val stringValue = Some(`type`.toString)   }
case class SetDlType    (`type`: UShort)    extends Action { def name = SetDlTypeName;  override val stringValue = Some(`type`.toString)   }
case class SetNwSrc     (addr: InetAddress) extends Action { def name = SetNwSrcName;   override val stringValue = Some(addr.toString)     }
case class SetNwDst     (addr: InetAddress) extends Action { def name = SetNwDstName;   override val stringValue = Some(addr.toString)     }
case class SetNwTos     (tos: UByte)        extends Action { def name = SetNwTosName;   override val stringValue = Some(tos.toString)      }
case class SetTpSrc     (port: UShort)      extends Action { def name = SetTpSrcName;   override val stringValue = Some(port.toString)     }
case class SetTpDst     (port: UShort)      extends Action { def name = SetTpDstName;   override val stringValue = Some(port.toString)     }
case class Drop         ()                  extends Action { def name = DropName         }
case class Loopback     ()                  extends Action { def name = LoopbackName     }
case class Flood        ()                  extends Action { def name = FloodName        }
case class FloodAll     ()                  extends Action { def name = FloodAllName     }
case class Controller   ()                  extends Action { def name = ControllerName   }
case class Interface    ()                  extends Action { def name = InterfaceName    }
case class SoftwarePath ()                  extends Action { def name = SoftwarePathName }
case class HarwarePath  ()                  extends Action { def name = HarwarePathName  }
case class SetVlanCif   ()                  extends Action { def name = SetVlanCifName   }
case class PopVlan      ()                  extends Action { def name = PopVlanName      }
case class SetNextHop   ()                  extends Action { def name = SetNextHopName   }
