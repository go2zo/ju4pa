package org.eclipse.papyrus.uml.diagram.sequence.figures;

import org.eclipse.gmf.runtime.gef.ui.figures.NodeFigure;


public class ApexCustomLifelineDotLineCustomFigure extends LifelineDotLineCustomFigure {
	
	@Override
	protected NodeFigure apexCreateDashLineRectangle() {
		return new ApexCustomNodeFigure();
	}
}
