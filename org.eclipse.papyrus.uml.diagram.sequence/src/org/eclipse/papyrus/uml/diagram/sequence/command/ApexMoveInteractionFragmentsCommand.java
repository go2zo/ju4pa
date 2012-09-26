package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexOccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceRequestConstant;
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
	
	private final static int EXECUTION_BOTTOM_MARGIN = 20;
	
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

		Map<LifelineEditPart, Collection<MessageOccurrenceSpecification>> needToMoveMessages = new HashMap<LifelineEditPart, Collection<MessageOccurrenceSpecification>>();
		Map<LifelineEditPart, Integer> needToMoveBottoms = new HashMap<LifelineEditPart, Integer>();
		
		Collection<InteractionFragment> fragments = getInteractionFragments(fragment);
		Point realMoveDelta = getRealMoveDelta(getMoveDelta(), fragments);
		for (InteractionFragment ift : fragments) {
			if (notToMoveEObject.contains(ift))
				continue;
			
			if (ift instanceof ExecutionSpecification) {
				IGraphicalEditPart editPart = getEditPart(ift);
				Rectangle bounds = SequenceUtil.getAbsoluteBounds(editPart);
				if (bounds.y > location.y) {
//					ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
//					request.setMoveDelta(realMoveDelta);
//					request.setSizeDelta(new Dimension(0, 0));
//					command.add(editPart.getCommand(request));
					
					Rectangle newBounds = bounds.getCopy();
					Rectangle parentBounds = editPart.getFigure().getParent().getBounds();
					editPart.getFigure().translateToRelative(newBounds);
					newBounds.translate(-parentBounds.x, -parentBounds.y);
					newBounds.translate(realMoveDelta);
					SetBoundsCommand sbCommand = new SetBoundsCommand(getEditingDomain(), "Set Bounds", editPart, newBounds);
					command.add(new ICommandProxy(sbCommand));
				}
				else if (bounds.bottom() > location.y) {
					ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
					request.getExtendedData().put(SequenceRequestConstant.PRESERVE_ANCHOR_RELATIVE_BOUNDS, location);
					request.getExtendedData().put(SequenceRequestConstant.DO_NOT_MOVE_EDIT_PARTS, true);
					request.setResizeDirection(PositionConstants.SOUTH);
					request.setSizeDelta(new Dimension(0, realMoveDelta.y));
					command.add(editPart.getCommand(request));
				}
			}
			else if (ift instanceof CombinedFragment) {
				IGraphicalEditPart editPart = getEditPart(ift);
				Rectangle bounds = SequenceUtil.getAbsoluteBounds(editPart);
				if (bounds.y > location.y) {
//					ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
//					request.setMoveDelta(realMoveDelta);
//					request.setSizeDelta(new Dimension(0, 0));
//					command.add(editPart.getCommand(request));
					
					Rectangle newBounds = bounds.getCopy();
					Rectangle parentBounds = editPart.getFigure().getParent().getBounds();
					editPart.getFigure().translateToRelative(newBounds);
					newBounds.translate(-parentBounds.x, -parentBounds.y);
					newBounds.translate(realMoveDelta);
					SetBoundsCommand sbCommand = new SetBoundsCommand(getEditingDomain(), "Set Bounds", editPart, newBounds);
					command.add(new ICommandProxy(sbCommand));
				}
			}
			else if (ift instanceof MessageOccurrenceSpecification) {
				Message message = ((MessageOccurrenceSpecification)ift).getMessage();
				IGraphicalEditPart editPart = getEditPart(message);
				if (ift.equals(message.getSendEvent()) && editPart instanceof ConnectionNodeEditPart) {
					EditPart source = ((ConnectionNodeEditPart)editPart).getSource();
					Point edge = SequenceUtil.getAbsoluteEdgeExtremity((ConnectionNodeEditPart) editPart, true);
					if (source instanceof LifelineEditPart && edge != null && edge.y > location.y) {
						LifelineEditPart lifelineEP = (LifelineEditPart)source;
						Collection<MessageOccurrenceSpecification> occurrenceSpecifications = needToMoveMessages.get(lifelineEP);
						if (occurrenceSpecifications == null) {
							occurrenceSpecifications = new HashSet<MessageOccurrenceSpecification>();
							needToMoveMessages.put(lifelineEP, occurrenceSpecifications);
						}
						occurrenceSpecifications.add((MessageOccurrenceSpecification) ift);
						
						Integer bottom = needToMoveBottoms.get(lifelineEP);
						if (bottom == null) {
							needToMoveBottoms.put(lifelineEP, edge.y + realMoveDelta.y);
						}
						else if (bottom < edge.y + realMoveDelta.y) {
							needToMoveBottoms.put(lifelineEP, edge.y + realMoveDelta.y);
						}
					}
				}
			}
		}
		
		command.add(createPreserveAnchorCommands(needToMoveMessages, needToMoveBottoms, realMoveDelta));
		
		command.execute();
		
		return CommandResult.newOKCommandResult();
	}
	
	private Command createPreserveAnchorCommands(Map<LifelineEditPart, Collection<MessageOccurrenceSpecification>> needToMoveMessages,
			Map<LifelineEditPart, Integer> needToMoveBottoms, Point realMoveDelta) {
		CompoundCommand compCmd = new CompoundCommand();
		
		for (Entry<LifelineEditPart, Collection<MessageOccurrenceSpecification>> entry : needToMoveMessages.entrySet()) {
			LifelineEditPart lifelineEP = entry.getKey();
			Collection<MessageOccurrenceSpecification> occurrenceSpecifications = entry.getValue();
			Integer bottom = needToMoveBottoms.get(lifelineEP);
			
			if (bottom == null) {
				continue;
			}

			Rectangle oldBounds = lifelineEP.getPrimaryShape().getFigureLifelineDotLineFigure().getBounds().getCopy();
			lifelineEP.getFigure().translateToAbsolute(oldBounds);
			Rectangle newBounds = oldBounds.getCopy();
			if (newBounds.bottom() < bottom) {
				newBounds.height = bottom - newBounds.y;
			}
			
			if (!newBounds.equals(oldBounds)) {
				ChangeBoundsRequest request = new ChangeBoundsRequest(RequestConstants.REQ_RESIZE);
				request.getExtendedData().put(SequenceRequestConstant.PRESERVE_ANCHOR_RELATIVE_BOUNDS, location);
				request.getExtendedData().put(SequenceRequestConstant.DO_NOT_MOVE_EDIT_PARTS, true);
				request.setResizeDirection(PositionConstants.SOUTH);
				request.setSizeDelta(new Dimension(0, newBounds.bottom() - oldBounds.bottom()));
				compCmd.add(lifelineEP.getCommand(request));
			}
			
			for (MessageOccurrenceSpecification occurrenceSpecification : occurrenceSpecifications) {
				Message message = occurrenceSpecification.getMessage();
				EditPart editPart = getEditPart(message);
				if (occurrenceSpecification.equals(message.getSendEvent())
						&& editPart instanceof ConnectionNodeEditPart) {
					ConnectionNodeEditPart messageEP = (ConnectionNodeEditPart)editPart;
					Point edge = SequenceUtil.getAbsoluteEdgeExtremity(messageEP, true);
					
					List<EditPart> empty = Collections.emptyList();
					compCmd.add(ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
							occurrenceSpecification, edge.y + realMoveDelta.y, newBounds, lifelineEP, lifelineEP, empty));
				}
			}
		}
		return compCmd.size() > 0 ? compCmd : null;
	}
	
	public Point getMoveDelta() {
		return moveDelta;
	}
	
	public Point getRealMoveDelta(Point delta, Collection<InteractionFragment> fragments) {
		Point newDelta = delta.getCopy();
		for (InteractionFragment ift : fragments) {
			if (ift instanceof ExecutionSpecification) {
				ExecutionSpecification execution = (ExecutionSpecification)ift;
				IGraphicalEditPart editPart = getEditPart(execution);
				Rectangle bounds = SequenceUtil.getAbsoluteBounds(editPart);
				if (bounds.y < location.y && bounds.bottom() > location.y
						&& bounds.bottom() < location.y + EXECUTION_BOTTOM_MARGIN) {
					newDelta.y = delta.y + location.y + EXECUTION_BOTTOM_MARGIN - bounds.bottom();
				}
			}
		}
		return newDelta;
	}
	
	private Collection<InteractionFragment> getInteractionFragments(InteractionFragment fragment) {
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
		return allFragments;
	}
	
	/**
	 * sort 필요 없음
	 * @param fragment
	 * @param location
	 * @return
	 */
	private Collection<InteractionFragment> getUnderInteractionFragments(InteractionFragment fragment, Point location) {
		Set<InteractionFragment> result = new HashSet<InteractionFragment>();
		
		Collection<InteractionFragment> allFragments = getInteractionFragments(fragment);
		
		for (InteractionFragment ift : allFragments) {
			if (notToMoveEObject.contains(ift))
				continue;
			
			if (ift instanceof MessageOccurrenceSpecification) {
				Message message = ((MessageOccurrenceSpecification)ift).getMessage();
				IGraphicalEditPart editPart = getEditPart(message);
				if (editPart instanceof ConnectionEditPart) {
					if (ift.equals(message.getSendEvent())) {
						EditPart source = ((ConnectionEditPart)editPart).getSource();
						if (!(source instanceof AbstractExecutionSpecificationEditPart)) {
							result.add(ift);
						}
					}
					else if (ift.equals(message.getReceiveEvent())) {
						EditPart target = ((ConnectionEditPart)editPart).getTarget();
						if (!(target instanceof AbstractExecutionSpecificationEditPart)) {
							result.add(ift);
						}
					}
				}
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
	
	private IGraphicalEditPart getEditPart(EObject eObject) {
		return (IGraphicalEditPart)ApexSequenceUtil.getEditPart(eObject, viewer);
	}
	
}
