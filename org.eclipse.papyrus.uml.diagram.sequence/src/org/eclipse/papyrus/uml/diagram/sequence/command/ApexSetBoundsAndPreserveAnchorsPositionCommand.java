package org.eclipse.papyrus.uml.diagram.sequence.command;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.papyrus.uml.diagram.common.commands.PreserveAnchorsPositionCommand;

public class ApexSetBoundsAndPreserveAnchorsPositionCommand extends
		PreserveAnchorsPositionCommand {
	
	private ApexSetBoundsForExecutionSpecificationCommand setBoundsCommand;

	public ApexSetBoundsAndPreserveAnchorsPositionCommand(ShapeNodeEditPart shapeEP,
			ApexSetBoundsForExecutionSpecificationCommand setBoundsCommand, int preserveAxis, IFigure figure, int resizeDirection) {
		super(shapeEP, null, preserveAxis, figure, resizeDirection);
		this.setBoundsCommand = setBoundsCommand;
	}

	@Override
	public Dimension getSizeDelta() {
		if (setBoundsCommand != null)
			return setBoundsCommand.getSizeDelta();
		return super.getSizeDelta();
	}
}