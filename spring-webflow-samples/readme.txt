/*
 * spring-webflow-samples
 * ----------------------
 * getting-started-with-spring-webflow: Starter application template for Spring MVC and Web Flow
 * booking-mvc: Hotel Booking Reference App (Spring MVC + Web Flow + JSP version)
 * booking-faces: Hotel Booking Reference App (Spring MVC + Web Flow + JavaServerFaces version)
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
1. cd into the directory of the sample you are interested in building.

2. run 'ant jar' to build the sample.  The built .war file is placed in the 'target/artifacts' directory.

To import Web Flow projects into Eclipse:
-----------------------------------------
1. Use the SpringSource Tool Suite or Eclipse IDE for Java EE Developers
   Creating a dedicated workspace for importing Web Flow projects is recommended.

2. Use the 'File -> Import -> Existing Projects into Workspace' wizard to import the Web Flow projects into Eclipse.

3. The projects will not compile initially as Eclipse will not be able to find dependent libraries.
   To compile, define a IVY_CACHE classpath variable pointing to the local artifact repository created by the Web Flow build.
   To do this, access 'Preferences -> Java -> Build Path -> Classpath Variables'.
   Then select 'New...'; enter IVY_CACHE for the Name and select the 'projects/ivy-cache/repository' Folder as the path.

3. After the projects compile within Eclipse, run a sample web project on your local server by right-clicking on it and selecting 'Run on Server'.