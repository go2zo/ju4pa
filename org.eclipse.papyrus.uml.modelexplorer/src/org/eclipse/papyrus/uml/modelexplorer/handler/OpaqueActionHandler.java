/*
 * 
 */
package org.eclipse.papyrus.uml.modelexplorer.handler;

import org.eclipse.core.commands.IHandler;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;

/**
 * <pre>
 * Command handler for OpaqueAction creation
 * 
 * </pre>
 * 
 * @generated
 */
public class OpaqueActionHandler extends CreateCommandHandler implements IHandler {

	/**
	 * <pre>
	 * @see org.eclipse.papyrus.uml.service.creation.handler.CreateHandler#getElementTypeToCreate()
	 * 
	 * @return the IElementType this handler is supposed to create
	 * 
	 * </pre>
	 * 
	 * @generated
	 */
	protected IElementType getElementTypeToCreate() {
		return UMLElementTypes.OPAQUE_ACTION;
	}
}
