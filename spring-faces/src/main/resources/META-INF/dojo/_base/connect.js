/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo._base.connect"]){dojo._hasResource["dojo._base.connect"]=true;dojo.provide("dojo._base.connect");dojo.require("dojo._base.lang");dojo._listener={getDispatcher:function(){return function(){var ap=Array.prototype,c=arguments.callee,ls=c._listeners,t=c.target;var r=t&&t.apply(this,arguments);for(var i in ls){if(!(i in ap)){ls[i].apply(this,arguments);}}return r;};},add:function(_7,_8,_9){_7=_7||dojo.global;var f=_7[_8];if(!f||!f._listeners){var d=dojo._listener.getDispatcher();d.target=f;d._listeners=[];f=_7[_8]=d;}return f._listeners.push(_9);},remove:function(_c,_d,_e){var f=(_c||dojo.global)[_d];if(f&&f._listeners&&_e--){delete f._listeners[_e];}}};dojo.connect=function(obj,_11,_12,_13,_14){var a=arguments,_16=[],i=0;_16.push(dojo.isString(a[0])?null:a[i++],a[i++]);var a1=a[i+1];_16.push(dojo.isString(a1)||dojo.isFunction(a1)?a[i++]:null,a[i++]);for(var l=a.length;i<l;i++){_16.push(a[i]);}return dojo._connect.apply(this,_16);};dojo._connect=function(obj,_1b,_1c,_1d){var l=dojo._listener,h=l.add(obj,_1b,dojo.hitch(_1c,_1d));return [obj,_1b,h,l];};dojo.disconnect=function(_20){if(_20&&_20[0]!==undefined){dojo._disconnect.apply(this,_20);delete _20[0];}};dojo._disconnect=function(obj,_22,_23,_24){_24.remove(obj,_22,_23);};dojo._topics={};dojo.subscribe=function(_25,_26,_27){return [_25,dojo._listener.add(dojo._topics,_25,dojo.hitch(_26,_27))];};dojo.unsubscribe=function(_28){if(_28){dojo._listener.remove(dojo._topics,_28[0],_28[1]);}};dojo.publish=function(_29,_2a){var f=dojo._topics[_29];if(f){f.apply(this,_2a||[]);}};dojo.connectPublisher=function(_2c,obj,_2e){var pf=function(){dojo.publish(_2c,arguments);};return (_2e)?dojo.connect(obj,_2e,pf):dojo.connect(obj,pf);};}