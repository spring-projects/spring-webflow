/*
 * Copyright 2004-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.faces.security;

import java.io.IOException;

/**
 * This class provides static methods that are registered as EL functions and available for use in Unified EL
 * expressions in standard Facelets views.
 * 
 * @author Rossen Stoyanchev
 * @since 2.2.0
 */
public abstract class FaceletsAuthorizeTagUtils {

	/**
	 * Returns true if the user has all of of the given authorities.
	 * 
	 * @param authorities a comma-separated list of user authorities.
	 */
	public static boolean areAllGranted(String authorities) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setIfAllGranted(authorities);
		return authorizeTag.authorize();
	}

	/**
	 * Returns true if the user has any of the given authorities.
	 * 
	 * @param authorities a comma-separated list of user authorities.
	 */
	public static boolean areAnyGranted(String authorities) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setIfAnyGranted(authorities);
		return authorizeTag.authorize();
	}

	/**
	 * Returns true if the user does not have any of the given authorities.
	 * 
	 * @param authorities a comma-separated list of user authorities.
	 */
	public static boolean areNotGranted(String authorities) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setIfNotGranted(authorities);
		return authorizeTag.authorize();
	}

	/**
	 * Returns true if the user is allowed to access the given URL and HTTP method combination. The HTTP method is
	 * optional and case insensitive.
	 */
	public static boolean isAllowed(String url, String method) throws IOException {
		FaceletsAuthorizeTag authorizeTag = new FaceletsAuthorizeTag();
		authorizeTag.setUrl(url);
		authorizeTag.setMethod(method);
		return authorizeTag.authorizeUsingUrlCheck();
	}

}
