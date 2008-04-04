/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


Spring={};Spring.advisors=[];Spring.advisors.applied=false;Spring.applyAdvisors=function(){if(!Spring.advisors.applied){for(var x=0;x<Spring.advisors.length;x++){Spring.advisors[x].apply();}Spring.advisors.applied=true;}};Spring.validateAll=function(){var _2=true;for(x in Spring.advisors){if(Spring.advisors[x].decorator&&!Spring.advisors[x].validate()){_2=false;}}return _2;};Spring.validateRequired=function(){var _3=true;for(x in Spring.advisors){if(Spring.advisors[x].decorator&&Spring.advisors[x].isRequired()&&!Spring.advisors[x].validate()){_3=false;}}return _3;};Spring.ValidatingFieldAdvisor=function(){};Spring.ValidatingFieldAdvisor.prototype={targetElId:"",decoratorType:"",decorator:null,decoratorAttrs:"",apply:function(){},validate:function(){},isRequired:function(){}};Spring.ValidateAllAdvisor=function(){};Spring.ValidateAllAdvisor.prototype={event:"",targetId:"",connection:null,apply:function(){},cleanup:function(){},handleEvent:function(_4){}};Spring.CommandLinkAdvisor=function(){};Spring.CommandLinkAdvisor.prototype={targetElId:"",linkHtml:"",apply:function(){},submitFormFromLink:function(_5,_6,_7){}};Spring.RemoteEventAdvisor=function(){};Spring.RemoteEventAdvisor.prototype={event:"",targetId:"",sourceId:"",formId:"",processIds:"",renderIds:"",params:[],connection:null,apply:function(){},cleanup:function(){},submit:function(_8){}};Spring.RemotingHandler=function(){};Spring.RemotingHandler.prototype={submitForm:function(_9,_a,_b,_c,_d){},getResource:function(_e,_f,_10){},handleResponse:function(){},handleError:function(){}};