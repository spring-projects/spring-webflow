Contained in this directory are the Spring Web Flow (SWF) related project sources.

DIRECTORIES
-----------

1. build-spring-webflow - Contains the metadata needed to build the SWF projects using spring-build.

2. spring-build - A linked in SVN external to Spring's master build system. 
                  Used to power the build for all Spring projects.

3. spring-binding - the data binding and mapping project, a Spring Web Flow driven internal library.

4. spring-webflow - The Spring Web Flow project.  Contains the Web Flow engine.

5. spring-js - Server side support for Spring's JavaScript abstraction framework.

6. spring-js-resources - Client-side resources for Spring's JavaScript abstraction framework including the Dojo Toolkit.

7. spring-faces - Spring's JavaServerFaces integration module, which includes Web Flow & JavaScript support.

8. spring-webflow-samples - The Spring Web Flow sample applications, illustrating the framework in action.


BUILDING
--------

To build all projects, cd to build-spring-webflow and run the following with ant (version 1.7 or >):

ant clean clean-integration jar

To install all jars as local Maven dependencies:

ant clean clean-integration jar publish-maven-central-local

