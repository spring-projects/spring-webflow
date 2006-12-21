Contained in this directory are the Spring Web Flow (SWF) related project sources.

DIRECTORIES

1. build-spring-webflow - Contains the build scripts needed to build all SWF projects.
                          To build all, simply execute the 'dist' ant target.

2. spring-binding - the data binding and mapping project, a Spring Web Flow driven internal library.

3. spring-webflow - The core Spring Web Flow project.

4. spring-webflow-samples - The Spring Web Flow sample applications, illustrating the framework in action.

ARCHITECTURE DOCUMENTS

Also contained in this directory are two SonarJ files
1. webflow-architecture.xml
2. webflow-workspace.xml

When opened from SonarJ these provide an architectural breakdown of the Spring Web Flow projects.
It is recommended that you view this breakdown to familiarize yourself with the Spring Web Flow system
architecture, including its layers, subsystems, dependencies, and various architectural metrics such
as total lines of code and average component dependency.

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