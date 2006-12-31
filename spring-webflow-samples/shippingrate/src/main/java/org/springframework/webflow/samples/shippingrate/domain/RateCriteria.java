/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.samples.shippingrate.domain;

import java.io.Serializable;

public class RateCriteria implements Serializable {

	private boolean residential = true;

	private String senderZipCode;

	private String receiverZipCode;

	private String senderCountryCode;

	private String receiverCountryCode;

	private int packageType = -1;

	private double packageWeight;

	public int getPackageType() {
		return packageType;
	}

	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}

	public double getPackageWeight() {
		return packageWeight;
	}

	public void setPackageWeight(double packageWeight) {
		this.packageWeight = packageWeight;
	}

	public String getReceiverCountryCode() {
		return receiverCountryCode;
	}

	public void setReceiverCountryCode(String receiverCountryCode) {
		this.receiverCountryCode = receiverCountryCode;
	}

	public String getReceiverZipCode() {
		return receiverZipCode;
	}

	public void setReceiverZipCode(String receiverZipCode) {
		this.receiverZipCode = receiverZipCode;
	}

	public boolean isResidential() {
		return residential;
	}

	public void setResidential(boolean residential) {
		this.residential = residential;
	}

	public String getSenderCountryCode() {
		return senderCountryCode;
	}

	public void setSenderCountryCode(String senderCountryCode) {
		this.senderCountryCode = senderCountryCode;
	}

	public String getSenderZipCode() {
		return senderZipCode;
	}

	public void setSenderZipCode(String senderZipCode) {
		this.senderZipCode = senderZipCode;
	}
}