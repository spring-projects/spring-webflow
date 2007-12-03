if(!dojo._hasResource["dijit.form._Spinner"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form._Spinner"] = true;
dojo.provide("dijit.form._Spinner");

dojo.require("dijit.form.ValidationTextBox");

dojo.declare(
	"dijit.form._Spinner",
	dijit.form.RangeBoundTextBox,
	{

		// summary: Mixin for validation widgets with a spinner
		// description: This class basically (conceptually) extends dijit.form.ValidationTextBox.
		//	It modifies the template to have up/down arrows, and provides related handling code.

		// defaultTimeout: Number
		//	  number of milliseconds before a held key or button becomes typematic
		defaultTimeout: 500,

		// timeoutChangeRate: Number
		//	  fraction of time used to change the typematic timer between events
		//	  1.0 means that each typematic event fires at defaultTimeout intervals
		//	  < 1.0 means that each typematic event fires at an increasing faster rate
		timeoutChangeRate: 0.90,

		// smallDelta: Number
		//	  adjust the value by this much when spinning using the arrow keys/buttons
		smallDelta: 1,
		// largeDelta: Number
		//	  adjust the value by this much when spinning using the PgUp/Dn keys
		largeDelta: 10,

		templateString:"<table style=\"-moz-inline-stack;\" class=\"dijit dijitReset dijitInlineTable dijitLeft\" cellspacing=\"0\"  cellpadding=\"0\"\n\tid=\"widget_${id}\" name=\"${name}\"\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onkeypress:_onKeyPress\"\n\twaiRole=\"presentation\"\n\t><tr class=\"dijitReset\"\n\t\t><td rowspan=\"2\" class=\"dijitReset dijitStretch dijitInputField\" width=\"100%\"\n\t\t\t><input dojoAttachPoint=\"textbox,focusNode\" type=\"${type}\" dojoAttachEvent=\"onfocus,onkeyup\"\n\t\t\t\twaiRole=\"spinbutton\" autocomplete=\"off\" name=\"${name}\"\n\t\t></td\n\t\t><td rowspan=\"2\" class=\"dijitReset dijitValidationIconField\" width=\"0%\" \n\t\t\t><div dojoAttachPoint='iconNode' class='dijitInline dijitValidationIcon'></div\n\t\t></td\n\t\t><td class=\"dijitReset dijitRight dijitButtonNode dijitUpArrowButton\" width=\"0%\"\n\t\t\t\tdojoAttachPoint=\"upArrowNode\"\n\t\t\t\tdojoAttachEvent=\"onmousedown:_handleUpArrowEvent,onmouseup:_handleUpArrowEvent,onmouseover:_handleUpArrowEvent,onmouseout:_handleUpArrowEvent\"\n\t\t\t\tstateModifier=\"UpArrow\"\n\t\t\t><div class=\"dijitA11yUpArrow\">&#9650;</div\n\t\t></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset dijitRight dijitButtonNode dijitDownArrowButton\" width=\"0%\"\n\t\t\t\tdojoAttachPoint=\"downArrowNode\"\n\t\t\t\tdojoAttachEvent=\"onmousedown:_handleDownArrowEvent,onmouseup:_handleDownArrowEvent,onmouseover:_handleDownArrowEvent,onmouseout:_handleDownArrowEvent\"\n\t\t\t\tstateModifier=\"DownArrow\"\n\t\t\t><div class=\"dijitA11yDownArrow\">&#9660;</div\n\t\t></td\n\t></tr\n></table>\n\n",
		baseClass: "dijitSpinner",

		adjust: function(/* Object */ val, /*Number*/ delta){
			// summary: user replaceable function used to adjust a primitive value(Number/Date/...) by the delta amount specified
			// the val is adjusted in a way that makes sense to the object type
			return val;
		},

		_handleUpArrowEvent : function(/*Event*/ e){
			this._onMouse(e, this.upArrowNode);
		},

		_handleDownArrowEvent : function(/*Event*/ e){
			this._onMouse(e, this.downArrowNode);
		},


		_arrowPressed: function(/*Node*/ nodePressed, /*Number*/ direction){
			if(this.disabled){ return; }
			dojo.addClass(nodePressed, "dijitSpinnerButtonActive");
			this.setValue(this.adjust(this.getValue(), direction*this.smallDelta));
		},

		_arrowReleased: function(/*Node*/ node){
			if(this.disabled){ return; }
			this._wheelTimer = null;
			dijit.focus(this.textbox);
			dojo.removeClass(node, "dijitSpinnerButtonActive");
		},

		_typematicCallback: function(/*Number*/ count, /*DOMNode*/ node, /*Event*/ evt){
			if(node == this.textbox){ node = (evt.keyCode == dojo.keys.UP_ARROW) ? this.upArrowNode : this.downArrowNode; }
			if(count == -1){ this._arrowReleased(node); }
			else{ this._arrowPressed(node, (node == this.upArrowNode) ? 1 : -1); }
		},

		_wheelTimer: null,
		_mouseWheeled: function(/*Event*/ evt){
			dojo.stopEvent(evt);
			var scrollAmount = 0;
			if(typeof evt.wheelDelta == 'number'){ // IE
				scrollAmount = evt.wheelDelta;
			}else if(typeof evt.detail == 'number'){ // Mozilla+Firefox
				scrollAmount = -evt.detail;
			}
			if(scrollAmount > 0){
				var node = this.upArrowNode;
				var dir = +1;
			}else if(scrollAmount < 0){
				var node = this.downArrowNode;
				var dir = -1;
			}else{ return; }
			this._arrowPressed(node, dir);
			if(this._wheelTimer != null){
				clearTimeout(this._wheelTimer);
			}
			var _this = this;
			this._wheelTimer = setTimeout(function(){_this._arrowReleased(node);}, 50);
		},

		postCreate: function(){
			this.inherited('postCreate', arguments);

			// extra listeners
			this.connect(this.textbox, dojo.isIE ? "onmousewheel" : 'DOMMouseScroll', "_mouseWheeled");
			dijit.typematic.addListener(this.upArrowNode, this.textbox, {keyCode:dojo.keys.UP_ARROW,ctrlKey:false,altKey:false,shiftKey:false}, this, "_typematicCallback", this.timeoutChangeRate, this.defaultTimeout);
			dijit.typematic.addListener(this.downArrowNode, this.textbox, {keyCode:dojo.keys.DOWN_ARROW,ctrlKey:false,altKey:false,shiftKey:false}, this, "_typematicCallback", this.timeoutChangeRate, this.defaultTimeout);
		}
});

}
