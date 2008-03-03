Spring.DojoValidatingFieldAdvisor = function(config){
			
	dojo.mixin(this, config);
};
		
Spring.DojoValidatingFieldAdvisor.prototype = {
			
	targetElId : "",
	decoratorType : "",
	decorator : null,
	decoratorAttrs : "",
			
	apply : function(){
	        	
       	this.decorator = eval("new "+ this.decoratorType + "(" + this.decoratorAttrs +", dojo.byId('"+this.targetElId+"'));" );
       	this.decorator.startup();
       	
   		//return this to support method chaining
   		return this;
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

Spring.ValidatingFieldAdvisor = Spring.DojoValidatingFieldAdvisor;

Spring.DojoRemoteEventAdvisor = function(config){
	dojo.mixin(this, config);
};

Spring.DojoRemoteEventAdvisor.prototype = {
	
	event : "",
	targetId : "",
	sourceId : "",
	formId : "",
	processIds : "",
	renderIds : "",
	params : [],
	connection : null,
	
	apply : function() {
		connection = dojo.connect(dojo.byId(this.targetId), this.event, this, "submit");
		return this;	
	},
	
	cleanup : function(){
		dojo.disconnect(this.connection);
	},
	
	submit : function(){
		Spring.RemotingHandler.submitForm(this.sourceId, this.formId, this.processIds, this.renderIds, this.params);
	}
};

Spring.RemoteEventAdvisor = Spring.DojoRemoteEventAdvisor;

Spring.DojoRemotingHandler = function(){};

Spring.DojoRemotingHandler.prototype = {
	
	submitForm : function(/*String */ sourceId, /*String*/formId, /*String*/ processIds, /*String*/renderIds, /*Array*/ params) {
		var content = new Object();
		var sourceComponent = dojo.byId(sourceId);
		content['processIds'] = processIds; 
		content['renderIds'] = renderIds;
	
	    if (sourceComponent != null){
	    	if(sourceComponent.value) {
	    		content[sourceId] = sourceComponent.value;
	    	} else {
	    		content[sourceId] = sourceId;
	    	}
	    }
	    
	    dojo.forEach(params, function(param){
	    	content[param.name] = param.value;
	    });
	    
	    content['ajaxSource'] = sourceId;
	    
		dojo.xhrPost({
			
			content: content,
			
			form: formId,
			
			handleAs: "text",
			
			headers: {"Accept" : "text/html;type=ajax"},
			
			// The LOAD function will be called on a successful response.
	        load: this.handleResponse,
	
	        // The ERROR function will be called in an error case.
	        error: this.handleError
        });	

	},
	
	handleResponse: function(response, ioArgs) {
		//alert("handling the response");
		
		//First check if this response should redirect
		var redirectURL = ioArgs.xhr.getResponseHeader('Flow-Redirect-URL');
		var modalViewHeader = ioArgs.xhr.getResponseHeader('Flow-Modal-View');
		var modalView = dojo.isString(modalViewHeader) && modalViewHeader.length > 0;
		
		if (dojo.isString(redirectURL) && redirectURL.length > 0) {
			if (modalView) {
				//render a popup with the new URL
				Spring.RemotingHandler.renderURLToModalDialog(redirectURL, ioArgs);
				return response;
			}
			else {
				window.location.pathname = redirectURL;
				return response;
			}
		}
		
		//Extract and store all <script> elements from the response
		var scriptPattern = '(?:<script.*?>)((\n|\r|.)*?)(?:<\/script>)';
		var extractedScriptNodes = [];
		var matchAll = new RegExp(scriptPattern, 'img');
		var matchOne = new RegExp(scriptPattern, 'im');
	
		var scriptNodes = response.match(matchAll);
		if (scriptNodes != null)
		{
			for (var i=0; i<scriptNodes.length; i++)
			{
				var script = (scriptNodes[i].match(matchOne) || ['',''])[1];
				script = script.replace(/<!--/mg,'').replace(/\/\/-->/mg,'');
				extractedScriptNodes.push(script);
			}
		}
		response = response.replace(matchAll, '');

		//Extract the new DOM nodes from the response
		var tempDiv = dojo.doc.createElement("div");
		tempDiv.id="ajaxResponse";
		tempDiv.style.visibility= "hidden";
		document.body.appendChild(tempDiv);
		var tempContainer = new dojo.NodeList(tempDiv);
		var newNodes = tempContainer.addContent(response, "first").query("#ajaxResponse > *").orphan();
		tempContainer.orphan();
	
		//For a modal view, just dump the new nodes into a modal dialog
		if (modalView) {
			Spring.RemotingHandler.renderNodeListToModalDialog(newNodes);
			return response;
		}
	
		//Insert the new DOM nodes and update the Form's action URL
		newNodes.forEach(function(item) {
			if (item.id != null && item.id != "") {
			    var target = dojo.byId(item.id);
				target.parentNode.replaceChild(item, target);
			}
		});
		
		//Evaluate any script code
		dojo.forEach(extractedScriptNodes, function(script){
			dojo.eval(script);
		});
		
		return response;
	},
	
	handleError: function(response, ioArgs) {
		//alert("handling an error");
		console.error("HTTP status code: ", ioArgs.xhr.status);
		return response;
	},
	
	renderURLToModalDialog: function(url, ioArgs) {
		dojo.require("dijit.Dialog");
		
		url = url + "&"+dojo.objectToQuery(ioArgs.args.content);
		
		var dialog = new dijit.Dialog({href: url});
		dialog.show();
		
	},
	
	renderNodeListToModalDialog: function(nodes) {
		dojo.require("dijit.Dialog");
		
		var dialog = new dijit.Dialog({});
		dialog.setContent(nodes);
		dialog.show();
	}
};

Spring.RemotingHandler = new Spring.DojoRemotingHandler();

Spring.DojoCommandLinkAdvisor = function(config){
	dojo.mixin(this, config);
};

Spring.DojoCommandLinkAdvisor.prototype = {
	
	targetElId : "",
	
	linkHtml : "",
	
	apply : function(){
		var advisedNode = dojo.byId(this.targetElId);
		if (!dojo.hasClass(advisedNode, "progressiveLink")) {
			//Node must be replaced
			var nodeToReplace = new dojo.NodeList(advisedNode);
			nodeToReplace.addContent(this.linkHtml, "after").orphan("*");
			//Get the new node
			advisedNode = dojo.byId(this.targetElId);
		}
		advisedNode.submitFormFromLink = this.submitFormFromLink;
		//return this to support method chaining
   		return this;
	},
	
	submitFormFromLink : function(/*String*/ formId, /*String*/ sourceId, /*Array of name,value params*/ params){
		var addedNodes = [];
		var formNode = dojo.byId(formId);
		var linkNode = document.createElement("input");
		linkNode.name = sourceId;
		linkNode.value = "submitted";
		addedNodes.push(linkNode);
		
		dojo.forEach(params, function(param){
			var paramNode = document.createElement("input");
			paramNode.name=param.name;
			paramNode.value=param.value;
			addedNodes.push(paramNode);
		});
		
		dojo.forEach(addedNodes, function(nodeToAdd){
			dojo.addClass(nodeToAdd, "SpringLinkInput");
			dojo.place(nodeToAdd, formNode, "last");
		});		
		
		if ((formNode.onsubmit ? !formNode.onsubmit() : false) || !formNode.submit()) {
			dojo.forEach(addedNodes, function(hiddenNode){
				formNode.removeChild(hiddenNode);
			});
		}
	}
};

Spring.CommandLinkAdvisor = Spring.DojoCommandLinkAdvisor;

dojo.addOnLoad(Spring.applyAdvisors);