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

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentCombinedFragmentCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.OperandBoundsComputeHelper;

/**
 * The customn DragDropEditPolicy for InteractionOperandEditPart.
 */
public class InteractionOperandDragDropEditPolicy extends ResizableEditPolicy {

	/**
	 * Disable drag and allow only south resize. {@inheritDoc}
	 */
	public InteractionOperandDragDropEditPolicy() {
		super();
		setDragAllowed(false);
	}

	/**
	 * Handle resize InteractionOperand {@inheritDoc}
	 */
	@Override
	protected Command getResizeCommand(ChangeBoundsRequest request) {
//		if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) {
//			EditPart parent = getHost().getParent().getParent();
//			return parent.getCommand(request);
//		} else{
			
			if (this.getHost() instanceof InteractionOperandEditPart
					&& this.getHost().getParent() instanceof CombinedFragmentCombinedFragmentCompartmentEditPart) {
				InteractionOperandEditPart currentIOEP = (InteractionOperandEditPart) this
						.getHost();
				CombinedFragmentCombinedFragmentCompartmentEditPart compartEP = (CombinedFragmentCombinedFragmentCompartmentEditPart) this
						.getHost().getParent();
				// if first interaction operand and resize direction is NORTH
				/* apex added start */
				// 첫번째 Op의 상단에서의 resize를 아래와 같이 따로 처리해주지 않으면
				// 상단에서 아래로 Op 축소 시 CF의 경계가 축소되고
				// 상단에서 위로 Op 확장 시 CF의 경계가 확장되는 원치 않는 결과가 나옴
				/* apex added end */
				if(this.getHost() == OperandBoundsComputeHelper.findFirstIOEP(compartEP)&&(request.getResizeDirection() & PositionConstants.NORTH) != 0){ 
					return getHost().getParent().getParent().getCommand(request);
				} else {
					/* apex improved start */
					int heightDelta = request.getSizeDelta().height();
					int widthDelta = request.getSizeDelta().width();
					int direction = request.getResizeDirection();

					return OperandBoundsComputeHelper.createIOEPResizeCommand(request, 
                                                                              currentIOEP,
                                                                              widthDelta,
                                                                              heightDelta,
                                                                              compartEP,
                                                                              direction);
				
					/* apex improved end */
					/* apex replaced
					int heightDelta = request.getSizeDelta().height();
					
					if ((request.getResizeDirection() & PositionConstants.NORTH) != 0) {
						return OperandBoundsComputeHelper.createIOEPResizeCommand(request, 
								                                                  currentIOEP,
								                                                  widthDelta,
								                                                  heightDelta,
								                                                  compartEP,PositionConstants.NORTH);
					} else if ((request.getResizeDirection() & PositionConstants.SOUTH) != 0) {
						return OperandBoundsComputeHelper.createIOEPResizeCommand(request, currentIOEP, heightDelta,
								compartEP,PositionConstants.SOUTH);
					}
					*/
				}
			}
			return null;
//		}
	}

}
