This is where the master build that creates releases of Spring Web Flow resides.

USERS
- To build all Spring Web Flow related projects:

	1. From this directory, run:
		ant dist
        
Build Pre-requisites:
- javac 1.5 or > must be in your system path
- ant 1.6 or > must be in your system path
- ivy 1.3 or > (Note: a version of Ivy is included and will be used automatically if you do not already have
                Ivy installed in your ANT_HOME/lib directory.
                If you have Ivy already installed in %ANT_HOME%/lib make sure it is 1.3 or >.  1.2 won't work.)

DEVELOPERS
- To build a new Spring Web Flow product release:

  1. Update project.properties to reflect the new release version, if necessary.

  2. From this directory, run:
		ant release
		
     The release archive will be created and placed in:
     	target/release
     	                	
Questions? See http://forum.springframework.org.