SPRING WEB FLOW 2.3.1
----------------------
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
Spring Web Flow 2.3.x requires Java SE 1.5 and Spring Framework 3.0.5 or above to run.

Java SE 5.0 with Ant 1.7 is also required to build.

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
"projects/spring-js" contains buildable Spring JavaScript project sources
"projects/spring-js-resources" bundles library containing client-side Ajax and Dojo integration
"projects/spring-webflow" contains buildable Spring Web Flow project sources
"projects/spring-webflow-samples" contains buildable Spring Web Flow sample application sources

See the readme.txt within the above directories for additional information.

Spring Web Flow is released under the terms of the Apache Software License (see license.txt).

3. DISTRIBUTION JAR FILES
-------------------------
The following jar files are included in the distribution.
The contents of each jar and its dependencies are noted.
Dependencies in [brackets] are optional, and are just necessary for certain functionality.

* org.springframework.webflow-2.3.1.RELEASE.jar
- Contents: The Spring Web Flow system
- Dependencies: Commons Logging, spring-core, spring-beans, spring-context, spring-expression, spring-binding, spring-web, spring-web-servlet, spring-js
                [Log4J, Xerces, XML APIs, OGNL, EL API, JPA API, Hibernate, Spring Security, Servlet API, Portlet API, JUnit]
              
* org.springframework.binding-2.3.1.RELEASE.jar
- Contents: The Spring Data Binding framework, a utility library used by Web Flow
- Dependencies: Commons Logging, spring-beans, spring-core, spring-context, spring-expression
                [Log4J, OGNL, EL API]

* org.springframework.js-2.3.1.RELEASE.jar
- Contents: The Spring JavaScript module
- Dependencies: spring-beans, spring-core, spring-context, spring-web, spring-web-servlet

* org.springframework.js.resources-2.3.1.RELEASE.jar
- Contents: Spring's custom Dojo build and client-side Dojo integration files.
- Dependencies: 

* org.springframework.faces-2.3.1.RELEASE.jar
- Contents: The Spring Faces module, containing Spring's integration with Java Server Faces (JSF) and additional JSF functionality.
- Dependencies: spring-webflow, spring-js, spring-js-resources, JSF API
                                
For an exact list of project dependencies, see each project's ivy file at "projects/${project_name}/ivy.xml".

4. GETTING STARTED
------------------
This distribution contains documentation and sample applications demonstrating the features of the Spring Web Flow projects.
A great way to get started is to review and run the sample applications, supplementing with reference manual material as you go.
To build deployable .war files for the samples, access the projects/spring-webflow-samples directory and run "mvn package".
Sample projects can be imported into an IDE as Maven projects or by generating IDE settings (e.g. "mvn eclipse:eclipse").

5. OBTAINING RELEASE JARS WITH MAVEN OR IVY
-------------------------------------------
Release versions of Spring Web Flow artifacts are available via Maven Central.

For milestones and snapshots only (and for Ivy dependencies) you'll need to use the
SpringSource repository. Add the following repository to your Maven pom.xml:

<repository>
    <id>spring-repository</id>
    <name>Spring project snapshots, milestones, and releases</name>
    <url>http://repo.springsource.org/snapshot</url>
</repository>

Then declare the following dependencies (note that many times it's sufficient
to include spring-webflow only, which will give you spring-binding and spring-js
via transitive dependencies):

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-binding</artifactId>
    <version>2.3.1.RELEASE</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-js</artifactId>
    <version>2.3.1.RELEASE</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-webflow</artifactId>
    <version>2.3.1.RELEASE</version>
</dependency> 

If using JSF all you need to include is:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>spring-faces</artifactId>
    <version>2.3.1.RELEASE</version>
</dependency> 

To access jars using Ivy, add the following repositories to your Ivy config:

<url name="spring-project-releases">
    <ivy pattern="http://repo.springsource.org/release/[organization]/[module]/[revision]/[type]s/ivy-[revision].xml" />
    <artifact pattern="http://repo.springsource.org/release/[organization]/[module]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]" />
</url>

<url name="spring-project-releases-milestones-snapshots">
    <ivy pattern="http://repo.springsource.org/snapshot/[organization]/[module]/[revision]/[type]s/ivy-[revision].xml" />
    <artifact pattern="http://repo.springsource.org/snapshot/[organization]/[module]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]" />
</url>

Then declare the following dependencies in your ivy.xml:

<dependency org="org.springframework.webflow" name="spring-binding" rev="2.3.1.RELEASE" conf="compile->runtime" />
<dependency org="org.springframework.webflow" name="spring-js" rev="2.3.1.RELEASE" conf="compile->runtime" />
<dependency org="org.springframework.webflow" name="spring-webflow" rev="2.3.1.RELEASE" conf="compile->runtime" />

If using JavaServerFaces:

<dependency org="org.springframework.webflow" name="spring-faces" rev="2.3.1.RELEASE" conf="compile->runtime" />

For more information see the SpringSource Repository FAQ:
https://github.com/SpringSource/spring-framework/wiki/SpringSource-repository-FAQ
	
6. ADDITIONAL RESOURCES
-----------------------
The Spring Web Flow homepage is located at:
    http://www.springsource.org/spring-web-flow

The Spring Web Flow community forums are located at:
    http://forum.springframework.org

