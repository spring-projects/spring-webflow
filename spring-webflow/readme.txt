SPRING WEB FLOW 2.0-M4 (March 2008)
----------------------------------
http://www.springframework.org/webflow
http://forum.springframework.org

1. INTRODUCTION

Spring Web Flow (SWF) is a component of the Spring Framework's web stack focused on the definition
and execution of user interface (UI) flow within a web application.

The system allows you to capture a logical flow of your web application as a self-contained module
that can be reused in different situations. Such a flow guides a single user through the implementation
of a business task, and represents a single user conversation. Flows often execute across HTTP requests,
have state, exhibit transactional characteristics, and may be dynamic and/or long-running in nature.

Spring Web Flow exists at a higher level of abstraction, integrating as a self-contained flow engine
within base frameworks such as Spring MVC and JSF. SWF provides you the capability to capture your
application's UI flow explicitly in a declarative, portable, and manageable fashion.

SWF is a controller framework that addresses the "C" in MVC. 

2. RELEASE INFO

Spring Web Flow 2.0 requires J2SE 1.4 and J2EE 1.4 (Servlet 2.4) or > to run.

J2SE 5.0 with Ant 1.6 and Ivy 2.0 or > is required to build.  A compatible version of Ivy is shipped with this release.

SWF release contents:

"." contains Spring Web Flow distribution units (jars and source zip archives), readme, and copyright
"docs" contains the Spring Web Flow reference manual and API Javadocs
"ivys" contains Ivy dependency descriptors for the Spring Web Flow and Spring Data Binding projects
"projects" contains all buildable projects, including sample applications (each importable into Eclipse)
"projects/common-build" contains the Ant-based "common build system" used by all projects to compile/build/test
"projects/repository" contains Spring Web Flow dependencies (dependent jars)
"projects/spring-webflow/build-spring-webflow" contains the master build file used to build all Spring Web Flow projects
"projects/spring-webflow/spring-binding" contains buildable Spring Data Binding project sources, an internal library used by SWF
"projects/spring-webflow/spring-faces" contains buildable Spring Faces project sources, a library containing SWF's JSF integration
"projects/spring-webflow/spring-webflow" contains buildable Spring Web Flow project sources
"projects/spring-webflow/spring-webflow-samples" contains buildable Spring Web Flow sample application sources

See the readme.txt within the above directories for additional information.

Spring Web Flow is released under the terms of the Apache Software License (see license.txt).

3. DISTRIBUTION JAR FILES

The following distinct jar files are included in the distribution. This list
specifies the respective contents and third-party dependencies. Libraries in [brackets] are
optional, i.e. just necessary for certain functionality.

* spring-webflow-2.0-m4.jar
- Contents: The Spring Web Flow system
- Dependencies: Commons Logging, spring-core, spring-beans, spring-context, spring-binding, spring-web, spring-webmvc
                [Log4J, Xerces, XML APIs, OGNL, EL API, JPA API, Servlet API, Portlet API, Struts, JUnit]
              
* spring-binding-2.0-m4.jar
- Contents: The Spring Data Binding framework, an internal library used by SWF
- Dependencies: Commons Logging, spring-beans, spring-core, spring-context
                [Log4J]

* spring-faces-2.0-m4.jar
- Contents: The Spring Faces module, containing Spring Web Flow's integration with Java Server Faces (JSF) and additional JSF functionality.
- Dependencies: spring-webflow, JSF API
                                
For an exact list of Spring Web Flow project dependencies see "projects/spring-webflow/ivy.xml".

4. WHERE TO START

This distribution contains extensive documentation and sample applications illustrating the features of Spring Web Flow.

*** A great way to get started is to review and run the sample applications, supplementing with
reference manual material as needed.  To build deployable .war files for all samples, simply 
access the projects/spring-webflow/build-spring-webflow directory and execute the "dist" target.
Also, all projects are directly importable into Eclipse as "Dynamic Web Projects".
	
5. ADDITIONAL RESOURCES

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