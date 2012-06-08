package org.springframework.faces.webflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextWrapper;

import junit.framework.TestCase;

import org.springframework.webflow.execution.RequestContext;
import org.springframework.webflow.execution.RequestContextHolder;
import org.springframework.webflow.execution.View;
import org.springframework.webflow.test.MockRequestContext;

public class FlowPartialViewContextTests extends TestCase {

	protected void tearDown() throws Exception {
		super.tearDown();
		RequestContextHolder.setRequestContext(null);
	}

	public void testReturnFragmentIds() throws Exception {
		String[] fragmentIds = new String[] { "foo", "bar" };

		RequestContext requestContext = new MockRequestContext();
		requestContext.getFlashScope().asMap().put(View.RENDER_FRAGMENTS_ATTRIBUTE, fragmentIds);
		RequestContextHolder.setRequestContext(requestContext);

		assertEquals(Arrays.asList(fragmentIds), new FlowPartialViewContext(null).getRenderIds());
	}

	public void testNoFragmentIds() throws Exception {
		final List<String> renderIds = Arrays.asList("foo", "bar");
		FlowPartialViewContext context = new FlowPartialViewContext(new PartialViewContextWrapper() {
			public Collection<String> getRenderIds() {
				return renderIds;
			}

			public void setPartialRequest(boolean isPartialRequest) {
			}

			public PartialViewContext getWrapped() {
				return null;
			}
		});

		RequestContextHolder.setRequestContext(new MockRequestContext());

		assertEquals(renderIds, context.getRenderIds());
	}

	public void testReturnFragmentIdsMutable() throws Exception {
		String[] fragmentIds = new String[] { "foo", "bar" };

		RequestContext requestContext = new MockRequestContext();
		requestContext.getFlashScope().asMap().put(View.RENDER_FRAGMENTS_ATTRIBUTE, fragmentIds);
		RequestContextHolder.setRequestContext(requestContext);

		Collection<String> renderIds = new FlowPartialViewContext(null).getRenderIds();
		renderIds.add("baz");

		assertEquals(Arrays.asList("foo", "bar", "baz"), renderIds);
	}

}
