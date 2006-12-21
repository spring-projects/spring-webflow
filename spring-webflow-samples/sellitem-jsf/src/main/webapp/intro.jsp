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
				<UL>
					<LI>
						Note on continuations: The original sellitem sample shows 
						continuations in use, in the words of the intro, "Using
						continuations to make the flow completely stable, no matter
						how browser navigation buttons are used."<br/>
						This JSF version of sellitem is currently set to use normal
						session storage.<br/>
						The JSF Web Flow integration does support the continuation storages.
						However, because JSF page components themselves have internal state,
						it is not enough for Web Flow to be using continuation storage, the
						JSF engine itself must be configured to use client-side or server-side
						continuation style storage for the component state, instead of the
						normal shared, Session based storage. We have not yet investigated
						how to set MyFaces or the JSR RI to use client side storage, but
						it is theoretically possible at least to the extent that the JSF
						specification talks about JSF implementations offering it as an
						<em>option</em>. We have seen no discussion of server-side continuation-
						style storage for JSF component state<br/>
						If you do configure your JSF engine for client-side storage of 
						component state, and set Web Flow to use client side continuation
						storage it does mean that pages can not trigger the auto-creation of
						flow variables on demand since the flow execution id needs to be known
						to the page, and the id _is_ the storage for the flow state, a classic
						chicken and egg situation. Just make sure any flow-scoped variables
						are created ahead of time in the flow, before any JSF page component
						tried to reference them.
					</LI>
				</UL>
			</P>
		</DIV>
		
		<HR>

		<DIV align="right"></DIV>
	</BODY>
	
</f:view>
	
</HTML>