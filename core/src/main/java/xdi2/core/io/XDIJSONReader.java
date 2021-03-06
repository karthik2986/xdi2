package xdi2.core.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.parser.ParserException;

public class XDIJSONReader extends AbstractXDIReader {

	private static final long serialVersionUID = 1450041480967749122L;

	public static final String FORMAT_NAME = "XDI/JSON";
	public static final String MIME_TYPE = "application/xdi+json";
	public static final String DEFAULT_FILE_EXTENSION = "json";

	private static final Logger log = LoggerFactory.getLogger(XDIJSONReader.class);

	private String lastXriString;

	private synchronized XRI3Segment makeXRI3Segment(String xriString) {

		this.lastXriString = xriString;
		return new XRI3Segment(xriString);
	}

	private synchronized XRI3SubSegment makeXRI3SubSegment(String xriString) {

		this.lastXriString = xriString;
		return new XRI3SubSegment(xriString);
	}

	private synchronized void readContextNode(ContextNode contextNode, JSONObject graphObject) throws IOException, Xdi2ParseException, JSONException {

		String contextNodeXri = contextNode.getXri().toString();

		String key = contextNodeXri + "/()";
		if (! graphObject.has(key)) return;
		JSONArray jsonArray = graphObject.getJSONArray(key);
		graphObject.remove(key);

		for (int i=0; i<jsonArray.length(); i++) {

			String arcXri = jsonArray.getString(i);
			ContextNode innerContextNode = contextNode.createContextNode(this.makeXRI3SubSegment(arcXri));
			if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created context node " + innerContextNode.getArcXri() + " --> " + innerContextNode.getXri());

			this.readContextNode(innerContextNode, graphObject);
		}
	}

	public synchronized void read(Graph graph, JSONObject graphObject) throws IOException, Xdi2ParseException, JSONException {

		this.readContextNode(graph.getRootContextNode(), graphObject);

		for (Iterator<?> keys = graphObject.keys(); keys.hasNext(); ) {

			String key = (String) keys.next();
			JSONArray value = graphObject.getJSONArray(key);

			String[] strings = key.split("/");
			if (strings.length != 2) throw new Xdi2ParseException("Invalid key: " + key);

			String subject = strings[0];
			String predicate = strings[1];
			ContextNode contextNode = graph.findContextNode(makeXRI3Segment(subject), true);

			if (predicate.equals("!")) {

				if (value.length() != 1) throw new Xdi2ParseException("JSON array for key " + key + " must have exactly one item");

				String literalData = value.getString(0);

				Literal literal = contextNode.createLiteral(literalData);
				if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created literal --> " + literal.getLiteralData());
			} else {

				XRI3Segment arcXri = this.makeXRI3Segment(predicate);

				for (int i=0; i<value.length(); i++) {

					XRI3Segment relationXri = this.makeXRI3Segment(value.getString(i));

					Relation relation = contextNode.createRelation(arcXri, relationXri);
					if (log.isDebugEnabled()) log.debug("Under " + contextNode.getXri() + ": Created relation " + relation.getArcXri() + " --> " + relation.getRelationXri());
				}
			}
		}
	}

	private synchronized void read(Graph graph, BufferedReader bufferedReader) throws IOException, Xdi2ParseException, JSONException {

		String line;
		StringBuffer graphString = new StringBuffer();

		while ((line = bufferedReader.readLine()) != null) {

			graphString.append(line + "\n");
		}

		this.read(graph, new JSONObject(graphString.toString()));
	}

	public synchronized Reader read(Graph graph, Reader reader, Properties parameters) throws IOException, Xdi2ParseException {

		this.lastXriString = null;

		try {

			this.read(graph, new BufferedReader(reader));
		} catch (JSONException ex) {

			throw new Xdi2ParseException("JSON parse error: " + ex.getMessage(), ex);
		} catch (Xdi2GraphException ex) {

			throw new Xdi2ParseException("Graph problem: " + ex.getMessage(), ex);
		} catch (ParserException ex) {

			throw new Xdi2ParseException("Cannot parse XRI " + this.lastXriString + ": " + ex.getMessage(), ex);
		}

		return reader;
	}
}
