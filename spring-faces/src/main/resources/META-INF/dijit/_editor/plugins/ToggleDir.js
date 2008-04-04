/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._editor.plugins.ToggleDir"]){dojo._hasResource["dijit._editor.plugins.ToggleDir"]=true;dojo.provide("dijit._editor.plugins.ToggleDir");dojo.experimental("dijit._editor.plugins.ToggleDir");dojo.require("dijit._editor._Plugin");dojo.declare("dijit._editor.plugins.ToggleDir",dijit._editor._Plugin,{useDefaultCommand:false,command:"toggleDir",_initButton:function(){this.inherited("_initButton",arguments);this.connect(this.button,"onClick",this._toggleDir);},updateState:function(){},_toggleDir:function(){var _1=this.editor.editorObject.contentWindow.document.documentElement;var _2=dojo.getComputedStyle(_1).direction=="ltr";_1.dir=_2?"rtl":"ltr";}});dojo.subscribe(dijit._scopeName+".Editor.getPlugin",null,function(o){if(o.plugin){return;}switch(o.args.name){case "toggleDir":o.plugin=new dijit._editor.plugins.ToggleDir({command:o.args.name});}});}