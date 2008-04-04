/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.dnd.Mover"]){dojo._hasResource["dojo.dnd.Mover"]=true;dojo.provide("dojo.dnd.Mover");dojo.require("dojo.dnd.common");dojo.require("dojo.dnd.autoscroll");dojo.declare("dojo.dnd.Mover",null,{constructor:function(_1,e,_3){this.node=dojo.byId(_1);this.marginBox={l:e.pageX,t:e.pageY};this.mouseButton=e.button;var h=this.host=_3,d=_1.ownerDocument,_6=dojo.connect(d,"onmousemove",this,"onFirstMove");this.events=[dojo.connect(d,"onmousemove",this,"onMouseMove"),dojo.connect(d,"onmouseup",this,"onMouseUp"),dojo.connect(d,"ondragstart",dojo,"stopEvent"),dojo.connect(d,"onselectstart",dojo,"stopEvent"),_6];if(h&&h.onMoveStart){h.onMoveStart(this);}},onMouseMove:function(e){dojo.dnd.autoScroll(e);var m=this.marginBox;this.host.onMove(this,{l:m.l+e.pageX,t:m.t+e.pageY});},onMouseUp:function(e){if(this.mouseButton==e.button){this.destroy();}},onFirstMove:function(){var s=this.node.style,l,t;switch(s.position){case "relative":case "absolute":l=Math.round(parseFloat(s.left));t=Math.round(parseFloat(s.top));break;default:s.position="absolute";var m=dojo.marginBox(this.node);l=m.l;t=m.t;break;}this.marginBox.l=l-this.marginBox.l;this.marginBox.t=t-this.marginBox.t;this.host.onFirstMove(this);dojo.disconnect(this.events.pop());},destroy:function(){dojo.forEach(this.events,dojo.disconnect);var h=this.host;if(h&&h.onMoveStop){h.onMoveStop(this);}this.events=this.node=null;}});}