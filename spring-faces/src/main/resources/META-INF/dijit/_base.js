/*
	Copyright (c) 2004-2008, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["dijit._base"]){dojo._hasResource["dijit._base"]=true;dojo.provide("dijit._base");dojo.require("dijit._base.focus");dojo.require("dijit._base.manager");dojo.require("dijit._base.place");dojo.require("dijit._base.popup");dojo.require("dijit._base.scroll");dojo.require("dijit._base.sniff");dojo.require("dijit._base.bidi");dojo.require("dijit._base.typematic");dojo.require("dijit._base.wai");dojo.require("dijit._base.window");if(dojo.isSafari){dojo.connect(window,"load",function(){window.resizeBy(1,0);setTimeout(function(){window.resizeBy(-1,0);},10);});}}