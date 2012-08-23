package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.ui.commands.CreateOrSelectElementCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateConnectionViewRequest.ConnectionViewDescriptor;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.IHintedType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.papyrus.uml.diagram.common.util.DiagramEditPartsUtil;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ActionExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.Messages;
import org.eclipse.papyrus.uml.diagram.sequence.part.UMLVisualIDRegistry;
import org.eclipse.papyrus.uml.diagram.sequence.providers.UMLElementTypes;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceRequestConstant;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.uml2.uml.InteractionFragment;

public class PromptCreateElementAndNodeCommand extends
		CreateOrSelectElementCommand {
	private static final List<IElementType> executionTypes = new ArrayList<IElementType>();
	static {
		executionTypes.add(UMLElementTypes.ActionExecutionSpecification_3006);
		executionTypes.add(UMLElementTypes.BehaviorExecutionSpecification_3003);
	}
	private final CompoundCommand command;
	private TransactionalEditingDomain editingDomain;
	private ConnectionViewDescriptor descriptor;
	private ShapeNodeEditPart targetEP;
	private EObject target;
	private Point location;
	private InteractionFragment container;
	private CreateConnectionRequest request;
	private EditPart sourceEP;

	public PromptCreateElementAndNodeCommand(Command createCommand,
			TransactionalEditingDomain editingDomain,
			ConnectionViewDescriptor descriptor, ShapeNodeEditPart targetEP,
			EObject target, EditPart sourceEP, CreateConnectionRequest request, InteractionFragment container) {
		super(Display.getCurrent().getActiveShell(), executionTypes);
		this.editingDomain = editingDomain;
		this.descriptor = descriptor;
		this.targetEP = targetEP;
		this.target = target;
		this.sourceEP = sourceEP;
		this.request = request;
		this.location = request.getLocation();
		this.container = container;
		command = new CompoundCommand();
		command.add(createCommand);
	}

	/**
	 * apex updated
	 */
	protected CommandResult doExecuteWithResult(
			IProgressMonitor progressMonitor, IAdaptable info)
			throws ExecutionException {
		sourceEP.eraseSourceFeedback(request);
		targetEP.eraseSourceFeedback(request);

		CommandResult cmdResult = super.doExecuteWithResult(progressMonitor,
				info);
		if (!cmdResult.getStatus().isOK()) {
			return cmdResult;
		}
		IHintedType connectionType = (IHintedType) cmdResult.getReturnValue();
		
		CreateElementAndNodeCommand createExecutionSpecificationCommand = new CreateElementAndNodeCommand(
				editingDomain, (ShapeNodeEditPart) targetEP, target,
				connectionType, location);
		createExecutionSpecificationCommand.putCreateElementRequestParameter(
				SequenceRequestConstant.INTERACTIONFRAGMENT_CONTAINER,
				container);
		command.add(new ICommandProxy(createExecutionSpecificationCommand));

		/* apex added start */
		// jiho - Source인 ExecSpec의 Bounds, Connection의 Anchor을 자동변경하는 Command 생성
//		if (sourceEP instanceof AbstractExecutionSpecificationEditPart) {
//			AbstractExecutionSpecificationEditPart execuSpecEP = (AbstractExecutionSpecificationEditPart)sourceEP;
//			View view = (View)execuSpecEP.getModel();
//			ApexSetBoundsForExecutionSpecificationCommand setBoundsCommand = new ApexSetBoundsForExecutionSpecificationCommand(
//					editingDomain, createExecutionSpecificationCommand, new EObjectAdapter(view));
//
//			command.add(new ICommandProxy(setBoundsCommand));
//
//			command.add(new ICommandProxy(new ApexSetBoundsAndPreserveAnchorsPositionCommand( execuSpecEP, setBoundsCommand,
//					ApexPreserveAnchorsPositionCommand.PRESERVE_Y, execuSpecEP.getFigure(), PositionConstants.SOUTH) ));
//		}
		/* apex added end */
		/* apex added start */
		ApexSetBoundsAndMoveInteractionFragmentsCommand mifCmd = new ApexSetBoundsAndMoveInteractionFragmentsCommand(
				editingDomain, createExecutionSpecificationCommand, descriptor, sourceEP, container, location);
		command.add(new ICommandProxy(mifCmd));

		/* apex added end */

		// put the anchor at the top of the figure
		ChangeEdgeTargetCommand changeTargetCommand = new ChangeEdgeTargetCommand(
				editingDomain, createExecutionSpecificationCommand, descriptor,
				"(0.5, 0.0)");
		command.add(new ICommandProxy(changeTargetCommand));

		command.execute();
		
		return CommandResult.newOKCommandResult(descriptor);
	}

	public boolean canUndo() {
		return command != null && command.canUndo();
	}

	protected CommandResult doRedoWithResult(IProgressMonitor progressMonitor,
			IAdaptable info) throws ExecutionException {
		if (command != null) {
			command.redo();
		}
		return super.doRedoWithResult(progressMonitor, info);
	}

	protected CommandResult doUndoWithResult(IProgressMonitor progressMonitor,
			IAdaptable info) throws ExecutionException {
		if (command != null) {
			command.undo();
		}
		return super.doUndoWithResult(progressMonitor, info);
	}

	protected ILabelProvider getLabelProvider() {
		return new LabelProvider() {

			@Override
			public String getText(Object object) {
				if (object instanceof IHintedType) {
					IHintedType elementType = (IHintedType) object;
					switch (UMLVisualIDRegistry.getVisualID(elementType
							.getSemanticHint())) {
					case ActionExecutionSpecificationEditPart.VISUAL_ID:
						return Messages.createActionExecutionSpecification2CreationTool_title;
					case BehaviorExecutionSpecificationEditPart.VISUAL_ID:
						return Messages.createBehaviorExecutionSpecification3CreationTool_title;
					}
				}
				return super.getText(object);
			}
		};

	}
	
	/**
	 * InteractionFragment들의 위치 중복을 방지하기 위해
	 * Message가 생성될 새로운 Point 값을 계산
	 * @param location
	 * @return new location
	 */
	private Point apexGetCorrectedLocation(Point location) {
		Point newLocation = new Point(location);
		
		List<IGraphicalEditPart> editParts = ApexSequenceUtil.apexGetEditPartsContainerAt(location.y, container, sourceEP.getViewer());
		for (IGraphicalEditPart editPart : editParts) {
			if (editPart instanceof AbstractExecutionSpecificationEditPart ||
					editPart instanceof ConnectionNodeEditPart) {
				List<IGraphicalEditPart> linkedEditParts = ApexSequenceUtil.apexGetLinkedEditPartList(editPart, true, true, false);
				if (linkedEditParts.contains(sourceEP)) {
					continue;
				}
				
				int bottom = Integer.MIN_VALUE;
				for (IGraphicalEditPart linkedEP : linkedEditParts) {
					int tmp = ApexSequenceUtil.apexGetAbsolutePosition(linkedEP, SWT.BOTTOM);
					if (bottom < tmp) {
						bottom = tmp;
					}
				}
				
				if (newLocation.y < bottom) {
					newLocation.setY(bottom);
				}
			}
			else {
				int bottom = ApexSequenceUtil.apexGetAbsolutePosition(editPart, SWT.BOTTOM);
				if (newLocation.y < bottom) {
					newLocation.setY(bottom);
				}
			}
		}
		
		return newLocation;
	}
}
