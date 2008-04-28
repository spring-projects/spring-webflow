/*
 * spring-webflow-samples
 * ----------------------
 * booking-faces: Hotel Booking Reference App (Spring MVC + Web Flow + JavaServerFaces version)
 * booking-mvc: Hotel Booking Reference App (Spring MVC + Web Flow + JSP version)
 * booking-portlet-mvc: Hotel Booking Reference App (Spring Portlet MVC + Web Flow + JSP version)
 * booking-portlet-faces: Hotel Booking Reference App (Spring Portlet MVC + Web Flow + JavaServerFaces version)
 * jsf-booking: Traditional JSF-based implementation; here to support comparison with "booking-faces"
 */

Sample pre-requisites:
----------------------
* JDK 1.5 or > must be installed and in your system path
* Ant 1.7 or > must be installed and in your system path

To build the samples:
---------------------
1. cd to the ../build-spring-webflow directory

2. run 'ant' to build the Spring Web Flow projects and the main-line 'booking-faces' and 'booking-mvc' sample projects.
   Built .war files are placed in the 'target/artifacts' directory of each sample.

3. After running the master build, you may also cd to any sample directory and run 'ant jar' to build a sample manually.

To import Web Flow projects into Eclipse:
-----------------------------------------
1. Use the Eclipse IDE for Java EE developers, version 3.3 or above.
   Creating a dedicated workspace for importing Web Flow projects is recommended.

2. Use the 'File -> Import -> Existing Projects into Workspace' wizard to import the Web Flow projects into Eclipse.

3. The projects will not compile initially as Eclipse will not be able to find dependent libraries.
   To compile, define a IVY_CACHE classpath variable pointing to the local artifact repository created by the Web Flow build.
   To do this, access 'Preferences -> Java -> Build Path -> Classpath Variables'.
   Then select 'New...'; enter IVY_CACHE for the Name and select the 'projects/ivy-cache/repository' Folder as the path.

3. After the projects compile within Eclipse, run a sample web project on your local server by right-clicking on it and selecting 'Run on Server'.