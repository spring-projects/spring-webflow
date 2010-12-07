/*
 * Hotel Booking Reference App Versions
 * ------------------------------------
 * booking-mvc: Spring MVC + Web Flow + JSP 
 * booking-faces: Spring MVC + Web Flow + JavaServerFaces 
 * booking-portlet-mvc: Spring Portlet MVC + Web Flow + JSP version
 * booking-portlet-faces: Spring Portlet MVC + Web Flow + JavaServerFaces version
 * jsf-booking: Traditional JSF-based implementation; here to support comparison with "booking-faces"
 */

Sample pre-requisites:
----------------------
* JDK 1.5 or > must be installed and in your system path
* Ant 1.7 or > must be installed and in your system path

To build the samples:
---------------------
Running 'mvn package' from the this directory builds all samples.
You can find .war files under the appropriate '<sample>/target' directory.
Samples can be built individually by running 'mvn package' from the sample sub-directory.

To import the samples into Eclipse:
-----------------------------------
Fulfill the requirements below and then import all existing projects found under this directory:

1. Run 'mvn test' once from the command line in order to download required dependencies via Maven.

2. Define an M2_REPO classpath variable in your Eclipse preferences pointing to your local Maven 
repository. Note that this step can be skipped if using the SpringSource Tool Suite.

To deploy a project, right-click on it and select 'Run on Server'.
