package com.xlcatlin.wm.interceptor.mock.conditional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLResponseParser extends DefaultHandler {

	List<ConditionResponse> responseExpressions = new ArrayList<ConditionResponse>();
	String currentElement = null;
	String expression;
	String response;
	StringBuilder sb;
	String id;

	public List<ConditionResponse> parse(InputStream xml) throws SAXException, ParserConfigurationException, IOException {
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(xml, this);
		return responseExpressions;
	}

	public List<ConditionResponse> parse(String xml) throws SAXException, ParserConfigurationException, IOException {
		return parse(new ByteArrayInputStream(xml.getBytes()));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		currentElement = qName;
		sb = new StringBuilder();
		id = attributes.getValue("id");
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("expression".equals(qName)) {
			expression = sb.toString();
		}
		if ("response".equals(qName)) {
			responseExpressions.add(new ConditionResponse(id, sb.toString(), expression));
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if ("expression".equals(currentElement)) {
			sb.append(new String(ch, start, length).trim());
		} else if ("response".equals(currentElement)) {
			sb.append(new String(ch, start, length).trim());
		}
	}

}
