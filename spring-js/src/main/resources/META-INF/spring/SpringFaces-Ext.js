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
	},
	
	validate : function(){
		return this.decorator.validate();
	}
				
};

Ext.onReady(SpringFaces.applyAdvisors);