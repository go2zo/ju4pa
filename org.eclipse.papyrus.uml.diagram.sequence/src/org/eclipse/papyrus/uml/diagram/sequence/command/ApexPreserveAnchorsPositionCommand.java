package org.eclipse.papyrus.uml.diagram.sequence.command;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.papyrus.uml.diagram.common.commands.PreserveAnchorsPositionCommand;

public class ApexPreserveAnchorsPositionCommand
		extends PreserveAnchorsPositionCommand {

	public ApexPreserveAnchorsPositionCommand(ShapeNodeEditPart shapeEP,
			Dimension sizeDelta, int preserveAxis, IFigure figure, int resizeDirection) {
		super(shapeEP, sizeDelta, preserveAxis, figure, resizeDirection);
	}
}