package xdi2.messaging.target.impl.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.AutoReader;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * An XDI messaging target backed by a file in one of the serialization formats.
 * 
 * @author markus
 */
public class FileMessagingTarget extends GraphMessagingTarget {

	private static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private String filename;
	private String format;

	public FileMessagingTarget() {

		super();

		this.filename = null;
		this.format = XDIReaderRegistry.getDefault().getFormat();
	}

	public void init() throws Exception {

	}

	public void shutdown() throws Exception {

	}

	public boolean execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.setGraph(this.readGraph());

		boolean handled = super.execute(messageEnvelope, messageResult, executionContext);

		this.writeGraph(this.getGraph());

		return handled;
	}

	private Graph readGraph() throws Xdi2MessagingException {

		XDIReader xdiReader = XDIReaderRegistry.forFormat(this.format);
		if (xdiReader == null) throw new Xdi2MessagingException("Cannot read this format: " + this.format);

		Graph graph = graphFactory.openGraph();

		FileReader reader = null;

		try {

			File file = new File(this.filename);
			reader = new FileReader(file);
			xdiReader.read(graph, reader, null);
			reader.close();
		} catch (FileNotFoundException ex) {

		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot read file: " + ex.getMessage(), ex);
		} finally {

			if (reader != null) {

				try {

					reader.close();
				} catch (Exception ex) { }
			}
		}

		if (xdiReader instanceof AutoReader) this.format = ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat();
		if (this.format == null) this.format = XDIWriterRegistry.getDefault().getFormat();

		return graph;
	}

	private void writeGraph(Graph graph) throws Xdi2MessagingException {

		XDIWriter xdiWriter = XDIWriterRegistry.forFormat(this.format);
		if (xdiWriter == null) throw new Xdi2MessagingException("Cannot write this format: " + this.format);

		FileWriter writer = null;

		try {

			File file = new File(this.filename);
			file.createNewFile();
			writer = new FileWriter(file);
			xdiWriter.write(graph, writer, null);
			writer.close();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot write file: " + ex.getMessage(), ex);
		} finally {

			try {

				if (writer != null) writer.close();
			} catch (Exception ex) { }
		}

		graph.close();
	}

	public String getFilename() {

		return this.filename;
	}

	public void setFilename(String filename) {

		this.filename = filename;
	}

	public String getFormat() {

		return this.format;
	}

	public void setFormat(String format) {

		this.format = format;
	}
}
