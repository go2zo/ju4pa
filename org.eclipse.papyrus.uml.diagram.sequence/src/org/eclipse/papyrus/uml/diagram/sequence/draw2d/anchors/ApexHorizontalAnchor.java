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
		
		/*8
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		for (int i = 1; i < 10; i++) {
			StackTraceElement trace = traces[i];
			System.out.println(trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
		}
		System.out.println("------------------------------------------------------");
		//*/
		/*8
		System.out.println("location=" + location + " reference=" + reference);
		System.out.println("======================================================");
		//*/
		
		// SEQ-LL-005
		location.y = reference.y;
		return location;
	}

	@Override
	public Point getReferencePoint() {
		/*8
		StackTraceElement[] traces = Thread.currentThread().getStackTrace();
		for (int i = 1; i < 10; i++) {
			StackTraceElement trace = traces[i];
			System.out.println(trace.getClassName() + ":" + trace.getMethodName() + "(" + trace.getLineNumber() + ")");
		}
		System.out.println("======================================================");
		//*/
		Point reference = super.getReferencePoint();
		/*8
		System.out.println("reference=" + reference);
		System.out.println("======================================================");
		//*/
		return reference;
	}

}
