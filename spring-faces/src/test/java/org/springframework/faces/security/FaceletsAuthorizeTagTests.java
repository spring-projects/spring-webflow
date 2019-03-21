/*
 * Copyright 2002-2015 the original author or authors.
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

import junit.framework.TestCase;

/**
 * Unit tests for {@link FaceletsAuthorizeTag}.
 * @author Rossen Stoyanchev
 */
public class FaceletsAuthorizeTagTests extends TestCase {

	public void testIfAllGrantedWithOneRole() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfAllGranted("ROLE_A");
		assertEquals("hasRole('ROLE_A')", tag.getAccess());
	}

	public void testIfAllGrantedWithMultipleRoles() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfAllGranted("ROLE_A, ROLE_B, ROLE_C");
		assertEquals("hasRole('ROLE_A') and hasRole('ROLE_B') and hasRole('ROLE_C')", tag.getAccess());
	}

	public void testIfAnyGrantedWithOneRole() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfAnyGranted("ROLE_A");
		assertEquals("hasAnyRole('ROLE_A')", tag.getAccess());
	}

	public void testIfAnyGrantedWithMultipleRole() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfAnyGranted("ROLE_A, ROLE_B, ROLE_C");
		assertEquals("hasAnyRole('ROLE_A','ROLE_B','ROLE_C')", tag.getAccess());
	}

	public void testIfNoneGrantedWithOneRole() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfNotGranted("ROLE_A");
		assertEquals("!hasAnyRole('ROLE_A')", tag.getAccess());
	}

	public void testIfNoneGrantedWithMultipleRole() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfNotGranted("ROLE_A, ROLE_B, ROLE_C");
		assertEquals("!hasAnyRole('ROLE_A','ROLE_B','ROLE_C')", tag.getAccess());
	}

	public void testIfAllAnyNotGranted() {
		FaceletsAuthorizeTag tag = new FaceletsAuthorizeTag();
		tag.setIfAllGranted("ROLE_A");
		tag.setIfAnyGranted("ROLE_B");
		tag.setIfNotGranted("ROLE_C");
		assertEquals("hasRole('ROLE_A') and hasAnyRole('ROLE_B') and !hasAnyRole('ROLE_C')", tag.getAccess());
	}

}
