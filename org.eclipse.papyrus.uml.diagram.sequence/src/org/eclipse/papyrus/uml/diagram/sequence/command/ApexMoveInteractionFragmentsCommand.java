package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

public class ApexMoveInteractionFragmentsCommand extends
		AbstractTransactionalCommand {

	protected final static String COMMAND_LABEL = "Move InteractionFragments";
	
	protected EditPartViewer viewer;
	protected ViewDescriptor descriptor;
	protected InteractionFragment fragment;
	protected Point location;
	protected Point moveDelta;

	private CompoundCommand command;
	
	protected Collection<EObject> notToMoveEObject;

	
	public ApexMoveInteractionFragmentsCommand(
			TransactionalEditingDomain domain, ViewDescriptor descriptor, EditPartViewer viewer, InteractionFragment fragment, Point location, Point moveDelta) {
		super(domain, COMMAND_LABEL, null);
		this.viewer = viewer;
		this.descriptor = descriptor;
		this.fragment = fragment;
		this.location = location;
		this.moveDelta = moveDelta;
		
		command = new CompoundCommand();
		notToMoveEObject = new HashSet<EObject>();
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		
		View view = (View)descriptor.getAdapter(View.class);
		if (view != null) {
			EObject eObject = view.getElement();
			if (eObject instanceof Message) {
				Message message = (Message)eObject;
				notToMoveEObject.add(message.getSendEvent());
				notToMoveEObject.add(message.getReceiveEvent());
			}
		}
		
		Collection<InteractionFragment> underFragments = getUnderInteractionFragments(fragment, location);
		for (InteractionFragment underFragment : underFragments) {
			if (underFragment instanceof ExecutionOccurrenceSpecification) {
				ExecutionSpecification execution = ((ExecutionOccurrenceSpecification)underFragment).getExecution();
				IGraphicalEditPart editPart = getEditPart(execution);
				Point moveDelta = getMoveDelta();
				ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
				request.setMoveDelta(new Point(0, 0));
				request.setResizeDirection(PositionConstants.SOUTH);
				request.setSizeDelta(new Dimension(0, moveDelta.y));
				command.add(editPart.getCommand(request));
			}
			else if (underFragment instanceof ExecutionSpecification ||
					underFragment instanceof CombinedFragment) {
				IGraphicalEditPart editPart = getEditPart(underFragment);
				Point moveDelta = getMoveDelta();

				ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
				request.setMoveDelta(moveDelta);
				request.setSizeDelta(new Dimension(0, 0));

				command.add(editPart.getCommand(request));
			}
		}
		
		command.execute();
		
		return CommandResult.newOKCommandResult();
	}
	
	public Point getMoveDelta() {
		return moveDelta;
	}
	
	public void setMoveDelta(Point moveDelta) {
		this.moveDelta = moveDelta;
	}
	
	/**
	 * sort 필요 없음
	 * @param fragment
	 * @param location
	 * @return
	 */
	private Collection<InteractionFragment> getUnderInteractionFragments(InteractionFragment fragment, Point location) {
		Set<InteractionFragment> result = new HashSet<InteractionFragment>();
		
		Set<InteractionFragment> allFragments = new HashSet<InteractionFragment>();
		if (fragment instanceof Interaction) {
			allFragments.addAll(((Interaction)fragment).getFragments());
		}
		else if (fragment instanceof InteractionOperand) {
			allFragments.addAll(((InteractionOperand)fragment).getFragments());
		}
		else {
			if (fragment.getEnclosingOperand() != null) {
				InteractionOperand operand = fragment.getEnclosingOperand();
				allFragments.addAll(operand.getFragments());
			}
			else if (fragment.getEnclosingInteraction() != null) {
				Interaction interaction = fragment.getEnclosingInteraction();
				allFragments.addAll(interaction.getFragments());
			}
		}
		
		for (InteractionFragment ift : allFragments) {
			if (notToMoveEObject.contains(ift))
				continue;
			
			if (ift instanceof MessageOccurrenceSpecification) {
				continue;
			}
			else if (ift instanceof ExecutionOccurrenceSpecification) {
				ExecutionSpecification execution = ((ExecutionOccurrenceSpecification)ift).getExecution();
				if (ift.equals(execution.getFinish())) {
					IGraphicalEditPart editPart = getEditPart(execution);
					Rectangle bounds = SequenceUtil.getAbsoluteBounds(editPart);
					Point loc = bounds.getBottom();
					if (loc.y > location.y) {
						result.add(ift);
					}
				}
			}
			else {
				IGraphicalEditPart editPart = getEditPart(ift);
				Rectangle bounds = SequenceUtil.getAbsoluteBounds(editPart);
				Point loc = bounds.getTop();
				if (loc.y > location.y) {
					result.add(ift);
				}
			}
		}
		
		Set<InteractionFragment> removedFragments = new HashSet<InteractionFragment>();
		for (InteractionFragment ift : result) {
			if (ift instanceof ExecutionSpecification) {
				removedFragments.add(((ExecutionSpecification)ift).getStart());
				removedFragments.add(((ExecutionSpecification)ift).getFinish());
			}
		}
		result.removeAll(removedFragments);

		return result;
	}
	
	private IGraphicalEditPart getEditPart(InteractionFragment fragment) {
		return (IGraphicalEditPart)ApexSequenceUtil.getEditPart(fragment, viewer);
	}
	
}
