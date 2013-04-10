/*
 *   Copyright (c) 2012 Mike Heath.  All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package nats.vcap.message;

import nats.vcap.MessageBody;
import nats.vcap.NatsSubject;
import vcap.common.JsonObject;

/**
 * A request for components to announce themselves.
 *
 * See http://apidocs.cloudfoundry.com/health-manager/subscribe-vcap-component-discover
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
@NatsSubject("vcap.component.discover")
public class ComponentDiscover  extends JsonObject implements MessageBody<ComponentAnnounce> {
}
