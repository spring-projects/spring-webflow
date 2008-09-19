/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo._base.json"]){dojo._hasResource["dojo._base.json"]=true;dojo.provide("dojo._base.json");dojo.fromJson=function(_1){try{return eval("("+_1+")");}catch(e){console.debug(e);return _1;}};dojo._escapeString=function(_2){return ("\""+_2.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");};dojo.toJsonIndentStr="\t";dojo.toJson=function(it,_4,_5){_5=_5||"";var _6=(_4?_5+dojo.toJsonIndentStr:"");var _7=(_4?"\n":"");var _8=typeof (it);if(_8=="undefined"){return "undefined";}else{if((_8=="number")||(_8=="boolean")){return it+"";}else{if(it===null){return "null";}}}if(dojo.isString(it)){return dojo._escapeString(it);}if(it.nodeType&&it.cloneNode){return "";}var _9=arguments.callee;var _a;if(typeof it.__json__=="function"){_a=it.__json__();if(it!==_a){return _9(_a,_4,_6);}}if(typeof it.json=="function"){_a=it.json();if(it!==_a){return _9(_a,_4,_6);}}if(dojo.isArray(it)){var _b=[];for(var i=0;i<it.length;i++){var _d=_9(it[i],_4,_6);if(typeof (_d)!="string"){_d="undefined";}_b.push(_7+_6+_d);}return "["+_b.join(", ")+_7+_5+"]";}if(_8=="function"){return null;}var _e=[];for(var _f in it){var _10;if(typeof (_f)=="number"){_10="\""+_f+"\"";}else{if(typeof (_f)=="string"){_10=dojo._escapeString(_f);}else{continue;}}_d=_9(it[_f],_4,_6);if(typeof (_d)!="string"){continue;}_e.push(_7+_6+_10+": "+_d);}return "{"+_e.join(", ")+_7+_5+"}";};}