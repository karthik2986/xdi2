package xdi2.core.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import xdi2.core.Graph;

public class XDIStatementsWriterWithContextStatements extends XDIStatementsWriter {

	private static final long serialVersionUID = -8401736205910361341L;

	public static final String FORMAT_NAME = "STATEMENTS_WITH_CONTEXT_STATEMENTS";
	public static final String MIME_TYPE = "text/plain";
	public static final String DEFAULT_FILE_EXTENSION = "xdi";

	@Override
	public Writer write(Graph graph, Writer writer, Properties parameters) throws IOException {

		if (parameters == null) parameters = new Properties();

		parameters.put(XDIStatementsWriter.PARAMETER_WRITE_CONTEXT_STATEMENTS, Boolean.TRUE.toString());

		return super.write(graph, writer, parameters);
	}
}
