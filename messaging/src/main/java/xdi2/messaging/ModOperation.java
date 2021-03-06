package xdi2.messaging;

import xdi2.core.Relation;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * A $mod XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class ModOperation extends Operation {

	private static final long serialVersionUID = -5386430243720744523L;

	protected ModOperation(Message message, Relation relation) {

		super(message, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $mod operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI operation.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDIMessagingConstants.XRI_SS_MOD.equals(relation.getArcXri())) return false;
		if (! XDIMessagingConstants.XRI_SS_DO.equals(relation.getContextNode().getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param relation The relation that is an XDI $mod operation.
	 * @return The XDI $mod operation.
	 */
	public static ModOperation fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new ModOperation(null, relation);
	}
}
