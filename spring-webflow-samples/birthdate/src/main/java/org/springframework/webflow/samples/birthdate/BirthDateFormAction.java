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
package org.springframework.webflow.samples.birthdate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.webflow.action.FormAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.ScopeType;

public class BirthDateFormAction extends FormAction {

	// standard European format
	private static final String BIRTH_DATE_PATTERN = "dd-MM-yyyy";

	private static final String AGE_NAME = "age";

	public BirthDateFormAction() {
		// tell the superclass about the form object and validator we want to use
		// you could also do this in the application context XML ofcourse
		setFormObjectName("birthDate");
		setFormObjectClass(BirthDate.class);
		setFormObjectScope(ScopeType.FLOW);
		setValidator(new BirthDateValidator());
	}

	protected void registerPropertyEditors(PropertyEditorRegistry registry) {
		// register a custom property editor to handle the date input
		SimpleDateFormat dateFormat = new SimpleDateFormat(BIRTH_DATE_PATTERN);
		registry.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	/*
	 * Our "onSubmit" hook: an action execute method.
	 */
	public Event calculateAge(RequestContext context) throws Exception {
		// pull the date from the model
		BirthDate birthDate = (BirthDate)getFormObject(context);

		// calculate the age (quick & dirty)
		// in a real application you would delegate to the business layer for
		// this kind of logic
		Calendar calBirthDate = new GregorianCalendar();
		calBirthDate.setTime(birthDate.getDate());
		Calendar calNow = new GregorianCalendar();

		int ageYears = calNow.get(Calendar.YEAR) - calBirthDate.get(Calendar.YEAR);
		long ageMonths = calNow.get(Calendar.MONTH) - calBirthDate.get(Calendar.MONTH);
		long ageDays = calNow.get(Calendar.DAY_OF_MONTH) - calBirthDate.get(Calendar.DAY_OF_MONTH);

		if (ageDays < 0) {
			ageMonths--;
			ageDays += calBirthDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		}

		if (ageMonths < 0) {
			ageYears--;
			ageMonths += 12;
		}

		// create a nice age string
		StringBuffer ageStr = new StringBuffer();
		ageStr.append(ageYears).append(" years, ");
		ageStr.append(ageMonths).append(" months and ");
		ageStr.append(ageDays).append(" days");

		// put it in the model for display by the view
		context.getRequestScope().put(AGE_NAME, ageStr);

		return success();
	}
}