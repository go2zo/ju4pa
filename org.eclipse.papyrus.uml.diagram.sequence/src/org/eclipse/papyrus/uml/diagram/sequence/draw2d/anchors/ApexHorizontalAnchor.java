package org.eclipse.papyrus.uml.diagram.sequence.draw2d.anchors;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableAnchor;

public class ApexHorizontalAnchor extends SlidableAnchor {

	static private int STRAIGHT_LINE_TOLERANCE = 0;

	public ApexHorizontalAnchor(IFigure f, PrecisionPoint p) {
		super(f, p);
	}

	public ApexHorizontalAnchor(IFigure f) {
		super(f);
	}

	public Point getLocation(Point reference) {
		Point ownReference = normalizeToStraightlineTolerance(reference, getReferencePoint(), STRAIGHT_LINE_TOLERANCE);
		
		Point location = getLocation(ownReference, reference);
		if (location == null) {
			location = getLocation(new PrecisionPoint(getBox().getCenter()), reference);
			if (location == null) {
				location = getBox().getCenter();
			}
		}
		
		// SEQ-LL-005
//		location.y = reference.y;
		
		return location;
	}

}
