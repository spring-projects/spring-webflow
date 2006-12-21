/*
 * Copyright 2002-2006 the original author or authors.
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

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public class SaleProcessorIntegrationTests extends AbstractTransactionalDataSourceSpringContextTests {

	private SaleProcessor saleProcessor;

	public void setSaleProcessor(SaleProcessor saleProcessor) {
		this.saleProcessor = saleProcessor;
	}

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "classpath:org/springframework/webflow/samples/sellitem/services-config.xml" };
	}

	public void testProcessSale() {
		int beforeCount = jdbcTemplate.queryForInt("select count(*) from T_SALES");
		Sale sale = new Sale();
		sale.setItemCount(25);
		sale.setPrice(100.0);
		sale.setCategory("A");
		sale.setShippingType("Express");
		saleProcessor.process(sale);
		int afterCount = jdbcTemplate.queryForInt("select count(*) from T_SALES");
		assertEquals("Wrong after count", beforeCount + 1, afterCount);
	}
}