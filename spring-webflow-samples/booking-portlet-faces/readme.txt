
The current status of this sample as of June 2, 2010 is 'not working'. 

See notes below on combining JSF 1.2, Portlet 2.0, and Facelets.

JSF 2 is not yet supported by any jsf-portlet bridge implementation. 
The jsf-portlet bridges by Sun and Apache MyFaces support JSF 1.2.

Portlet 2.0 (required by Spring 3 and Spring Web Flow 2.1) is supported by Apache MyFaces only. 

Official Facelets support is missing from both implementations. While there are some 
suggestions on how to create a PortletFaceletsViewHandler on the Apache MyFaces user 
list (see the thread referenced in the JavaDoc of the PortletFaceletsViewHandler in 
this project), the suggested solution doesn't appear to be complete. 

