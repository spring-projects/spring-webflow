/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.layout.LayoutContainer"]){dojo._hasResource["dijit.layout.LayoutContainer"]=true;dojo.provide("dijit.layout.LayoutContainer");dojo.require("dijit.layout._LayoutWidget");dojo.declare("dijit.layout.LayoutContainer",dijit.layout._LayoutWidget,{constructor:function(){dojo.deprecated("dijit.layout.LayoutContainer is deprecated","use BorderContainer instead",2);},layout:function(){dijit.layout.layoutChildren(this.domNode,this._contentBox,this.getChildren());},addChild:function(_1,_2){dijit._Container.prototype.addChild.apply(this,arguments);if(this._started){dijit.layout.layoutChildren(this.domNode,this._contentBox,this.getChildren());}},removeChild:function(_3){dijit._Container.prototype.removeChild.apply(this,arguments);if(this._started){dijit.layout.layoutChildren(this.domNode,this._contentBox,this.getChildren());}}});dojo.extend(dijit._Widget,{layoutAlign:"none"});}