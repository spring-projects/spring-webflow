/*
 * spring-webflow-samples
 *
 * birthdate - demonstrates Spring Web Flow Struts 1.1 or > integration
 * fileupload - demonstrates multipart file upload
 * flowlauncher - demonstrates the different ways to launch flows from web pages
 * itemlist - demonstrates application transactions and inline flows
 * numberguess - demonstrates how to play a game using a flow
 * phonebook - central sample demonstrating most features
 * phonebook-portlet - the phonebook sample in a portlet environment (notice how the flow definitions do not change)
 * sellitem - demonstrates a wizard with conditional transitions and continuations
 * sellitem-jsf - the sellitem sample in a jsf environment
 * shippingrate - demonstrates Spring Web Flow together with Ajax technology
 */

Sample pre-requisites:
----------------------
* JDK 1.5 or > must be installed with the JAVA_HOME variable set

* Ant 1.6 or > must be installed and in your system path

* Ivy 1.3 or >; if you already have Ivy installed into your ant lib path, it must be Ivy 1.3 or >.  Ivy 1.2 or < won't work.
  If you do not have Ivy installed, a compatible version will be picked up automatically from ../../common-build/lib

* A Servlet 2.4 and JSP 2.0-capable servlet container must be installed for sample app deployment
    - The samples all use jsp 2.0 to take advantage of EL ${expressions} for elegance.

To build all samples:
---------------------
1. cd to the ../build-spring-webflow directory

2. run 'ant dist' to produce deployable .war files for all samples
   Built .war files are placed in target/artifacts/war within each sample directory.

To build an individual sample:
---------------------
1. cd to the sample root directory

2. run 'ant dist' to produce a deployable .war file within target/artifacts/war