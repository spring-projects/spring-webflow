if(!dojo._hasResource["dijit.InlineEditBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.InlineEditBox"] = true;
dojo.provide("dijit.InlineEditBox");

dojo.require("dojo.i18n");

dojo.require("dijit._Widget");
dojo.require("dijit._Container");
dojo.require("dijit.form.Button");
dojo.require("dijit.form.TextBox");

dojo.requireLocalization("dijit", "common", null, "ko,zh,ja,zh-tw,ru,it,hu,fr,pt,ROOT,pl,es,de,cs");

dojo.declare(
	"dijit.InlineEditBox",
	dijit._Widget,
{
	// summary
	//		Behavior for an existing node (<p>, <div>, <span>, etc.) so that
	// 		when you click it, an editor shows up in place of the original
	//		text.  Optionally, Save and Cancel button are displayed below the edit widget.
	//		When Save is clicked, the text is pulled from the edit
	//		widget and redisplayed and the edit widget is again hidden.
	//		By default a plain Textarea widget is used as the editor (or for
	//		inline values a TextBox), but you can specify an editor such as
	//		dijit.Editor (for editing HTML) or a Slider (for adjusting a number).
	//		An edit widget must support the following API to be used:
	//		String getDisplayedValue() OR String getValue()
	//		void setDisplayedValue(String) OR void setValue(String)
	//		void focus()

	// editing: Boolean
	//		Is the node currently in edit mode?
	editing: false,

	// autoSave: Boolean
	//		Changing the value automatically saves it; don't have to push save button
	//		(and save button isn't even displayed)
	autoSave: true,

	// buttonSave: String
	//		Save button label
	buttonSave: "",

	// buttonCancel: String
	//		Cancel button label
	buttonCancel: "",

	// renderAsHtml: Boolean
	//		Set this to true if the specified Editor's value should be interpreted as HTML
	//		rather than plain text (ie, dijit.Editor)
	renderAsHtml: false,

	// editor: String
	//		Class name for Editor widget
	editor: "dijit.form.TextBox",

	// editorParams: Object
	//		Set of parameters for editor, like {required: true}
	editorParams: {},

	onChange: function(value){
		// summary: User should set this handler to be notified of changes to value
	},

	// width: String
	//		Width of editor.  By default it's width=100% (ie, block mode)
	width: "100%",

	// value: String
	//		The display value of the widget in read-only mode
	value: "",

	// noValueIndicator: String
	//		The text that gets displayed when there is no value (so that the user has a place to click to edit)
	noValueIndicator: "<span style='font-family: wingdings; text-decoration: underline;'>&nbsp;&nbsp;&nbsp;&nbsp;&#x270d;&nbsp;&nbsp;&nbsp;&nbsp;</span>",

	postMixInProperties: function(){
		this.inherited('postMixInProperties', arguments);

		// save pointer to original source node, since Widget nulls-out srcNodeRef
		this.displayNode = this.srcNodeRef;

		// connect handlers to the display node
		var events = {
			ondijitclick: "_onClick",
			onmouseover: "_onMouseOver",
			onmouseout: "_onMouseOut",
			onfocus: "_onMouseOver",
			onblur: "_onMouseOut"			
		};
		for(var name in events){
			this.connect(this.displayNode, name, events[name]);
		}
		dijit.setWaiRole(this.displayNode, "button");
		if(!this.displayNode.getAttribute("tabIndex")){
			this.displayNode.setAttribute("tabIndex", 0);
		}

		if(!this.value){
			this.value = this.displayNode.innerHTML;
		}
		this._setDisplayValue(this.value);	// if blank, change to icon for "input needed"
	},

	_onMouseOver: function(){
		dojo.addClass(this.displayNode, this.disabled ? "dijitDisabledClickableRegion" : "dijitClickableRegion");
	},

	_onMouseOut: function(){
		dojo.removeClass(this.displayNode, this.disabled ? "dijitDisabledClickableRegion" : "dijitClickableRegion");
	},

	_onClick: function(/*Event*/ e){
		if(this.disabled){ return; }
		if(e){ dojo.stopEvent(e); }
		this._onMouseOut();

		// Since FF gets upset if you move a node while in an event handler for that node...
		setTimeout(dojo.hitch(this, "_edit"), 0);
	},

	_edit: function(){
		// summary: display the editor widget in place of the original (read only) markup

		this.editing = true;

		var editValue = 
				(this.renderAsHtml ?
				this.value :
				this.value.replace(/\s*\r?\n\s*/g,"").replace(/<br\/?>/gi, "\n").replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&amp;/g,"&"));

		// Placeholder for edit widget
		// Put place holder (and eventually editWidget) before the display node so that it's positioned correctly
		// when Calendar dropdown appears, which happens automatically on focus.
		var placeholder = document.createElement("span");
		dojo.place(placeholder, this.domNode, "before");

		var ew = this.editWidget = new dijit._InlineEditor({
			value: dojo.trim(editValue),
			autoSave: this.autoSave,
			buttonSave: this.buttonSave,
			buttonCancel: this.buttonCancel,
			renderAsHtml: this.renderAsHtml,
			editor: this.editor,
			editorParams: this.editorParams,
			style: dojo.getComputedStyle(this.displayNode),
			save: dojo.hitch(this, "save"),
			cancel: dojo.hitch(this, "cancel"),
			width: this.width
		}, placeholder);

		// to avoid screen jitter, we first create the editor with position:absolute, visibility:hidden,
		// and then when it's finished rendering, we switch from display mode to editor
		var ews = ew.domNode.style;
		this.displayNode.style.display="none";
		ews.position = "static";
		ews.visibility = "visible";

		// Replace the display widget with edit widget, leaving them both displayed for a brief time so that
		// focus can be shifted without incident.  (browser may needs some time to render the editor.)
		this.domNode = ew.domNode;
		setTimeout(function(){
			ew.focus();
		}, 100);
	},

	_showText: function(/*Boolean*/ focus){
		// summary: revert to display mode, and optionally focus on display node

		// display the read-only text and then quickly hide the editor (to avoid screen jitter)
		this.displayNode.style.display="";
		var ews = this.editWidget.domNode.style;
		ews.position="absolute";
		ews.visibility="hidden";

		this.domNode = this.displayNode;

		// give the browser some time to render the display node and then shift focus to it
		// and hide the edit widget
		var _this = this;
		setTimeout(function(){
			if(focus){
				dijit.focus(_this.displayNode);
			}
			_this.editWidget.destroy();
			delete _this.editWidget;
		}, 100);
	},

	save: function(/*Boolean*/ focus){
		// summary
		//		Save the contents of the editor and revert to display mode.
		// focus: Boolean
		//		Focus on the display mode text
		this.editing = false;

		this.value = this.editWidget.getValue() + "";
		if(this.renderAsHtml){
			this.value = this.value.replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;").replace(/"/gm, "&quot;")
				.replace("\n", "<br>");
		}
		this._setDisplayValue(this.value);

		// tell the world that we have changed
		this.onChange(this.value);

		this._showText(focus);	
	},

	_setDisplayValue: function(/*String*/ val){
		// summary: inserts specified HTML value into this node, or an "input needed" character if node is blank
		this.displayNode.innerHTML = val || this.noValueIndicator;
	},

	cancel: function(/*Boolean*/ focus){
		// summary:
		//		Revert to display mode, discarding any changes made in the editor
		this.editing = false;
		this._showText(focus);
	}
});

