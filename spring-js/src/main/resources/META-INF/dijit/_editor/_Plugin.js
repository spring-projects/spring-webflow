/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._editor._Plugin"]){dojo._hasResource["dijit._editor._Plugin"]=true;dojo.provide("dijit._editor._Plugin");dojo.require("dijit._Widget");dojo.require("dijit.Editor");dojo.require("dijit.form.Button");dojo.declare("dijit._editor._Plugin",null,{constructor:function(_1,_2){if(_1){dojo.mixin(this,_1);}},editor:null,iconClassPrefix:"dijitEditorIcon",button:null,queryCommand:null,command:"",commandArg:null,useDefaultCommand:true,buttonClass:dijit.form.Button,updateInterval:200,_initButton:function(){if(this.command.length){var _3=this.editor.commands[this.command];var _4="dijitEditorIcon "+this.iconClassPrefix+this.command.charAt(0).toUpperCase()+this.command.substr(1);if(!this.button){var _5={label:_3,showLabel:false,iconClass:_4,dropDown:this.dropDown};this.button=new this.buttonClass(_5);}}},updateState:function(){var _e=this.editor;var _c=this.command;if(!_e){return;}if(!_e.isLoaded){return;}if(!_c.length){return;}if(this.button){try{var _8=_e.queryCommandEnabled(_c);this.button.setDisabled(!_8);if(this.button.setChecked){this.button.setChecked(_e.queryCommandState(_c));}}catch(e){console.debug(e);}}},setEditor:function(_9){this.editor=_9;this._initButton();if((this.command.length)&&(!this.editor.queryCommandAvailable(this.command))){if(this.button){this.button.domNode.style.display="none";}}if(this.button&&this.useDefaultCommand){dojo.connect(this.button,"onClick",dojo.hitch(this.editor,"execCommand",this.command,this.commandArg));}dojo.connect(this.editor,"onNormalizedDisplayChanged",this,"updateState");},setToolbar:function(_a){if(this.button){_a.addChild(this.button);}}});}