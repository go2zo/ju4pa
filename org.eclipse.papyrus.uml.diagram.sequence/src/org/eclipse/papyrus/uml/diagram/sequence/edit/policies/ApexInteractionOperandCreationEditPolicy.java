package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.CreationEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewAndElementRequest;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionOperatorKind;

public class ApexInteractionOperandCreationEditPolicy extends
		CreationEditPolicy {

	/**
	 * apex updated
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Command getCreateElementAndViewCommand(CreateViewAndElementRequest request) {
		/* apex improved start */
		EditPart targetEditPart = getTargetEditPart(request);
		System.out.println("targetEditPart : " + targetEditPart);
		
		Command createElementAndViewCmd = super.getCreateElementAndViewCommand(request);

		
		// InteractionOperand의 경우 Operand가 alt가 아닌 경우는 X 표시
		
		if ( targetEditPart instanceof InteractionOperandEditPart ) {
			InteractionOperandEditPart ioep = (InteractionOperandEditPart)targetEditPart;				
			CombinedFragmentEditPart cfep = (CombinedFragmentEditPart)ioep.getParent().getParent();
			CombinedFragment cf = (CombinedFragment)cfep.resolveSemanticElement();
			InteractionOperatorKind ioKind = cf.getInteractionOperator();
			// Operator가 ALT와 같지 않으면

			System.out
					.println("ApexInteractionOperandCreationEditPolicy.getCreateElementAndViewCommand(), line : "
							+ Thread.currentThread().getStackTrace()[1]
									.getLineNumber());
		    System.out.println("ioKind.compareTo(ioKind.ALT_LITERAL) : " + ioKind.compareTo(ioKind.ALT_LITERAL));
		    Point location = request.getLocation();
		    System.out.println("location : " + location);
			
		    if ( InteractionOperatorKind.OPT_LITERAL.equals(ioKind) 
		    		|| InteractionOperatorKind.LOOP_LITERAL.equals(ioKind) 
		    		|| InteractionOperatorKind.BREAK_LITERAL.equals(ioKind) 
		    		|| InteractionOperatorKind.NEG_LITERAL.equals(ioKind)
		       ) {
				// UnexecutableCommand.INSTANCE 리턴해도 X 표시 되지 않음 ㅠㅜ
				return UnexecutableCommand.INSTANCE;
			}				
		}

		return createElementAndViewCmd;
		/* apex improved end */
		
	}
}
