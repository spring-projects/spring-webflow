/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._editor.plugins.TextColor"]){dojo._hasResource["dijit._editor.plugins.TextColor"]=true;dojo.provide("dijit._editor.plugins.TextColor");dojo.require("dijit._editor._Plugin");dojo.require("dijit.ColorPalette");dojo.declare("dijit._editor.plugins.TextColor",dijit._editor._Plugin,{buttonClass:dijit.form.DropDownButton,constructor:function(){this.dropDown=new dijit.ColorPalette();dojo.connect(this.dropDown,"onChange",this,function(_1){this.editor.execCommand(this.command,_1);});}});}