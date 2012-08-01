package org.eclipse.papyrus.uml.diagram.sequence.edit.policies;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.commands.UnexecutableCommand;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editpolicies.ConnectionBendpointEditPolicy;
import org.eclipse.gmf.runtime.diagram.ui.util.SelectInDiagramHelper;
import org.eclipse.gmf.runtime.gef.ui.internal.editpolicies.LineMode;
import org.eclipse.papyrus.uml.diagram.sequence.draw2d.routers.MessageRouter.RouterKind;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.Messages;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexOccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.OccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.ExecutionSpecification;
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
				for (IGraphicalEditPart part : prevParts) {
					minY = Math.max(minY, ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.BOTTOM) + MARGIN);
					if (realMinY < minY) {
						realMinY = minY;
					}
					
					if (part instanceof ConnectionNodeEditPart) {
						// activation중 가장 하위 검색. realMinY는 activation 포함 가장 하위 y값
						ConnectionNodeEditPart prevConnPart = (ConnectionNodeEditPart)part;
						EditPart prevSourcePart = prevConnPart.getSource();
						EditPart prevTargetPart = prevConnPart.getTarget();
						if ((prevSourcePart instanceof AbstractExecutionSpecificationEditPart)
								&& !prevSourcePart.equals(srcPart)) {
							int tMinY = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevTargetPart, SWT.BOTTOM) + MARGIN;
							if (realMinY < tMinY) {
								realMinY = tMinY;
								realPrevPart = (IGraphicalEditPart)prevTargetPart;
							}
						}
						if ((prevTargetPart instanceof AbstractExecutionSpecificationEditPart)
								&& !prevTargetPart.equals(srcPart)) {
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
					Rectangle newBounds = null;
					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart executionSpecificationEP = (IGraphicalEditPart)srcPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(executionSpecificationEP);
						if (oldBounds != null && oldBounds.bottom() < y + PADDING) {
							ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
							cbRequest.setSizeDelta(new Dimension(0, y + PADDING - oldBounds.bottom()));
							cbRequest.setResizeDirection(PositionConstants.SOUTH);
							
							compoudCmd.add( executionSpecificationEP.getCommand(cbRequest) );
						}
						
						newBounds = oldBounds.getCopy();
						if (newBounds.bottom() < y + PADDING) {
							newBounds.height = y + PADDING - newBounds.y;
						}
					}
					else if (srcPart.equals(srcLifelinePart)) { // source : LifelineEditPart
						IFigure figure = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
						Rectangle oldBounds = figure.getBounds().getCopy();
						figure.translateToAbsolute(oldBounds);

						if (oldBounds.bottom() < y + MARGIN) {
							ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
							cbRequest.setSizeDelta(new Dimension(0, y + MARGIN - oldBounds.bottom()));
							cbRequest.setResizeDirection(PositionConstants.SOUTH);

							compoudCmd.add( srcLifelinePart.getCommand(cbRequest) );
						}
						
						newBounds = oldBounds.getCopy();
						if (newBounds.bottom() < y + MARGIN) {
							newBounds.height = y + MARGIN - oldBounds.y;
						}
					}
					compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
							(OccurrenceSpecification)send, y, newBounds, srcLifelinePart, empty) );
