/*
 * spring-webflow-samples
 *
 * booking-faces - The Spring Web Flow 2.x Hotel Booking Reference Application (Standalone Web Flow + JavaServerFaces version).
 * booking-mvc - The Spring Web Flow 2.x Hotel Booking Reference Application (Embedded Web Flow + Spring MVC version).
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