
Use the script in this directory to upgrade the version of Dojo bundled with spring-js.

Upgrade procedures:
 1) Open build.properties and modify the dojo.version property
 2) Run 'ant clean' from this directory 
 3) Run 'ant'
 4) Commit changes in build-spring-js/dojo-build directory

Notes:
Step 3) above creates a dojo-build.zip and copies it to build-spring-js/dojo-build.
The file dojo-build.zip is then used during the spring-js build to bundle Dojo.



