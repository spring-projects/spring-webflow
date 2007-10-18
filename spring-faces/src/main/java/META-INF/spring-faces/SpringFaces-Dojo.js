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
	}			
};

dojo.addOnLoad(SpringFaces.applyAdvisors);