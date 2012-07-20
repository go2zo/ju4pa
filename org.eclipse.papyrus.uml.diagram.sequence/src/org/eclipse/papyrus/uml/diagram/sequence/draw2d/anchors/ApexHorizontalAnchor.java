package org.eclipse.papyrus.uml.diagram.sequence.draw2d.anchors;

import java.text.Format;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gmf.runtime.draw2d.ui.figures.BaseSlidableAnchor;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableAnchor;

public class ApexHorizontalAnchor extends SlidableAnchor {

	static private int STRAIGHT_LINE_TOLERANCE = 0;

	private Point initLocation;
	
	public ApexHorizontalAnchor(IFigure f, PrecisionPoint p) {
		super(f, p);
		/* apex added start */
		Rectangle bounds = new PrecisionRectangle(f.getBounds());
		initLocation = new PrecisionPoint(p.preciseX() * bounds.preciseWidth()
				+ bounds.preciseX(), p.preciseY() * bounds.preciseHeight()
				+ bounds.preciseY());
		f.translateToAbsolute(initLocation);
		/* apex added end */
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
		
		/*8
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		for (int i = 1; i < 10; i++) {
			StackTraceElement trace = traces[i];
			System.out.println(trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
		}
		System.out.println("------------------------------------------------------");
		//*/
		
		// SEQ-LL-005
//		location.y = reference.y;
		
		return location;
	}
	
	public Point getReferencePoint() {
//		if (initLocation != null)
//			return getAnchorPosition();
		return super.getReferencePoint();
	}

	/**
	 * From relative reference returns the relative coordinates of the anchor
	 * Method's visibility can be changed as needed
	 */
	private Point getAnchorPosition() {
		PrecisionRectangle rBox = new PrecisionRectangle(getBox());
		if (isDefaultAnchor())
			return rBox.getCenter();
		PrecisionPoint relativeReference = BaseSlidableAnchor.getAnchorRelativeLocation(initLocation, rBox);

		return new PrecisionPoint(relativeReference.preciseX() * rBox.preciseWidth()
				+ rBox.preciseX(), relativeReference.preciseY() * rBox.preciseHeight()
				+ rBox.preciseY());
	}
}
