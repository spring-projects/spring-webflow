SPRING WEB FLOW 2.0.0 (April 2008)
----------------------------------
http://www.springframework.org/webflow

1. INTRODUCTION
---------------
Spring Web Flow (SWF) is the module of Spring that allows you to implement the "flows" of your web application.

A flow is an application module that encapsulates a sequences of steps that can execute in different contexts.
Flows typically guide a single user through the execution of some business task.
They often execute across HTTP requests, have state, exhibit transactional characteristics, are reused, and may be dynamic and long-running in nature.
Good examples of flows include a trip booking process, a loan application process, and an insurance quoter.

Spring Web Flow exists at a higher level of abstraction, providing you a declarative flow definition language for authoring flows.
Web Flow also integrates as a self-contained flow engine within base web frameworks such as Spring MVC.
It allows different UI technologies to be integrated, including UI component technologies such as JavaServerFaces.

Spring Web Flow lets you capture reusable application UI flow in a declarative, portable, and manageable fashion.
It is a powerful controller engine that addresses the "C" in MVC.

2. RELEASE NOTES
----------------
Spring Web Flow 2.0.0 requires Java SE 1.4 and Spring Framework 2.5.4 or above to run.

Java SE 5.0 with Ant 1.7 is required to build.

Release distribution contents:

"." contains the Web Flow distribution readme and copyright
"dist" contains the Web Flow distribution jar files
"src" contains the Web FLow distribution source jar files
"docs" contains the Spring Web Flow reference manual and API Javadocs
"projects" contains all buildable projects, including sample applications
"projects/build-spring-webflow" is the directory to access to build the Web Flow distribution
"projects/spring-build" is the master build system used by all Spring projects, including Spring Web Flow
"projects/spring-binding" contains buildable Spring Data Binding project sources, a utility library used by SWF
"projects/spring-faces" contains buildable Spring Faces project sources, a library containing SWF's JSF integration
"projects/spring-js" contains buildable Spring JavaScript project sources, a library containing client-side Ajax and Dojo integration
"projects/spring-webflow" contains buildable Spring Web Flow project sources
"projects/spring-webflow-samples" contains buildable Spring Web Flow sample application sources

See the readme.txt within the above directories for additional information.

Spring Web Flow is released under the terms of the Apache Software License (see license.txt).

3. DISTRIBUTION JAR FILES
-------------------------
The following jar files are included in the distribution.
The contents of each jar and its dependencies are noted.
Dependencies in [brackets] are optional, and are just necessary for certain functionality.

* spring-webflow-2.0.0.jar
- Contents: The Spring Web Flow system
- Dependencies: Commons Logging, spring-core, spring-beans, spring-context, spring-binding, spring-web, spring-webmvc, spring-js
                [Log4J, Xerces, XML APIs, OGNL, EL API, JPA API, Servlet API, Portlet API, Struts, JUnit]
              
* spring-binding-2.0.0.jar
- Contents: The Spring Data Binding framework, a utility library used by Web Flow
- Dependencies: Commons Logging, spring-beans, spring-core, spring-context
                [Log4J]

* spring-js-2.0.0.jar
- Contents: The Spring JavaScript module, containing Spring's Dojo integration and additional JavaScript functionality.
- Dependencies: Dojo Toolkit 1.1

* spring-faces-2.0.0.jar
- Contents: The Spring Faces module, containing Spring's integration with Java Server Faces (JSF) and additional JSF functionality.
- Dependencies: spring-webflow, spring-javascript, JSF API
                                
For an exact list of project dependencies, see each project's ivy file at "projects/${project_name}/ivy.xml".

4. GETTING STARTED
------------------
This distribution contains documentation and sample applications demonstrating the features of the Spring Web Flow projects.
A great way to get started is to review and run the sample applications, supplementing with reference manual material as you go.
To build deployable .war files for all samples, simply access the projects/build-spring-webflow directory and run ant.
All projects are directly importable into Eclipse as "Dynamic Web Projects".
See projects/spring-webflow-samples/readme.txt for more information.

5. OBTAINING RELEASE JARS USING MAVEN OR IVY
--------------------------------------------
Each jar in the Web Flow distribution is available in the SpringSource release repository.
These jars may be accessed using Maven or Ivy dependency managers.

To access jars using Maven, add the following repositories to your Maven pom:

<repository>
    <id>springsource-release</id>
    <name>SpringSource Release Artifact Repository</name>
    <url>http://repository.springsource.com/maven/release</url>
</repository>

<repository>
    <id>springsource-external</id>
    <name>SpringSource External Artifact Repository</name>
    <url>http://repository.springsource.com/maven/external</url>
</repository>

Then declare the following dependencies:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-binding</artifactId>
    <version>2.0.0</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-js</artifactId>
    <version>2.0.0</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-webflow</artifactId>
    <version>2.0.0</version>
</dependency> 

If using JavaServerFaces:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-faces</artifactId>
    <version>2.0.0</version>
</dependency> 

To access jars using Ivy, add the following repositories to your Ivy config:

<url name="springsource-release">
    <ivy pattern="http://repository.springsource.com/ivy/release/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
    <artifact pattern="http://repository.springsource.com/ivy/release/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
</url>

<url name="springsource-external">
    <ivy pattern="http://repository.springsource.com/ivy/external/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
    <artifact pattern="http://repository.springsource.com/ivy/external/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
</url>

Then declare the following dependencies in your ivy.xml:

<dependency org="org.springframework.webflow" name="spring-binding" rev="2.0.0" conf="compile->compile" />
<dependency org="org.springframework.webflow" name="spring-js" rev="2.0.0" conf="compile->compile" />
<dependency org="org.springframework.webflow" name="spring-webflow" rev="2.0.0" conf="compile->compile" />

If using JavaServerFaces:

<dependency org="org.springframework.webflow" name="spring-faces" rev="2.0.0" conf="compile->compile" />

Refer to the reference documentation for more in-depth coverage on obtaining Web Flow jars using Maven or Ivy.
	
6. ADDITIONAL RESOURCES
-----------------------
The Spring Web Flow homepage is located at:

    http://www.springframework.org/webflow

There you will find resources such as a 'Quick Start' guide and a 'Frequently Asked Questions' section.

The Spring Web Flow community forums are located at:

    http://forum.springframework.org
	
There you will find an active community of users collaborating about the project.

The Spring Community portal is located at:

    http://www.springframework.org

There you will find links to many resources on Spring Portfolio projects,
including on-line access to Spring and Spring Web Flow documentation.