/*****************************************************************************
 * Copyright (c) 2012 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		
 *		CEA LIST - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.sysml.diagram.common.edit.policy;

import java.util.Arrays;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.emf.type.core.ISpecializationType;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.papyrus.gmf.diagram.common.edit.policy.CompartmentSemanticEditPolicy;
import org.eclipse.papyrus.infra.services.edit.commands.IConfigureCommandFactory;
import org.eclipse.papyrus.sysml.blocks.Block;
import org.eclipse.papyrus.sysml.diagram.common.commands.CreateActorPartWithTypeConfigureCommandFactory;
import org.eclipse.papyrus.sysml.diagram.common.commands.CreatePartWithTypeConfigureCommandFactory;
import org.eclipse.papyrus.sysml.diagram.common.commands.CreateReferenceWithTypeConfigureCommandFactory;
import org.eclipse.papyrus.sysml.diagram.common.commands.CreateValueWithTypeConfigureCommandFactory;
import org.eclipse.papyrus.sysml.service.types.element.SysMLElementTypes;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;

/**
 * Semantic edit policy for {@link Block} structure compartment.
 */
public class BlockPropertyStructureCompartmentSemanticEditPolicy extends CompartmentSemanticEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCreateCommand(CreateElementRequest req) {

		// Property (and variants) creation is allowed if the semantic element is 
		// a Property typed by a Block, the Property is created on this Block.
		EObject eObject = req.getContainer();
		if ((eObject != null) && (eObject instanceof Property)) {
			Type type = ((Property) eObject).getType();
			if ((type != null) && (((ISpecializationType)SysMLElementTypes.BLOCK).getMatcher().matches(type))) {
				
				if(SysMLElementTypes.PART_PROPERTY == req.getElementType()) {
					req.setContainer(type);
					req.setParameter(IConfigureCommandFactory.CONFIGURE_COMMAND_FACTORY_ID, new CreatePartWithTypeConfigureCommandFactory());
				}

				if(SysMLElementTypes.REFERENCE_PROPERTY == req.getElementType()) {
					req.setContainer(type);
					req.setParameter(IConfigureCommandFactory.CONFIGURE_COMMAND_FACTORY_ID, new CreateReferenceWithTypeConfigureCommandFactory());
				}

				if(SysMLElementTypes.ACTOR_PART_PROPERTY == req.getElementType()) {
					req.setContainer(type);
					req.setParameter(IConfigureCommandFactory.CONFIGURE_COMMAND_FACTORY_ID, new CreateActorPartWithTypeConfigureCommandFactory());
				}

				if(SysMLElementTypes.VALUE_PROPERTY == req.getElementType()) {
					req.setContainer(type);
					req.setParameter(IConfigureCommandFactory.CONFIGURE_COMMAND_FACTORY_ID, new CreateValueWithTypeConfigureCommandFactory());
				}
				
				if(UMLElementTypes.PROPERTY == req.getElementType()) {
					req.setContainer(type);
				}
				
			} else if ((UMLElementTypes.PROPERTY == req.getElementType()) || (Arrays.asList(req.getElementType().getAllSuperTypes()).contains(UMLElementTypes.PROPERTY))) { 
				// Forbid Property::qualifier creation
				return UnexecutableCommand.INSTANCE;
			}
		}
		
		return super.getCreateCommand(req);
	}
}
