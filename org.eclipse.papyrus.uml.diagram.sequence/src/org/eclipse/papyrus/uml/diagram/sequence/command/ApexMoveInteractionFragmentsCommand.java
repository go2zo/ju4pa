package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.OccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;

public class ApexMoveInteractionFragmentsCommand extends
		AbstractTransactionalCommand {

	protected final static String COMMAND_LABEL = "Move below InteractionFragments";

	private List<InteractionFragment> movedFragments;
	private List<InteractionFragment> belowFragments;
	private List<InteractionFragment> oppositeFragments;
	
	private EditPartViewer viewer;
	private InteractionFragment interactionFragment;
	private int yLocation;
	private int moveDeltaY;
	private boolean changedSequence;
	
	public ApexMoveInteractionFragmentsCommand(TransactionalEditingDomain domain, EditPartViewer viewer,
			InteractionFragment interactionFragment, int yLocation, int moveDeltaY, boolean changedSequence) {
		super(domain, COMMAND_LABEL, null);
		
		movedFragments = new ArrayList<InteractionFragment>();
		belowFragments = new ArrayList<InteractionFragment>();
		oppositeFragments = new ArrayList<InteractionFragment>();
		
		this.viewer = viewer;
		this.interactionFragment = interactionFragment;
		this.yLocation = yLocation;
		this.moveDeltaY = moveDeltaY;
		this.changedSequence = changedSequence;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		
		classifyInteractionFragments(interactionFragment);
		
		for (InteractionFragment fragment : movedFragments) {
			
		}
		
		return CommandResult.newOKCommandResult();
	}
	
	private void classifyInteractionFragments(InteractionFragment fragment) {
		List<InteractionFragment> allFragments = getAllFragments(fragment);
		
		movedFragments.addAll(getLinkedInteractionFragments(fragment));
		
		Rectangle bounds = getRectangle(movedFragments);
		int belowTop = !changedSequence ? bounds.x : bounds.bottom();
		
		for (InteractionFragment ift : allFragments) {
			if (ift instanceof ExecutionSpecification) {
				continue;
			}
			
			Point loc = getLocation(ift);
			if (loc.y > belowTop) {
				belowFragments.add(ift);
			}
			
			if (changedSequence) {
				if ((moveDeltaY > 0 && loc.y > bounds.bottom() && loc.y < yLocation) ||
						(moveDeltaY < 0 && loc.y < bounds.y && loc.y > yLocation)) {
					oppositeFragments.add(ift);
				}
			}
		}
		
		belowFragments.removeAll(movedFragments);
		belowFragments.removeAll(oppositeFragments);
	}

	private List<InteractionFragment> getAllFragments(InteractionFragment fragment) {
		List<InteractionFragment> list = new ArrayList<InteractionFragment>();
		
		Interaction interaction = fragment.getEnclosingInteraction();
		InteractionOperand operand = fragment.getEnclosingOperand();
		if (operand != null) {
			list.addAll(operand.getFragments());
		}
		else if (interaction != null) {
			list.addAll(interaction.getFragments());
		}
		
		return list;
	}
	
	private List<InteractionFragment> getLinkedInteractionFragments(InteractionFragment fragment) {
		List<InteractionFragment> list = new ArrayList<InteractionFragment>();
		getLinkedInteractionFragments(fragment, list);
		return list;
	}
	
	private void getLinkedInteractionFragments(InteractionFragment fragment, List<InteractionFragment> list) {
		list.add(fragment);
		
		if (fragment instanceof MessageOccurrenceSpecification) {
			Message message = ((MessageOccurrenceSpecification)fragment).getMessage();
			if (fragment.equals(message.getSendEvent())) {
				MessageEnd receiveEvent = message.getReceiveEvent();
				if (receiveEvent instanceof InteractionFragment) {
					getLinkedInteractionFragments((InteractionFragment)receiveEvent, list);
				}
			}
			else if (fragment.equals(message.getReceiveEvent())) {
				EditPart messageEP = getEditPart(message);
				EditPart targetEP = null;
				if (messageEP instanceof ConnectionEditPart) {
					targetEP = ((ConnectionEditPart)messageEP).getTarget();
				}
				if (targetEP instanceof AbstractExecutionSpecificationEditPart) {
					EObject element = ViewUtil.resolveSemanticElement((View)targetEP.getModel());
					if (element instanceof ExecutionSpecification) {
						getLinkedInteractionFragments(((ExecutionSpecification)element).getStart(), list);
					}
				}
			}
		}
		else if (fragment instanceof ExecutionOccurrenceSpecification) {
			ExecutionSpecification executionSpecification = ((ExecutionOccurrenceSpecification)fragment).getExecution();
			if (fragment.equals(executionSpecification.getStart())) {
				getLinkedInteractionFragments(executionSpecification.getFinish(), list);
			}
			else if (fragment.equals(executionSpecification.getFinish())) {
				EditPart executionEP = getEditPart(executionSpecification);
				if (executionEP instanceof AbstractExecutionSpecificationEditPart) {
					List conns = ((AbstractExecutionSpecificationEditPart) executionEP).getSourceConnections();
					for (int i = 0; i < conns.size(); i++) {
						ConnectionEditPart connEP = (ConnectionEditPart)conns.get(i);
						EObject element = ViewUtil.resolveSemanticElement((View)connEP.getModel());
						if (element instanceof Message) {
							MessageEnd sendEvent = ((Message)element).getSendEvent();
							if (sendEvent instanceof InteractionFragment) {
								getLinkedInteractionFragments((InteractionFragment)sendEvent, list);
							}
						}
					}
				}
			}
		}
	}
	
	private Point getLocation(InteractionFragment fragment) {
		LifelineEditPart lifelineEditPart = null;

		EditPart editPart = getEditPart(fragment);
		lifelineEditPart = SequenceUtil.getParentLifelinePart(editPart);
		
		if (lifelineEditPart == null) {
			for (Lifeline lifeline : fragment.getCovereds()) {
				editPart = getEditPart(lifeline);
				lifelineEditPart = SequenceUtil.getParentLifelinePart(editPart);
				if (lifelineEditPart != null)
					break;
			}
		}
		
		return SequenceUtil.findLocationOfEvent(lifelineEditPart, fragment);
	}
	
	private Rectangle getRectangle(List<InteractionFragment> list) {
		Rectangle rect = null;
		for (InteractionFragment fragment : list) {
			Point loc = getLocation(fragment);
			if (rect == null) {
				rect = new Rectangle(loc.x, loc.y, 0, 0);
			}
			else {
				rect.union(getLocation(fragment));
			}
		}
		return rect;
	}
	
	private EditPart getEditPart(EObject parserElement) {
		Map editPartRegistry = viewer.getEditPartRegistry();
		
		for (Object ob : editPartRegistry.keySet()) {
			if (ob instanceof View && parserElement.equals(((View)ob).getElement())) {
				EditPart editPart = (EditPart)editPartRegistry.get(ob);
				if (editPart != null) {
					return editPart;
				}
			}
		}
		
		return null;
	}
}
