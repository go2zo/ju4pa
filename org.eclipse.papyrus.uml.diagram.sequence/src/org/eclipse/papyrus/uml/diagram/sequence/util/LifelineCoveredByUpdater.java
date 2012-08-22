/*****************************************************************************
 * Copyright (c) 2009 CEA
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Soyatec - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Bounds;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.figures.LifelineDotLineCustomFigure;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.PartDecomposition;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * This class updates the property CoveredBy of Lifeline when a Lifeline gets created, moved/resize 
 * and also the resize of moving of each CombinedFragment  
 * 
 * @author yyang
 *
 */
public class LifelineCoveredByUpdater {
	protected GraphicalEditPart context;
	protected Map<LifelineEditPart, Rectangle> lifelines = new HashMap<LifelineEditPart, Rectangle>();
	protected HashMap<InteractionFragmentEditPart, Rectangle> interactionFragments = new HashMap<InteractionFragmentEditPart, Rectangle>();
	
	protected List<InteractionFragment> coveredByLifelinesToAdd = new ArrayList<InteractionFragment>();
	protected List<InteractionFragment> coveredByLifelinesToRemove = new ArrayList<InteractionFragment>();
	
	protected TransactionalEditingDomain editingDomain;

	public LifelineCoveredByUpdater() {
	}

	protected void init() {
		editingDomain = this.context.getEditingDomain();
		GraphicalEditPart parent = context;
		while (true) {
			EditPart editPart = parent.getParent();
			if (editPart instanceof GraphicalEditPart) {
				parent = (GraphicalEditPart) editPart;
			} else {
				break;
			}
		}
		childrenCollect(parent);
	}

	private void childrenCollect(GraphicalEditPart editPart) {
		if (editPart instanceof LifelineEditPart) {
			IFigure figure = editPart.getFigure();
			Rectangle childBounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(childBounds);
			Rectangle centralLineBounds = new Rectangle(
					childBounds.x() +  childBounds.width() / 2,
					childBounds.y(), 1,  childBounds.height());
			
			lifelines.put((LifelineEditPart)editPart, centralLineBounds);
		}
		if (editPart instanceof InteractionFragmentEditPart) {
			IFigure figure = editPart.getFigure();
			Rectangle childBounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(childBounds);
			interactionFragments.put((InteractionFragmentEditPart)editPart, childBounds);
		}
		for (Object child : editPart.getChildren()) {
			if (child instanceof GraphicalEditPart) {
				childrenCollect((GraphicalEditPart)child);
			}
		}
	}

	/**
	 * apex updated
	 * @param context
	 * @param afterRect 경계 변경 후 상대좌표
	 */
	public void update(GraphicalEditPart context, Rectangle afterRect) {
		this.context = context;
		this.init();
		
		for (Map.Entry<LifelineEditPart, Rectangle> entry : lifelines.entrySet()) {
			LifelineEditPart editPart = entry.getKey();
			Rectangle centralLineRect = entry.getValue();			
			context.getFigure().translateToAbsolute(afterRect);
			updateLifeline(editPart, centralLineRect, afterRect);
		}
	}	
	
