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
Spring={};Spring.advisors=[];Spring.advisors.applied=false;Spring.applyAdvisors=function(){if(!Spring.advisors.applied){for(var x=0;x<Spring.advisors.length;x++){Spring.advisors[x].apply();}Spring.advisors.applied=true;}};Spring.validateAll=function(){var _2=true;for(x in Spring.advisors){if(Spring.advisors[x].decorator&&!Spring.advisors[x].validate()){_2=false;}}return _2;};Spring.validateRequired=function(){var _3=true;for(x in Spring.advisors){if(Spring.advisors[x].decorator&&Spring.advisors[x].isRequired()&&!Spring.advisors[x].validate()){_3=false;}}return _3;};Spring.ValidatingFieldAdvisor=function(){};Spring.ValidatingFieldAdvisor.prototype={targetElId:"",decoratorType:"",decorator:null,decoratorAttrs:"",apply:function(){},validate:function(){},isRequired:function(){}};Spring.ValidateAllAdvisor=function(){};Spring.ValidateAllAdvisor.prototype={event:"",targetId:"",connection:null,apply:function(){},cleanup:function(){},handleEvent:function(_4){}};Spring.CommandLinkAdvisor=function(){};Spring.CommandLinkAdvisor.prototype={targetElId:"",linkHtml:"",apply:function(){},submitFormFromLink:function(_5,_6,_7){}};Spring.RemoteEventAdvisor=function(){};Spring.RemoteEventAdvisor.prototype={event:"",targetId:"",sourceId:"",formId:"",processIds:"",renderIds:"",params:[],connection:null,apply:function(){},cleanup:function(){},submit:function(_8){}};Spring.RemotingHandler=function(){};Spring.RemotingHandler.prototype={submitForm:function(_9,_a,_b,_c,_d){},getResource:function(_e,_f,_10){},handleResponse:function(){},handleError:function(){}};