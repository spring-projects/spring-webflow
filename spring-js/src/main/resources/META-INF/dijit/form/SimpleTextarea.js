/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.SimpleTextarea"]){dojo._hasResource["dijit.form.SimpleTextarea"]=true;dojo.provide("dijit.form.SimpleTextarea");dojo.require("dijit.form._FormWidget");dojo.declare("dijit.form.SimpleTextarea",dijit.form._FormValueWidget,{baseClass:"dijitTextArea",attributeMap:dojo.mixin(dojo.clone(dijit.form._FormValueWidget.prototype.attributeMap),{rows:"focusNode",cols:"focusNode"}),rows:"",cols:"",templateString:"<textarea name='${name}' dojoAttachPoint='focusNode,containerNode'>",postMixInProperties:function(){if(this.srcNodeRef){this.value=this.srcNodeRef.value;}},setValue:function(_1){this.domNode.value=_1;this.inherited(arguments);},getValue:function(){return this.domNode.value.replace(/\r/g,"");}});}