SpringUI = {};
		
SpringUI.advisors = [];

SpringUI.advisorsApplied = false;
		
SpringUI.applyAdvisors = function(){
	if (!SpringUI.advisorsApplied) {		
		for (var x=0; x<SpringUI.advisors.length; x++) {
			SpringUI.advisors[x].apply();
		}
		SpringUI.advisorsApplied = true;
	}
};

SpringUI.validateAll = function(){
	var valid = true;
	for(x in SpringUI.advisors) {
		if (SpringUI.advisors[x].decorator &&
			!SpringUI.advisors[x].validate()) {
			valid = false;
		}
	}
	return valid;
};

SpringUI.validateRequired = function(){
	var valid = true;
	for(x in SpringUI.advisors) {
		if(SpringUI.advisors[x].decorator &&
			SpringUI.advisors[x].isRequired() &&
			!SpringUI.advisors[x].validate())
			valid = false;
	}
	return valid;
};

SpringUI.ValidatingFieldAdvisor = function(){};

SpringUI.ValidatingFieldAdvisor.prototype = {
	
	targetElId : "",
	decoratorType : "",
	decorator : null,
	decoratorAttrs : "",
	
	apply : function(){},
	
	validate : function(){},
	
	isRequired : function(){}
};

SpringUI.RemoteEventAdvisor = function(){};

SpringUI.RemoteEventAdvisor.prototype = {
	
	event : "",
	targetId : "",
	sourceId : "",
	formId : "",
	processIds : "",
	renderIds : "",
	params : [],
	connection : null,
	
	apply : function(){},
	
	cleanup : function(){},
	
	submit : function(event){}
};

SpringUI.RemotingHandler = function(){};

SpringUI.RemotingHandler.prototype = {

	submitForm : function(/*String */ sourceId, /*String*/formId, /*String*/ processIds, /*String*/renderIds, /*Array*/ params){}, 
	
	getResource : function(/*String */ sourceId, /*String*/ processIds, /*String*/renderIds) {},
	
	handleResponse : function() {},
	
	handleError : function() {}
};
