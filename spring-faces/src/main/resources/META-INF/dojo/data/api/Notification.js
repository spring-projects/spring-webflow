/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dojo.data.api.Notification"]){dojo._hasResource["dojo.data.api.Notification"]=true;dojo.provide("dojo.data.api.Notification");dojo.require("dojo.data.api.Read");dojo.declare("dojo.data.api.Notification",dojo.data.api.Read,{getFeatures:function(){return {"dojo.data.api.Read":true,"dojo.data.api.Notification":true};},onSet:function(_1,_2,_3,_4){throw new Error("Unimplemented API: dojo.data.api.Notification.onSet");},onNew:function(_5,_6){throw new Error("Unimplemented API: dojo.data.api.Notification.onNew");},onDelete:function(_7){throw new Error("Unimplemented API: dojo.data.api.Notification.onDelete");}});}