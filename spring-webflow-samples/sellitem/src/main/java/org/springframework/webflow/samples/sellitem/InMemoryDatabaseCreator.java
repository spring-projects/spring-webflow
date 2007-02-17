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

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class InMemoryDatabaseCreator extends JdbcDaoSupport {

	@Override
	protected void initDao() throws Exception {
		String createSalesSql =
			"create table T_SALES (ID int not null identity primary key, ITEM_COUNT int not null, " +
			"PRICE double NOT NULL, category VARCHAR(1) NOT NULL, SHIPPING_TYPE varchar(1))";
		getJdbcTemplate().execute(createSalesSql);
	}

}
