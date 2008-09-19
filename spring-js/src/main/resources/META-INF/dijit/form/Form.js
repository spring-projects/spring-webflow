/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.form.Form"]){dojo._hasResource["dijit.form.Form"]=true;dojo.provide("dijit.form.Form");dojo.require("dijit._Widget");dojo.require("dijit._Templated");dojo.declare("dijit.form._FormMixin",null,{action:"",method:"",enctype:"",name:"","accept-charset":"",accept:"",target:"",attributeMap:dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap),{action:"",method:"",enctype:"","accept-charset":"",accept:"",target:""}),execute:function(_1){},onCancel:function(){},onExecute:function(){},templateString:"<form dojoAttachPoint='containerNode' dojoAttachEvent='onsubmit:_onSubmit' name='${name}' enctype='multipart/form-data'></form>",_onSubmit:function(e){dojo.stopEvent(e);this.onExecute();this.execute(this.getValues());},submit:function(){this.containerNode.submit();},setValues:function(_3){var _4={};dojo.forEach(this.getDescendants(),function(_5){if(!_5.name){return;}var _6=_4[_5.name]||(_4[_5.name]=[]);_6.push(_5);});for(var _7 in _4){var _8=_4[_7],_9=dojo.getObject(_7,false,_3);if(!dojo.isArray(_9)){_9=[_9];}if(_8[0].setChecked){dojo.forEach(_8,function(w,i){w.setChecked(dojo.indexOf(_9,w.value)!=-1);});}else{dojo.forEach(_8,function(w,i){w.setValue(_9[i]);});}}},getValues:function(){var _e={};dojo.forEach(this.getDescendants(),function(_f){var _10=_f.getValue?_f.getValue():_f.value;var _11=_f.name;if(!_11){return;}if(_f.setChecked){if(/Radio/.test(_f.declaredClass)){if(_f.checked){dojo.setObject(_11,_10,_e);}}else{var ary=dojo.getObject(_11,false,_e);if(!ary){ary=[];dojo.setObject(_11,ary,_e);}if(_f.checked){ary.push(_10);}}}else{dojo.setObject(_11,_10,_e);}});return _e;},isValid:function(){return dojo.every(this.getDescendants(),function(_13){return !_13.isValid||_13.isValid();});}});dojo.declare("dijit.form.Form",[dijit._Widget,dijit._Templated,dijit.form._FormMixin],null);}