SpringFaces = {};
		
SpringFaces.advisors = [];
		
SpringFaces.ExtGenericFieldAdvisor = function(config){
			
	Ext.apply(this, config);
};
		
SpringFaces.ExtGenericFieldAdvisor.prototype = {
			
	targetElId : "",
	msgElId : "",
	decoratorType : "",
	decorator : null,
	decoratorAttrs : "",
			
	apply : function(){
			
		var target = document.getElementById(this.targetElId);
       	var msgEl = document.getElementById(this.msgElId);
	        	
       	this.decorator = eval("new "+ this.decoratorType + "(" + this.decoratorAttrs +");" );
       	
       	this.decorator.msgTarget=msgEl;
       	this.decorator.applyTo(target);
	}			
};
		
SpringFaces.applyAdvisors = function(){
			
	for (x in SpringFaces.advisors) {
		SpringFaces.advisors[x].apply();
	}
};

SpringFaces.validateAll = function(){
	var valid = true;
	for(x in SpringFaces.advisors) {
		if (SpringFaces.advisors[x].decorator &&
			!SpringFaces.advisors[x].decorator.validate()) {
			valid = false;
		}
	}
	return valid;
};
		
Ext.onReady(SpringFaces.applyAdvisors);