//					compoudCmd.add( OccurrenceSpecificationMoveHelper
//							.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty) );
					
					// target : AbstractExecutionSpecificationEditPart
					if (tgtPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart executionSpecificationEP = (IGraphicalEditPart)tgtPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(executionSpecificationEP);
						
						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
						cbRequest.setSizeDelta(new Dimension(0, oldBounds.y - y));
						cbRequest.setMoveDelta(new Point(0, y - oldBounds.y));
						cbRequest.setResizeDirection(PositionConstants.NORTH);
						compoudCmd.add( executionSpecificationEP.getCommand(cbRequest) );
//						EObject semanticElement = executionSpecificationEP.resolveSemanticElement();
//						if (semanticElement instanceof ExecutionSpecification) {
//							ExecutionSpecification executionSpecification = (ExecutionSpecification)semanticElement;
//							OccurrenceSpecification start = executionSpecification.getStart();
//							compoudCmd.add( OccurrenceSpecificationMoveHelper
//									.getMoveOccurrenceSpecificationsCommand(start, null, y, -1, tgtLifelinePart, empty) );
//						}
					}
				}
				else {
					if (moveDeltaY < 0) {
						y = Math.min(maxY, Math.max(minY, y));
						moveDeltaY = y - oldLocation.y();
					}
					
					// flexiblePrev인 경우, 상당 ExecutionSpecification의 크기 줄임
					if (flexiblePrev && realPrevPart instanceof AbstractExecutionSpecificationEditPart && realMinY > y) {
						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
						cbRequest.setSizeDelta(new Dimension(0, y - realMinY));
						cbRequest.setResizeDirection(PositionConstants.SOUTH);
						compoudCmd.add( realPrevPart.getCommand(cbRequest) );
//						EObject semanticElement = realPrevPart.resolveSemanticElement();
//						if (semanticElement instanceof ExecutionSpecification) {
//							OccurrenceSpecification finish = ((ExecutionSpecification)semanticElement).getFinish();
//							LifelineEditPart lifelinePart = SequenceUtil.getParentLifelinePart(realPrevPart);
//							compoudCmd.add( OccurrenceSpecificationMoveHelper
//									.getMoveOccurrenceSpecificationsCommand(finish, null, y - MARGIN, -1, lifelinePart, empty) );
//						}					
					}

					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
						AbstractExecutionSpecificationEditPart executionSpecificationEP = (AbstractExecutionSpecificationEditPart)srcPart;
						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
						cbRequest.setSizeDelta(new Dimension(0, moveDeltaY));
						cbRequest.setResizeDirection(PositionConstants.SOUTH);

						compoudCmd.add( executionSpecificationEP.getCommand(cbRequest) );

						Rectangle newBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(executionSpecificationEP);
						newBounds.height += moveDeltaY;

						List sourceConnections = executionSpecificationEP.getSourceConnections();
						int oldLocationY = oldLocation.y;
						for (Object sourceConnection : sourceConnections) {
							if (sourceConnection instanceof ConnectionNodeEditPart) {
								EObject semanticElement = ((ConnectionNodeEditPart)sourceConnection).resolveSemanticElement();
								if (semanticElement instanceof Message) {
									MessageEnd messageEnd = ((Message)semanticElement).getSendEvent();
									int tmp = SequenceUtil.findLocationOfMessageOccurrence(executionSpecificationEP, (MessageOccurrenceSpecification) messageEnd).y;
									
									if (oldLocationY < tmp || connectionPart.equals(sourceConnection)) {
										compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
												(OccurrenceSpecification)messageEnd, tmp + moveDeltaY, newBounds, srcLifelinePart, empty) );
//
//										List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(
//												(IGraphicalEditPart) sourceConnection, false, true, false);
//										
//										for (IGraphicalEditPart linkedPart : linkedParts) {
//											EObject semanticElement2 = linkedPart.resolveSemanticElement();
//											LifelineEditPart linkedLifelinePart = SequenceUtil.getParentLifelinePart(linkedPart);
//											if (semanticElement2 instanceof ExecutionSpecification) {
//												OccurrenceSpecification start = ((ExecutionSpecification)semanticElement2).getStart();
//												OccurrenceSpecification finish = ((ExecutionSpecification)semanticElement2).getFinish();
//												int yLocation1 = SequenceUtil.findLocationOfExecutionOccurrence(linkedLifelinePart, (ExecutionOccurrenceSpecification) start).y + moveDeltaY;
//												int yLocation2 = SequenceUtil.findLocationOfExecutionOccurrence(linkedLifelinePart, (ExecutionOccurrenceSpecification) finish).y + moveDeltaY;
//												compoudCmd.add( OccurrenceSpecificationMoveHelper
//														.getMoveOccurrenceSpecificationsCommand(start, finish, yLocation1, yLocation2, linkedLifelinePart, empty) );
//											}
//										}
									}
								}
							}
						}

						List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(executionSpecificationEP, false, true, false);
						for (IGraphicalEditPart linkedPart : linkedParts) {
							if (linkedPart.equals(executionSpecificationEP))
								continue;
							
							EObject semanticElement = linkedPart.resolveSemanticElement();
							LifelineEditPart linkedLifelinePart = SequenceUtil.getParentLifelinePart(linkedPart);
							if (semanticElement instanceof ExecutionSpecification) {
								OccurrenceSpecification start = ((ExecutionSpecification)semanticElement).getStart();
								OccurrenceSpecification finish = ((ExecutionSpecification)semanticElement).getFinish();
								int yLocation1 = SequenceUtil.findLocationOfExecutionOccurrence(linkedLifelinePart, (ExecutionOccurrenceSpecification) start).y + moveDeltaY;
								int yLocation2 = SequenceUtil.findLocationOfExecutionOccurrence(linkedLifelinePart, (ExecutionOccurrenceSpecification) finish).y + moveDeltaY;
								compoudCmd.add( OccurrenceSpecificationMoveHelper
										.getMoveOccurrenceSpecificationsCommand(start, finish, yLocation1, yLocation2, linkedLifelinePart, empty) );
							}
						}
						
						if (moveDeltaY > 0) {
							linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(executionSpecificationEP, true, false, false);
							nextParts.removeAll(linkedParts);
							IGraphicalEditPart realNextPart = null;
							int top = Integer.MAX_VALUE;
							for (IGraphicalEditPart part : nextParts) {
								int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP);
								if (top > tmp) {
									top = tmp;
									realNextPart = part;
								}
							}

							Point newMoveDelta = new Point(0, moveDeltaY);
							ChangeBoundsRequest newRequest = new ChangeBoundsRequest(REQ_MOVE);
							newRequest.setMoveDelta(newMoveDelta);

							if (realNextPart instanceof ConnectionNodeEditPart) {
								compoudCmd.add( apexGetMoveConnectionCommand(newRequest,
										(ConnectionNodeEditPart) realNextPart, false) );
							}
							else if (realNextPart != null) {
								compoudCmd.add( apexGetResizeOrMoveBelowItemsCommand(newRequest, realNextPart) );
							}
						}
					}
					else if (srcPart.equals(srcLifelinePart)) {
						IFigure figure = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
						Rectangle oldBounds = figure.getBounds().getCopy();
						figure.translateToAbsolute(oldBounds);

						if (oldBounds.bottom() < y + MARGIN) {
							ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
							cbRequest.setSizeDelta(new Dimension(0, y + MARGIN - oldBounds.bottom()));
							cbRequest.setResizeDirection(PositionConstants.SOUTH);

							compoudCmd.add( srcLifelinePart.getCommand(cbRequest) );
						}
					}
