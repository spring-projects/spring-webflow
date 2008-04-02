Contained in this directory are the Spring Web Flow (SWF) related project sources.

DIRECTORIES

1. build-spring-webflow - Contains the metadata needed to build the SWF projects using spring-build.
                          To build all projects, simply run 'ant' (v1.7 or >) in this directory.

2. spring-build - A linked in SVN external to Spring's master build system. 
                  Used to power the build for all Spring projects.

3. spring-binding - the data binding and mapping project, a Spring Web Flow driven internal library.

4. spring-webflow - The Spring Web Flow project.  Contains the Web Flow engine.

5. spring-javscript - Spring's JavaScript abstraction framework, which includes Dojo integration.

6. spring-faces - Spring's JavaServerFaces integration module, which includes Web Flow & JavaScript support.

7. spring-webflow-samples - The Spring Web Flow sample applications, illustrating the framework in action.

ARCHITECTURE DOCUMENTS

Also contained in this directory are two SonarJ files
1. webflow-architecture.xml
2. webflow-workspace.xml

SonarJ is an architecture analysis tool.
When opened in SonarJ, these files provide an architectural breakdown of the Spring Web Flow projects.
This can be a good way to familiarize yourself with the Spring Web Flow system architecture,
including its layers, subsystems, dependencies, and various architectural metrics such as total lines of code
and average component dependency.

To use SonarJ:
1. Download it from http://www.hello2morrow.com/en/sonarj/sonarj.php
2. Install it
3. Launch it
4. Point to license key
5. Open workspace (webflow-workspace.xml)
6. Open architecture template (webflow-architecture.xml)
7. Right click on Web Flow Workspace root and click "Run all operations"
8. Click the various tabs to see different views
   - Architecture view is recommended
       - Layers sub-tab shows layer diagram
       - Subsytstem sub-tab shows breakdown by subsystem