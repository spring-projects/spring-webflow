/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.NumberTextBox"]){dojo._hasResource["dijit.form.NumberTextBox"]=true;dojo.provide("dijit.form.NumberTextBox");dojo.require("dijit.form.ValidationTextBox");dojo.require("dojo.number");dojo.declare("dijit.form.NumberTextBoxMixin",null,{regExpGen:dojo.number.regexp,editOptions:{pattern:"#.######"},_onFocus:function(){this.setValue(this.getValue(),false);this.inherited(arguments);},_formatter:dojo.number.format,format:function(_1,_2){if(typeof _1=="string"){return _1;}if(isNaN(_1)){return "";}if(this.editOptions&&this._focused){_2=dojo.mixin(dojo.mixin({},this.editOptions),this.constraints);}return this._formatter(_1,_2);},parse:dojo.number.parse,filter:function(_3){if(typeof _3=="string"){return this.inherited("filter",arguments);}return isNaN(_3)?"":_3;},value:NaN});dojo.declare("dijit.form.NumberTextBox",[dijit.form.RangeBoundTextBox,dijit.form.NumberTextBoxMixin],{});}