	/**
	 * @param lifelineEditpart
	 * @param centralLineRect 중간 dashline의 절대좌표
	 * @param afterRect 옮겨진 후 절대좌표
	 */
	public void updateLifeline(LifelineEditPart lifelineEditpart, Rectangle centralLineRect, Rectangle afterRect) {
		Lifeline lifeline = (Lifeline) lifelineEditpart.resolveSemanticElement();
		EList<InteractionFragment> coveredByLifelines = lifeline
				.getCoveredBys();

		coveredByLifelinesToAdd.clear();
		coveredByLifelinesToRemove.clear();			
		
		Rectangle beforeRect = lifelineEditpart.getFigure().getBounds().getCopy();
		lifelineEditpart.getFigure().translateToAbsolute(beforeRect);
		
		
		List<CombinedFragment> coveredByCombinedFragmentsToAdd = new ArrayList<CombinedFragment>();
		List<CombinedFragment> coveredByCombinedFragmentsToRemove = new ArrayList<CombinedFragment>();
		
		/* apex improved start */			
		List<CombinedFragmentEditPart> cfEditPartsToCheck = new ArrayList<CombinedFragmentEditPart>();
		if (beforeRect.width == 0 && beforeRect.height == 0) { // 새 Lifeline 생성하는 경우 Interaction 내 모든 CF을 check			
			
			for (Map.Entry<InteractionFragmentEditPart, Rectangle> entry : interactionFragments.entrySet()) {
				InteractionFragmentEditPart editPart = entry.getKey();				
				InteractionFragment interactionFragment = (InteractionFragment)editPart.resolveSemanticElement();
				if ( interactionFragment instanceof CombinedFragment ) {
					cfEditPartsToCheck.add((CombinedFragmentEditPart)editPart);
					coveredByCombinedFragmentsToAdd.add((CombinedFragment)interactionFragment);
				}
			}
		} else { // 새로 생성이 아닌 lifeline 경계 변경인 경우
			
			if (afterRect.x != beforeRect.x ) { // 좌우측으로 이동 시 lifeline의 이동 전후 위치를 포함하던 CF을 check

				List<CombinedFragmentEditPart> cfEnclosingBeforeRect = ApexSequenceUtil.apexGetPositionallyLifelineCoveringCFEditParts(beforeRect, lifelineEditpart);
				List<CombinedFragmentEditPart> cfEnclosingAfterRect = ApexSequenceUtil.apexGetPositionallyLifelineCoveringCFEditParts(afterRect, lifelineEditpart);

				// 중복 제거
				cfEnclosingBeforeRect.removeAll(cfEnclosingAfterRect);
				
				cfEditPartsToCheck.addAll(cfEnclosingBeforeRect);
				cfEditPartsToCheck.addAll(cfEnclosingAfterRect);
			} else { // 확대/축소가 아닌 경우, 즉 상하이동의 경우
				cfEditPartsToCheck = null;
			}
		}
		
		// 상하이동의 경우 covereds가 바뀔 일이 없으므로
		// 상하이동이 아닌 경우만 updateCoveredLifelines 호출
		if ( cfEditPartsToCheck != null ) { 
			for(CombinedFragmentEditPart cfEditPartToCheck : cfEditPartsToCheck) {
				updateCoveredByCombinedFragment(cfEditPartToCheck,
						                        beforeRect,
						                        afterRect, 
						                        coveredByCombinedFragmentsToAdd, 
						                        coveredByCombinedFragmentsToRemove, 
						                        coveredByLifelines);
			}
			coveredByLifelinesToAdd.addAll(coveredByCombinedFragmentsToAdd);
			coveredByLifelinesToRemove.addAll(coveredByCombinedFragmentsToRemove);
		}		
		/* apex improved end */
			
		// 아래는 CF와 IO가 아닌 경우에만 작동하도록 변경 필요			
		for (Map.Entry<InteractionFragmentEditPart, Rectangle> entry : interactionFragments.entrySet()) {
			InteractionFragmentEditPart editPart = entry.getKey();
			Rectangle interactionFragmentBounds = entry.getValue();
			InteractionFragment interactionFragment = (InteractionFragment) editPart
					.resolveSemanticElement();
			if ( !(interactionFragment instanceof CombinedFragment)
				 && !(interactionFragment instanceof InteractionOperand) ) {			
				if (centralLineRect.intersects(interactionFragmentBounds)) {
					if (!coveredByLifelines.contains(interactionFragment)) {
						coveredByLifelinesToAdd.add(interactionFragment);
					}
				} else if (coveredByLifelines.contains(interactionFragment)) {
					coveredByLifelinesToRemove.add(interactionFragment);
				}
			}
		}

		if (!coveredByLifelinesToAdd.isEmpty()) {
			CommandHelper.executeCommandWithoutHistory(editingDomain,
					AddCommand.create(editingDomain, lifeline,
							UMLPackage.eINSTANCE.getLifeline_CoveredBy(),
							coveredByLifelinesToAdd), true);
		}
		if (!coveredByLifelinesToRemove.isEmpty()) {
			CommandHelper.executeCommandWithoutHistory(editingDomain,
					RemoveCommand.create(editingDomain, lifeline,
							UMLPackage.eINSTANCE.getLifeline_CoveredBy(),
							coveredByLifelinesToRemove), true);
		}
	}

	/**
	 * apex updated
	 * 
	 * 
	 * @param combinedFragmentEditPart
	 * @param beforeLifelineRect lifeline의 변경 전 절대좌표경계
	 * @param newLifelineRect lifeline의 변경 후 절대좌표경계
	 * @param coveredByCombinedFragmentsToAdd
	 * @param coveredByCombinedFragmentsToRemove
	 * @param coveredByLifelines
	 */
	private void updateCoveredByCombinedFragment(CombinedFragmentEditPart combinedFragmentEditPart, 
                                                 Rectangle beforeLifelineRect,
			                                     Rectangle newLifelineRect, 
			                                     List<CombinedFragment> coveredByCombinedFragmentsToAdd, 
			                                     List<CombinedFragment> coveredByCombinedFragmentsToRemove, 
			                                     EList<InteractionFragment> coveredByLifelines) {
		CombinedFragment combinedFragment = (CombinedFragment)combinedFragmentEditPart.resolveSemanticElement();
		System.out
				.println("LifelineCoveredByUpdater.updateCoveredByCombinedFragment(), line : "
						+ Thread.currentThread().getStackTrace()[1]
								.getLineNumber());
		System.out.println("beforeLifelineRect : " + beforeLifelineRect);
		System.out.println("newLifelineRect    : " + newLifelineRect);
		/* apex improved start */		
		Rectangle cfRect = combinedFragmentEditPart.getFigure().getBounds().getCopy();
		combinedFragmentEditPart.getFigure().translateToAbsolute(cfRect);
		
		// 새 lifeline 경계와 CF 경계가 교차되고
		if ( newLifelineRect.right() >= cfRect.x && newLifelineRect.x <= cfRect.right() ) {
		//if (cfRect.intersects(newLifelineRect)) { 
			
			// 원래의 coveredBy에 없던 combinedFragment이면
			if(!coveredByLifelines.contains(combinedFragment)) {
				// 원래 lifeline과 교차되었으면서 coveredByLifelines에 없는 것은
				// Property창에서 수동으로 coveredBy에서 제외한 것이므로
				// 원래 lifeline과 교차되지 않았던 CF만 add
				// beforeLifelineRect의 경우 height=-1인 경우도 있으므로 intersect가 아닌 x좌표로 비교
				if ( beforeLifelineRect.right() < cfRect.x || beforeLifelineRect.x > cfRect.right() ) {
					coveredByCombinedFragmentsToAdd.add(combinedFragment);
				}						
			}
		// 새로 생성된 lifeline이 아니면서도 새 lifeline 경계와 CF 경계가 교차되지 않고
		// 원래 coveredBy에 있던 combinedFragment이면 remove
		} else if ( newLifelineRect.width != 0 && newLifelineRect.height != 0) {
			coveredByCombinedFragmentsToRemove.add(combinedFragment);			
		}
		/* apex improved end */
	}
}
