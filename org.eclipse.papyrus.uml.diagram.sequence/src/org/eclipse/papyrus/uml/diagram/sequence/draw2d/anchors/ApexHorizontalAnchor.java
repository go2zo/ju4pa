package org.eclipse.papyrus.uml.diagram.sequence.draw2d.anchors;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableAnchor;

public class ApexHorizontalAnchor extends SlidableAnchor {

	final private static char TERMINAL_DELIMITER_CHAR = '|';
	
	private int relativeY = -1;

	public ApexHorizontalAnchor(IFigure f, PrecisionPoint p, int relativeY) {
		super(f, p);
		this.relativeY = relativeY;
	}

	public ApexHorizontalAnchor(IFigure f, PrecisionPoint p) {
		super(f, p);
	}

	public ApexHorizontalAnchor(IFigure f) {
		super(f);
	}

	@Override
	public boolean isDefaultAnchor() {
		return false;
	}

	@Override
	public Point getLocation(Point reference) {
		Point ownReference = getReferencePoint();

		Point location = getLocation(ownReference, reference);
		if (location == null) {
			location = getLocation(new PrecisionPoint(getBox().getCenter()), reference);
			if (location == null) {
				location = getBox().getCenter();
			}
		}
		
		return location;
	}

	@Override
	public Point getReferencePoint() {
		Point reference = super.getReferencePoint();
		Rectangle rBox = getBox();
		if (relativeY > -1) {
			reference.setY(relativeY + rBox.y());
		}
		return reference;
	}
	
	@Override
	public String getTerminal() {
		StringBuffer sb = new StringBuffer();
		
		sb.append(super.getTerminal());
		if (relativeY > -1) {
			sb.append(TERMINAL_DELIMITER_CHAR);
			sb.append(relativeY);
		}
		
		return sb.toString();
	}
	
	public static int apexParseTerminalString(String terminal) {
		try {
			return Integer.parseInt(terminal.substring(terminal.indexOf(TERMINAL_DELIMITER_CHAR) + 1));
		} catch (Exception e) {
			return -1;
		}
	}
}