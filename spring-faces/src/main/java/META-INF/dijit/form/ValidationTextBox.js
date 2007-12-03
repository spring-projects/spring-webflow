if(!dojo._hasResource["dijit.form.ValidationTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.ValidationTextBox"] = true;
dojo.provide("dijit.form.ValidationTextBox");

dojo.require("dojo.i18n");

dojo.require("dijit.form.TextBox");
dojo.require("dijit.Tooltip");

dojo.requireLocalization("dijit.form", "validate", null, "ko,zh-cn,zh,ja,zh-tw,ru,it,hu,ROOT,fr,pt,pl,es,de,cs");

dojo.declare(
	"dijit.form.ValidationTextBox",
	dijit.form.TextBox,
	{
		// summary:
		//		A subclass of TextBox.
		//		Over-ride isValid in subclasses to perform specific kinds of validation.

		templateString:"<table style=\"display: -moz-inline-stack;\" class=\"dijit dijitReset dijitInlineTable\" cellspacing=\"0\" cellpadding=\"0\"\n\tid=\"widget_${id}\" name=\"${name}\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse\" waiRole=\"presentation\"\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset dijitInputField\" width=\"100%\"\n\t\t\t><input dojoAttachPoint='textbox,focusNode' dojoAttachEvent='onfocus,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress' autocomplete=\"off\"\n\t\t\ttype='${type}' name='${name}'\n\t\t/></td\n\t\t><td class=\"dijitReset dijitValidationIconField\" width=\"0%\"\n\t\t\t><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div><div class='dijitValidationIconText'>&Chi;</div\n\t\t></td\n\t></tr\n></table>\n",
		baseClass: "dijitTextBox",

		// default values for new subclass properties
		// required: Boolean
		//		Can be true or false, default is false.
		required: false,
		// promptMessage: String
		//		Hint string
		promptMessage: "",
		// invalidMessage: String
		// 		The message to display if value is invalid.
		invalidMessage: "\x00", // read from the message file if not overridden
		// constraints: Object
		//		user-defined object needed to pass parameters to the validator functions
		constraints: {},
		// regExp: String
		//		regular expression string used to validate the input
		//		Do not specify both regExp and regExpGen
		regExp: ".*",
		// regExpGen: Function
		//		user replaceable function used to generate regExp when dependent on constraints
		//		Do not specify both regExp and regExpGen
		regExpGen: function(constraints){ return this.regExp; },
		
		// state: String
		//		Shows current state (ie, validation result) of input (Normal, Warning, or Error)
		state: "",

		setValue: function(){
			this.inherited('setValue', arguments);
			this.validate(false);
		},

		validator: function(value,constraints){
			// summary: user replaceable function used to validate the text input against the regular expression.
			return (new RegExp("^(" + this.regExpGen(constraints) + ")"+(this.required?"":"?")+"$")).test(value) &&
				(!this.required || !this._isEmpty(value)) &&
				(this._isEmpty(value) || this.parse(value, constraints) !== null);
		},

		isValid: function(/* Boolean*/ isFocused){
			// summary: Need to over-ride with your own validation code in subclasses
			return this.validator(this.textbox.value, this.constraints);
		},

		_isEmpty: function(value){
			// summary: Checks for whitespace
			return /^\s*$/.test(value); // Boolean
		},

		getErrorMessage: function(/* Boolean*/ isFocused){
			// summary: return an error message to show if appropriate
			return this.invalidMessage;
		},

		getPromptMessage: function(/* Boolean*/ isFocused){
			// summary: return a hint to show if appropriate
			return this.promptMessage;
		},

		validate: function(/* Boolean*/ isFocused){
			// summary:
			//		Called by oninit, onblur, and onkeypress.
			// description:
			//		Show missing or invalid messages if appropriate, and highlight textbox field.
			var message = "";
			var isValid = this.isValid(isFocused);
			var isEmpty = this._isEmpty(this.textbox.value);
			this.state = (isValid || (!this._hasBeenBlurred && isEmpty)) ? "" : "Error";
			this._setStateClass();
			dijit.setWaiState(this.focusNode, "invalid", (isValid? "false" : "true"));
			if(isFocused){
				if(isEmpty){
					message = this.getPromptMessage(true);
				}
				if(!message && !isValid){
					message = this.getErrorMessage(true);
				}
			}
			this._displayMessage(message);
		},

		// currently displayed message
		_message: "",

		_displayMessage: function(/*String*/ message){
			if(this._message == message){ return; }
			this._message = message;
			this.displayMessage(message);
		},

		displayMessage: function(/*String*/ message){
			// summary:
			//		User overridable method to display validation errors/hints.
			//		By default uses a tooltip.
			if(message){
				dijit.showTooltip(message, this.domNode);
			}else{
				dijit.hideTooltip(this.domNode);
			}
		},

		_hasBeenBlurred: false,

		_onBlur: function(evt){
			this._hasBeenBlurred = true;
			this.validate(false);
			this.inherited('_onBlur', arguments);
		},

		onfocus: function(evt){
			// TODO: change to _onFocus?
			this.validate(true);
			this._onMouse(evt);	// update CSS classes
		},

		onkeyup: function(evt){
			this.onfocus(evt);
		},

		//////////// INITIALIZATION METHODS ///////////////////////////////////////
		constructor: function(){
			this.constraints = {};
		},

		postMixInProperties: function(){
			this.inherited('postMixInProperties', arguments);
			this.constraints.locale=this.lang;
			this.messages = dojo.i18n.getLocalization("dijit.form", "validate", this.lang);
			if(this.invalidMessage == "\x00"){ this.invalidMessage = this.messages.invalidMessage; }
			var p = this.regExpGen(this.constraints);
			this.regExp = p;
			// make value a string for all types so that form reset works well
		}
	}
);

dojo.declare(
	"dijit.form.MappedTextBox",
	dijit.form.ValidationTextBox,
	{
		// summary:
		//		A subclass of ValidationTextBox.
		//		Provides a hidden input field and a serialize method to override

		serialize: function(val, /*Object?*/options){
			// summary: user replaceable function used to convert the getValue() result to a String
			return (val.toString ? val.toString() : "");
		},

		toString: function(){
			// summary: display the widget as a printable string using the widget's value
			var val = this.filter(this.getValue());
			return (val!=null) ? ((typeof val == "string") ? val : this.serialize(val, this.constraints)) : "";
		},

		validate: function(){
			this.valueNode.value = this.toString();
			this.inherited('validate', arguments);
		},

		postCreate: function(){
			var textbox = this.textbox;
			var valueNode = (this.valueNode = document.createElement("input"));
			valueNode.setAttribute("type", textbox.type);
			valueNode.setAttribute("value", this.toString());
			dojo.style(valueNode, "display", "none");
			valueNode.name = this.textbox.name;
			this.textbox.name = "_" + this.textbox.name + "_displayed_";
			this.textbox.removeAttribute("name");
			dojo.place(valueNode, textbox, "after");

			this.inherited('postCreate', arguments);
		}
	}
);

dojo.declare(
	"dijit.form.RangeBoundTextBox",
	dijit.form.MappedTextBox,
	{
		// summary:
		//		A subclass of MappedTextBox.
		//		Tests for a value out-of-range
		/*===== contraints object:
		// min: Number
		//		Minimum signed value.  Default is -Infinity
		min: undefined,
		// max: Number
		//		Maximum signed value.  Default is +Infinity
		max: undefined,
		=====*/

		// rangeMessage: String
		//		The message to display if value is out-of-range
		rangeMessage: "",

		compare: function(val1, val2){
			// summary: compare 2 values
			return val1 - val2;
		},

		rangeCheck: function(/* Number */ primitive, /* Object */ constraints){
			// summary: user replaceable function used to validate the range of the numeric input value
			var isMin = (typeof constraints.min != "undefined");
			var isMax = (typeof constraints.max != "undefined");
			if(isMin || isMax){
				return (!isMin || this.compare(primitive,constraints.min) >= 0) &&
					(!isMax || this.compare(primitive,constraints.max) <= 0);
			}else{ return true; }
		},

		isInRange: function(/* Boolean*/ isFocused){
			// summary: Need to over-ride with your own validation code in subclasses
			return this.rangeCheck(this.getValue(), this.constraints);
		},

		isValid: function(/* Boolean*/ isFocused){
			return this.inherited('isValid', arguments) &&
				((this._isEmpty(this.textbox.value) && !this.required) || this.isInRange(isFocused));
		},

		getErrorMessage: function(/* Boolean*/ isFocused){
			if(dijit.form.RangeBoundTextBox.superclass.isValid.call(this, false) && !this.isInRange(isFocused)){ return this.rangeMessage; }
			else{ return this.inherited('getErrorMessage', arguments); }
		},

		postMixInProperties: function(){
			this.inherited('postMixInProperties', arguments);
			if(!this.rangeMessage){
				this.messages = dojo.i18n.getLocalization("dijit.form", "validate", this.lang);
				this.rangeMessage = this.messages.rangeMessage;
			}
		},

		postCreate: function(){
			this.inherited('postCreate', arguments);
			if(typeof this.constraints.min != "undefined"){
				dijit.setWaiState(this.focusNode, "valuemin", this.constraints.min);
			}
			if(typeof this.constraints.max != "undefined"){
				dijit.setWaiState(this.focusNode, "valuemax", this.constraints.max);
			}
		}
	}
);

}
