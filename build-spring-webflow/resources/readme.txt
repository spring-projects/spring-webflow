SPRING WEB FLOW 2.0 RC1 (April 2008)
----------------------------------
http://www.springframework.org/webflow
http://forum.springframework.org

1. INTRODUCTION
---------------
Spring Web Flow (SWF) is the module of Spring that allows you to implement the "flows" of your web application.

A flow is an application module that encapsulates a sequences of steps that can execute in different contexts.
Flows typically guides a single user through the execution of some business task.
They often execute across HTTP requests, have state, exhibit transactional characteristics, are reused, and may be dynamic and/or long-running in nature.

Spring Web Flow exists at a higher level of abstraction, and provides you a declarative flow definition language for authoring flows.
SWF also integrates as a self-contained flow engine within base web frameworks such as Spring MVC.  It allows different UI technologies to be integrated, including UI component technologies such as JavaServerFaces.

Spring Web Flow provides you the capability to capture reusable application UI flow in a declarative, portable, and manageable fashion.
It is a powerful controller engine that addresses the "C" in MVC.

2. RELEASE NOTES
----------------
Spring Web Flow 2.0 requires J2SE 1.4 and J2EE 1.4 (Servlet 2.4) or > to run.

J2SE 5.0 with Ant 1.7 is required to build.

SWF release contents:

"." contains Web Flow distribution readme and copyright
"dist" contains the Web Flow distribution jar files
"src" contains the Web FLow distribution source jar files
"docs" contains the Spring Web Flow reference manual and API Javadocs
"projects" contains all buildable projects, including sample applications (each importable into Eclipse)
"projects/build-spring-webflow" the directory to access to build the Web Flow distribution
"projects/spring-build" The master build system used by all Spring projects, including Spring Web Flow
"projects/spring-binding" contains buildable Spring Data Binding project sources, an internal library used by SWF
"projects/spring-faces" contains buildable Spring Faces project sources, a library containing SWF's JSF integration
"projects/spring-javascript" contains buildable Spring JavaScript project sources, a library containing SWF's Dojo integration
"projects/spring-webflow" contains buildable Spring Web Flow project sources
"projects/spring-webflow-samples" contains buildable Spring Web Flow sample application sources

See the readme.txt within the above directories for additional information.

Spring Web Flow is released under the terms of the Apache Software License (see license.txt).

3. DISTRIBUTION JAR FILES
-------------------------
The following distinct jar files are included in the distribution.
This list specifies the respective contents and third-party dependencies.
Libraries in [brackets] are optional, i.e. just necessary for certain functionality.

* spring-webflow-2.0.0-rc1.jar
- Contents: The Spring Web Flow system
- Dependencies: Commons Logging, spring-core, spring-beans, spring-context, spring-binding, spring-web, spring-webmvc
                [Log4J, Xerces, XML APIs, OGNL, EL API, JPA API, Servlet API, Portlet API, Struts, JUnit]
              
* spring-binding-2.0.0-rc1.jar
- Contents: The Spring Data Binding framework, an internal library used by SWF
- Dependencies: Commons Logging, spring-beans, spring-core, spring-context
                [Log4J]

* spring-javascript-2.0.0-rc1.jar
- Contents: The Spring JavaScript module, containing Spring Web Flow's integration with Dojo and additional JavaScript functionality.
- Dependencies: Dojo Toolkit 1.1

* spring-faces-2.0.0-rc1.jar
- Contents: The Spring Faces module, containing Spring Web Flow's integration with Java Server Faces (JSF) and additional JSF functionality.
- Dependencies: spring-webflow, spring-javascript, JSF API
                                
For an exact list of Spring Web Flow project dependencies see "projects/spring-webflow/ivy.xml".

4. WHERE TO START
-----------------
This distribution contains extensive documentation and sample applications illustrating the features of Spring Web Flow and its projects.

[**] A great way to get started is to review and run the sample applications, supplementing with reference manual material as you go.
To build deployable .war files for all samples, simply access the projects/build-spring-webflow directory and run ant.
Also, all projects are directly importable into Eclipse as "Dynamic Web Projects".
	
5. ADDITIONAL RESOURCES
-----------------------
The Spring Web Flow homepage is located at:

    http://www.springframework.org/webflow

There you will find resources such as a 'Quick Start' guide and a 'Frequently Asked Questions' section.

The Spring Web Flow support forums are located at:

    http://forum.springframework.org
	
There you will find an active community supporting the use of the product.

The Spring Community portal is located at:

    http://www.springframework.org

There you will find links to many resources related to the Spring Framework, including on-line access 
to Spring and Spring Web Flow documentation.