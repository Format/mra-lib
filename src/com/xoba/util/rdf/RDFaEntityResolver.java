package com.xoba.util.rdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.xoba.mra.ILogger;
import com.xoba.mra.LogFactory;
import com.xoba.mra.MraUtils;

public class RDFaEntityResolver implements EntityResolver {

	private static final ILogger logger = LogFactory.getDefault().create();

	private final Map<String, UUID> map = new HashMap<String, UUID>();
	private final Map<String, String> map2 = new HashMap<String, String>();

	public RDFaEntityResolver() throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(
				MraUtils.getResourceAsString(
						RDFaEntityResolver.class.getPackage(), "manifest.txt")));
		try {
			boolean done = false;
			while (!done) {
				String line = reader.readLine();
				if (line == null) {
					done = true;
				} else {
					UUID uuid = UUID.fromString(line);
					String publicID = reader.readLine();
					String systemID = reader.readLine();
					map.put(publicID, uuid);
					map2.put(publicID, systemID);
				}
			}
		} finally {
			reader.close();
		}
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		if (map.containsKey(publicId)) {
			UUID u = map.get(publicId);
			URI uri = MraUtils.getResourceURI(
					RDFaEntityResolver.class.getPackage(), u.toString());
			return new InputSource(uri.toString());
		}
		logger.debugf("can't resolve %s %s", publicId, systemId);
		return null;
	}

}
