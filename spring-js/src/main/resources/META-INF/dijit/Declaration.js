/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.Declaration"]){dojo._hasResource["dijit.Declaration"]=true;dojo.provide("dijit.Declaration");dojo.require("dijit._Widget");dojo.require("dijit._Templated");dojo.declare("dijit.Declaration",dijit._Widget,{_noScript:true,widgetClass:"",replaceVars:true,defaults:null,mixins:[],buildRendering:function(){var _1=this.srcNodeRef.parentNode.removeChild(this.srcNodeRef);var _2=dojo.query("> script[type='dojo/method'][event='preamble']",_1).orphan();var _3=dojo.query("> script[type^='dojo/']",_1).orphan();var _4=_1.nodeName;var _5=this.defaults||{};this.mixins=this.mixins.length?dojo.map(this.mixins,function(_6){return dojo.getObject(_6);}):[dijit._Widget,dijit._Templated];if(_2.length){_5.preamble=dojo.parser._functionFromScript(_2[0]);}var _7=dojo.map(_3,function(s){var _9=s.getAttribute("event")||"postscript";return {event:_9,func:dojo.parser._functionFromScript(s)};});this.mixins.push(function(){dojo.forEach(_7,function(s){dojo.connect(this,s.event,this,s.func);},this);});_5.widgetsInTemplate=true;_5._skipNodeCache=true;_5.templateString="<"+_4+" class='"+_1.className+"' dojoAttachPoint='"+(_1.getAttribute("dojoAttachPoint")||"")+"' dojoAttachEvent='"+(_1.getAttribute("dojoAttachEvent")||"")+"' >"+_1.innerHTML.replace(/\%7B/g,"{").replace(/\%7D/g,"}")+"</"+_4+">";dojo.query("[dojoType]",_1).forEach(function(_b){_b.removeAttribute("dojoType");});dojo.declare(this.widgetClass,this.mixins,_5);}});}