/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.dnd.Avatar"]){dojo._hasResource["dojo.dnd.Avatar"]=true;dojo.provide("dojo.dnd.Avatar");dojo.require("dojo.dnd.common");dojo.declare("dojo.dnd.Avatar",null,{constructor:function(_1){this.manager=_1;this.construct();},construct:function(){var a=dojo.doc.createElement("table");a.className="dojoDndAvatar";a.style.position="absolute";a.style.zIndex=1999;a.style.margin="0px";var b=dojo.doc.createElement("tbody");var tr=dojo.doc.createElement("tr");tr.className="dojoDndAvatarHeader";var td=dojo.doc.createElement("td");td.innerHTML=this._generateText();tr.appendChild(td);dojo.style(tr,"opacity",0.9);b.appendChild(tr);var k=Math.min(5,this.manager.nodes.length);var _7=this.manager.source;for(var i=0;i<k;++i){tr=dojo.doc.createElement("tr");tr.className="dojoDndAvatarItem";td=dojo.doc.createElement("td");if(_7.creator){node=_7._normalizedCreator(_7.getItem(this.manager.nodes[i].id).data,"avatar").node;}else{node=this.manager.nodes[i].cloneNode(true);if(node.tagName.toLowerCase()=="tr"){var _9=dojo.doc.createElement("table"),_a=dojo.doc.createElement("tbody");_a.appendChild(node);_9.appendChild(_a);node=_9;}}node.id="";td.appendChild(node);tr.appendChild(td);dojo.style(tr,"opacity",(9-i)/10);b.appendChild(tr);}a.appendChild(b);this.node=a;},destroy:function(){dojo._destroyElement(this.node);this.node=false;},update:function(){dojo[(this.manager.canDropFlag?"add":"remove")+"Class"](this.node,"dojoDndAvatarCanDrop");dojo.query("tr.dojoDndAvatarHeader td").forEach(function(_b){_b.innerHTML=this._generateText();},this);},_generateText:function(){return this.manager.nodes.length.toString();}});}