/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class NodeResource extends CoapResource {


	public NodeResource(String name) {
		super(name);
	}

	@Override
	public void handleRequest(Exchange exchange) {
		CoapExchange coapExchange=new CoapExchange(exchange,this);
		coapExchange.respond(CoAP.ResponseCode.CONTENT,"Original Path: "+exchange.getRequest().getOptions().getUriPathString()
				+"\nActual Dynamic Path: "+this.getPath()+getName());
	}
}
