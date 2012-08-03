/*****************************************************************************
 * Copyright (c) 2010 CEA
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Atos Origin - Initial API and implementation
 *
 *****************************************************************************/
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

/**
 * This bendpoint edit policy is used to allow drag of horizontal messages and forbid drag otherwise.
 * 
 * @author mvelten
 * 
 */
@SuppressWarnings("restriction")
public class MessageConnectionLineSegEditPolicy extends ConnectionBendpointEditPolicy {

	public MessageConnectionLineSegEditPolicy() {
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
			/* apex added start */
			ConnectionNodeEditPart connectionPart = (ConnectionNodeEditPart)getHost();
			int oldY = ApexSequenceUtil.apexGetAbsolutePosition(connectionPart, SWT.BOTTOM);
			
			ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_MOVE);
			Point location = request.getLocation().getCopy();
			Point moveDalta = new Point(0, location.y() - oldY);
			cbRequest.setMoveDelta(moveDalta);
			cbRequest.setLocation(location);

			Command result = apexGetMoveConnectionCommand(cbRequest, connectionPart, false);
			return result;
			/* apex added end */
			/* apex replaced
//			ConnectionNodeEditPart connectionPart = (ConnectionNodeEditPart)getHost();
//			EObject message = connectionPart.resolveSemanticElement();
//			if(message instanceof Message) {
//				MessageEnd send = ((Message)message).getSendEvent();
//				MessageEnd rcv = ((Message)message).getReceiveEvent();
//				EditPart srcPart = connectionPart.getSource();
//				LifelineEditPart srcLifelinePart = SequenceUtil.getParentLifelinePart(srcPart);
//				EditPart tgtPart = connectionPart.getTarget();
//				LifelineEditPart tgtLifelinePart = SequenceUtil.getParentLifelinePart(tgtPart);
//				if(send instanceof OccurrenceSpecification && rcv instanceof OccurrenceSpecification && srcLifelinePart != null && tgtLifelinePart != null) {
//					int y = request.getLocation().y;
//					List<EditPart> empty = Collections.emptyList();
//					Command srcCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty);
//					Command tgtCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)rcv, null, y, -1, tgtLifelinePart, empty);
//					CompoundCommand compoudCmd = new CompoundCommand(Messages.MoveMessageCommand_Label);
//					/*
//					 * Take care of the order of commands, to make sure target is always bellow the source.
//					 * Otherwise, moving the target above the source would cause order conflict with existing CF.
//					 */
//					Point oldLocation = SequenceUtil.getAbsoluteEdgeExtremity(connectionPart, true);
//					if(oldLocation != null) {
//						int oldY = oldLocation.y;
//						if(oldY < y) {
//							compoudCmd.add(tgtCmd);
//							compoudCmd.add(srcCmd);
//						} else {
//							compoudCmd.add(srcCmd);
//							compoudCmd.add(tgtCmd);
//						}
//						return compoudCmd;
//					}
//				}
//			}
//			*/
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
	 * @param moveNext
	 * @param flexiblePrev
	 * @param changedSequence
	 * @param force
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
					System.out.println("maxY=" + maxY + " minY=" + minY + " realMinY=" + realMinY);
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
					
