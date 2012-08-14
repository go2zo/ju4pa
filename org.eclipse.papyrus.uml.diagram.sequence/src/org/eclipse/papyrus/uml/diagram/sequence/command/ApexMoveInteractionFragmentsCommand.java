package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;

public class ApexMoveInteractionFragmentsCommand extends
		AbstractTransactionalCommand {

	protected final static String COMMAND_LABEL = "Move InteractionFragments";
	
	private EditPartViewer viewer;
	private InteractionFragment fragment;
	private Point location;
	private int moveDaltaY;

	private CompoundCommand command;

	
	public ApexMoveInteractionFragmentsCommand(
			TransactionalEditingDomain domain, InteractionFragment fragment, Point location, int moveDeltaY) {
		super(domain, COMMAND_LABEL, null);
		this.fragment = fragment;
		this.location = location;
		this.moveDaltaY = moveDeltaY;
		
		command = new CompoundCommand();
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		
		List<InteractionFragment> underFragments = getUnderInteractionFragments(fragment, location);
		
		for (InteractionFragment underFragment : underFragments) {
			IGraphicalEditPart editPart = getEditPart(underFragment);
			Rectangle bounds = editPart.getFigure().getBounds().getCopy();
			editPart.getFigure().translateToAbsolute(bounds);
			
			if (underFragment instanceof ExecutionOccurrenceSpecification) {
				bounds.setHeight(bounds.height + moveDaltaY);
			}
			else if (underFragment instanceof ExecutionSpecification ||
					underFragment instanceof CombinedFragment) {
				bounds.setY(bounds.y + moveDaltaY);
			}
			else {
				bounds.setY(bounds.y + moveDaltaY);
				continue;
			}
			
			editPart.getFigure().translateToRelative(bounds);
			
			SetBoundsCommand sbCmd = new SetBoundsCommand(getEditingDomain(), "", editPart, bounds);
			command.add(new ICommandProxy(sbCmd));
		}
		
		command.execute();
		
		return null;
	}

	private List<InteractionFragment> getUnderInteractionFragments(InteractionFragment fragment, Point location) {
		List<InteractionFragment> list = new ArrayList<InteractionFragment>();
		
		List<InteractionFragment> allFragments = new ArrayList<InteractionFragment>();
		if (fragment.getEnclosingOperand() != null) {
			InteractionOperand operand = fragment.getEnclosingOperand();
			allFragments.addAll(operand.getFragments());
		}
		else if (fragment.getEnclosingInteraction() != null) {
			Interaction interaction = fragment.getEnclosingInteraction();
			allFragments.addAll(interaction.getFragments());
		}
		
		
		
		return list;
	}
	
	private IGraphicalEditPart getEditPart(InteractionFragment fragment) {
		return (IGraphicalEditPart)ApexSequenceUtil.getEditPart(fragment, viewer);
	}
	
}
