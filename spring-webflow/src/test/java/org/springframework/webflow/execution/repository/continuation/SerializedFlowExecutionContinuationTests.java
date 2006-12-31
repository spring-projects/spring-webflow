/*
 * Copyright 2004-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.webflow.execution.repository.continuation;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import junit.framework.TestCase;

import org.springframework.webflow.definition.FlowDefinition;
import org.springframework.webflow.engine.SimpleFlow;
import org.springframework.webflow.engine.impl.FlowExecutionImplFactory;
import org.springframework.webflow.execution.FlowExecution;
import org.springframework.webflow.test.MockExternalContext;

/**
 * Unit tests for {@link SerializedFlowExecutionContinuation}.
 * 
 * @author Keith Donald
 */
public class SerializedFlowExecutionContinuationTests extends TestCase {
	
	public void testCreate() throws Exception {
		FlowDefinition flow = new SimpleFlow();
		FlowExecution execution = new FlowExecutionImplFactory().createFlowExecution(flow);
		execution.start(null, new MockExternalContext());
		SerializedFlowExecutionContinuation c = new SerializedFlowExecutionContinuation(execution, true);
		assertTrue(c.isCompressed());
		byte[] array = c.toByteArray();
		execution = c.unmarshal();
		
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array));
        try {
            c = (SerializedFlowExecutionContinuation)ois.readObject();
            assertTrue(c.isCompressed());
            execution = c.unmarshal();
        }
        finally {
            ois.close();
        }
	}

}
