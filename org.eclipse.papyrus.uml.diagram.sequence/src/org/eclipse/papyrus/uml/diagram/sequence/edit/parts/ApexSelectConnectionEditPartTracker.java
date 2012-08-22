package org.eclipse.papyrus.uml.diagram.sequence.edit.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.gmf.runtime.gef.ui.internal.l10n.Cursors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;

public class ApexSelectConnectionEditPartTracker extends SelectEditPartTracker {


	/**
	 * Key modifier for ignoring snap while dragging.  It's CTRL on Mac, and ALT on all
	 * other platforms.
	 */
	private static final int MODIFIER_CONSTRAINED_MOVE = SWT.SHIFT;
	private final int MODIFIER_NO_SNAPPING;
	private Request sourceRequest;
	private boolean bSourceFeedback = false;
	
	private PrecisionRectangle sourceRectangle;	
	private Point originalLocation = null;	
	
	
	/**
	 * Method SelectConnectionEditPartTracker.
	 * @param owner ConnectionNodeEditPart that creates and owns the tracker object
	 */
	public ApexSelectConnectionEditPartTracker(ConnectionEditPart owner) {
		super(owner);
		if (SWT.getPlatform().equals("carbon"))//$NON-NLS-1$
			MODIFIER_NO_SNAPPING = SWT.CTRL;
		else
			MODIFIER_NO_SNAPPING = SWT.ALT;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#handleButtonDown(int)
	 */
	protected boolean handleButtonDown(int button) {
		return super.handleButtonDown(button);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#handleButtonUp(int)
	 */
	protected boolean handleButtonUp(int button) {
		boolean bExecuteDrag = isInState(STATE_DRAG_IN_PROGRESS);
		
		boolean bRet = super.handleButtonUp(button);

		if (bExecuteDrag) {
			eraseSourceFeedback();
			setCurrentCommand(getCommand());
			executeCurrentCommand();
		}

		return bRet;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#handleDragInProgress()
	 */
	protected boolean handleDragInProgress() {
		if (isInState(STATE_DRAG_IN_PROGRESS)) {
			updateSourceRequest();
			showSourceFeedback();
			setCurrentCommand(getCommand());
		}
		return true;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#handleDragStarted()
	 */
	protected boolean handleDragStarted() {
		originalLocation = null;
		sourceRectangle = null;		
		return stateTransition(STATE_DRAG, STATE_DRAG_IN_PROGRESS);
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#calculateCursor()
	 */
	protected Cursor calculateCursor() {
		if (isInState(STATE_DRAG_IN_PROGRESS)) {
			return Cursors.CURSOR_SEG_MOVE;
		}
		
		return getConnection().getCursor();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.Tool#deactivate()
	 */
	public void deactivate() {
		if (!isInState(STATE_TERMINAL))
			eraseSourceFeedback();
		sourceRequest = null;
		super.deactivate();
	}

	/**
	 * @return boolean true if feedback is being displayed, false otherwise.
	 */
	private boolean isShowingFeedback() {
		return bSourceFeedback;
	}

	/**
	 * Method setShowingFeedback.
	 * @param bSet boolean to set the feedback flag on or off.
	 */
	private void setShowingFeedback(boolean bSet) {
		bSourceFeedback = bSet;
	}

	/**
	 * @see org.eclipse.gef.tools.AbstractTool#createOperationSet()
	 */
	protected List createOperationSet() {
		List<ConnectionEditPart> list = new ArrayList<ConnectionEditPart>();
		list.add(getConnectionEditPart());
		return list;
	}
	
	/**
	 * Method showSourceFeedback.
	 * Show the source drag feedback for the drag occurring
	 * within the viewer.
	 */
	private void showSourceFeedback() {
		List editParts = getOperationSet();
		for (int i = 0; i < editParts.size(); i++) {
			EditPart editPart = (EditPart) editParts.get(i);
			editPart.showSourceFeedback(getSourceRequest());
		}
		setShowingFeedback(true);
	}

	/**
	 * Method eraseSourceFeedback.
	 * Show the source drag feedback for the drag occurring
	 * within the viewer.
	 */
	private void eraseSourceFeedback() {	
		if (!isShowingFeedback())
			return;
		setShowingFeedback(false);
		List editParts = getOperationSet();

		for (int i = 0; i < editParts.size(); i++) {
			EditPart editPart = (EditPart) editParts.get(i);
			editPart.eraseSourceFeedback(getSourceRequest());
		}
	}

	/**
	 * Method getSourceRequest.
	 * @return Request
	 */
	private Request getSourceRequest() {
		if (sourceRequest == null)
			sourceRequest = createSourceRequest();
		return sourceRequest;
	}

	/**
	 * Creates the source request that is activated when the drag operation
	 * occurs.
	 * 
	 * @return a <code>Request</code> that is the newly created source request
	 */
	protected Request createSourceRequest() {
		ChangeBoundsRequest request = new ChangeBoundsRequest(REQ_MOVE);
		return request;
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#getCommand()
	 */
	protected Command getCommand() {
		return getSourceEditPart().getCommand(getSourceRequest());
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#getCommandName()
	 */
	protected String getCommandName() {
		return REQ_MOVE.toString();
	}

	/**
	 * @return the <code>Connection</code> that is referenced by the connection edit part.
	 */
	private Connection getConnection() {
		return (Connection) getConnectionEditPart().getFigure();
	}

	/**
	 * Method getConnectionEditPart.
	 * @return ConnectionEditPart
	 */
	private ConnectionEditPart getConnectionEditPart() {
		return (ConnectionEditPart)getSourceEditPart();
	}

	/* 
	 * (non-Javadoc)
	 * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
	 */
	protected String getDebugName() {
		return "ApexSelectionEditPartTracker: " + getCommandName(); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.gef.tools.SimpleDragTracker#updateSourceRequest()
	 */
	protected void updateSourceRequest() {
		ChangeBoundsRequest request = (ChangeBoundsRequest) getSourceRequest();	
			
		if (originalLocation == null){						
			originalLocation = getStartLocation().getCopy();
		}
			
		Dimension delta = getDragMoveDelta();
		
		request.setConstrainedMove(getCurrentInput().isModKeyDown(
				MODIFIER_CONSTRAINED_MOVE));
		request.setSnapToEnabled(!getCurrentInput().isModKeyDown(
				MODIFIER_NO_SNAPPING));
		
		Point moveDelta = new Point(delta.width, delta.height);
		request.getExtendedData().clear();
		request.setMoveDelta(moveDelta);
		snapPoint(request);

		request.setLocation(getLocation());
		request.setType(getCommandName());
	}	

	/**
	 * This method can be overridden by clients to customize the snapping
	 * behavior.
	 * 
	 * @param request
	 *            the <code>ChangeBoundsRequest</code> from which the move delta
	 *            can be extracted and updated
	 * @since 3.4
	 */
	protected void snapPoint(ChangeBoundsRequest request) {
		Point moveDelta = request.getMoveDelta();
		SnapToHelper snapToHelper = (SnapToHelper)getConnectionEditPart().getAdapter(SnapToHelper.class);
		
		Rectangle rect = new Rectangle(originalLocation.x, originalLocation.y, 1, 1);		
		if (sourceRectangle == null) {
			sourceRectangle = new PrecisionRectangle(rect);	
		}

		if (snapToHelper != null && request.isSnapToEnabled()) {
			PrecisionRectangle baseRect = sourceRectangle.getPreciseCopy();
			baseRect.translate(moveDelta);

			PrecisionPoint preciseDelta = new PrecisionPoint(moveDelta);
			snapToHelper.snapPoint(request, PositionConstants.HORIZONTAL
					| PositionConstants.VERTICAL, new PrecisionRectangle[] {
					baseRect}, preciseDelta);
			request.setMoveDelta(preciseDelta);
		}
	}
}
