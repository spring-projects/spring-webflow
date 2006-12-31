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
package org.springframework.webflow.samples.phonebook.stub;

import java.util.ArrayList;
import java.util.List;

import org.springframework.webflow.samples.phonebook.Person;
import org.springframework.webflow.samples.phonebook.Phonebook;
import org.springframework.webflow.samples.phonebook.SearchCriteria;

public class StubPhonebook implements Phonebook {

	private List<Person> persons = new ArrayList<Person>();

	public StubPhonebook() {
		// setup some dummy test data
		Person kd = new Person(1, "Keith", "Donald", "kdonald", "11111");
		Person ev = new Person(2, "Erwin", "Vervaet", "klr8", "22222");
		Person cs = new Person(3, "Colin", "Sampaleanu", "sampa", "33333");
		Person jh = new Person(4, "Juergen", "Hoeller", "jhoeller", "44444");
		Person rj = new Person(5, "Rod", "Johnson", "rod", "55555");
		Person tr = new Person(6, "Thomas", "Risberg", "trisberg", "66666");
		Person aa = new Person(7, "Alef", "Arendsen", "alef", "77777");
		Person mp = new Person(8, "Mark", "Pollack", "mark", "88888");

		kd.addColleague(ev);
		kd.addColleague(cs);
		kd.addColleague(jh);
		kd.addColleague(rj);
		kd.addColleague(tr);
		kd.addColleague(aa);
		kd.addColleague(mp);

		ev.addColleague(kd);
		ev.addColleague(cs);
		ev.addColleague(jh);
		ev.addColleague(rj);

		cs.addColleague(kd);
		cs.addColleague(ev);
		cs.addColleague(jh);
		cs.addColleague(rj);
		cs.addColleague(aa);
		cs.addColleague(mp);

		rj.addColleague(cs);
		rj.addColleague(kd);
		rj.addColleague(ev);
		rj.addColleague(jh);
		rj.addColleague(tr);
		rj.addColleague(aa);
		rj.addColleague(mp);

		jh.addColleague(cs);
		jh.addColleague(kd);
		jh.addColleague(ev);
		jh.addColleague(jh);
		jh.addColleague(tr);
		jh.addColleague(aa);

		Person sa = new Person(9, "Shaun", "Alexander", "rolltide", "44444");
		Person dj = new Person(10, "Darell", "Jackson", "gatorcountry", "55555");
		sa.addColleague(dj);
		dj.addColleague(sa);

		persons.add(kd);
		persons.add(ev);
		persons.add(cs);
		persons.add(jh);
		persons.add(rj);
		persons.add(tr);
		persons.add(aa);
		persons.add(mp);

		persons.add(sa);
		persons.add(dj);
	}

	public List<Person> search(SearchCriteria query) {
		List<Person> res = new ArrayList<Person>();
		for (Person person : persons) {
			if ((person.getFirstName().indexOf(query.getFirstName()) != -1)
					&& (person.getLastName().indexOf(query.getLastName()) != -1)) {
				res.add(person);
			}
		}
		return res;
	}

	public Person getPerson(Long id) {
		for (Person person : persons) {
			if (person.getId().equals(id)) {
				return person;
			}
		}
		return null;
	}

	public Person getPerson(String userId) {
		for (Person person : persons) {
			if (person.getUserId().equals(userId)) {
				return person;
			}
		}
		return null;
	}

}