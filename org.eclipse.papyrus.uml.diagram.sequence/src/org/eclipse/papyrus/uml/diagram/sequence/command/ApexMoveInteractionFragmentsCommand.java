package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.editparts.INodeEditPart;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexOccurrenceSpecificationMoveHelper;
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

	protected final static String COMMAND_LABEL = "Move InteractionFragments";
	
	private final CompoundCommand command;

	private Set<InteractionFragment> movedFragments;
	private Set<InteractionFragment> belowFragments;
	private Set<InteractionFragment> oppositeFragments;
	
	private int belowMoveDeltaY;
	private int oppositeMoveDeltaY;
	
	private EditPartViewer viewer;
	private List<InteractionFragment> interactionFragments;
	private int yLocation;
	private int moveDeltaY;
	private boolean created;
	private boolean changedSequence;
	
	public ApexMoveInteractionFragmentsCommand(TransactionalEditingDomain domain, EditPartViewer viewer,
			Collection<InteractionFragment> interactionFragments, int yLocation, int moveDeltaY, boolean created, boolean changedSequence) {
		super(domain, COMMAND_LABEL, null);
		
		movedFragments = new HashSet<InteractionFragment>();
		belowFragments = new HashSet<InteractionFragment>();
		oppositeFragments = new HashSet<InteractionFragment>();
		
		this.viewer = viewer;
		this.interactionFragments = new ArrayList<InteractionFragment>();
		this.interactionFragments.addAll(interactionFragments);
		this.yLocation = yLocation;
		this.moveDeltaY = moveDeltaY;
		this.created = created;
		this.changedSequence = changedSequence;
		
		command = new CompoundCommand();
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		if (interactionFragments == null || interactionFragments.size() == 0) {
			return CommandResult.newOKCommandResult();
		}
		
		classifyInteractionFragments(interactionFragments);
		
		Rectangle movedRect = getRectangle(movedFragments);
		Rectangle oppositeRect = getRectangle(oppositeFragments);
		
		oppositeMoveDeltaY = moveDeltaY > 0 ? -movedRect.height : movedRect.height;
		if (changedSequence)
			moveDeltaY = moveDeltaY > 0 ? oppositeRect.height : -oppositeRect.height; 
		belowMoveDeltaY = 0;

		// opposite fragments
		Map<InteractionFragment, Rectangle> constraints = new HashMap<InteractionFragment, Rectangle>();

		for (InteractionFragment fragment : oppositeFragments) {
			if (fragment instanceof ExecutionOccurrenceSpecification) {
				ExecutionSpecification executionSpecification = ((ExecutionOccurrenceSpecification)fragment).getExecution();
				INodeEditPart nodeEditPart = (INodeEditPart)getEditPart(executionSpecification, INodeEditPart.class);
				if (fragment.equals(executionSpecification.getFinish()) && nodeEditPart != null) {
					Rectangle bounds = nodeEditPart.getFigure().getBounds().getCopy();
					bounds.height += oppositeMoveDeltaY;
					constraints.put(fragment, bounds);
				}
			}
			else if (fragment instanceof MessageOccurrenceSpecification) {
				continue;
			}

			Point loc = getLocation(fragment);
			command.add( ApexOccurrenceSpecificationMoveHelper.getMoveInteractionFragmentCommand(fragment, loc.y + oppositeMoveDeltaY) );
		}
		
		for (InteractionFragment fragment : oppositeFragments) {
			if (fragment instanceof MessageOccurrenceSpecification) {
				Message message = ((MessageOccurrenceSpecification)fragment).getMessage();
				ConnectionEditPart connectionEP= (ConnectionEditPart)getEditPart(message, ConnectionEditPart.class);
				EditPart sourceEP = connectionEP.getSource();
				EObject semanticElement = ViewUtil.resolveSemanticElement((View)sourceEP.getModel());
				if (semanticElement instanceof InteractionFragment && constraints.get(semanticElement) != null) {
					Rectangle rect = constraints.get(semanticElement);
					command.add( ApexOccurrenceSpecificationMoveHelper.getMoveInteractionFragmentCommand(fragment, oppositeMoveDeltaY) );
				}
			}
		}

		// moved fragments
		constraints = new HashMap<InteractionFragment, Rectangle>();
		
		for (InteractionFragment fragment : movedFragments) {
			if (fragment instanceof ExecutionOccurrenceSpecification) {
				ExecutionSpecification executionSpecification = ((ExecutionOccurrenceSpecification)fragment).getExecution();
				INodeEditPart nodeEditPart = (INodeEditPart)getEditPart(executionSpecification, INodeEditPart.class);
				if (fragment.equals(executionSpecification.getFinish()) && nodeEditPart != null) {
					Rectangle bounds = nodeEditPart.getFigure().getBounds().getCopy();
					bounds.height += moveDeltaY;
					constraints.put(fragment, bounds);
				}
			}

			Point loc = getLocation(fragment);
			command.add( ApexOccurrenceSpecificationMoveHelper.getMoveInteractionFragmentCommand(fragment, loc.y + moveDeltaY) );
		}
		
		for (InteractionFragment fragment : oppositeFragments) {
			if (fragment instanceof MessageOccurrenceSpecification) {
				Message message = ((MessageOccurrenceSpecification)fragment).getMessage();
				ConnectionEditPart connectionEP= (ConnectionEditPart)getEditPart(message, ConnectionEditPart.class);
				EditPart sourceEP = connectionEP.getSource();
				EObject semanticElement = ViewUtil.resolveSemanticElement((View)sourceEP.getModel());
				if (semanticElement instanceof InteractionFragment && constraints.get(semanticElement) != null) {
					Rectangle rect = constraints.get(semanticElement);
					command.add( ApexOccurrenceSpecificationMoveHelper.getMoveInteractionFragmentCommand(fragment, oppositeMoveDeltaY) );
				}
			}
		}
		
		// below fragments
		for (InteractionFragment fragment : belowFragments) {
			command.add( ApexOccurrenceSpecificationMoveHelper.getMoveInteractionFragmentCommand(fragment, belowMoveDeltaY) );
		}
		
		command.execute();
		
		return CommandResult.newOKCommandResult();
	}
	
	private void classifyInteractionFragments(List<InteractionFragment> fragments) {
		Set<InteractionFragment> allFragments = getAllFragments(fragments.get(0));

		Map<InteractionFragment, Rectangle> constraints = new HashMap<InteractionFragment, Rectangle>(fragments.size());
		for (InteractionFragment fragment : fragments) {
			List<InteractionFragment> linkedFragments = getLinkedInteractionFragments(fragment);
			constraints.put(fragment, getRectangle(linkedFragments));
			movedFragments.addAll(linkedFragments);
		}
		
		for (InteractionFragment ift : allFragments) {
			if (movedFragments.contains(ift)) {
				continue;
			}
			
			Point loc = getLocation(ift);
			if (loc.y > yLocation) {
				belowFragments.add(ift);
			}
		}
		
		for (InteractionFragment ift : belowFragments) {
			
		}
		
		belowFragments.removeAll(movedFragments);
		belowFragments.removeAll(oppositeFragments);
	}

	private Set<InteractionFragment> getAllFragments(InteractionFragment fragment) {
		Set<InteractionFragment> set = new HashSet<InteractionFragment>();
		
		Interaction interaction = fragment.getEnclosingInteraction();
		InteractionOperand operand = fragment.getEnclosingOperand();
		if (operand != null) {
			set.addAll(operand.getFragments());
		}
		else if (interaction != null) {
			set.addAll(interaction.getFragments());
		}
		
		return set;
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
				EditPart messageEP = getEditPart(message, INodeEditPart.class);
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
				EditPart executionEP = getEditPart(executionSpecification, INodeEditPart.class);
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

		EditPart editPart = getEditPart(fragment, INodeEditPart.class);
		lifelineEditPart = SequenceUtil.getParentLifelinePart(editPart);
		
		if (lifelineEditPart == null) {
			for (Lifeline lifeline : fragment.getCovereds()) {
				editPart = getEditPart(lifeline, INodeEditPart.class);
				lifelineEditPart = SequenceUtil.getParentLifelinePart(editPart);
				if (lifelineEditPart != null)
					break;
			}
		}
		
		return SequenceUtil.findLocationOfEvent(lifelineEditPart, fragment);
	}
	
	private Rectangle getRectangle(Collection<InteractionFragment> fragments) {
		Rectangle rect = null;
		for (InteractionFragment fragment : fragments) {
			Point loc = getLocation(fragment);
			if (rect == null) {
				rect = new Rectangle(loc.x, loc.y, 0, 0);
			}
			else {
				rect.union(getLocation(fragment));
			}
		}
		return rect != null ? rect : new Rectangle();
	}
	
	private EditPart getEditPart(EObject parserElement, Class clazz) {
		Map editPartRegistry = viewer.getEditPartRegistry();
		
		for (Object ob : editPartRegistry.keySet()) {
			if (ob instanceof View && parserElement.equals(((View)ob).getElement())) {
				EditPart editPart = (EditPart)editPartRegistry.get(ob);
				if (clazz.isInstance(editPart)) {
					return editPart;
				}
			}
		}
		
		return null;
	}
}
