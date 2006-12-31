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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StubRateService implements RateService {

	public Map getCountries() {
		Map countries = new HashMap();
		countries.put("US", "United States");
		countries.put("CA", "Canada");
		return countries;
	}

	public Map getPackageTypes() {
		Map packageTypes = new HashMap();
		packageTypes.put("1", "Letter Envelope");
		packageTypes.put("2", "Express Box");
		packageTypes.put("3", "Tube");
		return packageTypes;
	}

	public Rate getRate(RateCriteria criteria) {
		return new Rate(new BigDecimal("1.39"));
	}
}
