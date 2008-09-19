/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._editor.plugins.FontChoice"]){dojo._hasResource["dijit._editor.plugins.FontChoice"]=true;dojo.provide("dijit._editor.plugins.FontChoice");dojo.require("dijit._editor._Plugin");dojo.require("dijit.form.FilteringSelect");dojo.require("dojo.data.ItemFileReadStore");dojo.require("dojo.i18n");dojo.requireLocalization("dijit._editor","FontChoice",null,"ROOT");dojo.declare("dijit._editor.plugins.FontChoice",dijit._editor._Plugin,{_uniqueId:0,buttonClass:dijit.form.FilteringSelect,_initButton:function(){this.inherited("_initButton",arguments);var _1={fontName:["serif","sans-serif","monospaced","cursive","fantasy"],fontSize:[1,2,3,4,5,6,7],formatBlock:["p","h1","h2","h3","pre"]}[this.command];var _2=dojo.i18n.getLocalization("dijit._editor","FontChoice");var _3=dojo.map(_1,function(x){return {name:_2[x],value:x};});_3.push({name:"",value:""});this.button.store=new dojo.data.ItemFileReadStore({data:{identifier:"value",items:_3}});this.button.setValue("");dojo.connect(this.button,"onChange",this,function(_5){this.editor.execCommand(this.command,_5);});},updateState:function(){this.inherited("updateState",arguments);var _e=this.editor;var _c=this.command;if(!_e||!_e.isLoaded||!_c.length){return;}if(this.button){var _8=_e.queryCommandValue(_c);this.button.setValue(_8);}},setToolbar:function(){this.inherited("setToolbar",arguments);var _9=this.button;if(!_9.id){_9.id="dijitEditorButton-"+this.command+(this._uniqueId++);}var _a=dojo.doc.createElement("label");_a.setAttribute("for",_9.id);var _b=dojo.i18n.getLocalization("dijit._editor","FontChoice");_a.appendChild(dojo.doc.createTextNode(_b[this.command]));dojo.place(_a,this.button.domNode,"before");}});}