/*
 * spring-webflow-samples
 *
 * booking-faces - The Spring Web Flow 2.x Hotel Booking Reference Application (Standalone Web Flow + JavaServerFaces version).
 * booking-mvc - The Spring Web Flow 2.x Hotel Booking Reference Application (Embedded Web Flow + Spring MVC version).
 */

Sample pre-requisites:
----------------------
* JDK 1.5 or > must be installed with the JAVA_HOME variable set

* Ant 1.7 or > must be installed and in your system path

To build all samples:
---------------------
1. cd to the ../build-spring-webflow directory

2. run 'ant' to produce deployable .war files for all samples
   Built .war files are placed in target/artifacts within each sample directory.

To build an individual sample:
---------------------
1. cd to the sample root directory

2. run 'ant dist' to produce a deployable .war file within target/artifacts