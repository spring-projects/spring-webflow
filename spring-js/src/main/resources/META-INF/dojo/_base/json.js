/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo._base.json"]){dojo._hasResource["dojo._base.json"]=true;dojo.provide("dojo._base.json");dojo.fromJson=function(_1){return eval("("+_1+")");};dojo._escapeString=function(_2){return ("\""+_2.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");};dojo.toJsonIndentStr="\t";dojo.toJson=function(it,_4,_5){if(it===undefined){return "undefined";}var _6=typeof it;if(_6=="number"||_6=="boolean"){return it+"";}if(it===null){return "null";}if(dojo.isString(it)){return dojo._escapeString(it);}if(it.nodeType&&it.cloneNode){return "";}var _7=arguments.callee;var _8;_5=_5||"";var _9=_4?_5+dojo.toJsonIndentStr:"";if(typeof it.__json__=="function"){_8=it.__json__();if(it!==_8){return _7(_8,_4,_9);}}if(typeof it.json=="function"){_8=it.json();if(it!==_8){return _7(_8,_4,_9);}}var _a=_4?" ":"";var _b=_4?"\n":"";if(dojo.isArray(it)){var _c=dojo.map(it,function(_d){var _e=_7(_d,_4,_9);if(typeof _e!="string"){_e="undefined";}return _b+_9+_e;});return "["+_c.join(","+_a)+_b+_5+"]";}if(_6=="function"){return null;}var _f=[];for(var key in it){var _11;if(typeof key=="number"){_11="\""+key+"\"";}else{if(typeof key=="string"){_11=dojo._escapeString(key);}else{continue;}}val=_7(it[key],_4,_9);if(typeof val!="string"){continue;}_f.push(_b+_9+_11+":"+_a+val);}return "{"+_f.join(","+_a)+_b+_5+"}";};}