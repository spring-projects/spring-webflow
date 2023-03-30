# Overview

Spring Web Flow facilitates building web applications that require guided navigation -- 
e.g. a shopping cart, flight check-in, a loan application, and many others. 
In contrast to stateless, free-form navigation such use cases have a clear start and end
point, one or more screens to go through in a specific order, and a set of changes
that are not finalized to the end.

A distinguishing feature is the ability to define a **flow definition** consisting of
*states*, *transitions*, and *data*. For example, view states correspond to the
individual screens of the flow while transitions are caused by events resulting from
the click of a button or a link. Data may be stored in scopes such as
*flash*, *view*, *flow*, and others. Scoped data is cleared when it is no longer in scope.

In REST terms a flow represents as a single resource. 
The same URL used to start the flow is also the URL used to step through the flow
(there is also an execution key uniquely identifying the current flow instance).
As a result of this approach navigation remains encapsulated in the flow definition.

Some key benefits of using Spring Web Flow:

+ A flow abstraction to model *"long conversations"* in web applications
+ Proper encapsulation for navigation rules
+ Multiple scopes in which to keep data
+ Automatic use of the POST/REDIRECT/GET pattern to avoid browser warnings
+ Impossible to return to completed flow sessions via browser back button
+ Rapid prototyping of flow requirements
+ Development mode in which flow definition changes are detected on the fly
+ IDE visualization for flow definitions
+ Much more...

# Documentation

See the current 
[Javadoc](https://docs.spring.io/spring-webflow/docs/current/api/) and 
[Reference](https://docs.spring.io/spring-webflow/docs/current/reference/html/) docs.

# Samples

Samples can be found in the
[spring-webflow-samples](https://github.com/SpringSource/spring-webflow-samples) repository.

## Code of Conduct
This project adheres to the Contributor Covenant [code of conduct](CODE_OF_CONDUCT.adoc).
By participating, you  are expected to uphold this code.
Please report unacceptable behavior to spring-code-of-conduct@pivotal.io.

# Downloading artifacts

Download [instructions](https://github.com/SpringSource/spring-webflow/wiki/Downloading-Spring-Web-Flow-Artifacts)
for Spring Web Flow artifacts via Maven and other build systems are available via the project wiki.

# Getting support

Please, use the [spring-webflow](https://stackoverflow.com/questions/tagged/spring-webflow) tag on StackOverflow.
[Commercial support](https://tanzu.vmware.com/spring-runtime) is available too.

# Building from source

Check out sources:
````
git clone git://github.com/spring-projects/spring-webflow.git
````

Compile and test, build all jars, distribution zips and docs:
````
./gradlew build
````

Install into your local Maven repository:
````
./gradlew install
````

Generate Eclipse settings and then manually import projects:
````
./import-into-eclipse.sh
````

The Spring Framework and Spring Web Flow use a very similar build system.
For this reason the following
[Gradle build FAQ](https://github.com/SpringSource/spring-framework/wiki/Gradle-build-and-release-FAQ)
would be a very useful read.

# Contributing

[Pull requests](https://help.github.com/send-pull-requests) are welcome.
You'll be asked to sign our contributor license agreement ([CLA](https://support.springsource.com/spring_committer_signup)).
Trivial changes like typo fixes are especially appreciated
(just [fork and edit](https://github.com/blog/844-forking-with-the-edit-button)!).
For larger changes, please search through JIRA for similiar issues,
creating a new one if necessary, and discuss your ideas with the Spring Web Flow team.

# License

Spring Web Flow is released under version 2.0 of the
[Apache License](https://www.apache.org/licenses/LICENSE-2.0).