					// target : AbstractExecutionSpecificationEditPart
					if (tgtPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart executionSpecificationEP = (IGraphicalEditPart)tgtPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(executionSpecificationEP);
						
						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
						cbRequest.setSizeDelta(new Dimension(0, oldBounds.y - y));
						cbRequest.setMoveDelta(new Point(0, y - oldBounds.y));
						cbRequest.setResizeDirection(PositionConstants.NORTH);
						compoudCmd.add( executionSpecificationEP.getCommand(cbRequest) );
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
					}

//					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
//						AbstractExecutionSpecificationEditPart executionSpecificationEP = (AbstractExecutionSpecificationEditPart)srcPart;
//						
//						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
//						cbRequest.setSizeDelta(new Dimension(0, moveDeltaY));
//						cbRequest.setResizeDirection(PositionConstants.SOUTH);
//
//						Rectangle newBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(executionSpecificationEP);
//						newBounds.height += moveDeltaY;
//
//						List sourceConnections = executionSpecificationEP.getSourceConnections();
//						int oldLocationY = oldLocation.y;
//						boolean moveToBelow = false;
//						for (Object sourceConnection : sourceConnections) {
//							if (sourceConnection instanceof ConnectionNodeEditPart) {
//								EObject semanticElement = ((ConnectionNodeEditPart)sourceConnection).resolveSemanticElement();
//								if (semanticElement instanceof Message) {
//									MessageEnd messageEnd = ((Message)semanticElement).getSendEvent();
//									int tmp = SequenceUtil.findLocationOfMessageOccurrence(executionSpecificationEP, (MessageOccurrenceSpecification) messageEnd).y;
//									
//									if ((moveDeltaY > 0 && oldLocationY < tmp) || connectionPart.equals(sourceConnection)) {
//										moveToBelow = true;
//										
//										compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
//												(OccurrenceSpecification)messageEnd, tmp + moveDeltaY, newBounds, srcLifelinePart, empty) );
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
//									}
//								}
//							}
//						}
//						
//						compoudCmd.add( executionSpecificationEP.getCommand(cbRequest) );
//
//						if (moveDeltaY > 0) {
//							nextParts.removeAll( ApexSequenceUtil.apexGetLinkedEditPartList(executionSpecificationEP, true, false, false) );
//							IGraphicalEditPart realNextPart = null;
//							int top = Integer.MAX_VALUE;
//							for (IGraphicalEditPart part : nextParts) {
//								int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP);
//								if (top > tmp) {
//									top = tmp;
//									realNextPart = part;
//								}
//							}
//
//							Point newMoveDelta = new Point(0, moveDeltaY);
//							ChangeBoundsRequest newRequest = new ChangeBoundsRequest(REQ_MOVE);
//							newRequest.setMoveDelta(newMoveDelta);
//
//							if (realNextPart instanceof ConnectionNodeEditPart) {
//								compoudCmd.add( apexGetMoveConnectionCommand(newRequest, (ConnectionNodeEditPart) realNextPart, false) );
//							}
//							else if (realNextPart != null) {
//								compoudCmd.add( apexGetResizeOrMoveBelowItemsCommand(newRequest, realNextPart) );
//							}
//						}
//					}
//					else if (srcPart.equals(srcLifelinePart)) {
//						IFigure figure = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
//						Rectangle oldBounds = figure.getBounds().getCopy();
//						figure.translateToAbsolute(oldBounds);
//
//						if (oldBounds.bottom() < y + MARGIN) {
//							ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
//							cbRequest.setSizeDelta(new Dimension(0, y + MARGIN - oldBounds.bottom()));
//							cbRequest.setResizeDirection(PositionConstants.SOUTH);
//
//							compoudCmd.add( srcLifelinePart.getCommand(cbRequest) );
//						}
//						
//						Rectangle newBounds = oldBounds.getCopy();
//						newBounds.height = y + MARGIN - newBounds.y;
//						
//						compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
//								(OccurrenceSpecification)send, y, newBounds, srcLifelinePart, empty) );
//					}
					
					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart srcExecSpecEP = (IGraphicalEditPart)srcPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(srcExecSpecEP);
						Rectangle newBounds = oldBounds.getCopy(); 
						
