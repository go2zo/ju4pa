package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.commands.SetBoundsCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionBendpointEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.util.SelectInDiagramHelper;
import org.eclipse.gmf.runtime.emf.core.util.EObjectAdapter;
import org.eclipse.gmf.runtime.gef.ui.internal.editpolicies.LineMode;
import org.eclipse.papyrus.uml.diagram.sequence.draw2d.routers.MessageRouter.RouterKind;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.Messages;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexOccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.OccurrenceSpecification;

@SuppressWarnings("restriction")
public class ApexMessageConnectionLineSegEditPolicy extends
		ConnectionBendpointEditPolicy {
	public ApexMessageConnectionLineSegEditPolicy() {
		super(LineMode.ORTHOGONAL_FREE);
	}

	@Override
	public Command getCommand(Request request) {
		if(isHorizontal()) {
			return super.getCommand(request);
		}
		return null;
	}

	/**
	 * apex updated
	 * 
	 * Move the anchors along with the line and update bendpoints accordingly.
	 */
	@Override
	protected Command getBendpointsChangedCommand(BendpointRequest request) {
		if((getHost().getViewer() instanceof ScrollingGraphicalViewer) && (getHost().getViewer().getControl() instanceof FigureCanvas)) {
			SelectInDiagramHelper.exposeLocation((FigureCanvas)getHost().getViewer().getControl(), request.getLocation().getCopy());
		}

		if(getHost() instanceof ConnectionNodeEditPart) {
			ConnectionNodeEditPart connectionPart = (ConnectionNodeEditPart)getHost();
			int oldY = ApexSequenceUtil.apexGetAbsolutePosition(connectionPart, SWT.BOTTOM);
			
			ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_MOVE);
			Point location = request.getLocation().getCopy();
			Point moveDalta = new Point(0, location.y() - oldY);
			cbRequest.setMoveDelta(moveDalta);
			cbRequest.setLocation(location);

			Command result = apexGetMoveConnectionCommand(cbRequest, connectionPart, false);
			return result;
		}
		return UnexecutableCommand.INSTANCE;
	}

	/* apex added start */
	private static final int MARGIN = 10;
	private static final int PADDING = 10;
	
	private static boolean flexiblePrev = false;
	/* apex added end */

	/**
	 * 
	 * @param request
	 * @param connectionPart
	 * @param moveAlone
	 * @return
	 */
	public static Command apexGetMoveConnectionCommand(ChangeBoundsRequest request, ConnectionNodeEditPart connectionPart, boolean moveAlone) {
		EObject message = connectionPart.resolveSemanticElement();
		if(message instanceof Message) {
			MessageEnd send = ((Message)message).getSendEvent();
			MessageEnd rcv = ((Message)message).getReceiveEvent();
			EditPart srcPart = connectionPart.getSource();
			LifelineEditPart srcLifelinePart = SequenceUtil.getParentLifelinePart(srcPart);
			EditPart tgtPart = connectionPart.getTarget();
			LifelineEditPart tgtLifelinePart = SequenceUtil.getParentLifelinePart(tgtPart);

			CompoundCommand compoudCmd = new CompoundCommand(Messages.MoveMessageCommand_Label);
			
			if(send instanceof OccurrenceSpecification && rcv instanceof OccurrenceSpecification && srcLifelinePart != null && tgtLifelinePart != null) {
				Point moveDelta = request.getMoveDelta().getCopy();
				int moveDeltaY = moveDelta.y();

				Point oldLocation = ApexSequenceUtil.apexGetAbsoluteRectangle(connectionPart).getLocation();
				if (oldLocation == null)
					return null;
				
				int y = oldLocation.y() + moveDeltaY;
				List<EditPart> empty = Collections.emptyList();
				
				int minY = Integer.MIN_VALUE, maxY = Integer.MAX_VALUE;
				int realMinY = Integer.MIN_VALUE;
				IGraphicalEditPart realPrevPart = null;	// ExecutionSpecificationEditPart 포함하여 가장 하위
				List<IGraphicalEditPart> prevParts = ApexSequenceUtil.apexGetPrevSiblingEditParts(connectionPart);
				List<IGraphicalEditPart> frontLinkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, true, false, true);
				for (IGraphicalEditPart part : prevParts) {
					minY = Math.max(minY, ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.BOTTOM) + MARGIN);
					if (realMinY < minY) {
						realMinY = minY;
					}
					
					if (part instanceof ConnectionNodeEditPart && !frontLinkedParts.contains(part)) {
						// activation중 가장 하위 검색. realMinY는 activation 포함 가장 하위 y값
						ConnectionNodeEditPart prevConnPart = (ConnectionNodeEditPart)part;
						EditPart prevSourcePart = prevConnPart.getSource();
						EditPart prevTargetPart = prevConnPart.getTarget();
						if (prevSourcePart instanceof AbstractExecutionSpecificationEditPart) {
							int tMinY = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevTargetPart, SWT.BOTTOM) + MARGIN;
							if (realMinY < tMinY) {
								realMinY = tMinY;
								realPrevPart = (IGraphicalEditPart)prevTargetPart;
							}
						}
						if (prevTargetPart instanceof AbstractExecutionSpecificationEditPart) {
							int ty = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevTargetPart, SWT.BOTTOM) + MARGIN;
							if (realMinY < ty) {
								realMinY = ty;
								realPrevPart = (IGraphicalEditPart)prevTargetPart;
							}
						}
					}
				}
				
				if (prevParts.size() == 0) {
					IFigure dotLine = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
					Rectangle dotLineBounds = dotLine.getBounds().getCopy();
					dotLine.translateToAbsolute(dotLineBounds);
					minY = dotLineBounds.y() + MARGIN;
					realMinY = minY;
				}
				
				if (flexiblePrev && realPrevPart instanceof AbstractExecutionSpecificationEditPart) {
					Dimension minimumSize = realPrevPart.getFigure().getMinimumSize();
					int minimumBottom = ApexSequenceUtil.apexGetAbsolutePosition(realPrevPart, SWT.TOP) + minimumSize.height();
					minY = Math.max(minY, minimumBottom);
				}
				else {
					minY = realMinY;
				}
				
				List<IGraphicalEditPart> nextParts = ApexSequenceUtil.apexGetNextSiblingEditParts(connectionPart);
				for (IGraphicalEditPart part : nextParts) {
					int ty = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP) - MARGIN;
					if (maxY > ty) {
						maxY = ty;
					}
				}

				if (moveAlone) {
					// Target인 Activation의 Minimumsize 이하로 줄어들 수 없음
					if (tgtPart instanceof AbstractExecutionSpecificationEditPart) {
						List sourceConnections = ((IGraphicalEditPart)tgtPart).getSourceConnections();
						if (sourceConnections == null || sourceConnections.size() == 0) {
							int minimumHeight = ((IGraphicalEditPart)tgtPart).getFigure().getMinimumSize().height();
							int bottom = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)tgtPart, SWT.BOTTOM);
							maxY = bottom - minimumHeight;
						}
					}
					y = Math.min(maxY, Math.max(minY, y));
					moveDeltaY = y - oldLocation.y();
					
					// source : AbstractExecutionSpecificationEditPart
					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart srcExecSpecEP = (IGraphicalEditPart)srcPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(srcExecSpecEP);
						Rectangle newBounds = oldBounds.getCopy();
						if (newBounds.bottom() < y + PADDING) {
							newBounds.height = y + PADDING - newBounds.y;
						}
						
						compoudCmd.add( createChangeBoundsCommand(srcExecSpecEP, oldBounds, newBounds, true) );
						compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
								(OccurrenceSpecification)send, y, newBounds, srcPart, srcLifelinePart, empty) );
					}
					else if (srcPart.equals(srcLifelinePart)) { // source : LifelineEditPart
						IFigure figure = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
						Rectangle oldBounds = figure.getBounds().getCopy();
						figure.translateToAbsolute(oldBounds);
						Rectangle newBounds = oldBounds.getCopy();
						if (newBounds.bottom() < y + MARGIN) {
							newBounds.height = y + MARGIN - oldBounds.y;
						}
						
						compoudCmd.add( createChangeBoundsCommand(srcLifelinePart, oldBounds, newBounds, true) );
						compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
								(OccurrenceSpecification)send, y, newBounds, srcPart, srcLifelinePart, empty) );
					}
					
					// target : AbstractExecutionSpecificationEditPart
					if (tgtPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart tgtExecSpecEP = (IGraphicalEditPart)tgtPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(tgtExecSpecEP);
						Rectangle newBounds = oldBounds.getCopy();
						newBounds.y = y;
						newBounds.height -= moveDeltaY; 
						
						compoudCmd.add( createChangeBoundsCommand(tgtExecSpecEP, oldBounds, newBounds, true) );
					}
				}
				else {
					if (moveDeltaY < 0) {
						y = Math.min(maxY, Math.max(minY, y));
						moveDeltaY = y - oldLocation.y();
					}
					
					// flexiblePrev인 경우, 상당 ExecutionSpecification의 크기 줄임
					if (flexiblePrev && realPrevPart instanceof AbstractExecutionSpecificationEditPart && realMinY > y) {
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(realPrevPart);
						Rectangle newBounds = oldBounds.getCopy();
						newBounds.height += (y - realMinY); 
						
						compoudCmd.add( createChangeBoundsCommand(realPrevPart, oldBounds, newBounds, true) );
					}
					
					Command sendMessageMoveCmd = null;
					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart srcExecSpecEP = (IGraphicalEditPart)srcPart;
						
						ConnectionNodeEditPart lastConnPart = null;
						int lastY = Integer.MIN_VALUE;
						List srcConnParts = srcExecSpecEP.getSourceConnections();
						Iterator iter = srcConnParts.iterator();
						while (iter.hasNext()) {
							ConnectionNodeEditPart srcConnPart = (ConnectionNodeEditPart)iter.next();
							EObject semanticElement = srcConnPart.resolveSemanticElement();
							if (semanticElement instanceof Message) {
								MessageEnd sendEvent = ((Message)semanticElement).getSendEvent();
								Point location = SequenceUtil.findLocationOfMessageOccurrence((GraphicalEditPart) srcExecSpecEP, (MessageOccurrenceSpecification) sendEvent);
//								Point location = ApexSequenceUtil.apexGetAbsoluteRectangle(srcConnPart).getLocation();
								if (lastY < location.y) {
									lastY = location.y;
									lastConnPart = srcConnPart;
								}
							}
						}
						
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(srcExecSpecEP);
						Rectangle newBounds = oldBounds.getCopy();

						if (connectionPart.equals(lastConnPart)) {
							newBounds.height = oldBounds.height + moveDeltaY;
							compoudCmd.add( createChangeBoundsCommand(srcExecSpecEP, oldBounds, newBounds, true) );
						}
						
						if (moveDeltaY > 0) {
							newBounds.height = oldBounds.height + moveDeltaY;
						}
						sendMessageMoveCmd = ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
								(OccurrenceSpecification)send, y, newBounds, srcPart, srcLifelinePart, empty);
					}
					else if (srcPart.equals(srcLifelinePart)) { // source : LifelineEditPart
						IFigure figure = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
						Rectangle oldBounds = figure.getBounds().getCopy();
						figure.translateToAbsolute(oldBounds);
						Rectangle newBounds = oldBounds.getCopy();
						if (newBounds.bottom() < y + MARGIN) {
							newBounds.height = y + MARGIN - oldBounds.y;
						}
						
						compoudCmd.add( createChangeBoundsCommand(srcLifelinePart, oldBounds, newBounds, true) );
						compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
								(OccurrenceSpecification)send, y, newBounds, srcPart, srcLifelinePart, empty) );
					}

					List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, false, true, false);
					for (IGraphicalEditPart linkedPart : linkedParts) {
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(linkedPart);
						Rectangle newBounds = oldBounds.getCopy();
						newBounds.y += moveDeltaY;
						
						compoudCmd.add( createChangeBoundsCommand(linkedPart, oldBounds, newBounds, true) );
					}
					
					if (moveDeltaY > 0) {
						linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, true, false, false);
						nextParts.removeAll(linkedParts);
						if (nextParts.size() > 0) {
							IGraphicalEditPart nextSiblingEditPart = nextParts.get(0);
							if (nextSiblingEditPart instanceof ConnectionNodeEditPart) {
								Command nextCmd = apexGetMoveConnectionCommand(request, (ConnectionNodeEditPart) nextSiblingEditPart, moveAlone);
								compoudCmd.add(nextCmd);
							}
							else {
								Command nextCmd = nextSiblingEditPart.getCommand(request);
								compoudCmd.add(nextCmd);
//								apexGetResizeOrMoveBelowItemsCommand(request, nextSiblingEditPart);
							}
						}
					}
					
					compoudCmd.add(sendMessageMoveCmd);
				}

				return compoudCmd.size() > 0 ? compoudCmd : null;
			}
		}
		
		return null;
	}
	
	/**
	 * Message보다 하위의 item들을 delta만큼 이동
	 * @param request
	 * @param abstractGraphicalEditPart
	 * @return
	 */
	private static Command apexGetResizeOrMoveBelowItemsCommand(ChangeBoundsRequest request, IGraphicalEditPart gep) {
		CompoundCommand command = new CompoundCommand();
		gep.getCommand(request);
		command.add(InteractionCompartmentXYLayoutEditPolicy.getCombinedFragmentResizeChildrenCommand(request, (GraphicalEditPart)gep));
		return command;
	}
	
	private static Command createChangeBoundsCommand(IGraphicalEditPart gep, Rectangle oldBounds, Rectangle newBounds, boolean isPreserveAnchorsPosition) {
		Command command = null;
		
		if (oldBounds.x == newBounds.x && oldBounds.y == newBounds.y &&
				oldBounds.width == newBounds.width && oldBounds.height == newBounds.height) {
			return null;
		}
		
		if (!isPreserveAnchorsPosition) {
			TransactionalEditingDomain editingDomain = gep.getEditingDomain();
			EObject element = gep.resolveSemanticElement();
			command = new ICommandProxy( new SetBoundsCommand(editingDomain, "", new EObjectAdapter(element), newBounds) );
		}
		else {
			ChangeBoundsRequest request = createChangeBoundsRequest(oldBounds, newBounds);
			command = gep.getCommand(request);
		}
		
		return command;
	}
	
	private static ChangeBoundsRequest createChangeBoundsRequest(Rectangle oldBounds, Rectangle newBounds) {
		ChangeBoundsRequest request = new ChangeBoundsRequest(REQ_RESIZE);
		Point moveDelta = new Point(newBounds.x - oldBounds.x, newBounds.y - oldBounds.y);
		Dimension sizeDelta = new Dimension(newBounds.width - oldBounds.width, newBounds.height - oldBounds.height);
		request.setMoveDelta(moveDelta);
		request.setSizeDelta(sizeDelta);
		if (oldBounds.y == newBounds.y)
			request.setResizeDirection(PositionConstants.SOUTH);
		else if (oldBounds.bottom() == newBounds.bottom())
			request.setResizeDirection(PositionConstants.NORTH);
		else
			request.setResizeDirection(PositionConstants.NORTH_SOUTH);
		return request;
	}
	
	/**
	 * don't show feedback if the drag is forbidden (message not horizontal).
	 */
	@Override
	public void showSourceFeedback(Request request) {
		if(request instanceof BendpointRequest) {
			/* apex improved start */
			super.showSourceFeedback(request);
			/* apex improved end */
			/* apex replaced
			if(isHorizontal()) {
				super.showSourceFeedback(request);
			}
			*/
		}
	}
	
	/**
	 * apex updated
	 */
	@Override
	protected void showMoveLineSegFeedback(BendpointRequest request) {
		/* apex added start */
		ConnectionNodeEditPart host = (ConnectionNodeEditPart)getHost();
		Connection connection = host.getConnectionFigure();
		
		Point location = request.getLocation().getCopy();
		connection.translateToRelative(location);
		
		PointList pl = connection.getPoints().getCopy();
		int dy = location.y - pl.getFirstPoint().y;
		for (int i = 0; i < pl.size(); i++) {
			Point p = pl.getPoint(i);
			p.y += dy;
			pl.setPoint(p, i);
		}
		
		PolylineConnection feedbackConnection = getDragSourceFeedbackFigure();
		feedbackConnection.setPoints(pl);
		/* apex added end */
	}

	/**
	 * apex updated
	 */
	public void eraseSourceFeedback(Request request) {
		/* apex added start */
		if (feedback != null)
			removeFeedback(feedback);
		feedback = null;
		/* apex added end */
		super.eraseSourceFeedback(request);
	}
	
	/* apex added start */
	private PolylineConnection feedback;
	/* apex added end */
	
	@Override
	protected PolylineConnection createDragSourceFeedbackConnection() {
		/* apex improved start */
		PolylineConnection connection = new PolylineConnection();
		connection.setLineWidth(1);
		connection.setLineStyle(Graphics.LINE_DASHDOT);
//		connection.setForegroundColor(((IGraphicalEditPart)getHost()).getFigure().getLocalForegroundColor());
		connection.setForegroundColor(new Color(null, 100, 100, 255));
		return connection;
		/* apex improved end */
		/* apex replaced
		return super.createDragSourceReedbackConnection();
		 */
	}
	
	/* apex added start */
	/**
	 * Returns feedback figure
	 * 
	 * @return
	 */
	protected PolylineConnection getDragSourceFeedbackFigure() {
		if (feedback == null) {
			feedback = createDragSourceFeedbackConnection();
			addFeedback(feedback);
		}
		return feedback;
	}
	/* apex added end */

	private boolean isHorizontal() {
		Connection connection = getConnection();
		RouterKind kind = RouterKind.getKind(connection, connection.getPoints());
		if(kind.equals(RouterKind.HORIZONTAL)) {
			return true;
		}
		return false;
	}

}
