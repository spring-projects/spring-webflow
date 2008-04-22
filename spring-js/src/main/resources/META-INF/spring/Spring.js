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
Spring={};Spring.decorations=[];Spring.decorations.applied=false;Spring.initialize=function(){Spring.applyDecorations();Spring.remoting=new Spring.RemotingHandler();};Spring.addDecoration=function(_1){Spring.decorations.push(_1);if(Spring.decorations.applied){_1.apply();}};Spring.applyDecorations=function(){if(!Spring.decorations.applied){for(var x=0;x<Spring.decorations.length;x++){Spring.decorations[x].apply();}Spring.decorations.applied=true;}};Spring.validateAll=function(){var _3=true;for(x in Spring.decorations){if(Spring.decorations[x].widget&&!Spring.decorations[x].validate()){_3=false;}}return _3;};Spring.validateRequired=function(){var _4=true;for(x in Spring.decorations){if(Spring.decorations[x].decorator&&Spring.decorations[x].isRequired()&&!Spring.decorations[x].validate()){_4=false;}}return _4;};Spring.AbstractElementDecoration=function(){};Spring.AbstractElementDecoration.prototype={elementId:"",widgetType:"",widgetModule:"",widget:null,widgetAttrs:{},apply:function(){},validate:function(){},isRequired:function(){}};Spring.AbstractValidateAllDecoration=function(){};Spring.AbstractValidateAllDecoration.prototype={event:"",elementId:"",apply:function(){},cleanup:function(){},handleEvent:function(_5){}};Spring.AbstractCommandLinkDecoration=function(){};Spring.AbstractCommandLinkDecoration.prototype={elementId:"",linkHtml:"",apply:function(){},submitFormFromLink:function(_6,_7,_8){}};Spring.AbstractAjaxEventDecoration=function(){};Spring.AbstractAjaxEventDecoration.prototype={event:"",elementId:"",sourceId:"",formId:"",params:{},apply:function(){},cleanup:function(){},submit:function(_9){}};Spring.AbstractRemotingHandler=function(){};Spring.AbstractRemotingHandler.prototype={submitForm:function(_a,_b,_c){},getLinkedResource:function(_d,_e,_f){},getResource:function(_10,_11,_12){},handleResponse:function(){},handleError:function(){}};