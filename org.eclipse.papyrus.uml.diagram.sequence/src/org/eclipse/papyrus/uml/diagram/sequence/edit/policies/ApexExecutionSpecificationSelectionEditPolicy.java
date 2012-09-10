package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ResizableShapeEditPolicy;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceRequestConstants;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;

public class ApexExecutionSpecificationSelectionEditPolicy extends
		ResizableShapeEditPolicy {

	public ApexExecutionSpecificationSelectionEditPolicy() {
		setResizeDirections(PositionConstants.NORTH_SOUTH);
	}

	/**
	 * apex updated
	 */
	@Override
	protected List createSelectionHandles() {
		/* apex improved start */
		List<Handle> list = new ArrayList<Handle>();
		createMoveHandle(list);
		createResizeHandle(list, PositionConstants.NORTH);
		createResizeHandle(list, PositionConstants.SOUTH);
		return list;
		/* apex improved end */
		/* apex replaced
		return super.createSelectionHandles();
		 */
	}

	/**
	 * apex updated
	 */
	@Override
	protected void showChangeBoundsFeedback(ChangeBoundsRequest request) {
		/* apex added start */
		if (getHost() instanceof AbstractExecutionSpecificationEditPart) {
			// north로 resize할 경우에도 south 방향으로 resize되도록 변경 해주는 기능
			request.getMoveDelta().y = 0;
		}
		/* apex added end */
		super.showChangeBoundsFeedback(request);
	}

	@Override
	protected Command getMoveCommand(ChangeBoundsRequest request) {
//		if (request.getMoveDelta() != null && request.getMoveDelta().y != 0)
//			return UnexecutableCommand.INSTANCE;
		return super.getMoveCommand(request);
	}

	@Override
	protected Command getResizeCommand(ChangeBoundsRequest request) {
//		CompoundCommand compoundCmd = new CompoundCommand("");
//		if (getHost() instanceof AbstractExecutionSpecificationEditPart) {
//			AbstractExecutionSpecificationEditPart activationEP = (AbstractExecutionSpecificationEditPart)getHost();
//			InteractionOperandEditPart ioep = ApexSequenceUtil.apexGetEnclosingInteractionOperandEditpart(activationEP);
//			CombinedFragmentEditPart cfep = (CombinedFragmentEditPart)ioep.getParent().getParent();
//			compoundCmd.add(InteractionCompartmentXYLayoutEditPolicy.getCombinedFragmentResizeChildrenCommand(request, cfep, activationEP));
//		}
//		compoundCmd.add(super.getResizeCommand(request));
//		return compoundCmd;

		Command command = null;

		if ( request.getSizeDelta().height > 0 && ( (request.getResizeDirection() & PositionConstants.SOUTH) != 0 ) ) {
			View view = (View)getHost().getModel();
			EObject element = view.getElement();
			if (element instanceof ExecutionSpecification) {
				AbstractExecutionSpecificationEditPart aep = (AbstractExecutionSpecificationEditPart)ApexSequenceUtil.getEditPart(element, getHost());
				
				InteractionFragment parent = ((ExecutionSpecification)element).getEnclosingOperand();
				if (parent != null) { // IOEP 내에 있는 Activation의 경우
//					EditPart interactionOperandEP = ApexSequenceUtil.getEditPart(parent, getHost());
//					EditPart combinedFragmentCompartmentEP = interactionOperandEP.getParent();
//					EditPart combinedFragmentEP = combinedFragmentCompartmentEP.getParent();
//
//					command = combinedFragmentEP.getCommand(request);
					
					
					InteractionOperandEditPart ioep = (InteractionOperandEditPart)ApexSequenceUtil.getEditPart((InteractionOperand)parent, aep);
					ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
					cbRequest.setSizeDelta(new Dimension(0, request.getSizeDelta().height));
					cbRequest.setResizeDirection(PositionConstants.SOUTH);
					command = ioep.getCommand(cbRequest);
					
				} else { // Interaction 내에 있는 Activation의 경우
					List<IGraphicalEditPart> nextSiblingEditParts = ApexSequenceUtil.apexGetNextSiblingEditParts(aep);
					
					if ( nextSiblingEditParts.size() > 0 ) {
						IGraphicalEditPart nextSiblingEditPart = (IGraphicalEditPart)ApexSequenceUtil.apexGetNextSiblingEditParts(aep).get(0);
						
						if ( nextSiblingEditPart != null ) {							
							if ( nextSiblingEditPart instanceof CombinedFragmentEditPart ) {
								ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
								cbRequest.setMoveDelta(new Point(0, request.getSizeDelta().height));	
								command = nextSiblingEditPart.getCommand(cbRequest);
							}
						}		
					}
				}		
			}			
		}
		return command == null ? super.getResizeCommand(request) :
			command.chain(super.getResizeCommand(request));
	}
}
