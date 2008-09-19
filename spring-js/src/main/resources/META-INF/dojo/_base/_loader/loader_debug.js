/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo._base._loader.loader_debug"]){dojo._hasResource["dojo._base._loader.loader_debug"]=true;dojo.provide("dojo._base._loader.loader_debug");dojo.nonDebugProvide=dojo.provide;dojo.provide=function(_1){var _2=dojo["_xdDebugQueue"];if(_2&&_2.length>0&&_1==_2["currentResourceName"]){window.setTimeout("dojo._xdDebugFileLoaded('"+_1+"')",1);}return dojo.nonDebugProvide.apply(dojo,arguments);};dojo._xdDebugFileLoaded=function(_3){var _4=this._xdDebugQueue;if(_3&&_3==_4.currentResourceName){_4.shift();}if(_4.length==0){_4.currentResourceName=null;this._xdNotifyLoaded();}else{_4.currentResourceName=_4[0].resourceName;var _5=document.createElement("script");_5.type="text/javascript";_5.src=_4[0].resourcePath;document.getElementsByTagName("head")[0].appendChild(_5);}};}