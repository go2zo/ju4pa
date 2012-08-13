package org.eclipse.papyrus.uml.diagram.sequence.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.emf.commands.core.command.AbstractTransactionalCommand;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.util.OccurrenceSpecificationMoveHelper;
import org.eclipse.uml2.uml.OccurrenceSpecification;

public class ApexSetBoundsAndMoveMessageCommand extends AbstractTransactionalCommand {

	protected final static String COMMAND_LABEL = "Change bounds of source execution specification, And move message occurrence specification";
	
	private Command preCommand;
	private OccurrenceSpecification occurrence;
	private int yLocation;
	private LifelineEditPart lifelinePart;
	private List<EditPart> notToMoveEditParts;
	
	private Command postCommand;
	
	public ApexSetBoundsAndMoveMessageCommand(
			TransactionalEditingDomain domain, Command preCommand, OccurrenceSpecification occurrence, int yLocation, LifelineEditPart lifelinePart, List<EditPart> notToMoveEditParts) {
		super(domain, COMMAND_LABEL, null);
		this.preCommand = preCommand;
		this.occurrence = occurrence;
		this.yLocation = yLocation;
		this.lifelinePart = lifelinePart;
		this.notToMoveEditParts = notToMoveEditParts;
	}

	@Override
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException {
		if (preCommand != null && preCommand.canExecute()) {
			preCommand.execute();
		}
		
		postCommand = OccurrenceSpecificationMoveHelper.getMoveOccurrenceSpecificationsCommand(occurrence, null, yLocation, -1, lifelinePart, notToMoveEditParts);
		postCommand.execute();
		
		return CommandResult.newOKCommandResult();
	}

}
