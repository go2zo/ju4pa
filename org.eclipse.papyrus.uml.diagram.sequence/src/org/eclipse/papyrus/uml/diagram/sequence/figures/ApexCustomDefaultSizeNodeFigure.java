package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.gmf.runtime.draw2d.ui.figures.BaseSlidableAnchor;
import org.eclipse.gmf.runtime.gef.ui.figures.DefaultSizeNodeFigure;
import org.eclipse.gmf.runtime.gef.ui.figures.SlidableAnchor;
import org.eclipse.papyrus.uml.diagram.sequence.draw2d.anchors.ApexHorizontalAnchor;

public class ApexCustomDefaultSizeNodeFigure extends DefaultSizeNodeFigure {

	public ApexCustomDefaultSizeNodeFigure(Dimension defSize) {
		super(defSize);
	}

	public ApexCustomDefaultSizeNodeFigure(int width, int height) {
		super(width, height);
	}

	@Override
	protected ConnectionAnchor createDefaultAnchor() {
		return new ApexHorizontalAnchor(this);
	}

	protected ConnectionAnchor apexCreateAnchor(PrecisionPoint p, int y) {
		if (p==null)
			return createDefaultAnchor();
		return new ApexHorizontalAnchor(this, p, y);
	}

	@Override
	protected ConnectionAnchor createConnectionAnchor(Point p) {
		if (p == null) {
			return getConnectionAnchor(szAnchor);
		}
		else {
			Point temp = p.getCopy();
			translateToRelative(temp);
			PrecisionPoint pt = BaseSlidableAnchor.getAnchorRelativeLocation(temp, getBounds());
			if (isDefaultAnchorArea(pt))
				return getConnectionAnchor(szAnchor);
			/* apex improved start */
			return apexCreateAnchor(pt, temp.y() - getBounds().y());
			/* apex improved end */
			/* apex replaced
			return createAnchor(pt);
			 */
		}
	}
	
	@Override
	public ConnectionAnchor getConnectionAnchor(String terminal) {

		ConnectionAnchor connectAnchor =
			(ConnectionAnchor) getConnectionAnchors().get(terminal);
		if (connectAnchor == null) {
			if (terminal.equals(szAnchor)) {
				// get a new one - this figure doesn't support static anchors
				connectAnchor = createDefaultAnchor();
				getConnectionAnchors().put(terminal,connectAnchor);
			}
			else {
				/* apex improved start */
				connectAnchor = apexCreateAnchor(SlidableAnchor.parseTerminalString(terminal),
						ApexHorizontalAnchor.apexParseTerminalString(terminal));
				/* apex improved end */
				/* apex replaced
				connectAnchor = createAnchor(SlidableAnchor.parseTerminalString(terminal));
				 */
			}
		}

		return connectAnchor;
	}
	
	@Override
	protected boolean isDefaultAnchorArea(PrecisionPoint p) {
		/* apex improved start */
		return false;
		/* apex improved end */
		/* apex replaced
		return super.isDefaultAnchorArea(p);
		 */
	}

}
