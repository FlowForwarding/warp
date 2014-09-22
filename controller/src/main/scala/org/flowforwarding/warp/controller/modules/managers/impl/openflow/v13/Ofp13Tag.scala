/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.managers.impl.openflow.v13

import scala.reflect.ClassTag
import org.flowforwarding.warp.controller.modules.managers.{OFNodeConnector, NodeTag, OFNode}

trait Ofp13Tag extends NodeTag[OFNode, OFNodeConnector]{
  protected implicit val nodeTag: ClassTag[OFNode] = ClassTag(classOf[OFNode])
  protected implicit val connectorTag: ClassTag[OFNodeConnector] = ClassTag(classOf[OFNodeConnector])
}