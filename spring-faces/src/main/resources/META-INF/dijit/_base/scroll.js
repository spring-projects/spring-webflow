/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._base.scroll"]){dojo._hasResource["dijit._base.scroll"]=true;dojo.provide("dijit._base.scroll");dijit.scrollIntoView=function(_1){if(dojo.isMozilla){_1.scrollIntoView(false);}else{var _2=_1.parentNode;var _3=_2.scrollTop+dojo.marginBox(_2).h;var _4=_1.offsetTop+dojo.marginBox(_1).h;if(_3<_4){_2.scrollTop+=(_4-_3);}else{if(_2.scrollTop>_1.offsetTop){_2.scrollTop-=(_2.scrollTop-_1.offsetTop);}}}};}