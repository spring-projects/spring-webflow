SPRING WEB FLOW 2.0.3 (July 2008)
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
Spring Web Flow 2 requires Java SE 1.4 and Spring Framework 2.5.4 or above to run.

Java SE 5.0 with Ant 1.7 is required to build.

Release distribution contents:

"." contains the Web Flow distribution readme, license, changelog, and copyright
"dist" contains the Web Flow distribution jar files
"src" contains the Web Flow distribution source jar files
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

* org.springframework.webflow-2.0.3.RELEASE.jar
- Contents: The Spring Web Flow system
- Dependencies: Commons Logging, spring-core, spring-beans, spring-context, spring-binding, spring-web, spring-webmvc-servlet, spring-js
                [Log4J, Xerces, XML APIs, OGNL, EL API, JPA API, Hibernate, Spring Security, Servlet API, Portlet API, JUnit]
              
* org.springframework.binding-2.0.3.RELEASE.jar
- Contents: The Spring Data Binding framework, a utility library used by Web Flow
- Dependencies: Commons Logging, spring-beans, spring-core, spring-context
                [Log4J, OGNL, EL API]

* org.springframework.js-2.0.3.RELEASE.jar
- Contents: The Spring JavaScript module, containing Spring's Dojo integration and additional JavaScript functionality.
- Dependencies: Dojo Toolkit 1.1

* org.springframework.faces-2.0.3.RELEASE.jar
- Contents: The Spring Faces module, containing Spring's integration with Java Server Faces (JSF) and additional JSF functionality.
- Dependencies: spring-webflow, spring-js, JSF API
                                
For an exact list of project dependencies, see each project's ivy file at "projects/${project_name}/ivy.xml".

4. GETTING STARTED
------------------
This distribution contains documentation and sample applications demonstrating the features of the Spring Web Flow projects.
A great way to get started is to review and run the sample applications, supplementing with reference manual material as you go.
To build deployable .war files for all samples, simply access the projects/build-spring-webflow directory and run ant.
All projects are directly importable into Eclipse as "Dynamic Web Projects".
See http://www.springframework.org/webflow-samples for more information.

5. OBTAINING RELEASE JARS FROM THE SPRINGSOURCE BUNDLE REPOSITORY
-----------------------------------------------------------------
Each jar in the Web Flow distribution is available in the SpringSource release repository.
These jars may be accessed using Maven or Ivy dependency managers.
Browse the contents of the repository by accessing http://www.springsource.com/repository.

To access jars using Maven, add the following repositories to your Maven pom:

<repository>
    <id>com.springsource.repository.bundles.release</id>
    <name>SpringSource Enterprise Bundle Repository - SpringSource Releases</name>
    <url>http://repository.springsource.com/maven/bundles/release</url>
</repository>

<repository>
    <id>com.springsource.repository.bundles.external</id>
    <name>SpringSource Enterprise Bundle Repository - External Releases</name>
    <url>http://repository.springsource.com/maven/bundles/external</url>
</repository>

Then declare the following dependencies:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.binding</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.js</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.webflow</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency> 

If using JavaServerFaces:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.faces</artifactId>
    <version>2.0.3.RELEASE</version>
</dependency> 

To access jars using Ivy, add the following repositories to your Ivy config:

<url name="com.springsource.repository.bundles.release">
    <ivy pattern="http://repository.springsource.com/ivy/bundles/release/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
    <artifact pattern="http://repository.springsource.com/ivy/bundles/release/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
</url>

<url name="com.springsource.repository.bundles.external">
    <ivy pattern="http://repository.springsource.com/ivy/bundles/external/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
    <artifact pattern="http://repository.springsource.com/ivy/bundles/external/[organisation]/[module]/[revision]/[artifact]-[revision].[ext]" />
</url>

Then declare the following dependencies in your ivy.xml:

<dependency org="org.springframework.webflow" name="org.springframework.binding" rev="2.0.3.RELEASE" conf="compile->runtime" />
<dependency org="org.springframework.webflow" name="org.springframework.js" rev="2.0.3.RELEASE" conf="compile->runtime" />
<dependency org="org.springframework.webflow" name="org.springframework.webflow" rev="2.0.3.RELEASE" conf="compile->runtime" />

If using JavaServerFaces:

<dependency org="org.springframework.webflow" name="org.springframework.faces" rev="2.0.3.RELEASE" conf="compile->runtime" />

Refer to the reference documentation for more coverage on obtaining Web Flow jars using Maven or Ivy.
	
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

There you will find links to many resources on Spring Portfolio projects, including on-line access to Spring documentation.