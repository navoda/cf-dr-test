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

import org.eclipse.californium.core.CoapServer;

public class TestServer {

	public static void main(String[] args) {
		CoapServer server= new CoapServer(7070);
		server.setMessageDeliverer(new DynamicMessageDeliverer(server.getRoot()));
		/**
		 * resource tree
		 * /rd(root)
		 *          /devices[dynamic parent]
		 *                      /{deviceId}[dynamic - integer]
		 *                                  /stats
		 *                      /{deviceName}[dynamic - String]
		 *                                  /controls
		 *                      /list [static node]
		 *         /download [static node]
		 */

		//level 1
		NodeResource rd= new NodeResource("rd");
		//level 2
		DynamicResourceParent devices=new DynamicResourceParent("devices");
		rd.add(devices);
		rd.add(new NodeResource("download"));
		//level 3
		devices.add(new DynamicResource("{deviceId}", DynamicResource.DataType.INTEGER));
		devices.add(new DynamicResource("{deviceName}", DynamicResource.DataType.STRING));
		//level 4
		devices.getChild("{deviceId}").add(new NodeResource("stats"));
		devices.getChild("{deviceName}").add(new NodeResource("controls"));
		devices.add(new NodeResource("list"));

		server.add(rd);
		server.start();

		//further test [2 dynamic param in a path]
		//level 2
		DynamicResourceParent users= new DynamicResourceParent("users");
		rd.add(users);
		//level 3
		users.add(new DynamicResource("{userName}", DynamicResource.DataType.STRING));
		users.add(new NodeResource("list"));
		//level 4
		NodeResource userDevices=new NodeResource("devices");
		users.getChild("{userName}").add(userDevices);
		//level 5
		userDevices.add(new DynamicResource("{deviceId}", DynamicResource.DataType.INTEGER));
		userDevices.add(new DynamicResource("{deviceName}", DynamicResource.DataType.STRING));
		//level 6
		userDevices.getChild("{deviceId}").add(new NodeResource("stats"));
		userDevices.getChild("{deviceName}").add(new NodeResource("controls"));
		userDevices.add(new NodeResource("list"));

	}


}