						compoudCmd.add( ApexOccurrenceSpecificationMoveHelper.getMoveMessageOccurrenceSpecificationsCommand(
								(OccurrenceSpecification) send, y, newBounds, srcLifelinePart, empty));
					}
					
					List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, false, true, false);
					for (IGraphicalEditPart linkedPart : linkedParts) {
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
	
	public static Command apexGetMoveConnectionCommand2(ChangeBoundsRequest request, ConnectionNodeEditPart connectionPart, boolean moveAlone) {
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
				
				IGraphicalEditPart prevPart = getPrevSiblingPart(connectionPart);
				IGraphicalEditPart realPrevPart = getRealPrevSiblingPart(connectionPart);
				
				int minY = getApproachHight(prevPart, SWT.BOTTOM);
				int realMinY = getApproachHight(realPrevPart, SWT.BOTTOM);
				if (prevPart == null) {
					IFigure dotLine = srcLifelinePart.getPrimaryShape().getFigureLifelineDotLineFigure();
					Rectangle dotLineBounds = dotLine.getBounds().getCopy();
					dotLine.translateToAbsolute(dotLineBounds);
					minY = dotLineBounds.y() + MARGIN;
					realMinY = minY;
				}
				
				// 상단의 activation이 줄어들수 있는가?
				if (flexiblePrev && realPrevPart instanceof AbstractExecutionSpecificationEditPart) {
					Dimension minimumSize = realPrevPart.getFigure().getMinimumSize();
					int minimumBottom = ApexSequenceUtil.apexGetAbsolutePosition(realPrevPart, SWT.TOP) + minimumSize.height();
					minY = Math.max(minY, minimumBottom);
				}
				else {
					minY = realMinY;
				}
				
				IGraphicalEditPart nextPart = getNextSiblingPart(connectionPart);
				IGraphicalEditPart realNextPart = getRealNextSiblingPart(connectionPart);

				int maxY = getApproachHight(nextPart, SWT.TOP);
				
//				Command srcCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty);
//				Command tgtCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)rcv, null, y, -1, tgtLifelinePart, empty);
				Command srcCmd = null;
				Command tgtCmd = null;
				
				// 선택된 Message 혼자 이동
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
					
					srcCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty);
					if (srcPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart srcGEP = (IGraphicalEditPart)srcPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(srcGEP);
						if (oldBounds != null && oldBounds.bottom() < y + PADDING) {
							ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
							cbRequest.setSizeDelta(new Dimension(0, y + PADDING - oldBounds.bottom()));
							cbRequest.setResizeDirection(PositionConstants.SOUTH);
							
							srcCmd = srcCmd.chain(srcGEP.getCommand(cbRequest));
							
//							ApexSetBoundsAndMoveMessageCommand sbammCommand = new ApexSetBoundsAndMoveMessageCommand(
//									connectionPart.getEditingDomain(), srcCmd, (OccurrenceSpecification) send, y, srcLifelinePart, empty);
//							srcCmd = new ICommandProxy(sbammCommand);
						}
					}
					compoudCmd.add(srcCmd);
					
					if (tgtPart instanceof AbstractExecutionSpecificationEditPart) {
						IGraphicalEditPart tgtGEP = (IGraphicalEditPart)tgtPart;
						Rectangle oldBounds = ApexSequenceUtil.apexGetAbsoluteRectangle(tgtGEP);
						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
						cbRequest.setSizeDelta(new Dimension(0, oldBounds.y() - y));
						cbRequest.setMoveDelta(new Point(0, y - oldBounds.y()));
						cbRequest.setResizeDirection(PositionConstants.NORTH);
						tgtCmd = tgtGEP.getCommand(cbRequest);
						
						compoudCmd.add(tgtCmd);
					}
				}
				// 하단 모두 이동
				else {
					// 위로 이동
					if (moveDeltaY < 0) {
						y = Math.min(maxY, Math.max(minY, y));
						moveDeltaY = y - oldLocation.y();
					}

					srcCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty);
					if ( srcPart instanceof AbstractExecutionSpecificationEditPart && realNextPart instanceof ConnectionNodeEditPart
							&& !srcPart.equals(((ConnectionNodeEditPart)realNextPart).getSource()) ) {
						IGraphicalEditPart srcGEP = (IGraphicalEditPart)srcPart;

						ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(REQ_RESIZE);
						cbRequest.setSizeDelta(new Dimension(0, moveDeltaY));
						cbRequest.setResizeDirection(PositionConstants.SOUTH);

						srcCmd = srcCmd.chain(srcGEP.getCommand(cbRequest));
					}
					compoudCmd.add(srcCmd);
					
					List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(connectionPart, false, true, false);
					for (IGraphicalEditPart linkedPart : linkedParts) {
						EObject semantic = linkedPart.resolveSemanticElement();
						LifelineEditPart linkedLifelinePart = SequenceUtil.getParentLifelinePart(linkedPart);
						if (semantic instanceof ExecutionSpecification) {
							OccurrenceSpecification start = ((ExecutionSpecification)semantic).getStart();
							OccurrenceSpecification finish = ((ExecutionSpecification)semantic).getFinish();
							int top = moveDeltaY + ApexSequenceUtil.apexGetAbsolutePosition(linkedPart, SWT.TOP);
							int bottom = moveDeltaY + ApexSequenceUtil.apexGetAbsolutePosition(linkedPart, SWT.BOTTOM);
							compoudCmd.add( OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand(start, finish, top, bottom, linkedLifelinePart, empty) );
						}
					}
					
					if (moveDeltaY > 0 && realNextPart != null) {
						IGraphicalEditPart bottomPart = connectionPart;
						int bottom = ApexSequenceUtil.apexGetAbsolutePosition(bottomPart, SWT.BOTTOM);
						for (IGraphicalEditPart linkedPart : linkedParts) {
							int tmp = ApexSequenceUtil.apexGetAbsolutePosition(linkedPart, SWT.BOTTOM);
							if (bottom < tmp) {
								bottom = tmp;
								bottomPart = linkedPart;
							}
						}
						bottom += MARGIN + moveDeltaY;
						
						int top = ApexSequenceUtil.apexGetAbsolutePosition(realNextPart, SWT.TOP);

						if (bottom > top) {
							Point newMoveDelta = new Point(0, bottom - top);
							ChangeBoundsRequest newRequest = new ChangeBoundsRequest(REQ_MOVE);
							newRequest.setMoveDelta(newMoveDelta);

							if (realNextPart instanceof ConnectionNodeEditPart) {
								compoudCmd.add( apexGetMoveConnectionCommand2(newRequest,
										(ConnectionNodeEditPart) realNextPart, false) );
							}
							else {
								compoudCmd.add( apexGetResizeOrMoveBelowItemsCommand(newRequest, realNextPart) );
							}
						}
					}
				}
				
