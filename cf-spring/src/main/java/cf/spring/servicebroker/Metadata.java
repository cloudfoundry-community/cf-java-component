/*
 *   Copyright (c) 2014 Intellectual Reserve, Inc.  All rights reserved.
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
package cf.spring.servicebroker;

/**
 * Allows for setting metadata on the service. See http://docs.cloudfoundry.org/services/catalog-metadata.html for
 * more information on service metadata.
 *
 * @author Mike Heath <elcapo@gmail.com>
 */
public @interface Metadata {

	/**
	 * A short name for the service to be displayed in a catalog.
	 *
	 * <p>This maps to the "label" field on the Cloud Controller.</p>
	 *
	 * <p>This is a "CLI String". CLI strings are all lowercase, no spaces. Keep it short; imagine someone having to
	 * type it as an argument for a longer CLI command.</p>
	 */
	public static final String NAME = "name";

	/**
	 * A short 1-line description for the service, usually a single sentence or phrase.
	 *
	 * <p>This maps to the "description" field on the Cloud Controller.</p>
	 */
	public static final String DESCRIPTION = "description";

	/**
	 * V1 provider name.
	 *
	 * <p>This maps to the "provider" field on the Cloud Controller.</p>
	 *
	 * <p>This is a "CLI String". CLI strings are all lowercase, no spaces. Keep it short; imagine someone having to
	 * type it as an argument for a longer CLI command.</p>

	 * @deprecated This is a V1 field and will probably go away one day.
	 */
	public static final String PROVIDER = "provider";

	/**
	 * V1 service version.
	 *
	 * <p>This maps to the "version" field on the Cloud Controller.</p>
	 *
	 * @deprecated This is a v1 field and will probably go away one day.
	 */
	public static final String VERSION = "version";

	/**
	 * A list of permissions that the user must give up to the service, such as the ability to read application logs.
	 *
	 * <p>This maps to the "requires" field on the Cloud Controller.</p>
	 */
	public static final String REQUIRES = "requires";

	/**
	 * A list of strings that can be used by applications or front-ends to find or browse services. Such as ["mysql",
	 * "database", "relational"] for a mysql DB service.
	 *
	 * <p>This maps to the "tags" field on the Cloud Controller.</p>
	 */
	public static final String TAGS = "tags";

	/**
	 * The name of the service to be displayed in graphical clients.
	 *
	 * <p>This maps to the "extra.displayName" field on the Cloud Controller.</p>
	 */
	public static final String DISPLAY_NAME = "metadata.displayName";

	/**
	 * The URL to an image.
	 *
	 * <p>This maps to the "extra.imageUrl" field on the Cloud Controller.</p>
	 */
	public static final String IMAGE_URL = "metadata.imageUrl";

	/**
	 * Long description.
	 *
	 * <p>This maps to the "extra.longDescription" field on the Cloud Controller.</p>
	 */
	public static final String LONG_DESCRIPTION = "metadata.longDescription";

	/**
	 * The name of the upstream entity providing the actual service.
	 *
	 * <p>This maps to the "extra.providerDisplayName" field on the Cloud Controller.</p>
	 */
	public static final String PROVIDER_DISPLAY_NAME = "metadata.providerDisplayName";

	/**
	 * Link to documentation page for service.
	 *
	 * <p>This maps to the "extra.documentationUrl" field on the Cloud Controller.</p>
	 */
	public static final String DOCUMENTATION_URL = "extra.documentationUrl";

	/**
	 * Link to support for the service.
	 *
	 * <p>This maps to the "extra.supportUrl" field on the Cloud Controller.</p>
	 */
	public static final String SUPPORT_URL = "metadata.supportUrl";

	/**
	 * The metadata field.
	 */
	String field();

	/**
	 * The value of the field. May specify multiple values. Each value may be a SpEL expressions that returns a String
	 * or an object that is serializable to JSON by Jackson.
	 */
	String[] value();

}
