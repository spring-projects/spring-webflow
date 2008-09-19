/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._base.sniff"]){dojo._hasResource["dijit._base.sniff"]=true;dojo.provide("dijit._base.sniff");(function(){var d=dojo;var ie=d.isIE;var _3=d.isOpera;var _4=Math.floor;var _5={dj_ie:ie,dj_ie6:_4(ie)==6,dj_ie7:_4(ie)==7,dj_iequirks:ie&&d.isQuirks,dj_opera:_3,dj_opera8:_4(_3)==8,dj_opera9:_4(_3)==9,dj_khtml:d.isKhtml,dj_safari:d.isSafari,dj_gecko:d.isMozilla};for(var p in _5){if(_5[p]){var _7=dojo.doc.documentElement;if(_7.className){_7.className+=" "+p;}else{_7.className=p;}}}})();}