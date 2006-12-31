/*
 * Copyright 2002-2007 the original author or authors.
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

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class RateCriteriaValidator implements Validator {

	public boolean supports(Class clazz) {
		return RateCriteria.class.isAssignableFrom(clazz);
	}

	public void validate(Object obj, Errors errors) {
		RateCriteria criteria = (RateCriteria)obj;
		validateSender(criteria, errors);
		validateReceiver(criteria, errors);
		validatePackageDetails(criteria, errors);
	}

	public void validateSender(RateCriteria query, Errors errors) {
		if (!StringUtils.hasText(query.getSenderCountryCode()) || query.getSenderCountryCode().equals("null")) {
			errors.rejectValue("senderCountryCode", "senderCountryCodeRequired", "Sender country code is required");
		}
		if (!StringUtils.hasText(query.getSenderZipCode())) {
			errors.rejectValue("senderZipCode", "senderZipCodeRequired", "Sender zip code is required");
		}
	}

	public void validateReceiver(RateCriteria query, Errors errors) {
		if (!StringUtils.hasText(query.getReceiverCountryCode()) || query.getReceiverCountryCode().equals("null")) {
			errors.rejectValue("receiverCountryCode", "receiverCountryCodeRequired",
					"Receiver country code is required");
		}
		if (!StringUtils.hasText(query.getReceiverZipCode())) {
			errors.rejectValue("receiverZipCode", "receiverZipCodeRequired", "Receiver zip code is required");
		}
	}

	public void validatePackageDetails(RateCriteria query, Errors errors) {
		if (query.getPackageType() < 0) {
			errors.rejectValue("packageType", "packageTypeRequired", "Package type is required");
		}
		if (query.getPackageWeight() <= 0) {
			errors.rejectValue("packageWeight", "packageWeightRequired", "Package weight is required");
		}
	}
}
