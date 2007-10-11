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
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.util.FileCopyUtils;
import org.springframework.webflow.execution.FlowExecution;

/**
 * A continuation implementation that is based on standard Java serialization, created by a
 * {@link SerializedFlowExecutionContinuationFactory}.
 * 
 * @see SerializedFlowExecutionContinuationFactory
 * 
 * @author Keith Donald
 * @author Erwin Vervaet
 */
class SerializedFlowExecutionContinuation extends FlowExecutionContinuation implements Externalizable {

	/**
	 * The serialized flow execution.
	 */
	private byte[] flowExecutionData;

	/**
	 * Whether or not the flow execution byte array is compressed.
	 */
	private boolean compressed;

	/**
	 * Default constructor necessary for {@link Externalizable} custom serialization semantics. Should not be called by
	 * application code.
	 */
	public SerializedFlowExecutionContinuation() {
	}

	/**
	 * Creates a new serialized flow execution continuation. This will marshall given flow execution into a serialized
	 * continuation form.
	 * @param flowExecution the flow execution
	 * @param compress whether or not the flow execution should be compressed
	 */
	public SerializedFlowExecutionContinuation(FlowExecution flowExecution, boolean compress)
			throws ContinuationCreationException {
		try {
			flowExecutionData = serialize(flowExecution);
			if (compress) {
				flowExecutionData = compress(flowExecutionData);
			}
		} catch (NotSerializableException e) {
			throw new ContinuationCreationException(flowExecution, "Could not serialize flow execution; "
					+ "make sure all objects stored in flow or flash scope are serializable", e);
		} catch (IOException e) {
			throw new ContinuationCreationException(flowExecution,
					"IOException thrown serializing flow execution -- this should not happen!", e);
		}
		this.compressed = compress;
	}

	/**
	 * Returns whether or not the flow execution data in this continuation is compressed.
	 */
	public boolean isCompressed() {
		return compressed;
	}

	public FlowExecution unmarshal() throws ContinuationUnmarshalException {
		try {
			return deserialize(getFlowExecutionData());
		} catch (IOException e) {
			throw new ContinuationUnmarshalException(
					"IOException thrown deserializing the flow execution stored in this continuation -- this should not happen!",
					e);
		} catch (ClassNotFoundException e) {
			throw new ContinuationUnmarshalException(
					"ClassNotFoundException thrown deserializing the flow execution stored in this continuation -- "
							+ "This should not happen! Make sure there are no classloader issues. "
							+ "For example, perhaps the Web Flow system is being loaded by a classloader "
							+ "that is a parent of the classloader loading application classes?", e);
		}
	}

	public byte[] toByteArray() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(flowExecutionData.length + 40);
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			try {
				oos.writeObject(this);
				oos.flush();
			} finally {
				oos.close();
			}
			return baos.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException();
		}
	}

	public boolean equals(Object o) {
		if (!(o instanceof SerializedFlowExecutionContinuation)) {
			return false;
		}
		SerializedFlowExecutionContinuation c = (SerializedFlowExecutionContinuation) o;
		return Arrays.equals(flowExecutionData, c.flowExecutionData);
	}

	public int hashCode() {
		int hashCode = 0;
		for (int i = 0; i < flowExecutionData.length; i++) {
			hashCode += flowExecutionData[i];
		}
		return hashCode;
	}

	// implementing Externalizable for custom serialization

	public void writeExternal(ObjectOutput out) throws IOException {
		// write out length first
		out.writeInt(flowExecutionData.length);
		// write out contents
		out.write(flowExecutionData);
		out.writeBoolean(compressed);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		// read length of data array
		int length = in.readInt();
		flowExecutionData = new byte[length];
		// read in contents in full
		in.readFully(flowExecutionData);
		compressed = in.readBoolean();
	}

	// internal helpers

	/**
	 * Return the flow execution data in its raw byte[] form. Will decompress if necessary.
	 * @return the byte array
	 * @throws IOException a problem occured with decompression
	 */
	protected byte[] getFlowExecutionData() throws IOException {
		if (isCompressed()) {
			return decompress(flowExecutionData);
		} else {
			return flowExecutionData;
		}
	}

	/**
	 * Internal helper method to serialize given flow execution. Override if a custom serialization method is used.
	 * @param flowExecution flow execution to serialize
	 * @return serialized flow flow execution data
	 * @throws IOException when something goes wrong during during serialization
	 */
	protected byte[] serialize(FlowExecution flowExecution) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		try {
			oos.writeObject(flowExecution);
			oos.flush();
			return baos.toByteArray();
		} finally {
			oos.close();
		}
	}

	/**
	 * Internal helper method to deserialize given flow execution data. Override if a custom serialization method is
	 * used.
	 * @param data serialized flow flow execution data
	 * @return deserialized flow execution
	 * @throws IOException when something goes wrong during deserialization
	 * @throws ClassNotFoundException when required classes cannot be loaded
	 */
	protected FlowExecution deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
		try {
			return (FlowExecution) ois.readObject();
		} finally {
			ois.close();
		}
	}

	/**
	 * Internal helper method to compress given flow execution data using GZIP compression. Override if custom
	 * compression is desired.
	 */
	protected byte[] compress(byte[] dataToCompress) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream gzipos = new GZIPOutputStream(baos);
		try {
			gzipos.write(dataToCompress);
			gzipos.flush();
		} finally {
			gzipos.close();
		}
		return baos.toByteArray();
	}

	/**
	 * Internal helper method to decompress given flow execution data using GZIP decompression. Override if custom
	 * decompression is desired.
	 */
	protected byte[] decompress(byte[] dataToDecompress) throws IOException {
		GZIPInputStream gzipin = new GZIPInputStream(new ByteArrayInputStream(dataToDecompress));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			FileCopyUtils.copy(gzipin, baos);
		} finally {
			gzipin.close();
		}
		return baos.toByteArray();
	}
}