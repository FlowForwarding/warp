/*
 * © 2013 FlowForwarding.Org
 * All Rights Reserved.  Use is subject to license terms.
 *
 * @author Vitaliy Savkin (Vitaliy_Savkin@epam.com)
 */
package org.flowforwarding.warp.controller.modules.opendaylight_rest.impl.openflow.v13

import org.flowforwarding.warp.controller.modules.opendaylight_rest.managers.NodeTag
import org.flowforwarding.warp.controller.modules.opendaylight_rest.sal.{OFNode, OFNodeConnector}

import scala.reflect.ClassTag

trait Ofp13Tag extends NodeTag[OFNode, OFNodeConnector]{
  protected implicit val nodeTag: ClassTag[OFNode] = ClassTag(classOf[OFNode])
  protected implicit val connectorTag: ClassTag[OFNodeConnector] = ClassTag(classOf[OFNodeConnector])
}

