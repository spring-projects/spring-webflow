dependencies ={
	//Strip all console.* calls except console.warn and console.error. This is basically a work-around
	//for trac issue: http://bugs.dojotoolkit.org/ticket/6849 where Safari 3's console.debug seems
	//to be flaky to set up (apparently fixed in a webkit nightly).
	//But in general for a build, console.warn/error should be the only things to survive anyway.
	stripConsole: "normal",

    layers:  [
        {
		    name: "dojo.js",
		    dependencies: [
				"dijit.Dialog",
				"dijit.form.CurrencyTextBox",
				"dijit.form.DateTextBox"
		    ]
        },
        
        // Now add layers from Dojo's standard.profile.js

		{
			name: "../dijit/dijit.js",
			dependencies: [
				"dijit.dijit"
			]
		},
		{
			name: "../dijit/dijit-all.js",
			layerDependencies: [
				"../dijit/dijit.js"
			],
			dependencies: [
				"dijit.dijit-all"
			]
		}
    ],
    prefixes: [
        [ "dijit", "../dijit" ]
    ]
};
