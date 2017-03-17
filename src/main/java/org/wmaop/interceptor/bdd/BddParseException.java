package org.wmaop.interceptor.bdd;

import javax.xml.bind.JAXBException;

public class BddParseException extends Exception {

	public BddParseException(String message, JAXBException e) {
		super(message, e);
	}

}
