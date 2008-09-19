/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.CheckBox"]){dojo._hasResource["dijit.form.CheckBox"]=true;dojo.provide("dijit.form.CheckBox");dojo.require("dijit.form.Button");dojo.declare("dijit.form.CheckBox",dijit.form.ToggleButton,{templateString:"<fieldset class=\"dijitReset dijitInline\" waiRole=\"presentation\"\n\t><input\n\t \ttype=\"${type}\" name=\"${name}\"\n\t\tclass=\"dijitReset dijitCheckBoxInput\"\n\t\tdojoAttachPoint=\"inputNode,focusNode\"\n\t \tdojoAttachEvent=\"onmouseover:_onMouse,onmouseout:_onMouse,onclick:_onClick\"\n/></fieldset>\n",baseClass:"dijitCheckBox",type:"checkbox",value:"on",postCreate:function(){dojo.setSelectable(this.inputNode,false);this.setChecked(this.checked);this.inherited(arguments);},setChecked:function(_1){if(dojo.isIE){if(_1){this.inputNode.setAttribute("checked","checked");}else{this.inputNode.removeAttribute("checked");}}else{this.inputNode.checked=_1;}this.inherited(arguments);},setValue:function(_2){if(_2==null){_2="";}this.inputNode.value=_2;dijit.form.CheckBox.superclass.setValue.call(this,_2);}});dojo.declare("dijit.form.RadioButton",dijit.form.CheckBox,{type:"radio",baseClass:"dijitRadio",_groups:{},postCreate:function(){(this._groups[this.name]=this._groups[this.name]||[]).push(this);this.inherited(arguments);},uninitialize:function(){dojo.forEach(this._groups[this.name],function(_3,i,_5){if(_3===this){_5.splice(i,1);return;}},this);},setChecked:function(_6){if(_6){dojo.forEach(this._groups[this.name],function(_7){if(_7!=this&&_7.checked){_7.setChecked(false);}},this);}this.inherited(arguments);},_clicked:function(e){if(!this.checked){this.setChecked(true);}}});}