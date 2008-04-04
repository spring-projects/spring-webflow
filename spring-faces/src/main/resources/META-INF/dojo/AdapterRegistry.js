/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.AdapterRegistry"]){dojo._hasResource["dojo.AdapterRegistry"]=true;dojo.provide("dojo.AdapterRegistry");dojo.AdapterRegistry=function(_1){this.pairs=[];this.returnWrappers=_1||false;};dojo.extend(dojo.AdapterRegistry,{register:function(_2,_3,_4,_5,_6){this.pairs[((_6)?"unshift":"push")]([_2,_3,_4,_5]);},match:function(){for(var i=0;i<this.pairs.length;i++){var _8=this.pairs[i];if(_8[1].apply(this,arguments)){if((_8[3])||(this.returnWrappers)){return _8[2];}else{return _8[2].apply(this,arguments);}}}throw new Error("No match found");},unregister:function(_9){for(var i=0;i<this.pairs.length;i++){var _b=this.pairs[i];if(_b[0]==_9){this.pairs.splice(i,1);return true;}}return false;}});}