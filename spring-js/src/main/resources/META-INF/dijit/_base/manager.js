/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._base.manager"]){dojo._hasResource["dijit._base.manager"]=true;dojo.provide("dijit._base.manager");dojo.declare("dijit.WidgetSet",null,{constructor:function(){this._hash={};},add:function(_1){if(this._hash[_1.id]){throw new Error("Tried to register widget with id=="+_1.id+" but that id is already registered");}this._hash[_1.id]=_1;},remove:function(id){delete this._hash[id];},forEach:function(_3){for(var id in this._hash){_3(this._hash[id]);}},filter:function(_5){var _6=new dijit.WidgetSet();this.forEach(function(_7){if(_5(_7)){_6.add(_7);}});return _6;},byId:function(id){return this._hash[id];},byClass:function(_9){return this.filter(function(_a){return _a.declaredClass==_9;});}});dijit.registry=new dijit.WidgetSet();dijit._widgetTypeCtr={};dijit.getUniqueId=function(_b){var id;do{id=_b+"_"+(dijit._widgetTypeCtr[_b]!==undefined?++dijit._widgetTypeCtr[_b]:dijit._widgetTypeCtr[_b]=0);}while(dijit.byId(id));return id;};if(dojo.isIE){dojo.addOnUnload(function(){dijit.registry.forEach(function(_d){_d.destroy();});});}dijit.byId=function(id){return (dojo.isString(id))?dijit.registry.byId(id):id;};dijit.byNode=function(_f){return dijit.registry.byId(_f.getAttribute("widgetId"));};dijit.getEnclosingWidget=function(_10){while(_10){if(_10.getAttribute&&_10.getAttribute("widgetId")){return dijit.registry.byId(_10.getAttribute("widgetId"));}_10=_10.parentNode;}return null;};}