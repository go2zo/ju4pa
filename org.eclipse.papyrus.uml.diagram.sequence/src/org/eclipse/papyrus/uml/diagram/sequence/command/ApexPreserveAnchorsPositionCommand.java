package org.eclipse.papyrus.uml.diagram.sequence.command;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.notation.IdentityAnchor;
import org.eclipse.papyrus.uml.diagram.common.commands.PreserveAnchorsPositionCommand;
import org.eclipse.papyrus.uml.diagram.sequence.draw2d.anchors.ApexHorizontalAnchor;

public class ApexPreserveAnchorsPositionCommand
		extends PreserveAnchorsPositionCommand {

	public ApexPreserveAnchorsPositionCommand(ShapeNodeEditPart shapeEP,
			Dimension sizeDelta, int preserveAxis, IFigure figure, int resizeDirection) {
		super(shapeEP, sizeDelta, preserveAxis, figure, resizeDirection);
	}

	@Override
	protected String getNewIdStr(IdentityAnchor anchor) {
		String idStr = super.getNewIdStr(anchor);
		PrecisionPoint pp = ApexHorizontalAnchor.parseTerminalString(idStr);
		int anchorYPos = ApexHorizontalAnchor.apexParseTerminalString(anchor.getId());
		idStr = (new ApexHorizontalAnchor(null, pp, anchorYPos)).getTerminal();
		return idStr;
	}
}