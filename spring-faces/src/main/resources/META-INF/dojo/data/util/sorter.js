/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.data.util.sorter"]){dojo._hasResource["dojo.data.util.sorter"]=true;dojo.provide("dojo.data.util.sorter");dojo.data.util.sorter.basicComparator=function(a,b){var _3=0;if(a>b||typeof a==="undefined"||a===null){_3=1;}else{if(a<b||typeof b==="undefined"||b===null){_3=-1;}}return _3;};dojo.data.util.sorter.createSortFunction=function(_4,_5){var _6=[];function createSortFunction(_7,_8){return function(_9,_a){var a=_5.getValue(_9,_7);var b=_5.getValue(_a,_7);var _d=null;if(_5.comparatorMap){if(typeof _7!=="string"){_7=_5.getIdentity(_7);}_d=_5.comparatorMap[_7]||dojo.data.util.sorter.basicComparator;}_d=_d||dojo.data.util.sorter.basicComparator;return _8*_d(a,b);};};for(var i=0;i<_4.length;i++){sortAttribute=_4[i];if(sortAttribute.attribute){var _f=(sortAttribute.descending)?-1:1;_6.push(createSortFunction(sortAttribute.attribute,_f));}}return function(_10,_11){var i=0;while(i<_6.length){var ret=_6[i++](_10,_11);if(ret!==0){return ret;}}return 0;};};}