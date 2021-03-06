package xdi2.core.impl.keyvalue;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class KeyValueContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -4967051993820678931L;

	private KeyValueStore keyValueStore;
	private String key;

	private XRI3SubSegment arcXri;

	public KeyValueContextNode(Graph graph, ContextNode contextNode, KeyValueStore keyValueStore, String key, XRI3SubSegment arcXri) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.arcXri = arcXri;
	}

	@Override
	public synchronized void clear() {

		if (this.isRootContextNode()) {

			this.keyValueStore.clear();
		} else {

			super.clear();
		}
	}

	public XRI3SubSegment getArcXri() {

		return this.arcXri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	public synchronized ContextNode createContextNode(XRI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();

		if (this.containsContextNode(arcXri)) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the context node " + arcXri + ".");

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(arcXri);

		this.keyValueStore.put(contextNodesKey, arcXri.toString());
		this.keyValueStore.delete(contextNodeKey + "/--C");
		this.keyValueStore.delete(contextNodeKey + "/--R");
		this.keyValueStore.delete(contextNodeKey + "/--L");

		KeyValueContextNode contextNode = new KeyValueContextNode(this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);

		return contextNode;
	}

	public Iterator<ContextNode> getContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		return new MappingIterator<String, ContextNode> (this.keyValueStore.getAll(contextNodesKey)) {

			@Override
			public ContextNode map(String item) {

				XRI3SubSegment arcXri = new XRI3SubSegment(item);
				String contextNodeKey = KeyValueContextNode.this.getContextNodeKey(arcXri);

				return new KeyValueContextNode(KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, contextNodeKey, arcXri);
			}
		};
	}

	@Override
	public ContextNode getContextNode(XRI3SubSegment arcXri) {

		if (! this.containsContextNode(arcXri)) return null;

		String contextNodeKey = this.getContextNodeKey(arcXri);

		return new KeyValueContextNode(this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);
	}

	@Override
	public boolean containsContextNode(XRI3SubSegment arcXri) {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey, arcXri.toString());
	}

	@Override
	public boolean containsContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey);
	}

	public synchronized void deleteContextNode(XRI3SubSegment arcXri) {

		String contextNodesKey = this.getContextNodesKey();

		this.keyValueStore.delete(contextNodesKey, arcXri.toString());
	}

	public synchronized void deleteContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		this.keyValueStore.delete(contextNodesKey);
	}

	@Override
	public synchronized int getContextNodeCount() {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.count(contextNodesKey);
	}

	/*
	 * Methods related to relations of this context node
	 */

	public synchronized Relation createRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		if (arcXri == null) throw new NullPointerException();
		if (relationXri == null) throw new NullPointerException();

		if (this.containsRelation(arcXri, relationXri)) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the relation " + arcXri + "/" + relationXri + ".");

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		this.keyValueStore.put(relationsKey, arcXri.toString());
		this.keyValueStore.put(relationKey, relationXri.toString());

		KeyValueRelation relation = new KeyValueRelation(this.getGraph(), this, this.keyValueStore, relationKey, arcXri, relationXri);

		return relation;
	}

	@Override
	public Relation getRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		if (! this.containsRelation(arcXri, relationXri)) return null;

		String relationKey = this.getRelationKey(arcXri);

		return new KeyValueRelation(this.getGraph(), this, this.keyValueStore, relationKey, arcXri, relationXri);
	}

	@Override
	public Relation getRelation(XRI3Segment arcXri) {

		if (! this.containsRelations(arcXri)) return null;

		String relationKey = this.getRelationKey(arcXri);

		XRI3Segment relationXri = new XRI3Segment(this.keyValueStore.getOne(relationKey));

		return new KeyValueRelation(this.getGraph(), this, this.keyValueStore, relationKey, arcXri, relationXri);
	}

	public Iterator<Relation> getRelations(final XRI3Segment arcXri) {

		if (! this.containsRelations(arcXri)) return new EmptyIterator<Relation> ();

		final String relationKey = this.getRelationKey(arcXri);

		return new MappingIterator<String, Relation> (this.keyValueStore.getAll(relationKey)) {

			@Override
			public Relation map(String relationXriString) {

				XRI3Segment relationXri = new XRI3Segment(relationXriString);

				return new KeyValueRelation(KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arcXri, relationXri);
			}
		};
	}

	public Iterator<Relation> getRelations() {

		String relationsKey = this.getRelationsKey();

		return new DescendingIterator<String, Relation> (this.keyValueStore.getAll(relationsKey)) {

			@Override
			public Iterator<Relation> descend(String item) {

				final XRI3Segment arcXri = new XRI3Segment(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(arcXri);

				return new MappingIterator<String, Relation> (KeyValueContextNode.this.keyValueStore.getAll(relationKey)) {

					public Relation map(String relationXriString) {

						return new KeyValueRelation(KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arcXri, new XRI3Segment(relationXriString));
					}
				};
			}
		};
	}

	@Override
	public boolean containsRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		return this.keyValueStore.contains(relationsKey, arcXri.toString()) && this.keyValueStore.contains(relationKey, relationXri.toString());
	}

	@Override
	public boolean containsRelations(XRI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		return this.keyValueStore.contains(relationsKey, arcXri.toString()) && this.keyValueStore.contains(relationKey);
	}

	@Override
	public boolean containsRelations() {

		String relationsKey = this.getRelationsKey();

		return this.keyValueStore.contains(relationsKey);
	}

	public synchronized void deleteRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		this.keyValueStore.delete(relationKey, relationXri.toString());

		if (! this.keyValueStore.contains(relationKey)) {

			this.keyValueStore.delete(relationsKey, arcXri.toString());
		}
	}

	public synchronized void deleteRelations(XRI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();

		this.keyValueStore.delete(relationsKey, arcXri.toString());
	}

	public synchronized void deleteRelations() {

		String relationsKey = this.getRelationsKey();

		this.keyValueStore.delete(relationsKey);
	}

	@Override
	public int getRelationCount() {

		String relationsKey = this.getRelationsKey();

		Iterator<Integer> mappingIterator = new MappingIterator<String, Integer> (this.keyValueStore.getAll(relationsKey)) {

			@Override
			public Integer map(String item) {

				final XRI3Segment arcXri = new XRI3Segment(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(arcXri);

				return Integer.valueOf(KeyValueContextNode.this.keyValueStore.count(relationKey));
			}
		};

		int sum = 0;
		while (mappingIterator.hasNext()) sum += mappingIterator.next().intValue();

		return sum;
	}

	@Override
	public int getRelationCount(XRI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		if (! this.keyValueStore.contains(relationsKey, arcXri.toString())) return 0;

		return this.keyValueStore.count(relationKey);
	}

	/*
	 * Methods related to literals of this context node
	 */

	public synchronized Literal createLiteral(String literalData) {

		if (literalData == null) throw new NullPointerException();

		if (this.containsLiteral()) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains a literal.");

		String literalKey = this.getLiteralKey();

		this.keyValueStore.put(literalKey, literalData.toString());

		KeyValueLiteral literal = new KeyValueLiteral(this.getGraph(), this, this.keyValueStore, literalKey, literalData);

		return literal;
	}

	@Override
	public Literal getLiteral() {

		if (! this.containsLiteral()) return null;

		String literalKey = this.getLiteralKey();

		return new KeyValueLiteral(this.getGraph(), this, this.keyValueStore, literalKey, null);
	}

	@Override
	public boolean containsLiteral() {

		String literalKey = this.getLiteralKey();

		return this.keyValueStore.contains(literalKey);
	}

	public synchronized void deleteLiteral() {

		String literalKey = this.getLiteralKey();

		this.keyValueStore.delete(literalKey);
	}

	/*
	 * Misc methods
	 */

	private String getContextNodesKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--C";
	}

	private String getContextNodeKey(XRI3SubSegment arcXri) {

		return (this.isRootContextNode() ? "" : this.key) + arcXri.toString();
	}

	private String getRelationsKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--R";
	}

	private String getRelationKey(XRI3Segment arcXri) {

		return (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();
	}

	private String getLiteralKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--L";
	}

	KeyValueStore getKeyValueStore() {

		return this.keyValueStore;
	}

	String getKey() {

		return this.key;
	}
}
