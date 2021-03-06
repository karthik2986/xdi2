package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.xri3.impl.XRI3Segment;

public class BasicRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -8757473050724884998L;

	private XRI3Segment arcXri;
	private XRI3Segment relationXri;

	public BasicRelation(Graph graph, ContextNode contextNode, XRI3Segment arcXri, XRI3Segment relationXri) {

		super(graph, contextNode);

		this.arcXri = arcXri;
		this.relationXri = relationXri;
	}

	public BasicRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		this(null, null, arcXri, relationXri);
	}

	@Override
	public XRI3Segment getArcXri() {

		return this.arcXri;
	}

	@Override
	public XRI3Segment getRelationXri() {

		return this.relationXri;
	}
}
