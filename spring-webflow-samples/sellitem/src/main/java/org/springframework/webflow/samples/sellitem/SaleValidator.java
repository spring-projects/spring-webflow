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
package org.springframework.webflow.samples.sellitem;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SaleValidator implements Validator {

	public boolean supports(Class clazz) {
		return Sale.class.equals(clazz);
	}

	public void validate(Object obj, Errors errors) {
		Sale sale = (Sale)obj;
		validatePriceAndItemCount(sale, errors);
	}
	
	public void validatePriceAndItemCount(Sale sale, Errors errors) {
		if (sale.getItemCount() <= 0) {
			errors.rejectValue("itemCount", "tooLittle", "Item count must be greater than 0");
		}
		if (sale.getPrice() <= 0.0) {
			errors.rejectValue("price", "tooLittle", "Price must be greater than 0.0");
		}
	}
}