//					compoudCmd.add( OccurrenceSpecificationMoveHelper
//							.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty) );
					
//					List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, false, true, false);
//					for (IGraphicalEditPart linkedPart : linkedParts) {
//						EObject semanticElement = linkedPart.resolveSemanticElement();
//						LifelineEditPart linkedLifelinePart = SequenceUtil.getParentLifelinePart(linkedPart);
//						if (semanticElement instanceof ExecutionSpecification) {
//							OccurrenceSpecification start = ((ExecutionSpecification)semanticElement).getStart();
//							OccurrenceSpecification finish = ((ExecutionSpecification)semanticElement).getFinish();
//							int yLocation1 = SequenceUtil.findLocationOfExecutionOccurrence(linkedLifelinePart, (ExecutionOccurrenceSpecification) start).y + moveDeltaY;
//							int yLocation2 = SequenceUtil.findLocationOfExecutionOccurrence(linkedLifelinePart, (ExecutionOccurrenceSpecification) finish).y + moveDeltaY;
//							compoudCmd.add( OccurrenceSpecificationMoveHelper
//									.getMoveOccurrenceSpecificationsCommand(start, finish, yLocation1, yLocation2, linkedLifelinePart, empty) );
//						}
//					}
					
//					if (moveDeltaY > 0) {
//						List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, true, false, false);
//						nextParts.removeAll(linkedParts);
//						IGraphicalEditPart realNextPart = null;
//						int top = Integer.MAX_VALUE;
//						for (IGraphicalEditPart part : nextParts) {
//							int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP);
//							if (top > tmp) {
//								top = tmp;
//								realNextPart = part;
//							}
//						}
//
//						Point newMoveDelta = new Point(0, moveDeltaY);
//						ChangeBoundsRequest newRequest = new ChangeBoundsRequest(REQ_MOVE);
//						newRequest.setMoveDelta(newMoveDelta);
//
//						if (realNextPart instanceof ConnectionNodeEditPart) {
//							compoudCmd.add( apexGetMoveConnectionCommand(newRequest,
//									(ConnectionNodeEditPart) realNextPart, false) );
//						}
//						else if (realNextPart != null) {
//							compoudCmd.add( apexGetResizeOrMoveBelowItemsCommand(newRequest, realNextPart) );
//						}
//					}
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
//		command.add(InteractionCompartmentXYLayoutEditPolicy.getCombinedFragmentResizeChildrenCommand(request, (GraphicalEditPart)gep));
//		InteractionCompartmentXYLayoutEditPolicy.apexGetResizeOrMoveBelowItemsCommand(request, (GraphicalEditPart)gep, command);
		return command;
	}
	
	/**
	 * don't show feedback if the drag is forbidden (message not horizontal).
	 */
	@Override
	public void showSourceFeedback(Request request) {
		if(request instanceof BendpointRequest) {
			if(isHorizontal()) {
				super.showSourceFeedback(request);
			}
		}
	}
	
	/**
	 * apex updated
	 */
	@Override
	protected void showMoveLineSegFeedback(BendpointRequest request) {
		/* apex added start */
		ConnectionNodeEditPart host = (ConnectionNodeEditPart)getHost();
		
		Point location = request.getLocation().getCopy();
		host.getFigure().translateToRelative(location);
		
		Connection connection = host.getConnectionFigure();
		Point start = connection.getSourceAnchor().getReferencePoint();
		Point end = connection.getTargetAnchor().getReferencePoint();
//		Point start = SequenceUtil.getAbsoluteEdgeExtremity(host, true);
//		Point end = SequenceUtil.getAbsoluteEdgeExtremity(host, false);
		start.setY(location.y());
		end.setY(location.y());
		
		PolylineConnection feedbackConnection = getDragSourceFeedbackFigure();
		feedbackConnection.setStart(start);
		feedbackConnection.setEnd(end);
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
		connection.setForegroundColor(((IGraphicalEditPart)getHost()).getFigure().getLocalForegroundColor());
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
