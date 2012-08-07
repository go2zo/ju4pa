package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.gef.Handle;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ResizableShapeEditPolicy;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.IMapMode;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeUtil;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;

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
		if (request.getMoveDelta() != null && request.getMoveDelta().y != 0)
			return UnexecutableCommand.INSTANCE;
		return super.getMoveCommand(request);
	}

	@Override
	protected Command getResizeCommand(ChangeBoundsRequest request) {
		return super.getResizeCommand(request);
	}

}