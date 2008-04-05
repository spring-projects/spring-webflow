/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.NodeList-fx"]){dojo._hasResource["dojo.NodeList-fx"]=true;dojo.provide("dojo.NodeList-fx");dojo.require("dojo.fx");dojo.extend(dojo.NodeList,{_anim:function(_1,_2,_3){_3=_3||{};return dojo.fx.combine(this.map(function(_4){var _5={node:_4};dojo.mixin(_5,_3);return _1[_2](_5);}));},wipeIn:function(_6){return this._anim(dojo.fx,"wipeIn",_6);},wipeOut:function(_7){return this._anim(dojo.fx,"wipeOut",_7);},slideTo:function(_8){return this._anim(dojo.fx,"slideTo",_8);},fadeIn:function(_9){return this._anim(dojo,"fadeIn",_9);},fadeOut:function(_a){return this._anim(dojo,"fadeOut",_a);},animateProperty:function(_b){return this._anim(dojo,"animateProperty",_b);},anim:function(_c,_d,_e,_f,_10){var _11=dojo.fx.combine(this.map(function(_12){return dojo.animateProperty({node:_12,properties:_c,duration:_d||350,easing:_e});}));if(_f){dojo.connect(_11,"onEnd",_f);}return _11.play(_10||0);}});}