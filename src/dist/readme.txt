SPRING WEB FLOW 2.4.0
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
Spring Web Flow 2.4 requires JDK 1.6 to run.

Release distribution contents:

"." contains the Web Flow distribution readme, license, and copyright notice
"docs" contains the Spring Web Flow reference manual and API Javadocs
"libs" contains the Web Flow distribution jar files
"schema" contains all Web Flow schema definitions

Spring Web Flow is released under the terms of the Apache Software License (see license.txt).

3. GETTING STARTED
------------------
Current samples:
https://github.com/SpringSource/spring-webflow-samples.

4. OBTAINING RELEASE JARS WITH MAVEN OR IVY
-------------------------------------------
Release versions of Spring Web Flow artifacts are available via Maven Central.

For milestones and snapshots only (and for Ivy dependencies) you'll need to use the
SpringSource repository. Add the following repository to your Maven pom.xml:

<repository>
    <id>springsource-repository</id>
    <name>Spring project releases, milestones, and snapshots</name>
    <url>http://repo.spring.io/snapshot</url>
</repository>

Then declare the following dependencies (note that many times it's sufficient
to include spring-webflow only, which will give you spring-binding and spring-js
via transitive dependencies):

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.binding</artifactId>
    <version>2.4.0.RC1</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.js</artifactId>
    <version>2.4.0.RC1</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.webflow</artifactId>
    <version>2.4.0.RC1</version>
</dependency> 

If using JSF all you need to include is:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.faces</artifactId>
    <version>2.4.0.RC1</version>
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

<dependency org="org.springframework.webflow" name="org.springframework.binding" rev="2.4.0.RC1" conf="compile->runtime" />
<dependency org="org.springframework.webflow" name="org.springframework.js" rev="2.4.0.RC1" conf="compile->runtime" />
<dependency org="org.springframework.webflow" name="org.springframework.webflow" rev="2.4.0.RC1" conf="compile->runtime" />

If using JavaServerFaces:

<dependency org="org.springframework.webflow" name="org.springframework.faces" rev="2.4.0.RC1" conf="compile->runtime" />

For more information see the reference documentation and the SpringSource Repository FAQ:
https://github.com/spring-projects/spring-framework/wiki/Spring-repository-FAQ
	
5. ADDITIONAL RESOURCES
-----------------------
The Spring Web Flow homepage is located at:
    http://projects.spring.io/spring-webflow/

The Spring Web Flow community forums are located at:
    http://forum.spring.io/

