/*****************************************************************************
 * Copyright (c) 2009 Atos Origin.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Atos Origin - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.AlignmentRequest;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ResizableShapeEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateUnspecifiedTypeRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest;
import org.eclipse.gmf.runtime.diagram.ui.requests.RequestConstants;
import org.eclipse.gmf.runtime.draw2d.ui.figures.BaseSlidableAnchor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.emf.type.core.commands.SetValueCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.SetRequest;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.IdentityAnchor;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.osgi.util.NLS;
import org.eclipse.papyrus.infra.widgets.toolbox.notification.builders.NotificationBuilder;
import org.eclipse.papyrus.uml.diagram.common.commands.PreserveAnchorsPositionCommand;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ActionExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentCombinedFragmentCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.Messages;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLElementTypes;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.OperandBoundsComputeHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;

/**
 * The customn LayoutEditPolicy for InteractionOperandEditPart.
 */
public class InteractionOperandLayoutEditPolicy extends XYLayoutEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {
		EditPolicy result = super.createChildEditPolicy(child);
		if(result == null) {
			return new ResizableShapeEditPolicy();
		}
		return result;
	}

	/**
	 * apex update
	 * 
	 * 중첩된 child CombinedFragment 이동 시
	 * REQ_ORPHAN_CHILDREN에 대한 처리 로직 추가
	 * 
	 * Handle create InteractionOperand hover InteractionOperand {@inheritDoc}
	 */
	@Override
	public Command getCommand(Request request) {
		EditPart combinedFragmentCompartment = getHost().getParent();
		EditPart combinedFragment = combinedFragmentCompartment.getParent();
		EditPart interactionCompartment = combinedFragment.getParent();
		if(REQ_CREATE.equals(request.getType()) && request instanceof CreateUnspecifiedTypeRequest) {
			if(UMLElementTypes.InteractionOperand_3005.equals(((CreateUnspecifiedTypeRequest)request).getElementTypes().get(0))) {
				return combinedFragmentCompartment.getCommand(request);
			} else if(UMLElementTypes.CombinedFragment_3004.equals(((CreateUnspecifiedTypeRequest)request).getElementTypes().get(0))) {
				return interactionCompartment.getCommand(request);
			} else if(UMLElementTypes.Lifeline_3001.equals(((CreateUnspecifiedTypeRequest)request).getElementTypes().get(0))) {
				return interactionCompartment.getCommand(request);
			}
		} else if(request instanceof CreateConnectionViewAndElementRequest) {
			CreateConnectionRequest createConnectionRequest = (CreateConnectionRequest)request;
			if(getHost().equals(createConnectionRequest.getSourceEditPart())) {
				createConnectionRequest.setSourceEditPart(combinedFragment);
			}
			if(getHost().equals(createConnectionRequest.getTargetEditPart())) {
				createConnectionRequest.setTargetEditPart(combinedFragment);
			}
			return combinedFragment.getCommand(request);
		} else if (request instanceof CreateViewAndElementRequest  ) {
			//FIXME If necessary
			return null;
		}else if (REQ_RESIZE_CHILDREN.equals(request.getType())){
			return interactionCompartment.getCommand(request);
		} /* apex added start */
		else if (REQ_ORPHAN_CHILDREN.equals(request.getType())) {
			request.setType(REQ_MOVE_CHILDREN);
			return getResizeChildrenCommand((ChangeBoundsRequest)request);
		}
		/* apex added end */
		return super.getCommand(request);
	}

	/**
	 * Handle combined fragment resize
	 */
	@Override
	protected Command getResizeChildrenCommand(ChangeBoundsRequest request) {
		CompoundCommand compoundCmd = new CompoundCommand();
		compoundCmd.setLabel("Move or Resize");

		for(Object o : request.getEditParts()) {
			GraphicalEditPart child = (GraphicalEditPart)o;
			Object constraintFor = getConstraintFor(request, child);
			if(constraintFor != null) {
				if(child instanceof CombinedFragmentEditPart) {
					Command resizeChildrenCommand = InteractionCompartmentXYLayoutEditPolicy.getCombinedFragmentResizeChildrenCommand(request, (CombinedFragmentEditPart)child);
					if(resizeChildrenCommand != null && resizeChildrenCommand.canExecute()) {
						compoundCmd.add(resizeChildrenCommand);
					}
					/* apex added start */
					else compoundCmd.add(UnexecutableCommand.INSTANCE);
					/* apex added end */
				}

				Command changeConstraintCommand = createChangeConstraintCommand(request, child, translateToModelConstraint(constraintFor));
				compoundCmd.add(changeConstraintCommand);
			}
		}
		if(compoundCmd.isEmpty()) {
			return null;
		}
		return compoundCmd.unwrap();
	}

}
