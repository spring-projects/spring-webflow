if(!dojo._hasResource["dijit._editor.plugins.TextColor"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor.plugins.TextColor"] = true;
dojo.provide("dijit._editor.plugins.TextColor");

dojo.require("dijit._editor._Plugin");
dojo.require("dijit.ColorPalette");

dojo.declare("dijit._editor.plugins.TextColor",
	dijit._editor._Plugin,
	{
		buttonClass: dijit.form.DropDownButton,

//TODO: set initial focus/selection state?

		constructor: function(){
			this.dropDown = new dijit.ColorPalette();
			dojo.connect(this.dropDown, "onChange", this, function(color){
				this.editor.execCommand(this.command, color);
			});
		}
	}
);

}
