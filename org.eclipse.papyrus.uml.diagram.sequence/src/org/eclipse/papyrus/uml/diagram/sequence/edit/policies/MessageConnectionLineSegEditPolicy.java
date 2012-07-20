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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
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
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ActionExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.part.Messages;
import org.eclipse.papyrus.uml.diagram.sequence.util.ApexSequenceUtil;
import org.eclipse.papyrus.uml.diagram.sequence.util.OccurrenceSpecificationMoveHelper;
import org.eclipse.papyrus.uml.diagram.sequence.util.SequenceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.uml2.uml.ExecutionSpecification;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageEnd;
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
			
			Object constraint = getConnection().getRoutingConstraint();

			ChangeBoundsRequest cbRequest = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
			Point location = request.getLocation().getCopy();
			Point moveDalta = new Point(0, location.y() - oldY);
			cbRequest.setMoveDelta(moveDalta);

			return apexGetMoveConnectionCommand(cbRequest, connectionPart, true, false, false, false);
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
	public static Command apexGetMoveConnectionCommand(ChangeBoundsRequest request, ConnectionNodeEditPart connectionPart,
			boolean moveNext, boolean flexiblePrev, boolean changedSequence, boolean force) {
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
						if ((prevSourcePart instanceof ActionExecutionSpecificationEditPart || prevSourcePart instanceof BehaviorExecutionSpecificationEditPart)
								&& !prevSourcePart.equals(srcPart)) {
							int tMinY = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevTargetPart, SWT.BOTTOM) + MARGIN;
							if (realMinY < tMinY) {
								realMinY = tMinY;
								realPrevPart = (IGraphicalEditPart)prevTargetPart;
							}
						}
						if ((prevTargetPart instanceof ActionExecutionSpecificationEditPart || prevTargetPart instanceof BehaviorExecutionSpecificationEditPart)
								&& !prevTargetPart.equals(srcPart)) {
							int ty = ApexSequenceUtil.apexGetAbsolutePosition((IGraphicalEditPart)prevTargetPart, SWT.BOTTOM) + MARGIN;
							if (realMinY < ty) {
								realMinY = ty;
								realPrevPart = (IGraphicalEditPart)prevTargetPart;
							}
						}
					}
				}
				
				// 상단의 activation이 줄어들수 있는가?
				minY = flexiblePrev ? Math.min(minY - MARGIN + PADDING, realMinY) : Math.max(minY - MARGIN + PADDING, realMinY);
				
				IGraphicalEditPart realNextPart = null;
				List<IGraphicalEditPart> nextParts = ApexSequenceUtil.apexGetNextSiblingEditParts(connectionPart);
				for (IGraphicalEditPart part : nextParts) {
					int ty = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP) - MARGIN;
					if (ty < maxY) {
						realNextPart = part;
					}
					maxY = Math.min(maxY, ty);
				}

				int easyY = Math.min(maxY, Math.max(minY, y));
				if (!force && (moveDeltaY < 0 || !moveNext)) {
					y = easyY;
				}
				moveDeltaY = y - oldLocation.y();

				if (flexiblePrev && realPrevPart != null && realMinY > y - MARGIN) {
					EObject execSpec = realPrevPart.resolveSemanticElement();
					if (execSpec instanceof ExecutionSpecification) {
						OccurrenceSpecification finish = ((ExecutionSpecification)execSpec).getFinish();
						LifelineEditPart lifelinePart = SequenceUtil.getParentLifelinePart(realPrevPart);
						compoudCmd.add(OccurrenceSpecificationMoveHelper
								.getMoveOccurrenceSpecificationsCommand(finish, null, y - MARGIN, -1, lifelinePart, empty));
					}					
				}

				Command srcCmd = null;
				Command tgtCmd = null;

				if (srcPart instanceof ActionExecutionSpecificationEditPart || srcPart instanceof BehaviorExecutionSpecificationEditPart) {
					IGraphicalEditPart srcExecSpecEP = (IGraphicalEditPart)srcPart;
					EObject execSpec = srcExecSpecEP.resolveSemanticElement();
					
					if (execSpec instanceof ExecutionSpecification) {
						OccurrenceSpecification finish = ((ExecutionSpecification)execSpec).getFinish();
						int newBottom = ApexSequenceUtil.apexGetAbsolutePosition(srcExecSpecEP, SWT.BOTTOM) + moveDeltaY;
						srcCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand(finish, null, newBottom, -1, srcLifelinePart, empty);
					}
				} else {
//					srcCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)send, null, y, -1, srcLifelinePart, empty);
				}
				
				if (tgtPart instanceof ActionExecutionSpecificationEditPart || tgtPart instanceof BehaviorExecutionSpecificationEditPart) {
					IGraphicalEditPart tgtExecSpecEP = (IGraphicalEditPart)tgtPart;
					EObject execSpec = tgtExecSpecEP.resolveSemanticElement();
					
					Point nextMoveDelta = new Point(moveDelta.x(), moveDeltaY);

					if (execSpec instanceof ExecutionSpecification) {
						OccurrenceSpecification start = ((ExecutionSpecification)execSpec).getStart();
						OccurrenceSpecification finish = ((ExecutionSpecification)execSpec).getFinish();
						int top = moveDeltaY + ApexSequenceUtil.apexGetAbsolutePosition(tgtExecSpecEP, SWT.TOP);
						int bottom = moveDeltaY + ApexSequenceUtil.apexGetAbsolutePosition(tgtExecSpecEP, SWT.BOTTOM);

						if (!moveNext) {	// 하단이동 안함
							bottom = -1;
							finish = null;
						}
						
						tgtCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand(start, finish, top, bottom, tgtLifelinePart, empty);
					}
					
					if (moveNext) {
						ChangeBoundsRequest nextRequest = new ChangeBoundsRequest(RequestConstants.REQ_MOVE);
						nextRequest.setMoveDelta(nextMoveDelta);
						if (realNextPart instanceof ConnectionNodeEditPart)
							compoudCmd.add(apexGetMoveConnectionCommand(nextRequest, (ConnectionNodeEditPart)realNextPart, moveNext, false, true, true));
						else if (realNextPart != null)
							compoudCmd.add(apexGetResizeOrMoveBelowItemsCommand(nextRequest, realNextPart));
					}
				} else {
//					tgtCmd = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand((OccurrenceSpecification)rcv, null, y, -1, tgtLifelinePart, empty);
				}
				
				if(oldLocation != null) {
					int oldY = oldLocation.y;

					if(oldY < y) {
						compoudCmd.add(tgtCmd);
						compoudCmd.add(srcCmd);
					} else {
						compoudCmd.add(srcCmd);
						compoudCmd.add(tgtCmd);
					}
					return compoudCmd;
				}
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

	private boolean isHorizontal() {
		Connection connection = getConnection();
		RouterKind kind = RouterKind.getKind(connection, connection.getPoints());

		if(kind.equals(RouterKind.HORIZONTAL)) {
			return true;
		}
		return false;
	}

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


}
