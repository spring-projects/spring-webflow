This is where the master build that creates releases of Spring Web Flow resides.  The build system is based on spring-build, which is linked in using an SVN external to https://src.springframework.org/svn/spring-build.

USERS
- To build all Spring Web Flow projects:

    1. From this directory, run:
       ant
        
Build Pre-requisites:
- javac 1.5 or > must be in your system path
- ant 1.7 or > must be in your system path

DEVELOPERS
- To build a new Spring Web Flow distribution for release:

  1. Update the files containing the version number to reflect the new release version, if necessary.
	
         build.properties
         build-spring-webflow/resources/readme.txt
         spring-webflow-reference/src/spring-webflow-reference.xml

  2. From this directory, run:
	
         ant jar package
		
     The release archive will be created and placed in:
         target/artifacts
                	
Questions? See http://forum.springframework.org