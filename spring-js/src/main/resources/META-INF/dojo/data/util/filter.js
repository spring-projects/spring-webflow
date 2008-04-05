/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.data.util.filter"]){dojo._hasResource["dojo.data.util.filter"]=true;dojo.provide("dojo.data.util.filter");dojo.data.util.filter.patternToRegExp=function(_1,_2){var _3="^";var c=null;for(var i=0;i<_1.length;i++){c=_1.charAt(i);switch(c){case "\\":_3+=c;i++;_3+=_1.charAt(i);break;case "*":_3+=".*";break;case "?":_3+=".";break;case "$":case "^":case "/":case "+":case ".":case "|":case "(":case ")":case "{":case "}":case "[":case "]":_3+="\\";default:_3+=c;}}_3+="$";if(_2){return new RegExp(_3,"i");}else{return new RegExp(_3);}};}