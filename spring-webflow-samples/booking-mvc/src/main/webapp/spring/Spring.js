/*
 * Copyright 2004-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
Spring = {};
		
Spring.decorations = [];

Spring.decorations.applied = false;
		
Spring.applyDecorations = function(){
	if (!Spring.decorations.applied) {		
		for (var x=0; x<Spring.decorations.length; x++) {
			Spring.decorations[x].apply();
		}
		Spring.decorations.applied = true;
	}
};

Spring.validateAll = function(){
	var valid = true;
	for(x in Spring.decorations) {
		if (Spring.decorations[x].widget &&
			!Spring.decorations[x].validate()) {
			valid = false;
		}
	}
	return valid;
};

Spring.validateRequired = function(){
	var valid = true;
	for(x in Spring.decorations) {
		if(Spring.decorations[x].decorator &&
			Spring.decorations[x].isRequired() &&
			!Spring.decorations[x].validate())
			valid = false;
	}
	return valid;
};

Spring.AbstractElementDecoration = function(){};

Spring.AbstractElementDecoration.prototype = {
	
	elementId : "",
	widgetType : null,
	widget : null,
	widgetAttrs : {},
	
	apply : function(){},
	
	validate : function(){},
	
	isRequired : function(){}
};

Spring.AbstractValidateAllDecoration = function(){};

Spring.AbstractValidateAllDecoration.prototype = {
	
	event : "",
	elementId : "",
	
	apply : function() {},
	
	cleanup : function(){},
	
	handleEvent : function(event){}
};

Spring.AbstractCommandLinkDecoration = function(){};

Spring.AbstractCommandLinkDecoration.prototype = {
	
	elementId : "",
	linkHtml : "",
	
	apply : function(){},
	
	submitFormFromLink : function(/*String*/ formId, /*String*/ sourceId, /*Array of name,value params*/ params){}
};

Spring.AbstractRemoteEventDecoration = function(){};

Spring.AbstractRemoteEventDecoration.prototype = {
	
	event : "",
	elementId : "",
	sourceId : "",
	formId : "",
	processIds : "",
	params : [],
	
	apply : function(){},
	
	cleanup : function(){},
	
	submit : function(event){}
};

Spring.AbstractRemotingHandler = function(){};

Spring.AbstractRemotingHandler.prototype = {

	submitForm : function(/*String */ sourceId, /*String*/formId, /*String*/ processIds, /*Array*/ params){}, 
	
	getResource : function(/*String */ sourceId, /*String*/ processIds) {},
	
	handleResponse : function() {},
	
	handleError : function() {}
};
