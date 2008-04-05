/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.TimeTextBox"]){dojo._hasResource["dijit.form.TimeTextBox"]=true;dojo.provide("dijit.form.TimeTextBox");dojo.require("dijit._TimePicker");dojo.require("dijit.form._DateTimeTextBox");dojo.declare("dijit.form.TimeTextBox",dijit.form._DateTimeTextBox,{popupClass:"dijit._TimePicker",_selector:"time"});}