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

	public void update(GraphicalEditPart context, Bounds origBounds) {
		System.out.println("updated invoked");
		this.context = context;
		this.init();
		
		for (Map.Entry<LifelineEditPart, Rectangle> entry : lifelines.entrySet()) {
			LifelineEditPart editPart = entry.getKey();
			Rectangle centralLineRect = entry.getValue();
			Rectangle beforeRect = new Rectangle(origBounds.getX(), origBounds.getY(), origBounds.getWidth(), origBounds.getHeight());
			updateLifeline(editPart, centralLineRect, beforeRect);
		}
	}	
	
	/**
	 * @param lifelineEditpart
	 * @param centralLineRect 중간 dashline의 절대좌표
	 * @param beforeRect 옮겨지기 전 상대좌표
	 */
	public void updateLifeline(LifelineEditPart lifelineEditpart, Rectangle centralLineRect, Rectangle beforeRect) {
		Lifeline lifeline = (Lifeline) lifelineEditpart.resolveSemanticElement();
		EList<InteractionFragment> coveredByLifelines = lifeline
				.getCoveredBys();

		coveredByLifelinesToAdd.clear();
		coveredByLifelinesToRemove.clear();			
		
		Rectangle newRect = lifelineEditpart.getFigure().getBounds().getCopy();
		
		List<CombinedFragment> coveredByCombinedFragmentsToAdd = new ArrayList<CombinedFragment>();
		List<CombinedFragment> coveredByCombinedFragmentsToRemove = new ArrayList<CombinedFragment>();
		
		/* apex improved start */			
		List<CombinedFragmentEditPart> cfEditPartsToCheck = null;
		if (newRect.width == 0 && newRect.height == 0) { // 새 Lifeline 생성하는 경우 Interaction 내 모든 CF을 check			
			cfEditPartsToCheck = new ArrayList<CombinedFragmentEditPart>();
			for (Map.Entry<InteractionFragmentEditPart, Rectangle> entry : interactionFragments.entrySet()) {
				InteractionFragmentEditPart editPart = entry.getKey();
				Rectangle interactionFragmentBounds = entry.getValue();
				InteractionFragment interactionFragment = (InteractionFragment)editPart.resolveSemanticElement();
				if ( interactionFragment instanceof CombinedFragment ) {
					cfEditPartsToCheck.add((CombinedFragmentEditPart)editPart);
					coveredByCombinedFragmentsToAdd.add((CombinedFragment)interactionFragment);
				}
			}
		} else { // 새로 생성이 아닌 CF 경계 변경인 경우
			
			if (beforeRect.width > newRect.width ) { // width 축소 시 lifeline의 원래 위치를 포함하던 CF을 check
				cfEditPartsToCheck = ApexSequenceUtil.apexGetPositionallyLifelineCoveringCFEditParts(beforeRect, lifelineEditpart);	
			} else if (beforeRect.width < newRect.width) { // width 확대 시 새 lifeline의 위치를 포함하는 CF을 check
				cfEditPartsToCheck = ApexSequenceUtil.apexGetPositionallyLifelineCoveringCFEditParts(newRect, lifelineEditpart);
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
						                        newRect, 
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
	 * 0.9에서 추가된 메서드
	 * check lifelines in PartDecomposition, see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=364813
	 * 
	 * @param combinedFragmentEditPart
	 * @param newLifelineBounds
	 * @param coveredLifelinesToAdd
	 * @param coveredLifelinesToRemove
	 * @param coveredLifelines
	 */
	/**
	 * 
	 * 
	 * @param combinedFragmentEditPart
	 * @param beforeLifelineRect lifeline의 변경 전 상대좌표경계
	 * @param newLifelineRect lifeline의 변경 후 상대좌표경계
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
		
		/* apex improved start */
		context.getFigure().translateToAbsolute(beforeLifelineRect);
		context.getFigure().translateToAbsolute(newLifelineRect);
		
		Rectangle cfRect = combinedFragmentEditPart.getFigure().getBounds().getCopy();
		combinedFragmentEditPart.getFigure().translateToAbsolute(cfRect);
		
		// 새 lifeline 경계와 CF 경계가 교차되고
		if (cfRect.intersects(newLifelineRect)) { 
			
			// 원래의 coveredBy에 없던 combinedFragment이면
			if(!coveredByLifelines.contains(combinedFragment)) {
				// 원래 coveredBy에 있던 CF이면서 coveredByLifelines에 없는 것은
				// Property창에서 수동으로 coveredBy에서 제외한 것이므로
				// 원래 coveredBy에 없던 cf만 add
				if (!cfRect.intersects(beforeLifelineRect)) {
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
