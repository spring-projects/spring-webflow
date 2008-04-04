/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.CurrencyTextBox"]){dojo._hasResource["dijit.form.CurrencyTextBox"]=true;dojo.provide("dijit.form.CurrencyTextBox");dojo.require("dojo.currency");dojo.require("dijit.form.NumberTextBox");dojo.declare("dijit.form.CurrencyTextBox",dijit.form.NumberTextBox,{currency:"",regExpGen:dojo.currency.regexp,_formatter:dojo.currency.format,parse:dojo.currency.parse,postMixInProperties:function(){if(this.constraints===dijit.form.ValidationTextBox.prototype.constraints){this.constraints={};}this.constraints.currency=this.currency;dijit.form.CurrencyTextBox.superclass.postMixInProperties.apply(this,arguments);}});}