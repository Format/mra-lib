package com.xoba.util.xhtml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.xoba.mra.ILogger;
import com.xoba.mra.LogFactory;
import com.xoba.mra.MraUtils;
import com.xoba.util.rdf.RDFaEntityResolver;

public class XHtmlUtils {

	private static final ILogger logger = LogFactory.getDefault().create();

	public static class MYErrorHandler implements ErrorHandler {

		private boolean hasErrors;

		public boolean hasErrors() {
			return hasErrors;
		}

		private static void log(SAXParseException e) {
			if (e.getSystemId() == null) {
				logger.warnf("line %,d: %s", e.getLineNumber(), e.getMessage());
			} else {
				logger.warnf("%s line %,d: %s", e.getSystemId(),
						e.getLineNumber(), e.getMessage());
			}
		}

		public void warning(SAXParseException exception) throws SAXException {
			log(exception);
		}

		public void error(SAXParseException exception) throws SAXException {
			log(exception);
			hasErrors = true;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			log(exception);
			hasErrors = true;
		}

	}

	public static class XHTMLResolver implements EntityResolver {

		public XHTMLResolver() {

		}

		public XHTMLResolver(EntityResolver parent) {
			this.parent = parent;
		}

		private EntityResolver parent;

		public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException {
			if (publicId != null) {
				if (publicId.equals("-//W3C//DTD XHTML 1.0 Transitional//EN")) {
					return XHtmlUtils.getInputSource("xhtml1-transitional.dtd");
				} else if (publicId
						.equals("-//W3C//ENTITIES Latin 1 for XHTML//EN")) {
					return XHtmlUtils.getInputSource("xhtml-lat1.ent");
				} else if (publicId
						.equals("-//W3C//ENTITIES Symbols for XHTML//EN")) {
					return XHtmlUtils.getInputSource("xhtml-symbol.ent");
				} else if (publicId
						.equals("-//W3C//ENTITIES Special for XHTML//EN")) {
					return XHtmlUtils.getInputSource("xhtml-special.ent");
				}
			}
			if (parent != null) {
				return parent.resolveEntity(publicId, systemId);
			} else {
				logger.debugf("can't resolve %s %s", publicId, systemId);
				return null;
			}
		}
	}

	public static Document parseXhtml(URI u, boolean validating)
			throws Exception {
		return parseXhtml(u, new XHTMLResolver(), validating);
	}

	public static Document parseXhtml(URI u, EntityResolver er,
			boolean validating) throws Exception {
		DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
		fact.setValidating(validating);
		fact.setNamespaceAware(true);
		DocumentBuilder builder = fact.newDocumentBuilder();
		builder.setEntityResolver(er);
		builder.setErrorHandler(new MYErrorHandler());
		return builder.parse(u.toString());
	}

	public static boolean isValidHtml(byte[] html) {
		return isValidHtml(new InputSource(new ByteArrayInputStream(html)));
	}

	public static boolean isValidHtml(String html) {
		return isValidHtml(new InputSource(html));
	}

	public static boolean isValidHtml(InputSource html) {
		try {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setValidating(true);
			fact.setNamespaceAware(true);
			DocumentBuilder builder = fact.newDocumentBuilder();
			EntityResolver er = new XHTMLResolver(new RDFaEntityResolver());
			builder.setEntityResolver(er);
			MYErrorHandler h = new MYErrorHandler();
			builder.setErrorHandler(h);
			builder.parse(html);
			return !h.hasErrors();
		} catch (SAXParseException e) {
			logger.errorf("line %,d; can't validate %s: %s", e.getLineNumber(),
					html, e);
			return false;
		} catch (SAXException e) {
			logger.errorf("can't validate " + html + ": ", e);
			return false;
		} catch (Exception e) {
			logger.errorf("can't validate " + html + ": ", e);
			return false;
		}
	}

	public static boolean isValidHtml(URI html) {
		return isValidHtml(html.toString());
	}

	public static InputSource getInputSource(String name) throws IOException {
		URI uri = MraUtils.getResourceURI(XHtmlUtils.class.getPackage(), name);
		return new InputSource(uri.toString());
	}

}