//				if(oldLocation != null) {
//					int oldY = oldLocation.y();
//System.out.println(srcCmd.canExecute() + " " + tgtCmd.canExecute());
//					if(oldY < y) {
//						compoudCmd.add(tgtCmd);
//						compoudCmd.add(srcCmd);
//					} else {
//						compoudCmd.add(srcCmd);
//						compoudCmd.add(tgtCmd);
//					}
//					return compoudCmd;
//				}
			}
			System.out.println(compoudCmd.canExecute());
			return compoudCmd;
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

	@Override
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
	
	/* apex added start */
	private static IGraphicalEditPart getNextSiblingPart(ConnectionNodeEditPart editPart) {
		int y = Integer.MAX_VALUE;
		IGraphicalEditPart result = null;
		List<IGraphicalEditPart> parts = ApexSequenceUtil.apexGetNextSiblingEditParts(editPart);
		for (IGraphicalEditPart part : parts) {
			int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP);
			if (y > tmp) {
				y = tmp;
				result = part;
			}
		}
		return result;
	}
	
	private static IGraphicalEditPart getRealNextSiblingPart(ConnectionNodeEditPart editPart) {
		int y = Integer.MAX_VALUE;
		IGraphicalEditPart result = null;
		List<IGraphicalEditPart> parts = ApexSequenceUtil.apexGetNextSiblingEditParts(editPart);
		List<IGraphicalEditPart> linkedParts = ApexSequenceUtil.apexGetLinkedEditPartList(editPart, true, false, false);
		parts.removeAll(linkedParts);
		for (IGraphicalEditPart part : parts) {
			int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP);
			if (y > tmp) {
				y = tmp;
				result = part;
			}
		}
		return result;
	}

	private static IGraphicalEditPart getPrevSiblingPart(ConnectionNodeEditPart editPart) {
		int y = Integer.MIN_VALUE;
		IGraphicalEditPart result = null;
		List<IGraphicalEditPart> parts = ApexSequenceUtil.apexGetPrevSiblingEditParts(editPart);
		for (IGraphicalEditPart part : parts) {
			int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.BOTTOM);
			if (y < tmp) {
				y = tmp;
				result = part;
			}
		}
		return result;
	}
	
	private static IGraphicalEditPart getRealPrevSiblingPart(ConnectionNodeEditPart editPart) {
		int y = Integer.MIN_VALUE;
		IGraphicalEditPart result = null;
		List<IGraphicalEditPart> parts = ApexSequenceUtil.apexGetPrevSiblingEditParts(editPart);
		for (IGraphicalEditPart part : parts) {
			int tmp = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.BOTTOM);
			if (y < tmp) {
				y = tmp;
				result = part;
			}
			
			if (part instanceof ConnectionNodeEditPart) {
				// activation중 가장 하위 검색. realMinY는 activation 포함 가장 하위 y값
				ConnectionNodeEditPart prevConnPart = (ConnectionNodeEditPart)part;
				EditPart prevSourcePart = prevConnPart.getSource();
				EditPart prevTargetPart = prevConnPart.getTarget();
				if (prevSourcePart instanceof AbstractExecutionSpecificationEditPart && !prevSourcePart.equals(editPart.getSource())) {
					tmp = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevSourcePart, SWT.BOTTOM) + MARGIN;
					if (y < tmp) {
						y = tmp;
						result = (IGraphicalEditPart)prevSourcePart;
					}
				}
				if (prevTargetPart instanceof AbstractExecutionSpecificationEditPart && !prevTargetPart.equals(editPart.getSource())) {
					tmp = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevTargetPart, SWT.BOTTOM) + MARGIN;
					if (y < tmp) {
						y = tmp;
						result = (IGraphicalEditPart)prevTargetPart;
					}
				}
			}
		}
		return result;
	}
	
	private static int getApproachHight(IGraphicalEditPart editPart, int direction) {
		if (editPart == null)
			return direction == SWT.TOP ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		int hight = ApexSequenceUtil.apexGetAbsolutePosition(editPart, direction);
		return direction == SWT.TOP ? hight - MARGIN : hight + MARGIN;
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

	/* apex deleted
	final private static char TERMINAL_START_CHAR = '(';

	final private static char TERMINAL_DELIMITER_CHAR = ',';

	final private static char TERMINAL_END_CHAR = ')';

	private static String composeTerminalString(PrecisionPoint p) {
		StringBuffer s = new StringBuffer(24);
		s.append(TERMINAL_START_CHAR); // 1 char
		s.append(p.preciseX()); // 10 chars
		s.append(TERMINAL_DELIMITER_CHAR); // 1 char
		s.append(p.preciseY()); // 10 chars
		s.append(TERMINAL_END_CHAR); // 1 char
		return s.toString(); // 24 chars max (+1 for safety, i.e. for string termination)
	}
	 */

}
