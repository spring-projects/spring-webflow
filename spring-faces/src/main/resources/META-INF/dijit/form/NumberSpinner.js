/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.NumberSpinner"]){dojo._hasResource["dijit.form.NumberSpinner"]=true;dojo.provide("dijit.form.NumberSpinner");dojo.require("dijit.form._Spinner");dojo.require("dijit.form.NumberTextBox");dojo.declare("dijit.form.NumberSpinner",[dijit.form._Spinner,dijit.form.NumberTextBoxMixin],{required:true,adjust:function(_1,_2){var _3=_1+_2;if(isNaN(_1)||isNaN(_3)){return _1;}if((typeof this.constraints.max=="number")&&(_3>this.constraints.max)){_3=this.constraints.max;}if((typeof this.constraints.min=="number")&&(_3<this.constraints.min)){_3=this.constraints.min;}return _3;}});}