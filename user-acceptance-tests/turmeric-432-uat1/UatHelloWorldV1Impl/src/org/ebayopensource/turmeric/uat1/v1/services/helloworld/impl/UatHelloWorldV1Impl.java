
package org.ebayopensource.turmeric.uat1.v1.services.helloworld.impl;

import org.ebayopensource.turmeric.uat1.v1.services.SayHelloRequest;
import org.ebayopensource.turmeric.uat1.v1.services.SayHelloResponse;
import org.ebayopensource.turmeric.uat1.v1.services.helloworld.impl.UatHelloWorldV1;


public class UatHelloWorldV1Impl
    implements UatHelloWorldV1
{


    public SayHelloResponse sayHello(SayHelloRequest param0) {
        SayHelloResponse response = new SayHelloResponse();
        response.setHello("Hello, " + param0.getHello());
        return response;
    }

}
