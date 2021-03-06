package xdi2.core.impl.keyvalue;

import java.io.IOException;

import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates key/value graphs.
 * 
 * @author markus
 */
public abstract class AbstractKeyValueGraphFactory extends AbstractGraphFactory implements GraphFactory {

	private boolean supportGetContextNodes;
	private boolean supportGetRelations;

	public AbstractKeyValueGraphFactory(boolean supportGetContextNodes, boolean supportGetRelations) {

		this.supportGetContextNodes = supportGetContextNodes;
		this.supportGetRelations = supportGetRelations;
	}

	public final KeyValueGraph openGraph() throws IOException {

		KeyValueStore keyValueStore = this.openKeyValueStore();

		return new KeyValueGraph(keyValueStore, this.supportGetContextNodes, this.supportGetRelations);
	}

	protected abstract KeyValueStore openKeyValueStore() throws IOException;

	public boolean isSupportGetContextNodes() {

		return this.supportGetContextNodes;
	}

	public void setSupportGetContextNodes(boolean supportGetContextNodes) {

		this.supportGetContextNodes = supportGetContextNodes;
	}

	public boolean isSupportGetRelations() {

		return this.supportGetRelations;
	}

	public void setSupportGetRelations(boolean supportGetRelations) {

		this.supportGetRelations = supportGetRelations;
	}
}
