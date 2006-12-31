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
package org.springframework.webflow.samples.sellitem;

import java.io.Serializable;
import java.util.Date;

import org.springframework.core.style.ToStringCreator;

public class Sale implements Serializable {

	private double price;

	private int itemCount;

	private String category;

	private boolean shipping;

	private String shippingType;

	private Date shipDate;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isShipping() {
		return shipping;
	}

	public void setShipping(boolean shipping) {
		this.shipping = shipping;
	}

	public String getShippingType() {
		return shippingType;
	}

	public void setShippingType(String shippingType) {
		this.shippingType = shippingType;
	}

	public Date getShipDate() {
		return shipDate;
	}

	public void setShipDate(Date shipDate) {
		this.shipDate = shipDate;
	}

	// business logic methods

	/**
	 * Returns the base amount of the sale, without discount or delivery costs.
	 */
	public double getAmount() {
		return price * itemCount;
	}

	/**
	 * Returns the discount rate to apply.
	 */
	public double getDiscountRate() {
		double discount = 0.02;
		if ("A".equals(category)) {
			if (itemCount >= 100) {
				discount = 0.1;
			}
		}
		else if ("B".equals(category)) {
			if (itemCount >= 200) {
				discount = 0.2;
			}
		}
		return discount;
	}

	/**
	 * Returns the savings because of the discount.
	 */
	public double getSavings() {
		return getDiscountRate() * getAmount();
	}

	/**
	 * Returns the delivery cost.
	 */
	public double getDeliveryCost() {
		double delCost = 0.0;
		if ("S".equals(shippingType)) {
			delCost = 10.0;
		}
		else if ("E".equals(shippingType)) {
			delCost = 20.0;
		}
		return delCost;
	}

	/**
	 * Returns the total cost of the sale, including discount and delivery cost.
	 */
	public double getTotalCost() {
		return getAmount() + getDeliveryCost() - getSavings();
	}

	public String toString() {
		return new ToStringCreator(this).append("price", price).append("itemCount", itemCount).append("shippingType",
				shippingType).append("shipDate", shipDate).toString();
	}
}