if(!dojo._hasResource["dijit.form.Slider"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.Slider"] = true;
dojo.provide("dijit.form.Slider");

dojo.require("dijit.form._FormWidget");
dojo.require("dijit._Container");
dojo.require("dojo.dnd.move");
dojo.require("dijit.form.Button");
dojo.require("dojo.number");

dojo.declare(
	"dijit.form.HorizontalSlider",
	[dijit.form._FormWidget, dijit._Container],
{
	// summary
	//	A form widget that allows one to select a value with a horizontally draggable image

	templateString:"<table class=\"dijit dijitReset dijitSlider\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" rules=\"none\"\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset\" colspan=\"2\"></td\n\t\t><td dojoAttachPoint=\"containerNode,topDecoration\" class=\"dijitReset\" style=\"text-align:center;width:100%;\"></td\n\t\t><td class=\"dijitReset\" colspan=\"2\"></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset dijitSliderButtonContainer dijitHorizontalSliderButtonContainer\"\n\t\t\t><div class=\"dijitHorizontalSliderDecrementIcon\" tabIndex=\"-1\" style=\"display:none\" dojoAttachPoint=\"decrementButton\" dojoAttachEvent=\"onclick: decrement\"><span class=\"dijitSliderButtonInner\">-</span></div\n\t\t></td\n\t\t><td class=\"dijitReset\"\n\t\t\t><div class=\"dijitSliderBar dijitSliderBumper dijitHorizontalSliderBumper dijitSliderLeftBumper dijitHorizontalSliderLeftBumper\"></div\n\t\t></td\n\t\t><td class=\"dijitReset\"\n\t\t\t><input dojoAttachPoint=\"valueNode\" type=\"hidden\" name=\"${name}\"\n\t\t\t/><div style=\"position:relative;\" dojoAttachPoint=\"sliderBarContainer\"\n\t\t\t\t><div dojoAttachPoint=\"progressBar\" class=\"dijitSliderBar dijitHorizontalSliderBar dijitSliderProgressBar dijitHorizontalSliderProgressBar\" dojoAttachEvent=\"onclick:_onBarClick\"\n\t\t\t\t\t><div dojoAttachPoint=\"sliderHandle,focusNode\" class=\"dijitSliderMoveable dijitHorizontalSliderMoveable\" dojoAttachEvent=\"onkeypress:_onKeyPress,onclick:_onHandleClick\" waiRole=\"slider\" valuemin=\"${minimum}\" valuemax=\"${maximum}\"\n\t\t\t\t\t\t><div class=\"dijitSliderImageHandle dijitHorizontalSliderImageHandle\"></div\n\t\t\t\t\t></div\n\t\t\t\t></div\n\t\t\t\t><div dojoAttachPoint=\"remainingBar\" class=\"dijitSliderBar dijitHorizontalSliderBar dijitSliderRemainingBar dijitHorizontalSliderRemainingBar\" dojoAttachEvent=\"onclick:_onBarClick\"></div\n\t\t\t></div\n\t\t></td\n\t\t><td class=\"dijitReset\"\n\t\t\t><div class=\"dijitSliderBar dijitSliderBumper dijitHorizontalSliderBumper dijitSliderRightBumper dijitHorizontalSliderRightBumper\"></div\n\t\t></td\n\t\t><td class=\"dijitReset dijitSliderButtonContainer dijitHorizontalSliderButtonContainer\" style=\"right:0px;\"\n\t\t\t><div class=\"dijitHorizontalSliderIncrementIcon\" tabIndex=\"-1\" style=\"display:none\" dojoAttachPoint=\"incrementButton\" dojoAttachEvent=\"onclick: increment\"><span class=\"dijitSliderButtonInner\">+</span></div\n\t\t></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset\" colspan=\"2\"></td\n\t\t><td dojoAttachPoint=\"containerNode,bottomDecoration\" class=\"dijitReset\" style=\"text-align:center;\"></td\n\t\t><td class=\"dijitReset\" colspan=\"2\"></td\n\t></tr\n></table>\n",
	value: 0,

	// showButtons: boolean
	//	Show increment/decrement buttons at the ends of the slider?
	showButtons: true,

	// minimum:: integer
	//	The minimum value allowed.
	minimum: 0,

	// maximum: integer
	//	The maximum allowed value.
	maximum: 100,

	// discreteValues: integer
	//	The maximum allowed values dispersed evenly between minimum and maximum (inclusive).
	discreteValues: Infinity,

	// pageIncrement: integer
	//	The amount of change with shift+arrow
	pageIncrement: 2,

	// clickSelect: boolean
	//	If clicking the progress bar changes the value or not
	clickSelect: true,

	widgetsInTemplate: true,

	attributeMap: dojo.mixin(dojo.clone(dijit.form._FormWidget.prototype.attributeMap),
		{id:"", name:"valueNode"}),

	baseClass: "dijitSlider",

	_mousePixelCoord: "pageX",
	_pixelCount: "w",
	_startingPixelCoord: "x",
	_startingPixelCount: "l",
	_handleOffsetCoord: "left",
	_progressPixelSize: "width",
	_upsideDown: false,

	_onKeyPress: function(/*Event*/ e){
		if(this.disabled || e.altKey || e.ctrlKey){ return; }
		switch(e.keyCode){
			case dojo.keys.HOME:
				this.setValue(this.minimum);
				break;
			case dojo.keys.END:
				this.setValue(this.maximum);
				break;
			case dojo.keys.UP_ARROW:
			case (this._isReversed() ? dojo.keys.LEFT_ARROW : dojo.keys.RIGHT_ARROW):
			case dojo.keys.PAGE_UP:
				this.increment(e);
				break;
			case dojo.keys.DOWN_ARROW:
			case (this._isReversed() ? dojo.keys.RIGHT_ARROW : dojo.keys.LEFT_ARROW):
			case dojo.keys.PAGE_DOWN:
				this.decrement(e);
				break;
			default:
				this.inherited("_onKeyPress", arguments);
				return;
		}
		dojo.stopEvent(e);
	},

	_onHandleClick: function(e){
		if(this.disabled){ return; }
		if(!dojo.isIE){
			// make sure you get focus when dragging the handle
			// (but don't do on IE because it causes a flicker on mouse up (due to blur then focus)
			dijit.focus(this.sliderHandle);
		}
		dojo.stopEvent(e);
	},
	
	_isReversed: function() {
		return !(this._upsideDown || this.isLeftToRight());
	},

	_onBarClick: function(e){
		if(this.disabled || !this.clickSelect){ return; }
		dijit.focus(this.sliderHandle);
		dojo.stopEvent(e);
		var abspos = dojo.coords(this.sliderBarContainer, true);
		var pixelValue = e[this._mousePixelCoord] - abspos[this._startingPixelCoord];
		this._setPixelValue(this._isReversed() || this._upsideDown ? (abspos[this._pixelCount] - pixelValue) : pixelValue, abspos[this._pixelCount], true);
	},

	_setPixelValue: function(/*Number*/ pixelValue, /*Number*/ maxPixels, /*Boolean, optional*/ priorityChange){
		if(this.disabled){ return; }
		pixelValue = pixelValue < 0 ? 0 : maxPixels < pixelValue ? maxPixels : pixelValue;
		var count = this.discreteValues;
		if(count <= 1 || count == Infinity){ count = maxPixels; }
		count--;
		var pixelsPerValue = maxPixels / count;
		var wholeIncrements = Math.round(pixelValue / pixelsPerValue);
		this.setValue((this.maximum-this.minimum)*wholeIncrements/count + this.minimum, priorityChange);
	},

	setValue: function(/*Number*/ value, /*Boolean, optional*/ priorityChange){
		this.valueNode.value = this.value = value;
		this.inherited('setValue', arguments);
		var percent = (value - this.minimum) / (this.maximum - this.minimum);
		this.progressBar.style[this._progressPixelSize] = (percent*100) + "%";
		this.remainingBar.style[this._progressPixelSize] = ((1-percent)*100) + "%";
	},

	_bumpValue: function(signedChange){
		if(this.disabled){ return; }
		var s = dojo.getComputedStyle(this.sliderBarContainer);
		var c = dojo._getContentBox(this.sliderBarContainer, s);
		var count = this.discreteValues;
		if(count <= 1 || count == Infinity){ count = c[this._pixelCount]; }
		count--;
		var value = (this.value - this.minimum) * count / (this.maximum - this.minimum) + signedChange;
		if(value < 0){ value = 0; }
		if(value > count){ value = count; }
		value = value * (this.maximum - this.minimum) / count + this.minimum;
		this.setValue(value, true);
	},

	decrement: function(e){
		// summary
		//	decrement slider by 1 unit
		this._bumpValue(e.keyCode == dojo.keys.PAGE_DOWN?-this.pageIncrement:-1);
	},

	increment: function(e){
		// summary
		//	increment slider by 1 unit
		this._bumpValue(e.keyCode == dojo.keys.PAGE_UP?this.pageIncrement:1);
	},

	_mouseWheeled: function(/*Event*/ evt){
		dojo.stopEvent(evt);
		var scrollAmount = 0;
		if(typeof evt.wheelDelta == 'number'){ // IE
			scrollAmount = evt.wheelDelta;
		}else if(typeof evt.detail == 'number'){ // Mozilla+Firefox
			scrollAmount = -evt.detail;
		}
		if(scrollAmount > 0){
			this.increment(evt);
		}else if(scrollAmount < 0){
			this.decrement(evt);
		}
	},

	startup: function(){
		dojo.forEach(this.getChildren(), function(child){
			if(this[child.container] != this.containerNode){
				this[child.container].appendChild(child.domNode);
			}
		}, this);
	},

	_onBlur: function(){
		dijit.form.HorizontalSlider.superclass.setValue.call(this, this.value, true);
	},

	postCreate: function(){
		if(this.showButtons){
			this.incrementButton.style.display="";
			this.decrementButton.style.display="";
		}
		this.connect(this.domNode, dojo.isIE ? "onmousewheel" : 'DOMMouseScroll', "_mouseWheeled");

		// define a custom constructor for a SliderMover that points back to me
		var _self = this;
		var mover = function(){
			dijit.form._SliderMover.apply(this, arguments);
			this.widget = _self;
		};
		dojo.extend(mover, dijit.form._SliderMover.prototype);

		this._movable = new dojo.dnd.Moveable(this.sliderHandle, {mover: mover});
		this.inherited('postCreate', arguments);
	},

	destroy: function(){
		this._movable.destroy();
		this.inherited('destroy', arguments);	
	}
});

dojo.declare(
	"dijit.form.VerticalSlider",
	dijit.form.HorizontalSlider,
{
	// summary
	//	A form widget that allows one to select a value with a vertically draggable image

	templateString:"<table class=\"dijitReset dijitSlider\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" rules=\"none\"\n><tbody class=\"dijitReset\"\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset\"></td\n\t\t><td class=\"dijitReset dijitSliderButtonContainer dijitVerticalSliderButtonContainer\"\n\t\t\t><div class=\"dijitVerticalSliderIncrementIcon\" tabIndex=\"-1\" style=\"display:none\" dojoAttachPoint=\"incrementButton\" dojoAttachEvent=\"onclick: increment\"><span class=\"dijitSliderButtonInner\">+</span></div\n\t\t></td\n\t\t><td class=\"dijitReset\"></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset\"></td\n\t\t><td class=\"dijitReset\"\n\t\t\t><center><div class=\"dijitSliderBar dijitSliderBumper dijitVerticalSliderBumper dijitSliderTopBumper dijitVerticalSliderTopBumper\"></div></center\n\t\t></td\n\t\t><td class=\"dijitReset\"></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td dojoAttachPoint=\"leftDecoration\" class=\"dijitReset\" style=\"text-align:center;height:100%;\"></td\n\t\t><td class=\"dijitReset\" style=\"height:100%;\"\n\t\t\t><input dojoAttachPoint=\"valueNode\" type=\"hidden\" name=\"${name}\"\n\t\t\t/><center style=\"position:relative;height:100%;\" dojoAttachPoint=\"sliderBarContainer\"\n\t\t\t\t><div dojoAttachPoint=\"remainingBar\" class=\"dijitSliderBar dijitVerticalSliderBar dijitSliderRemainingBar dijitVerticalSliderRemainingBar\" dojoAttachEvent=\"onclick:_onBarClick\"></div\n\t\t\t\t><div dojoAttachPoint=\"progressBar\" class=\"dijitSliderBar dijitVerticalSliderBar dijitSliderProgressBar dijitVerticalSliderProgressBar\" dojoAttachEvent=\"onclick:_onBarClick\"\n\t\t\t\t\t><div dojoAttachPoint=\"sliderHandle,focusNode\" class=\"dijitSliderMoveable\" dojoAttachEvent=\"onkeypress:_onKeyPress,onclick:_onHandleClick\" style=\"vertical-align:top;\" waiRole=\"slider\" valuemin=\"${minimum}\" valuemax=\"${maximum}\"\n\t\t\t\t\t\t><div class=\"dijitSliderImageHandle dijitVerticalSliderImageHandle\"></div\n\t\t\t\t\t></div\n\t\t\t\t></div\n\t\t\t></center\n\t\t></td\n\t\t><td dojoAttachPoint=\"containerNode,rightDecoration\" class=\"dijitReset\" style=\"text-align:center;height:100%;\"></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset\"></td\n\t\t><td class=\"dijitReset\"\n\t\t\t><center><div class=\"dijitSliderBar dijitSliderBumper dijitVerticalSliderBumper dijitSliderBottomBumper dijitVerticalSliderBottomBumper\"></div></center\n\t\t></td\n\t\t><td class=\"dijitReset\"></td\n\t></tr\n\t><tr class=\"dijitReset\"\n\t\t><td class=\"dijitReset\"></td\n\t\t><td class=\"dijitReset dijitSliderButtonContainer dijitVerticalSliderButtonContainer\"\n\t\t\t><div class=\"dijitVerticalSliderDecrementIcon\" tabIndex=\"-1\" style=\"display:none\" dojoAttachPoint=\"decrementButton\" dojoAttachEvent=\"onclick: decrement\"><span class=\"dijitSliderButtonInner\">-</span></div\n\t\t></td\n\t\t><td class=\"dijitReset\"></td\n\t></tr\n></tbody></table>\n",
	_mousePixelCoord: "pageY",
	_pixelCount: "h",
	_startingPixelCoord: "y",
	_startingPixelCount: "t",
	_handleOffsetCoord: "top",
	_progressPixelSize: "height",
	_upsideDown: true
});

dojo.declare("dijit.form._SliderMover",
	dojo.dnd.Mover,
{
	onMouseMove: function(e){
		var widget = this.widget;
		var c = this.constraintBox;
		if(!c){
			var container = widget.sliderBarContainer;
			var s = dojo.getComputedStyle(container);
			var c = dojo._getContentBox(container, s);
			c[widget._startingPixelCount] = 0;
			this.constraintBox = c;
		}
		var m = this.marginBox;
		var pixelValue = widget._isReversed() ?
			e[widget._mousePixelCoord] - dojo._abs(widget.sliderBarContainer).x : 
			m[widget._startingPixelCount] + e[widget._mousePixelCoord];
		dojo.hitch(widget, "_setPixelValue")(widget._isReversed() || widget._upsideDown? (c[widget._pixelCount]-pixelValue) : pixelValue, c[widget._pixelCount]);
	},
	
	destroy: function(e){
		var widget = this.widget;
		widget.setValue(widget.value, true);
		dojo.dnd.Mover.prototype.destroy.call(this);
	}
});


dojo.declare("dijit.form.HorizontalRule", [dijit._Widget, dijit._Templated],
{
	//	Summary:
	//		Create hash marks for the Horizontal slider
	templateString: '<div class="RuleContainer HorizontalRuleContainer"></div>',

	// count: Integer
	//		Number of hash marks to generate
	count: 3,

	// container: Node
	//		If this is a child widget, connect it to this parent node
	container: "containerNode",

	// ruleStyle: String
	//		CSS style to apply to individual hash marks
	ruleStyle: "",

	_positionPrefix: '<div class="RuleMark HorizontalRuleMark" style="left:',
	_positionSuffix: '%;',
	_suffix: '"></div>',

	_genHTML: function(pos, ndx){
		return this._positionPrefix + pos + this._positionSuffix + this.ruleStyle + this._suffix;
	},
	
	_isHorizontal: true,

	postCreate: function(){
		if(this.count==1){
			var innerHTML = this._genHTML(50, 0);
		}else{
			var interval = 100 / (this.count-1);
			if(!this._isHorizontal || this.isLeftToRight()){
				var innerHTML = this._genHTML(0, 0);
				for(var i=1; i < this.count-1; i++){
					innerHTML += this._genHTML(interval*i, i);
				}
				innerHTML += this._genHTML(100, this.count-1);
			}else{
				var innerHTML = this._genHTML(100, 0);
				for(var i=1; i < this.count-1; i++){
					innerHTML += this._genHTML(100-interval*i, i);
				}
				innerHTML += this._genHTML(0, this.count-1);
			}
		}
		this.domNode.innerHTML = innerHTML;
	}
});

dojo.declare("dijit.form.VerticalRule", dijit.form.HorizontalRule,
{
	//	Summary:
	//		Create hash marks for the Vertical slider
	templateString: '<div class="RuleContainer VerticalRuleContainer"></div>',
	_positionPrefix: '<div class="RuleMark VerticalRuleMark" style="top:',
	
	_isHorizontal: false
});

dojo.declare("dijit.form.HorizontalRuleLabels", dijit.form.HorizontalRule,
{
	//	Summary:
	//		Create labels for the Horizontal slider
	templateString: '<div class="RuleContainer HorizontalRuleContainer"></div>',

	// labelStyle: String
	//		CSS style to apply to individual text labels
	labelStyle: "",

	// labels: Array
	//	Array of text labels to render - evenly spaced from left-to-right or bottom-to-top
	labels: [],

	// numericMargin: Integer
	//	Number of generated numeric labels that should be rendered as '' on the ends when labels[] are not specified
	numericMargin: 0,

	// numericMinimum: Integer
	//	Leftmost label value for generated numeric labels when labels[] are not specified
	minimum: 0,

	// numericMaximum: Integer
	//	Rightmost label value for generated numeric labels when labels[] are not specified
	maximum: 1,

	// constraints: object
	//	pattern, places, lang, et al (see dojo.number) for generated numeric labels when labels[] are not specified
	constraints: {pattern:"#%"},

	_positionPrefix: '<div class="RuleLabelContainer HorizontalRuleLabelContainer" style="left:',
	_labelPrefix: '"><span class="RuleLabel HorizontalRuleLabel">',
	_suffix: '</span></div>',

	_calcPosition: function(pos){
		return pos;
	},

	_genHTML: function(pos, ndx){
		return this._positionPrefix + this._calcPosition(pos) + this._positionSuffix + this.labelStyle + this._labelPrefix + this.labels[ndx] + this._suffix;
	},

	getLabels: function(){
		// summary: user replaceable function to return the labels array

		// if the labels array was not specified directly, then see if <li> children were
		var labels = this.labels;
		if(!labels.length){
			// for markup creation, labels are specified as child elements
			labels = dojo.query("> li", this.srcNodeRef).map(function(node){
				return String(node.innerHTML);
			});
		}
		this.srcNodeRef.innerHTML = '';
		// if the labels were not specified directly and not as <li> children, then calculate numeric labels
		if(!labels.length && this.count > 1){
			var start = this.minimum;
			var inc = (this.maximum - start) / (this.count-1);
			for (var i=0; i < this.count; i++){
				labels.push((i<this.numericMargin||i>=(this.count-this.numericMargin))? '' : dojo.number.format(start, this.constraints));
				start += inc;
			}
		}
		return labels;
	},

	postMixInProperties: function(){
		this.inherited('postMixInProperties', arguments);
		this.labels = this.getLabels();
		this.count = this.labels.length;
	}
});

dojo.declare("dijit.form.VerticalRuleLabels", dijit.form.HorizontalRuleLabels,
{
	//	Summary:
	//		Create labels for the Vertical slider
	templateString: '<div class="RuleContainer VerticalRuleContainer"></div>',

	_positionPrefix: '<div class="RuleLabelContainer VerticalRuleLabelContainer" style="top:',
	_labelPrefix: '"><span class="RuleLabel VerticalRuleLabel">',

	_calcPosition: function(pos){
		return 100-pos;
	},
	
	_isHorizontal: false
});

}
