/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.cookie"]){dojo._hasResource["dojo.cookie"]=true;dojo.provide("dojo.cookie");dojo.require("dojo.regexp");dojo.cookie=function(_1,_2,_3){var c=document.cookie;if(arguments.length==1){var _5=c.match(new RegExp("(?:^|; )"+dojo.regexp.escapeString(_1)+"=([^;]*)"));return _5?decodeURIComponent(_5[1]):undefined;}else{_3=_3||{};var _6=_3.expires;if(typeof _6=="number"){var d=new Date();d.setTime(d.getTime()+_6*24*60*60*1000);_6=_3.expires=d;}if(_6&&_6.toUTCString){_3.expires=_6.toUTCString();}_2=encodeURIComponent(_2);var _8=_1+"="+_2;for(propName in _3){_8+="; "+propName;var _9=_3[propName];if(_9!==true){_8+="="+_9;}}document.cookie=_8;}};dojo.cookie.isSupported=function(){if(!("cookieEnabled" in navigator)){this("__djCookieTest__","CookiesAllowed");navigator.cookieEnabled=this("__djCookieTest__")=="CookiesAllowed";if(navigator.cookieEnabled){this("__djCookieTest__","",{expires:-1});}}return navigator.cookieEnabled;};}