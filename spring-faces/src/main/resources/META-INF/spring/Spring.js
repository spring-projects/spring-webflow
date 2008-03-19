Spring = {};
		
Spring.advisors = [];

Spring.advisors.applied = false;
		
Spring.applyAdvisors = function(){
	if (!Spring.advisors.applied) {		
		for (var x=0; x<Spring.advisors.length; x++) {
			Spring.advisors[x].apply();
		}
		Spring.advisors.applied = true;
	}
};

Spring.validateAll = function(){
	var valid = true;
	for(x in Spring.advisors) {
		if (Spring.advisors[x].decorator &&
			!Spring.advisors[x].validate()) {
			valid = false;
		}
	}
	return valid;
};

Spring.validateRequired = function(){
	var valid = true;
	for(x in Spring.advisors) {
		if(Spring.advisors[x].decorator &&
			Spring.advisors[x].isRequired() &&
			!Spring.advisors[x].validate())
			valid = false;
	}
	return valid;
};

Spring.ValidatingFieldAdvisor = function(){};

Spring.ValidatingFieldAdvisor.prototype = {
	
	targetElId : "",
	decoratorType : "",
	decorator : null,
	decoratorAttrs : "",
	
	apply : function(){},
	
	validate : function(){},
	
	isRequired : function(){}
};

Spring.CommandLinkAdvisor = function(){};

Spring.CommandLinkAdvisor.prototype = {
	
	targetElId : "",
	linkHtml : "",
	
	apply : function(){},
	
	submitFormFromLink : function(/*String*/ formId, /*String*/ sourceId, /*Array of name,value params*/ params){}
};

Spring.RemoteEventAdvisor = function(){};

Spring.RemoteEventAdvisor.prototype = {
	
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

Spring.RemotingHandler = function(){};

Spring.RemotingHandler.prototype = {

	submitForm : function(/*String */ sourceId, /*String*/formId, /*String*/ processIds, /*String*/renderIds, /*Array*/ params){}, 
	
	getResource : function(/*String */ sourceId, /*String*/ processIds, /*String*/renderIds) {},
	
	handleResponse : function() {},
	
	handleError : function() {}
};
