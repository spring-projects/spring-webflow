/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo._base.window"]){dojo._hasResource["dojo._base.window"]=true;dojo.provide("dojo._base.window");dojo._gearsObject=function(){var _1;var _2;var _3=dojo.getObject("google.gears");if(_3){return _3;}if(typeof GearsFactory!="undefined"){_1=new GearsFactory();}else{if(dojo.isIE){try{_1=new ActiveXObject("Gears.Factory");}catch(e){}}else{if(navigator.mimeTypes["application/x-googlegears"]){_1=document.createElement("object");_1.setAttribute("type","application/x-googlegears");_1.setAttribute("width",0);_1.setAttribute("height",0);_1.style.display="none";document.documentElement.appendChild(_1);}}}if(!_1){return null;}dojo.setObject("google.gears.factory",_1);return dojo.getObject("google.gears");};dojo.isGears=(!!dojo._gearsObject())||0;dojo.doc=window["document"]||null;dojo.body=function(){return dojo.doc.body||dojo.doc.getElementsByTagName("body")[0];};dojo.setContext=function(_4,_5){dojo.global=_4;dojo.doc=_5;};dojo._fireCallback=function(_6,_7,_8){if(_7&&dojo.isString(_6)){_6=_7[_6];}return _6.apply(_7,_8||[]);};dojo.withGlobal=function(_9,_a,_b,_c){var _d;var _e=dojo.global;var _f=dojo.doc;try{dojo.setContext(_9,_9.document);_d=dojo._fireCallback(_a,_b,_c);}finally{dojo.setContext(_e,_f);}return _d;};dojo.withDoc=function(_10,_11,_12,_13){var _14;var _15=dojo.doc;try{dojo.doc=_10;_14=dojo._fireCallback(_11,_12,_13);}finally{dojo.doc=_15;}return _14;};}