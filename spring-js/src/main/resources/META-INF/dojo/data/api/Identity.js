/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.data.api.Identity"]){dojo._hasResource["dojo.data.api.Identity"]=true;dojo.provide("dojo.data.api.Identity");dojo.require("dojo.data.api.Read");dojo.declare("dojo.data.api.Identity",dojo.data.api.Read,{getFeatures:function(){return {"dojo.data.api.Read":true,"dojo.data.api.Identity":true};},getIdentity:function(_1){throw new Error("Unimplemented API: dojo.data.api.Identity.getIdentity");var _2=null;return _2;},getIdentityAttributes:function(_3){throw new Error("Unimplemented API: dojo.data.api.Identity.getIdentityAttributes");return null;},fetchItemByIdentity:function(_4){if(!this.isItemLoaded(_4.item)){throw new Error("Unimplemented API: dojo.data.api.Identity.fetchItemByIdentity");}}});}