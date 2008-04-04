/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit.layout.LinkPane"]){dojo._hasResource["dijit.layout.LinkPane"]=true;dojo.provide("dijit.layout.LinkPane");dojo.require("dijit.layout.ContentPane");dojo.require("dijit._Templated");dojo.declare("dijit.layout.LinkPane",[dijit.layout.ContentPane,dijit._Templated],{templateString:"<div class=\"dijitLinkPane\"></div>",postCreate:function(){if(this.srcNodeRef){this.title+=this.srcNodeRef.innerHTML;}this.inherited("postCreate",arguments);}});}