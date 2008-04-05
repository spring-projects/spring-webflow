/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.dnd.common"]){dojo._hasResource["dojo.dnd.common"]=true;dojo.provide("dojo.dnd.common");dojo.dnd._copyKey=navigator.appVersion.indexOf("Macintosh")<0?"ctrlKey":"metaKey";dojo.dnd.getCopyKeyState=function(e){return e[dojo.dnd._copyKey];};dojo.dnd._uniqueId=0;dojo.dnd.getUniqueId=function(){var id;do{id=dojo._scopeName+"Unique"+(++dojo.dnd._uniqueId);}while(dojo.byId(id));return id;};dojo.dnd._empty={};dojo.dnd.isFormElement=function(e){var t=e.target;if(t.nodeType==3){t=t.parentNode;}return " button textarea input select option ".indexOf(" "+t.tagName.toLowerCase()+" ")>=0;};}