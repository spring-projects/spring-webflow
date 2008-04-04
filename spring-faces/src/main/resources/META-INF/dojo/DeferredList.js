/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.DeferredList"]){dojo._hasResource["dojo.DeferredList"]=true;dojo.provide("dojo.DeferredList");dojo.declare("dojo.DeferredList",dojo.Deferred,{constructor:function(_1,_2,_3,_4,_5){this.list=_1;this.resultList=new Array(this.list.length);this.chain=[];this.id=this._nextId();this.fired=-1;this.paused=0;this.results=[null,null];this.canceller=_5;this.silentlyCancelled=false;if(this.list.length===0&&!_2){this.callback(this.resultList);}this.finishedCount=0;this.fireOnOneCallback=_2;this.fireOnOneErrback=_3;this.consumeErrors=_4;dojo.forEach(this.list,function(d,_7){d.addCallback(this,function(r){this._cbDeferred(_7,true,r);return r;});d.addErrback(this,function(r){this._cbDeferred(_7,false,r);return r;});},this);},_cbDeferred:function(_a,_b,_c){this.resultList[_a]=[_b,_c];this.finishedCount+=1;if(this.fired!==0){if(_b&&this.fireOnOneCallback){this.callback([_a,_c]);}else{if(!_b&&this.fireOnOneErrback){this.errback(_c);}else{if(this.finishedCount==this.list.length){this.callback(this.resultList);}}}}if(!_b&&this.consumeErrors){_c=null;}return _c;},gatherResults:function(_d){var d=new dojo.DeferredList(_d,false,true,false);d.addCallback(function(_f){var ret=[];dojo.forEach(_f,function(_11){ret.push(_11[1]);});return ret;});return d;}});}