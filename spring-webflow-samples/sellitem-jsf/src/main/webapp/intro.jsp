<%@ page session="true" %> <%-- make sure we have a session --%>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>

<HTML>
	<BODY>
	
<f:view>
	
		<DIV align="left">Sell Item - A Spring Web Flow Sample (JSF Version)</DIV>
		
		<HR>
		
		<DIV align="left">
			<P>
				<h:form>
				    <h:commandLink value="Sell Item" action="flowId:sellitem-flow"/>
				</h:form>
			</P>
			
			<P>
				This Spring web flow sample application implements the example application
				discussed in the article
				<A href="http://www-128.ibm.com/developerworks/java/library/j-contin.html">
				Use continuations to develop complex Web applications</A>. It illustrates
				the following concepts:
				<UL>
				    <LI>
				    	Spring Web Flow's JSF integration.
				    </LI>
					<LI>
						Using the flowId: command link prefix to let the view tell the web
						flow controller which flow needs to be started.
					</LI>
					<LI>
						Implementing a wizard using web flows.
					</LI>
					<LI>
						Using <A href="http://www.ognl.org/">OGNL</A> based conditional expressions.
					</LI>
				</UL>
			</P>
		</DIV>
		
		<HR>

		<DIV align="right"></DIV>
	</BODY>
	
</f:view>
	
</HTML>