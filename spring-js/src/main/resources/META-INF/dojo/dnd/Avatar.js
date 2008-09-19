/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.dnd.Avatar"]){dojo._hasResource["dojo.dnd.Avatar"]=true;dojo.provide("dojo.dnd.Avatar");dojo.require("dojo.dnd.common");dojo.dnd.Avatar=function(_1){this.manager=_1;this.construct();};dojo.extend(dojo.dnd.Avatar,{construct:function(){var a=dojo.doc.createElement("table");a.className="dojoDndAvatar";a.style.position="absolute";a.style.zIndex=1999;a.style.margin="0px";var b=dojo.doc.createElement("tbody");var tr=dojo.doc.createElement("tr");tr.className="dojoDndAvatarHeader";var td=dojo.doc.createElement("td");td.innerHTML=this._generateText();tr.appendChild(td);dojo.style(tr,"opacity",0.9);b.appendChild(tr);var k=Math.min(5,this.manager.nodes.length);var _7=this.manager.source;for(var i=0;i<k;++i){tr=dojo.doc.createElement("tr");tr.className="dojoDndAvatarItem";td=dojo.doc.createElement("td");var _9=_7.creator?_9=_7._normalizedCreator(_7.getItem(this.manager.nodes[i].id).data,"avatar").node:_9=this.manager.nodes[i].cloneNode(true);_9.id="";td.appendChild(_9);tr.appendChild(td);dojo.style(tr,"opacity",(9-i)/10);b.appendChild(tr);}a.appendChild(b);this.node=a;},destroy:function(){dojo._destroyElement(this.node);this.node=false;},update:function(){dojo[(this.manager.canDropFlag?"add":"remove")+"Class"](this.node,"dojoDndAvatarCanDrop");var t=this.node.getElementsByTagName("td");for(var i=0;i<t.length;++i){var n=t[i];if(dojo.hasClass(n.parentNode,"dojoDndAvatarHeader")){n.innerHTML=this._generateText();break;}}},_generateText:function(){return this.manager.nodes.length.toString();}});}