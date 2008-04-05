/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.regexp"]){dojo._hasResource["dojo.regexp"]=true;dojo.provide("dojo.regexp");dojo.regexp.escapeString=function(_1,_2){return _1.replace(/([\.$?*!=:|{}\(\)\[\]\\\/^])/g,function(ch){if(_2&&_2.indexOf(ch)!=-1){return ch;}return "\\"+ch;});};dojo.regexp.buildGroupRE=function(_4,re,_6){if(!(_4 instanceof Array)){return re(_4);}var b=[];for(var i=0;i<_4.length;i++){b.push(re(_4[i]));}return dojo.regexp.group(b.join("|"),_6);};dojo.regexp.group=function(_9,_a){return "("+(_a?"?:":"")+_9+")";};}