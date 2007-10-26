SpringFaces.DojoGenericFieldAdvisor = function(config){
			
	dojo.mixin(this, config);
};
		
SpringFaces.DojoGenericFieldAdvisor.prototype = {
			
	targetElId : "",
	decoratorType : "",
	decorator : null,
	decoratorAttrs : "",
			
	apply : function(){
	        	
       	this.decorator = eval("new "+ this.decoratorType + "(" + this.decoratorAttrs +", dojo.byId('"+this.targetElId+"'));" );

       	this.decorator.startup();
	},
	
	validate : function(){
		var isValid = this.decorator.isValid(false);
		if (!isValid) {
			this.decorator.state = "Error";
			this.decorator._setStateClass();
		}
		return isValid;
	}			
};

dojo.addOnLoad(SpringFaces.applyAdvisors);