/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.NodeList-fx"]){dojo._hasResource["dojo.NodeList-fx"]=true;dojo.provide("dojo.NodeList-fx");dojo.require("dojo.fx");dojo.extend(dojo.NodeList,{_anim:function(_1,_2,_3){var _4=[];_3=_3||{};this.forEach(function(_5){var _6={node:_5};dojo.mixin(_6,_3);_4.push(_1[_2](_6));});return dojo.fx.combine(_4);},wipeIn:function(_7){return this._anim(dojo.fx,"wipeIn",_7);},wipeOut:function(_8){return this._anim(dojo.fx,"wipeOut",_8);},slideTo:function(_9){return this._anim(dojo.fx,"slideTo",_9);},fadeIn:function(_a){return this._anim(dojo,"fadeIn",_a);},fadeOut:function(_b){return this._anim(dojo,"fadeOut",_b);},animateProperty:function(_c){return this._anim(dojo,"animateProperty",_c);}});}