<%@ page session="true" %> <%-- make sure we have a session --%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<HTML>
	<HEAD>
	</HEAD>
	<BODY>
	
		<DIV align="left">Item List - A Spring Web Flow Sample</DIV>
		
		<HR>
		
		<DIV align="left">
			<P>
				<A href="app/itemlist">Item List</A>
			</P>
			
			<P>
				This Spring web flow sample application illustrates several features:
				<UL>
					<LI>
						Launching flow's using bookmark-friendly, REST-style URLS
					</LI>
					<LI>
						Use of an inline-flow, including the ability to map subflow output attributes
						directly into collection attributes in parent flow scope.
					</LI>
					<LI>
						Event pattern matching, for matching eventId expressions to transitions.
					</LI>
					<LI>
						"Always redirect on pause" - to achieve the POST+REDIRECT+GET pattern with no special coding.
					</LI>
					<LI>
						Spring 1.2 compatible configuration, as an alternative to the Spring 2.0 support.
					</LI>
				</UL>
			</P>
		</DIV>
		
		<HR>

		<DIV align="right"></DIV>
		
	</BODY>
</HTML>
