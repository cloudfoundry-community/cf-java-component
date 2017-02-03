/*
 *   Copyright (c) 2013 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.client;

import cf.client.model.UaaUser;

import java.util.UUID;

/**
 * @author Mike Heath
 */
public interface Uaa {
	Token getUserToken(String client, String username, String password);
	
	Token getClientToken(String client, String clientSecret);

	TokenContents checkToken(String client, String clientSecret, Token token);

	UUID createUser(Token token, String username, String password, String origin);

	UaaUser getUser(Token token, String username);
}
