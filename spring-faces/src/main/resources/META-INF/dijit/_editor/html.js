/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._editor.html"]){dojo._hasResource["dijit._editor.html"]=true;dojo.provide("dijit._editor.html");dijit._editor.escapeXml=function(_1,_2){_1=_1.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");if(!_2){_1=_1.replace(/'/gm,"&#39;");}return _1;};dijit._editor.getNodeHtml=function(_3){var _4;switch(_3.nodeType){case 1:_4="<"+_3.nodeName.toLowerCase();var _5=[];if(dojo.isIE&&_3.outerHTML){var s=_3.outerHTML;s=s.substr(0,s.indexOf(">"));s=s.replace(/(['"])[^"']*\1/g,"");var _7=/([^\s=]+)=/g;var m,_9;while((m=_7.exec(s))){_9=m[1];if(_9.substr(0,3)!="_dj"){if(_9=="src"||_9=="href"){if(_3.getAttribute("_djrealurl")){_5.push([_9,_3.getAttribute("_djrealurl")]);continue;}}if(_9=="style"){_5.push([_9,_3.style.cssText.toLowerCase()]);}else{_5.push([_9,_9=="class"?_3.className:_3.getAttribute(_9)]);}}}}else{var _a,i=0,_c=_3.attributes;while((_a=_c[i++])){var n=_a.name;if(n.substr(0,3)!="_dj"){var v=_a.value;if(n=="src"||n=="href"){if(_3.getAttribute("_djrealurl")){v=_3.getAttribute("_djrealurl");}}_5.push([n,v]);}}}_5.sort(function(a,b){return a[0]<b[0]?-1:(a[0]==b[0]?0:1);});i=0;while((_a=_5[i++])){_4+=" "+_a[0]+"=\""+dijit._editor.escapeXml(_a[1],true)+"\"";}if(_3.childNodes.length){_4+=">"+dijit._editor.getChildrenHtml(_3)+"</"+_3.nodeName.toLowerCase()+">";}else{_4+=" />";}break;case 3:_4=dijit._editor.escapeXml(_3.nodeValue,true);break;case 8:_4="<!--"+dijit._editor.escapeXml(_3.nodeValue,true)+"-->";break;default:_4="Element not recognized - Type: "+_3.nodeType+" Name: "+_3.nodeName;}return _4;};dijit._editor.getChildrenHtml=function(dom){var out="";if(!dom){return out;}var _13=dom["childNodes"]||dom;var i=0;var _15;while((_15=_13[i++])){out+=dijit._editor.getNodeHtml(_15);}return out;};}