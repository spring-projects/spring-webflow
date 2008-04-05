/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(dojo.config["baseUrl"]){dojo.baseUrl=dojo.config["baseUrl"];}else{dojo.baseUrl="./";}dojo._name="spidermonkey";dojo.isSpidermonkey=true;dojo.exit=function(_1){quit(_1);};if(typeof print=="function"){console.debug=print;}if(typeof line2pc=="undefined"){throw new Error("attempt to use SpiderMonkey host environment when no 'line2pc' global");}dojo._spidermonkeyCurrentFile=function(_2){var s="";try{throw Error("whatever");}catch(e){s=e.stack;}var _4=s.match(/[^@]*\.js/gi);if(!_4){throw Error("could not parse stack string: '"+s+"'");}var _5=(typeof _2!="undefined"&&_2)?_4[_2+1]:_4[_4.length-1];if(!_5){throw Error("could not find file name in stack string '"+s+"'");}return _5;};dojo._loadUri=function(_6){var ok=load(_6);return 1;};if(dojo.config["modulePaths"]){for(var param in dojo.config["modulePaths"]){dojo.registerModulePath(param,dojo.config["modulePaths"][param]);}}