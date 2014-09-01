package org.flowforwarding.warp.protocol.ofstructures;

import java.util.ArrayList;
import java.util.List;

import org.flowforwarding.warp.protocol.ofmessages.ActionSet;
import org.flowforwarding.warp.util.Tuple;

public class OFStructureBucket  implements IOFStructure {
   
   protected List<Tuple<String, String>> parms = new ArrayList<>();
   protected ActionSet actions;
   
   
}
