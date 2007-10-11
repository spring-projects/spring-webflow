package org.springframework.webflow.context;

import java.util.Arrays;

import org.springframework.util.Assert;

/**
 * A representation of a flow request path. In a flow definition request URI,the request path is the portion after the
 * flow definition identifier. For example, in a URI of http://host/booking-flow/1 the request path is "/1".
 * 
 * @author Keith Donald
 */
public class RequestPath {

	private String[] elements;

	public RequestPath(String path) {
		Assert.notNull(path, "The path string cannot be null");
		Assert.isTrue(path.startsWith("/"), "The path must start with a '/'");
		this.elements = path.substring(1).split("/");
	}

	private RequestPath(String[] elements) {
		this.elements = elements;
	}

	public RequestPath pop(int elementCount) {
		if (elementCount < elements.length) {
			String[] newElements = new String[elements.length - elementCount];
			System.arraycopy(elements, elementCount, newElements, 0, newElements.length);
			return new RequestPath(newElements);
		} else {
			return null;
		}
	}

	public RequestPath pop() {
		return pop(1);
	}

	public int getElementCount() {
		return elements.length;
	}

	public String getFirstElement() {
		return getElement(0);
	}

	/**
	 * Get the elements of the request path.
	 * @return parsed request path elements
	 */
	public String[] getElements() {
		return elements;
	}

	/**
	 * Gets the path element at the specified index.
	 * @param index the element index
	 * @return the element
	 * @throws IndexOutOfBoundsException if the index is out of bounds
	 */
	public String getElement(int index) throws IndexOutOfBoundsException {
		return elements[index];
	}

	public boolean equals(Object o) {
		if (!(o instanceof RequestPath)) {
			return false;
		}
		RequestPath other = (RequestPath) o;
		return Arrays.equals(this.elements, other.elements);
	}

	public int hashCode() {
		return Arrays.hashCode(elements);
	}

	public static RequestPath valueOf(String[] elements) {
		return new RequestPath(elements);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("/");
		for (int i = 0; i < elements.length; i++) {
			buffer.append(elements[i]);
			if (i < (elements.length - 1)) {
				buffer.append("/");
			}
		}
		return buffer.toString();
	}
}