dojo.declare(
	"dijit._InlineEditor",
	 [dijit._Widget, dijit._Templated],
{
	// summary:
	// 		internal widget used by InlineEditBox, displayed when in editing mode
	//		to display the editor and maybe save/cancel buttons.  Calling code should
	//		connect to save/cancel methods to detect when editing is finished
	//
	//		Has mainly the same parameters as InlineEditBox, plus these values:
	//
	// style: Object
	//		Set of CSS attributes of display node, to replicate in editor
	//
	// value: String
	//		Value as an HTML string or plain text string, depending on renderAsHTML flag

	templateString:"<fieldset dojoAttachPoint=\"editNode\" waiRole=\"presentation\" style=\"position: absolute; visibility:hidden\" class=\"dijitReset dijitInline\"\n\tdojoAttachEvent=\"onkeypress: _onKeyPress\" \n\t><input dojoAttachPoint=\"editorPlaceholder\"\n\t/><span dojoAttachPoint=\"buttonContainer\"\n\t\t><button class='saveButton' dojoAttachPoint=\"saveButton\" dojoType=\"dijit.form.Button\" dojoAttachEvent=\"onClick:save\">${buttonSave}</button\n\t\t><button class='cancelButton' dojoAttachPoint=\"cancelButton\" dojoType=\"dijit.form.Button\" dojoAttachEvent=\"onClick:cancel\">${buttonCancel}</button\n\t></span\n></fieldset>\n",
	widgetsInTemplate: true,

	postMixInProperties: function(){
		this.inherited('postMixInProperties', arguments);
		this.messages = dojo.i18n.getLocalization("dijit", "common", this.lang);
		dojo.forEach(["buttonSave", "buttonCancel"], function(prop){
			if(!this[prop]){ this[prop] = this.messages[prop]; }
		}, this);
	},

	postCreate: function(){
		// Create edit widget in place in the template
		var cls = dojo.getObject(this.editor);
		var ew = this.editWidget = new cls(this.editorParams, this.editorPlaceholder);

		// Copy the style from the source
		// Don't copy ALL properties though, just the necessary/applicable ones
		var srcStyle = this.style;
		dojo.forEach(["fontWeight","fontFamily","fontSize","fontStyle"], function(prop){
			ew.focusNode.style[prop]=srcStyle[prop];
		}, this);
		dojo.forEach(["marginTop","marginBottom","marginLeft", "marginRight"], function(prop){
			this.domNode.style[prop]=srcStyle[prop];
		}, this);
		if(this.width=="100%"){
			// block mode
			ew.domNode.style.width = "100%";	// because display: block doesn't work for table widgets
			this.domNode.style.display="block";
		}else{
			// inline-block mode
			ew.domNode.style.width = this.width + (Number(this.width)==this.width ? "px" : "");			
		}

		(this.editWidget.setDisplayedValue||this.editWidget.setValue).call(this.editWidget, this.value);
		this._initialText = this.getValue();

		if(this.autoSave){
			this.buttonContainer.style.display="none";
		}

		this.connect(this.editWidget, "onChange", "_onChange");
	},

	destroy: function(){
		this.editWidget.destroy();
		this.inherited("destroy", arguments);
	},

	getValue: function(){
		var ew = this.editWidget;
		return ew.getDisplayedValue ? ew.getDisplayedValue() : ew.getValue();
	},

	_onKeyPress: function(e){
		// summary:
		//		Callback when keypress in the edit box (see template).
		//		For autoSave widgets, if Esc/Enter, call cancel/save.
		//		For non-autoSave widgets, enable save button if the text value is
		//		different than the original value.
		if(this._exitInProgress){
			return;
		}
		if(this.autoSave){
			// If Enter/Esc pressed, treat as save/cancel.
			if(e.keyCode == dojo.keys.ESCAPE){
				dojo.stopEvent(e);
				this._exitInProgress = true;
				this.cancel(true);
			}else if(e.keyCode == dojo.keys.ENTER){
				dojo.stopEvent(e);
				this._exitInProgress = true;
				this.save(true);
			}
		}else{
			var _this = this;
			// Delay before calling getValue().
			// The delay gives the browser a chance to update the Textarea.
			setTimeout(
				function(){
					_this.saveButton.setDisabled(_this.getValue() == _this._initialText);
				}, 100);
		}
	},

	_onBlur: function(){
		// summary:
		//	Called when focus moves outside the editor
		if(this._exitInProgress){
			// when user clicks the "save" button, focus is shifted back to display text, causing this
			// function to be called, but in that case don't do anything
			return;
		}
		if(this.autoSave){
			this._exitInProgress = true;
			if(this.getValue() == this._initialText){
				this.cancel(false);
			}else{
				this.save(false);
			}
		}
	},

	enableSave: function(){
		// summary: User replacable function returning a Boolean to indicate
		// if the Save button should be enabled or not - usually due to invalid conditions
		return this.editWidget.isValid ? this.editWidget.isValid() : true;
	},

	_onChange: function(){
		// summary:
		//	This is called when the underlying widget fires an onChange event,
		//	which means that the user has finished entering the value
		if(this._exitInProgress){
			// TODO: the onChange event might happen after the return key for an async widget
			// like FilteringSelect.  Shouldn't be deleting the edit widget on end-of-edit
			return;
		}
		if(this.autoSave){
			this._exitInProgress = true;
			this.save(true);
		}else{
			// in case the keypress event didn't get through (old problem with Textarea that has been fixed
			// in theory) or if the keypress event comes too quickly and the value inside the Textarea hasn't
			// been updated yet)
			this.saveButton.setDisabled((this.getValue() == this._initialText) || !this.enableSave());
		}
	},
	
	enableSave: function(){
		// summary: User replacable function returning a Boolean to indicate
		// if the Save button should be enabled or not - usually due to invalid conditions
		return this.editWidget.isValid ? this.editWidget.isValid() : true;
	},

	focus: function(){
		this.editWidget.focus();
		dijit.selectInputText(this.editWidget.focusNode);
	}
});

dijit.selectInputText = function(/*DomNode*/element){
	// summary: select all the text in an input element (TODO: use functions in _editor/selection.js?)
	var _window = dojo.global;
	var _document = dojo.doc;
	element = dojo.byId(element);
	if(_document["selection"] && dojo.body()["createTextRange"]){ // IE
		if(element.createTextRange){
			var range = element.createTextRange();
			range.moveStart("character", 0);
			range.moveEnd("character", element.value.length);
			range.select();
		}
	}else if(_window["getSelection"]){
		var selection = _window.getSelection();
		// FIXME: does this work on Safari?
		if(element.setSelectionRange){
			element.setSelectionRange(0, element.value.length);
		}
	}
	element.focus();
};


}
