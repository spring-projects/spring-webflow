package org.springframework.js.ajax.tiles2;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.tiles.context.TilesRequestContext;
import org.easymock.EasyMock;

public class AjaxTilesRequestContextTests extends TestCase {

	private TilesRequestContext targetContext;
	private AjaxTilesRequestContext ajaxContext;

	protected void setUp() throws Exception {
		targetContext = (TilesRequestContext) EasyMock.createMock(TilesRequestContext.class);
		ajaxContext = new AjaxTilesRequestContext(targetContext);
	}

	public void testGetHeader() {
		EasyMock.expect(targetContext.getHeader()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getHeader();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetHeaderValues() {
		EasyMock.expect(targetContext.getHeaderValues()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getHeaderValues();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetRequestScope() {
		EasyMock.expect(targetContext.getRequestScope()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getRequestScope();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetSessionScope() {
		EasyMock.expect(targetContext.getSessionScope()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getSessionScope();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testDispatch() throws IOException {
		targetContext.include(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.dispatch(null);
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testInclude() throws IOException {
		targetContext.include(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.include(null);
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetParam() {
		EasyMock.expect(targetContext.getParam()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getParam();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetParamValues() {
		EasyMock.expect(targetContext.getParamValues()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getParamValues();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetRequestLocale() {
		EasyMock.expect(targetContext.getRequestLocale()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getRequestLocale();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testIsUserInRole() {
		EasyMock.expect(Boolean.valueOf(targetContext.isUserInRole(null))).andReturn(Boolean.FALSE);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.isUserInRole(null);
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetRequest() {
		EasyMock.expect(targetContext.getRequest()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getRequest();
		EasyMock.verify(new Object[] { targetContext });
	}

	public void testGetResponse() {
		EasyMock.expect(targetContext.getResponse()).andReturn(null);
		EasyMock.replay(new Object[] { targetContext });
		ajaxContext.getResponse();
		EasyMock.verify(new Object[] { targetContext });
	}

}
