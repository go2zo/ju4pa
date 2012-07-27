package org.eclipse.papyrus.uml.diagram.sequence.command;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.papyrus.uml.diagram.common.commands.PreserveAnchorsPositionCommand;

public class ApexPreserveAnchorsPositionCommand
		extends PreserveAnchorsPositionCommand {
	
	private ApexSetBoundsForExecutionSpecificationCommand setBoundsCommand;

	public ApexPreserveAnchorsPositionCommand(ShapeNodeEditPart shapeEP,
			ApexSetBoundsForExecutionSpecificationCommand setBoundsCommand, int preserveAxis) {
		super(shapeEP, null, preserveAxis);
		this.setBoundsCommand = setBoundsCommand;
	}

	@Override
	public Dimension getSizeDelta() {
		if (setBoundsCommand != null)
			return setBoundsCommand.getSizeDelta();
		return super.getSizeDelta();
	}
	
}
