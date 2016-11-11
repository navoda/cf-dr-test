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
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.Resource;

import java.util.LinkedList;
import java.util.logging.Logger;

public class DynamicResourceParent extends CoapResource {


	private static final Logger LOGGER = Logger.getLogger(DynamicResourceParent.class.getCanonicalName());

	public enum dataType {INTEGER, STRING}

	private dataType paramType = dataType.STRING;

	public DynamicResourceParent(String name) {
		super(name);
		this.paramType = paramType;

	}

	@Override
	public void handleRequest(Exchange exchange) {

		Request request = exchange.getRequest();
		LinkedList<String> path = new LinkedList<String>(request.getOptions().getUriPath());

		for (String param : this.getPath().substring(1).split("/")) {
			path.removeFirst();
		}

		Resource current = this;
		if (current.getName().equals(path.removeFirst())) {
			current = searchDynamicPath(path, current);
			if (current != null)
				current.handleRequest(exchange);
			else{
				exchange.sendResponse(new Response(CoAP.ResponseCode.NOT_FOUND));
			}

		}
		else
		{
			exchange.sendResponse(new Response(CoAP.ResponseCode.NOT_FOUND));
		}



	}

	/**
	 * Recursive method to search for a matching path consists of dynamic parameters [DynamicResource]
	 * @param path    - path of next resource to the destination
	 * @param current - parent resource
	 * @return        - if there is an exact match of dynamic path the end resource of the path is returned; else null.
	 */
	public Resource searchDynamicPath(LinkedList<String> path, Resource current) {

		Resource resource = null;

		if (!path.isEmpty() && !current.getChildren().isEmpty()) {

			if (current.getChild(path.getFirst()) != null) //if a static resource and matches the name
			{
				current = current.getChild(path.removeFirst());
				Resource check = searchDynamicPath(path, current);
				if (check != null)
					resource = check;

			} else {

				/**
				 * for each child resource check if it is a dynamic resource with matching parameter type
				 */

				String param = path.removeFirst();
				for (Resource child : current.getChildren()) {
					if (child instanceof DynamicResource && ((DynamicResource) child).isParamType(param))
					{
						Resource check = searchDynamicPath(path, child);
						if (check != null)
							resource = check;
					}
				}


			}
		} else if (path.isEmpty() && current.getChildren().isEmpty()) {
			return current;
		}


		return resource;

	}

	public dataType getParamType() {
		return paramType;
	}

	public void setParamType(dataType paramType) {
		this.paramType = paramType;
	}

}
