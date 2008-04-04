/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.MultiSelect"]){dojo._hasResource["dijit.form.MultiSelect"]=true;dojo.provide("dijit.form.MultiSelect");dojo.require("dijit.form._FormWidget");dojo.declare("dijit.form.MultiSelect",dijit.form._FormWidget,{size:7,templateString:"<select multiple='true' dojoAttachPoint='containerNode,focusNode' dojoAttachEvent='onchange: _onChange'></select>",attributeMap:dojo.mixin(dojo.clone(dijit.form._FormWidget.prototype.attributeMap),{size:"focusNode"}),addSelected:function(_1){_1.getSelected().forEach(function(n){this.containerNode.appendChild(n);},this);},getSelected:function(){return dojo.query("option",this.containerNode).filter(function(n){return n.selected;});},_getValueDeprecated:false,getValue:function(){return this.getSelected().map(function(n){return n.value;});},_multiValue:true,setValue:function(_5){dojo.query("option",this.containerNode).forEach(function(n){n.selected=(dojo.indexOf(_5,n.value)!=-1);});},invertSelection:function(_7){dojo.query("option",this.containerNode).forEach(function(n){n.selected=!n.selected;});this._handleOnChange(this.getValue(),_7==true);},_onChange:function(e){this._handleOnChange(this.getValue(),true);},resize:function(_a){if(_a){dojo.marginBox(this.domNode,_a);}},postCreate:function(){this._onChange();}});}