package org.flowforwarding.warp.protocol.supply;

public interface IMethodGet <InType, OutType> {
   
   OutType get (InType in);
}
