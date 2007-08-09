package org.springframework.webflow.samples.booking.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//import org.hibernate.validator.Length;
//import org.hibernate.validator.NotNull;

@Entity
public class Hotel implements Serializable {
    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String country;
    private BigDecimal price;

    @Id
    @GeneratedValue
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    // @Length(max=50) @NotNull
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    // @Length(max=100) @NotNull
    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    // @Length(max=40) @NotNull
    public String getCity() {
	return city;
    }

    public void setCity(String city) {
	this.city = city;
    }

    // @Length(min=4, max=6) @NotNull
    public String getZip() {
	return zip;
    }

    public void setZip(String zip) {
	this.zip = zip;
    }

    // @Length(min=2, max=10) @NotNull
    public String getState() {
	return state;
    }

    public void setState(String state) {
	this.state = state;
    }

    // @Length(min=2, max=40) @NotNull
    public String getCountry() {
	return country;
    }

    public void setCountry(String country) {
	this.country = country;
    }

    @Column(precision = 6, scale = 2)
    public BigDecimal getPrice() {
	return price;
    }

    public void setPrice(BigDecimal price) {
	this.price = price;
    }

    @Override
    public String toString() {
	return "Hotel(" + name + "," + address + "," + city + "," + zip + ")";
    }
}
