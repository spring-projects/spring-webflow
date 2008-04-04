/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._base.wai"]){dojo._hasResource["dijit._base.wai"]=true;dojo.provide("dijit._base.wai");dijit.wai={onload:function(){var _1=dojo.doc.createElement("div");_1.id="a11yTestNode";_1.style.cssText="border: 1px solid;"+"border-color:red green;"+"position: absolute;"+"height: 5px;"+"top: -999px;"+"background-image: url(\""+dojo.moduleUrl("dojo","resources/blank.gif")+"\");";dojo.body().appendChild(_1);var cs=dojo.getComputedStyle(_1);if(cs){var _3=cs.backgroundImage;var _4=(cs.borderTopColor==cs.borderRightColor)||(_3!=null&&(_3=="none"||_3=="url(invalid-url:)"));dojo[_4?"addClass":"removeClass"](dojo.body(),"dijit_a11y");dojo.body().removeChild(_1);}}};if(dojo.isIE||dojo.isMoz){dojo._loaders.unshift(dijit.wai.onload);}dojo.mixin(dijit,{hasWaiRole:function(_5){return _5.hasAttribute?_5.hasAttribute("role"):!!_5.getAttribute("role");},getWaiRole:function(_6){var _7=_6.getAttribute("role");if(_7){var _8=_7.indexOf(":");return _8==-1?_7:_7.substring(_8+1);}else{return "";}},setWaiRole:function(_9,_a){_9.setAttribute("role",(dojo.isFF&&dojo.isFF<3)?"wairole:"+_a:_a);},removeWaiRole:function(_b){_b.removeAttribute("role");},hasWaiState:function(_c,_d){if(dojo.isFF&&dojo.isFF<3){return _c.hasAttributeNS("http://www.w3.org/2005/07/aaa",_d);}else{return _c.hasAttribute?_c.hasAttribute("aria-"+_d):!!_c.getAttribute("aria-"+_d);}},getWaiState:function(_e,_f){if(dojo.isFF&&dojo.isFF<3){return _e.getAttributeNS("http://www.w3.org/2005/07/aaa",_f);}else{var _10=_e.getAttribute("aria-"+_f);return _10?_10:"";}},setWaiState:function(_11,_12,_13){if(dojo.isFF&&dojo.isFF<3){_11.setAttributeNS("http://www.w3.org/2005/07/aaa","aaa:"+_12,_13);}else{_11.setAttribute("aria-"+_12,_13);}},removeWaiState:function(_14,_15){if(dojo.isFF&&dojo.isFF<3){_14.removeAttributeNS("http://www.w3.org/2005/07/aaa",_15);}else{_14.removeAttribute("aria-"+_15);}}});}