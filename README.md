# Overview

Spring Web Flow facilitates building web applications that require guided navigation -- e.g. a shopping cart, flight check-in, a loan application, and many others. In contrast to stateless, free-form navigation such use cases have a clear start and end point, one or more screens to go through in a specific order, and a set of changes that are not finalized until the end point.

A distinguishing feature is the ability to define a **flow definition** consisting of *states*, *transitions*, and *data*. For example, view states correspond to the individual screens of the flow while transitions are caused by events resulting from the click of a button or a link. Data may be stored in scopes such as *flash*, *view*, *flow*, etc. and is cleared when it goes out of scope.

In REST terms a flow represents as a single resource. The same URL used to start the flow is also the URL used to step through the flow (there is also an execution key uniquely identifying the current flow instance). As a result of this approach navigation remains encapsulated in the flow definition.

Some benefits of using Spring Web Flow:

+ A flow abstraction to model *long conversations* in a web application
+ Proper encapsulation for navigation rules
+ Multiple scopes in which to keep data
+ Automatic use of the POST/REDIRECT/GET pattern avoids browser warnings
+ Impossible to return to completed flow session via browser back button
+ Rapid prototyping in dev mode with flow definition changes detected on the fly
+ IDE visualization of flow definition
+ Much more...

# Downloading artifacts

Instructions on [downloading Spring Web Flow](https://github.com/SpringSource/spring-webflow/wiki/Downloading-Spring-Web-Flow-Artifacts) artifacts via Maven and other build systems are available via the project wiki.

# Documentation

See the current [Javadoc](http://static.springsource.org/spring-webflow/docs/current/javadoc-api/) and [Reference](http://static.springsource.org/spring-webflow/docs/current/spring-webflow-reference/) docs.

# Samples

The `./spring-webflow-samples/` sub-directory contains several samples that can be built with Maven. More samples can be found in the spring-samples repository: [webflow-showcase](https://src.springframework.org/svn/spring-samples/webflow-showcase) and [webflow-primefaces-showcase](https://src.springframework.org/svn/spring-samples/webflow-primefaces-showcase).

# Issue Tracking

Spring Web Flow's JIRA issue tracker can be found [here](http://jira.springsource.org/browse/SWF). If you think you've found a bug, please consider submitting a reproduction project via the [spring-webflow-issues](https://github.com/springsource/spring-webflow-issues) repository. The readme provides simple step-by-step instructions.

# Getting support

Check out the [Spring forums](http://forum.springsource.org/forumdisplay.php?36-Web-Flow) and the [spring-webflow tag](http://stackoverflow.com/questions/tagged/spring-webflow) on StackOverflow. [Commercial support](http://springsource.com/support/springsupport) is available too.

# Building from source

Instructions on [building Spring Web Flow](https://github.com/SpringSource/spring-webflow/wiki/Building-From-Source) from source are available via the project wiki.

# Contributing

[Pull requests](http://help.github.com/send-pull-requests) are welcome; you'll be asked to sign our contributor license agreement ([CLA](https://support.springsource.com/spring_committer_signup)). Trivial changes like typo fixes are especially appreciated (just [fork and edit](https://github.com/blog/844-forking-with-the-edit-button)!). For larger changes, please search through JIRA for similiar issues, creating a new one if necessary, and discuss your ideas with the Spring Web Flow team.

# License

Spring Web Flow is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).



