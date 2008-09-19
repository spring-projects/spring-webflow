/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.cookie"]){dojo._hasResource["dojo.cookie"]=true;dojo.provide("dojo.cookie");dojo.cookie=function(_1,_2,_3){var c=document.cookie;if(arguments.length==1){var _5=c.lastIndexOf(_1+"=");if(_5==-1){return null;}var _6=_5+_1.length+1;var _7=c.indexOf(";",_5+_1.length+1);if(_7==-1){_7=c.length;}return decodeURIComponent(c.substring(_6,_7));}else{_3=_3||{};_2=encodeURIComponent(_2);if(typeof (_3.expires)=="number"){var d=new Date();d.setTime(d.getTime()+(_3.expires*24*60*60*1000));_3.expires=d;}document.cookie=_1+"="+_2+(_3.expires?"; expires="+_3.expires.toUTCString():"")+(_3.path?"; path="+_3.path:"")+(_3.domain?"; domain="+_3.domain:"")+(_3.secure?"; secure":"");return null;}};}