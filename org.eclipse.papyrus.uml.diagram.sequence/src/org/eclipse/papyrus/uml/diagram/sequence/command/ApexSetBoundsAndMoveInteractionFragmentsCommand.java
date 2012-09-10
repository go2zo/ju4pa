package org.eclipse.papyrus.uml.diagram.sequence.command;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.diagram.core.util.ViewUtil;
import org.eclipse.gmf.runtime.diagram.ui.requests.CreateViewRequest.ViewDescriptor;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.InteractionFragment;

public class ApexSetBoundsAndMoveInteractionFragmentsCommand extends
		ApexMoveInteractionFragmentsCommand {

	private CreateElementAndNodeCommand createElementAndNodeCommand;
	
	public ApexSetBoundsAndMoveInteractionFragmentsCommand(TransactionalEditingDomain domain, CreateElementAndNodeCommand command,
			ViewDescriptor descriptor, EditPartViewer viewer, InteractionFragment fragment, Point location) {
		super(domain, descriptor, viewer, fragment, location, null);
		
		this.createElementAndNodeCommand = command;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		View view = createElementAndNodeCommand.getCreatedView();
		if (view != null) {
			EObject eObject = view.getElement();
			if (eObject instanceof ExecutionSpecification) {
				ExecutionSpecification execution = (ExecutionSpecification)eObject;
				notToMoveEObject.add(execution);
				notToMoveEObject.add(execution.getStart());
				notToMoveEObject.add(execution.getFinish());
			}
		}
		
		return super.doExecuteWithResult(monitor, info);
	}

	@Override
	public Point getMoveDelta() {
		Point delta = super.getMoveDelta();
		if (delta == null) {
			View createdView = createElementAndNodeCommand.getCreatedView();
			if (createdView != null) {
				int height = (Integer)ViewUtil.getStructuralFeatureValue(createdView, NotationPackage.eINSTANCE.getSize_Height());
				moveDelta = new Point(0, height);
				return moveDelta;
			}
		}
		return delta;
	}
}
