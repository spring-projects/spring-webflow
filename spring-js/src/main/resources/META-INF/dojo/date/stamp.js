/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.date.stamp"]){dojo._hasResource["dojo.date.stamp"]=true;dojo.provide("dojo.date.stamp");dojo.date.stamp.fromISOString=function(_1,_2){if(!dojo.date.stamp._isoRegExp){dojo.date.stamp._isoRegExp=/^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;}var _3=dojo.date.stamp._isoRegExp.exec(_1);var _4=null;if(_3){_3.shift();_3[1]&&_3[1]--;_3[6]&&(_3[6]*=1000);if(_2){_2=new Date(_2);dojo.map(["FullYear","Month","Date","Hours","Minutes","Seconds","Milliseconds"],function(_5){return _2["get"+_5]();}).forEach(function(_6,_7){if(_3[_7]===undefined){_3[_7]=_6;}});}_4=new Date(_3[0]||1970,_3[1]||0,_3[2]||0,_3[3]||0,_3[4]||0,_3[5]||0,_3[6]||0);var _8=0;var _9=_3[7]&&_3[7].charAt(0);if(_9!="Z"){_8=((_3[8]||0)*60)+(Number(_3[9])||0);if(_9!="-"){_8*=-1;}}if(_9){_8-=_4.getTimezoneOffset();}if(_8){_4.setTime(_4.getTime()+_8*60000);}}return _4;};dojo.date.stamp.toISOString=function(_a,_b){var _=function(n){return (n<10)?"0"+n:n;};_b=_b||{};var _e=[];var _f=_b.zulu?"getUTC":"get";var _10="";if(_b.selector!="time"){_10=[_a[_f+"FullYear"](),_(_a[_f+"Month"]()+1),_(_a[_f+"Date"]())].join("-");}_e.push(_10);if(_b.selector!="date"){var _11=[_(_a[_f+"Hours"]()),_(_a[_f+"Minutes"]()),_(_a[_f+"Seconds"]())].join(":");var _12=_a[_f+"Milliseconds"]();if(_b.milliseconds){_11+="."+(_12<100?"0":"")+_(_12);}if(_b.zulu){_11+="Z";}else{if(_b.selector!="time"){var _13=_a.getTimezoneOffset();var _14=Math.abs(_13);_11+=(_13>0?"-":"+")+_(Math.floor(_14/60))+":"+_(_14%60);}}_e.push(_11);}return _e.join("T");};}