SPRING WEB FLOW ${version}
--------------------------
https://projects.spring.io/spring-webflow/

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
Spring Web Flow 2.5 requires JDK 1.8 and Spring Framework 5.0 to run.

Release distribution contents:

"." contains the Web Flow distribution readme, license, and copyright notice
"docs" contains the Spring Web Flow reference manual and API Javadocs
"libs" contains the Web Flow distribution jar files
"schema" contains all Web Flow schema definitions

Spring Web Flow is released under the terms of the Apache Software License (see license.txt).

3. GETTING STARTED
------------------
Current samples:
https://github.com/spring-projects/spring-webflow-samples.

4. OBTAINING RELEASE JARS THROUGH MAVEN REPOSITORY
--------------------------------------------------
Release versions of Spring Web Flow artifacts are available via Maven Central.

For milestones and snapshots only you'll need to use the Spring repository.
Add the following repository to your Maven pom.xml:

<repository>
    <id>spring</id>
    <name>Spring Snapshots</name>
    <url>https://repo.spring.io/snapshot</url>
</repository>

Then declare the following dependencies (note that many times it's sufficient
to include spring-webflow only, which will give you spring-binding
via transitive dependencies):

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.binding</artifactId>
    <version>${version}</version>
</dependency> 

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.webflow</artifactId>
    <version>${version}</version>
</dependency> 

If using JSF all you need to include is:

<dependency>
    <groupId>org.springframework.webflow</groupId>
    <artifactId>org.springframework.faces</artifactId>
    <version>${version}</version>
</dependency> 

For more details see the following Spring Framework wiki page:
https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Artifacts

