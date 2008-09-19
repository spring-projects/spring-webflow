/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.DateTextBox"]){dojo._hasResource["dijit.form.DateTextBox"]=true;dojo.provide("dijit.form.DateTextBox");dojo.require("dijit._Calendar");dojo.require("dijit.form.TimeTextBox");dojo.declare("dijit.form.DateTextBox",dijit.form.TimeTextBox,{_popupClass:"dijit._Calendar",postMixInProperties:function(){this.inherited("postMixInProperties",arguments);this.constraints.selector="date";}});}