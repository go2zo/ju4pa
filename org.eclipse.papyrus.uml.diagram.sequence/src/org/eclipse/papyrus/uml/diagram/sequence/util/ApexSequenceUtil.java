/*****************************************************************************
 * Copyright (c) 2012 ApexSoft
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   ApexSoft - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.common.core.command.UnexecutableCommand;
import org.eclipse.gmf.runtime.diagram.ui.commands.ICommandProxy;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ConnectionNodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.INodeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.ShapeNodeEditPart;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.common.util.DiagramEditPartsUtil;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.AbstractExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ActionExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.BehaviorExecutionSpecificationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragment2EditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentCombinedFragmentCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.CombinedFragmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.ContinuationEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionInteractionCompartmentEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionOperandEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.InteractionUseEditPart;
import org.eclipse.papyrus.uml.diagram.sequence.edit.parts.LifelineEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.uml2.common.util.CacheAdapter;
import org.eclipse.uml2.uml.CombinedFragment;
import org.eclipse.uml2.uml.ExecutionOccurrenceSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.InteractionFragment;
import org.eclipse.uml2.uml.InteractionOperand;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.internal.impl.InteractionImpl;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ApexSequenceUtil {

	/**
	 * 주어진 EditPartEntry Set에서 해당 AbstractGraphicalEditPart 보다
	 * y 좌표가 아래에 있는 EditPartList 반환 
	 * 
	 * @param agep   기준이 되는 AbstractGraphicalEditPart
	 * @return aep보다 아래에 위치한 EditPart의 List
	 */
	public static List apexGetBelowEditPartList(IGraphicalEditPart agep) {
					
		Set<Entry<Object, EditPart>> wholeEditPartEntries = agep.getViewer().getEditPartRegistry().entrySet();
		
		Map<IGraphicalEditPart, Integer> belowEditPartMap = new HashMap<IGraphicalEditPart, Integer>();

		int yBottomOfAgep = apexGetAbsolutePosition(agep, SWT.BOTTOM);
		
		/*8
		System.out
		.println("ApexSequenceUtil.apexGetBelowEditPartList(), line : "
				+ Thread.currentThread().getStackTrace()[1]
						.getLineNumber());
		int i = 0;
		//*/
		for (Entry<Object, EditPart> aEPEntry : wholeEditPartEntries ) {
			
			EditPart editPart = aEPEntry.getValue();
			
			/*8
			System.out.println("entry["+ i++ +"] : " + editPart.getClass().toString().substring(editPart.getClass().toString().lastIndexOf('.')+1));
			//*/
			if (editPart.equals(agep))
				continue;
			if ( editPart instanceof IGraphicalEditPart ) {				
				IGraphicalEditPart agep1 = (IGraphicalEditPart)editPart;				
				int yTopThisEP = apexGetAbsolutePosition(agep1, SWT.TOP);
				/*8
				System.out
						.println("ApexSequenceUtil.apexGetBelowEditPartList(), line : "
								+ Thread.currentThread().getStackTrace()[1]
										.getLineNumber());
				System.out.println("belowEditPart["+i++ +"] : " + agep1.getClass() + ", yTop : " + yTopThisEP);
				//*/
				if ( yTopThisEP >= yBottomOfAgep
						&& !belowEditPartMap.containsKey(agep1)) {
					belowEditPartMap.put(agep1, yTopThisEP);
					
				}	
			}	
		}
		
		Collection<Entry<IGraphicalEditPart, Integer>> entrySet = belowEditPartMap.entrySet();
		List<Entry<IGraphicalEditPart, Integer>> entryList = new ArrayList<Entry<IGraphicalEditPart, Integer>>(entrySet);
		Collections.sort(entryList, new Comparator<Entry<IGraphicalEditPart, Integer>>() {
			public int compare(Entry<IGraphicalEditPart, Integer> o1,
					Entry<IGraphicalEditPart, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		List<IGraphicalEditPart> belowEditPartList = new ArrayList<IGraphicalEditPart>(entryList.size());
		for (Entry<IGraphicalEditPart, Integer> entry : entryList) {
			belowEditPartList.add(entry.getKey());
		}

		return belowEditPartList;
	}
	
	/**
	 * 중첩되지 않고 Sibling 중에서
	 * 해당 AbstractGraphicalEditPart 보다 y좌표가 아래에 있어
	 * 하향 이동 시 함께 움직여줘야 할 EditPartList 반환 
	 * 
	 * @param agep   기준이 되는 AbstractGraphicalEditPart
	 * @return aep보다 아래에 위치한 EditPart의 List
	 */
	public static List apexGetMovableEditPartList(IGraphicalEditPart agep) {
					
		Set<Entry<Object, EditPart>> wholeEditPartEntries = agep.getViewer().getEditPartRegistry().entrySet();
		
		Map<IGraphicalEditPart, Integer> belowEditPartMap = new HashMap<IGraphicalEditPart, Integer>();

		int yBottomOfAgep = apexGetAbsolutePosition(agep, SWT.BOTTOM);
		/*8
		System.out
		.println("ApexSequenceUtil.apexGetMovableEditPartList(), line : "
				+ Thread.currentThread().getStackTrace()[1]
						.getLineNumber());
		int i = 0;
		//*/
		for (Entry<Object, EditPart> aEPEntry : wholeEditPartEntries ) {
			
			EditPart editPart = aEPEntry.getValue();
			if (editPart.equals(agep))
				continue;
			if ( editPart instanceof IGraphicalEditPart ) {
				
				IGraphicalEditPart agep1 = (IGraphicalEditPart)editPart;
				/*8
				System.out.println("editPart["+i++ +"] : " + editPart.getClass().toString().substring(editPart.getClass().toString().lastIndexOf('.')+1) + ", parent : " + agep1.getParent().getClass());
				System.out.println("container : " + apexGetSimpleClassName(SequenceUtil.findInteractionFragmentContainerAt(agep1.getFigure().getBounds().getCopy(), agep1)));
				//*/
				// parent가 interactionCompartment인 것(즉, 중첩되지 않은 것)
				/*
				if ( agep1.getParent() instanceof InteractionInteractionCompartmentEditPart) {
					
					int yTopThisEP = apexGetAbsolutePosition(agep1, SWT.TOP);
	
					if ( yTopThisEP >= yBottomOfAgep
							&& !belowEditPartMap.containsKey(agep1)) {
						belowEditPartMap.put(agep1, yTopThisEP);
					}	
				}
				*/
				InteractionFragment ift = SequenceUtil.findInteractionFragmentContainerAt(agep1.getFigure().getBounds().getCopy(), agep1);
				// 중첩되지 않고 Interaction 에 속한 fragment의 경우
				if ( ift instanceof Interaction) {
					// CF의 경우 중첩되어 있는 경우에도 findInteractionFragmentContainerAt() 이 InteractionImpl을 반환하므로
					// IOEP를 통해 한 번 더 check 해야함
					if ( agep1 instanceof CombinedFragmentEditPart
						 && agep1.getParent() instanceof InteractionOperandEditPart ) { // 중첩되어있는 것은 put 하지 않음 
						continue;
					}						
					if ( agep1 instanceof InteractionOperandEditPart ) { // InteractionOperand는 put 하지 않음 
							continue;
					}
					int yTopThisEP = apexGetAbsolutePosition(agep1, SWT.TOP);
	
					if ( yTopThisEP >= yBottomOfAgep
							&& !belowEditPartMap.containsKey(agep1)) {
						belowEditPartMap.put(agep1, yTopThisEP);
					}	
				}
			}	
		}
		
	    // agep1의 sibling
		List<IGraphicalEditPart> siblings = apexGetSiblingEditParts2(agep);
		if ( siblings != null) {
			for ( IGraphicalEditPart ep : siblings ) {
				if (ep.equals(agep))
					continue;
				if ( ep instanceof IGraphicalEditPart ) {
					
					IGraphicalEditPart agep1 = (IGraphicalEditPart)ep;
					
					int yTopThisEP = apexGetAbsolutePosition(agep1, SWT.TOP);

					if ( yTopThisEP >= yBottomOfAgep
							&& !belowEditPartMap.containsKey(agep1)) {
						belowEditPartMap.put(agep1, yTopThisEP);
					}	
					
				}	
			}			
		}		

		
		Collection<Entry<IGraphicalEditPart, Integer>> entrySet = belowEditPartMap.entrySet();
		List<Entry<IGraphicalEditPart, Integer>> entryList = new ArrayList<Entry<IGraphicalEditPart, Integer>>(entrySet);
		Collections.sort(entryList, new Comparator<Entry<IGraphicalEditPart, Integer>>() {
			public int compare(Entry<IGraphicalEditPart, Integer> o1,
					Entry<IGraphicalEditPart, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		
		List<IGraphicalEditPart> belowEditPartList = new ArrayList<IGraphicalEditPart>(entryList.size());
		for (Entry<IGraphicalEditPart, Integer> entry : entryList) {
			belowEditPartList.add(entry.getKey());
		}
/*8
		System.out
				.println("ApexSequenceUtil.apexGetMovableEditPartList(), line : "
						+ Thread.currentThread().getStackTrace()[1]
								.getLineNumber());
		for ( int i = 0 ; i < belowEditPartList.size() ; i++ ) {
			System.out.println("["+i+"] " + belowEditPartList.get(i));
		}
//*/
		return belowEditPartList;
	}

	
	public static List<IGraphicalEditPart> apexGetSiblingEditParts2(IGraphicalEditPart gep) {
		
		List<IGraphicalEditPart> siblings = null;
		EditPart ep = gep.getParent();
		if ( ep instanceof InteractionInteractionCompartmentEditPart
		     || ep instanceof InteractionOperandEditPart ) {
			siblings = ep.getChildren();
		}
		return siblings;
	}
	
	/**
	 * 주어진 EditPartList를 검색하여
	 * y좌표 기준 주어진 AbstractGraphicalEditPart의 바로 아래에 위치한 AbstractGraphicalEditPart 반환
	 * 
	 * @param agep    기준이 되는 AbstractGraphicalEditPart
	 * @param belowEditPartList    검색할 EditPart의 List
	 * @return    y좌표 기준 agep의 바로 아래에 위치한 AbstractGraphicalEditPart
	 */
	public static IGraphicalEditPart apexGetBeneathEditPart(IGraphicalEditPart agep, List belowEditPartList) {

		int gap = Integer.MAX_VALUE;
		IGraphicalEditPart beneathEditPart = null;

		int yCF = apexGetAbsolutePosition(agep, SWT.BOTTOM);
		
		Iterator it = belowEditPartList.iterator();
		
		while( it.hasNext()) {
			
			IGraphicalEditPart sep = (IGraphicalEditPart)it.next();
			
			int yEP = apexGetAbsolutePosition(sep, SWT.TOP);
			
			int thisGap = yEP - yCF;
			
			if ( thisGap < gap ) {
				gap = thisGap;
				beneathEditPart = sep;
			}
		}
		return beneathEditPart;
	}
	
	/**
	 * y좌표 기준 주어진 AbstractGraphicalEditPart의 바로 아래에 위치한 AbstractGraphicalEditPart 반환
	 * 
	 * @param agep    기준이 되는 AbstractGraphicalEditPart
	 * @param belowEditPartList    검색할 EditPart의 List
	 * @return    y좌표 기준 agep의 바로 아래에 위치한 AbstractGraphicalEditPart
	 */
	public static IGraphicalEditPart apexGetBeneathEditPart(IGraphicalEditPart agep) {

		List belowEditPartList = apexGetBelowEditPartList(agep);
		
		int gap = Integer.MAX_VALUE;
		IGraphicalEditPart beneathEditPart = null;

		int yCF = apexGetAbsolutePosition(agep, SWT.BOTTOM);
		
		Iterator it = belowEditPartList.iterator();
		
		while( it.hasNext()) {
			
			IGraphicalEditPart sep = (IGraphicalEditPart)it.next();
			
			int yEP = apexGetAbsolutePosition(sep, SWT.TOP);
			
			int thisGap = yEP - yCF;
			
			if ( thisGap < gap ) {
				gap = thisGap;
				beneathEditPart = sep;
			}
		}
		return beneathEditPart;
	}

	/**
     * 주어진 EditPart의 모든 children을 Indent하여 출력하는 Util
	 * 코드 내에서 호출 시 전달 된 depth값을 0으로 하여 본 메서드 호출
	 * 
	 * @param ep
	 */
	public static void apexShowChildrenEditPart(EditPart ep) {
		apexShowChildrenEditPart(ep, 0);
	}
	
	/**
	 * 주어진 EditPart의 모든 children을 Indent하여 출력하는 Util
	 * 코드 내에서 호출 시 전달 된 depth값을 기준으로 Indent 처리
	 * (depth>=0인 어떤 값도 무방하나 호출 시 depth=0 권장) 
	 * 
	 * @param ep     출력될 children을 가진 EditPart
	 * @param depth  Indent 수준
	 */
	public static void apexShowChildrenEditPart(EditPart ep, int depth) {
		List childrenList = ep.getChildren();
		Iterator it = childrenList.iterator();
		
		while ( it.hasNext() ) {
			StringBuffer sb = new StringBuffer();
			for ( int i = 0 ; i < depth ; i++ ) {
				sb.append(" ");	
			}
			
			EditPart o = (EditPart)it.next();
			
			System.out.println(sb.toString() + o/*.getClass().getSimpleName()*/);
			// interactionOperand의 경우 포함되는 fragment를 출력
			if ( o instanceof InteractionOperandEditPart ) {
				InteractionOperand io = (InteractionOperand)((InteractionOperandEditPart)o).resolveSemanticElement();
				System.out.println(sb.toString() + "  " + "lifelines : " + io.getCovereds());
				System.out.println(sb.toString() + "  " + "fragments : " + io.getFragments());	
			}
			
			// children 있으면 depth 증가 후 재귀호출
			if ( o.getChildren().size() > 0 ) {
				apexShowChildrenEditPart(o, depth+2);	
			}

		}	
	}
	
	/**
	 * 해당 EditPart의 모든 child EditPart를 leveling 없이 반환
	 * 빈 List를 생성하여 본 메서드 apexGetChildEditPartList(EditPart, List) 호출
	 * 
	 * @param ep
	 * @return
	 */
	public static List apexGetChildEditPartList(EditPart ep) {		
		return apexGetChildEditPartList(ep, new ArrayList());
	}
	
	/**
	 * 해당 EditPart의 모든 child EditPart를 leveling 없이 반환
	 * 
	 * @param ep
	 * @param childEPList
	 * @return
	 */
	public static List apexGetChildEditPartList(EditPart ep, List childEPList) {
		
		List<EditPart> childrenList = ep.getChildren();
		Iterator it = childrenList.iterator();
		
		while ( it.hasNext() ) {
			EditPart o = (EditPart)it.next();

			childEPList.add(o);

			if ( o.getChildren().size() > 0 ) {
				apexGetChildEditPartList(o, childEPList);	
			}
		}
		return childEPList;
	}
	
	/**
	 * ArrayList를 파라미터로 추가하여 apexGetParentCombinedFragmentEditPartList(CombinedFragmentEditPart, List) 호출
	 * 
	 * @param cfep
	 * @return
	 */
	public static List<CombinedFragmentEditPart> apexGetParentCombinedFragmentEditPartList(CombinedFragmentEditPart cfep) {
		return apexGetParentCombinedFragmentEditPartList(cfep, new ArrayList<CombinedFragmentEditPart>());
	}

	/**
	 * 
	 * @param cfep
	 * @param parentCombinedFragmentEditParts
	 * @return
	 */
	public static List<CombinedFragmentEditPart> apexGetParentCombinedFragmentEditPartList(CombinedFragmentEditPart cfep, List<CombinedFragmentEditPart> parentCombinedFragmentEditParts) {
				
		EditPart opParent = cfep.getParent();

		if ( opParent instanceof InteractionOperandEditPart ) {
			
			CombinedFragmentEditPart parentCombinedFragmentEditPart = (CombinedFragmentEditPart)opParent.getParent().getParent();
			
			parentCombinedFragmentEditParts.add(parentCombinedFragmentEditPart);
			
			apexGetParentCombinedFragmentEditPartList(parentCombinedFragmentEditPart, parentCombinedFragmentEditParts);			
		}
		return parentCombinedFragmentEditParts;
	}
	
	/**
	 * 주어진 EditPartEntry Set에서 해당 AbstractGraphicalEditPart 보다
	 * y 좌표가 위에 있는 EditPartList 반환 
	 * 
	 * @param agep   기준이 되는 AbstractGraphicalEditPart
	 * @return aep보다 위에 위치한 EditPart의 List
	 */
	public static List apexGetHigherEditPartList(IGraphicalEditPart agep) {
		
//apexTestCoordinateSystem(agep);
		
		Set<Entry<Object, EditPart>> wholeEditPartEntries = agep.getViewer().getEditPartRegistry().entrySet();

		Map<IGraphicalEditPart, Integer> higherEditPartMap = new HashMap<IGraphicalEditPart, Integer>();
		
		int yTopOfAgep = apexGetAbsolutePosition(agep, SWT.TOP);
		
		for (Entry<Object, EditPart> aEPEntry : wholeEditPartEntries ) {
			
			EditPart editPart = aEPEntry.getValue();
			
			if (editPart.equals(agep))
				continue;
			if (!(editPart instanceof INodeEditPart))
				continue;
			if ( editPart instanceof IGraphicalEditPart ) {
				
				IGraphicalEditPart agep1 = (IGraphicalEditPart)editPart;
				
				int yBottomThisEP = apexGetAbsolutePosition(agep1, SWT.BOTTOM);
/*8
System.out.println("ApexSequenceUtil.apexGetHigherEditPartList, line : "
		+ Thread.currentThread().getStackTrace()[1].getLineNumber());
System.out.println("agep,     yTopOfAgep : " + agep.getClass() + ", " + yTopOfAgep);
if ( agep1 instanceof CombinedFragmentEditPart) {
System.out.println("agep.bounds     : " + agep.getFigure().getBounds().getCopy());	
System.out.println("agep.absBounds  : " + apexGetAbsoluteRectangle(agep));
}
System.out.println("agep1, yBottomThisEP : " + agep1.getClass() + ", " + yBottomThisEP);
if ( agep1 instanceof CombinedFragmentEditPart) {
System.out.println("agep1.bounds    : " + agep1.getFigure().getBounds().getCopy());	
System.out.println("agep1.absBounds : " + apexGetAbsoluteRectangle(agep1));
}
//*/
				if ( yBottomThisEP < yTopOfAgep
						&& !higherEditPartMap.containsKey(agep1)) {					
					higherEditPartMap.put(agep1, yBottomThisEP);
				}
			}	
		}

		Collection<Entry<IGraphicalEditPart, Integer>> entrySet = higherEditPartMap.entrySet();
		List<Entry<IGraphicalEditPart, Integer>> entryList = new ArrayList<Entry<IGraphicalEditPart, Integer>>(entrySet);
		Collections.sort(entryList, new Comparator<Entry<IGraphicalEditPart, Integer>>() {
			public int compare(Entry<IGraphicalEditPart, Integer> o1,
					Entry<IGraphicalEditPart, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		
		List<IGraphicalEditPart> higherEditPartList = new ArrayList<IGraphicalEditPart>(entryList.size());
		for (Entry<IGraphicalEditPart, Integer> entry : entryList) {
			higherEditPartList.add(entry.getKey());
		}

		return higherEditPartList;
	}

	/**
	 * 주어진 EditPartList를 검색하여
	 * y좌표 기준 주어진 AbstractGraphicalEditPart의 바로 위에 위치한 AbstractGraphicalEditPart 반환
	 * 
	 * @param agep    기준이 되는 AbstractGraphicalEditPart
	 * @param higherEditPartList    검색할 EditPart의 List
	 * @return    y좌표 기준 agep의 바로 위에 위치한 AbstractGraphicalEditPart
	 */
	public static IGraphicalEditPart apexGetAboveEditPart(IGraphicalEditPart agep, List higherEditPartList) {

		int gap = Integer.MAX_VALUE;
		IGraphicalEditPart aboveEditPart = null;
		
		int yCF = apexGetAbsolutePosition(agep, SWT.TOP);
		
		Iterator it = higherEditPartList.iterator();
		
		while( it.hasNext()) {
			IGraphicalEditPart sep = (IGraphicalEditPart)it.next();
			int yEP = apexGetAbsolutePosition(sep, SWT.BOTTOM);
			int thisGap = yCF - yEP;
			if ( thisGap < gap ) {
				gap = thisGap;
				aboveEditPart = sep;
			}
		}
		return aboveEditPart;
	}
	
	/**
	 * 주어진 EditPart List에서 bottom 기준 가장 아래에 있는 EditPart 반환
	 * @param editPartList
	 * @return
	 */
	public static IGraphicalEditPart apexGetLowestEditPartFromList(List<IGraphicalEditPart> editPartList) {
		
		int bottom = Integer.MIN_VALUE;
		IGraphicalEditPart lowestEditPart = null;
		
		for (IGraphicalEditPart ep : editPartList) {	
			
			int epBottom = apexGetAbsolutePosition(ep, SWT.BOTTOM);
			
			if ( epBottom > bottom) {
				lowestEditPart = ep;
				bottom = epBottom;
			}
		}
		
		return lowestEditPart;
	}
	
	/**
	 * 주어진 EditPart List에서 top 기준 가장 위에 있는 EditPart 반환
	 * @param editPartList
	 * @return
	 */
	public static IGraphicalEditPart apexGetHighestEditPartFromList(List<IGraphicalEditPart> editPartList) {
		
		int top = Integer.MAX_VALUE;
		IGraphicalEditPart highestEditPart = null;
		
		for (IGraphicalEditPart ep : editPartList) {	
			
			int epTop = apexGetAbsolutePosition(ep, SWT.TOP);
			
			if ( epTop < top) {
				highestEditPart = ep;
				top = epTop;
			}
		}
		
		return highestEditPart;
	}
	
	/**
	 * Message에 링크된 ExecutionSpec과 Message들을 리스트로 반환
	 * 
	 * @param agep
	 * @param findConnection	Whether to find Message or not 
	 * @param findExecSpec		Whether to find ExecutionSpec or not 
	 * @param findFront
	 * @return
	 */
	public static List<IGraphicalEditPart> apexGetLinkedEditPartList(IGraphicalEditPart agep, boolean findConnection, boolean findExecSpec, boolean findFront) {
		return apexGetLinkedEditPartList(agep, findConnection, findExecSpec, findFront, new ArrayList());
	}
	
	/**
	 * Message에 링크된 ExecutionSpec과 Message들을 리스트로 반환
	 * 
	 * @param agep Message or ExecutionSpecification
	 * @param findConnection
	 * @param findExecSpec
	 * @param findFront
	 * @param list
	 * @return
	 */
	public static List<IGraphicalEditPart> apexGetLinkedEditPartList(IGraphicalEditPart agep, boolean findConnection, boolean findExecSpec, boolean findFront, List list) {
		if (agep instanceof ConnectionNodeEditPart) {
			ConnectionNodeEditPart cep = (ConnectionNodeEditPart)agep;
			if (findConnection)
				list.add(cep);
			
			IGraphicalEditPart nextEditPart = null;
			if (!findFront)
				nextEditPart = (IGraphicalEditPart)cep.getTarget();
			else
				nextEditPart = (IGraphicalEditPart)cep.getSource();
			apexGetLinkedEditPartList(nextEditPart, findConnection, findExecSpec, findFront, list);
		}
		else if (agep instanceof AbstractExecutionSpecificationEditPart) {
			if (findExecSpec)
				list.add(agep);
			
			List connections = null;
			if (!findFront)
				connections = agep.getSourceConnections();
			else
				connections = agep.getTargetConnections();
			
			if (connections != null) {
				Iterator iter = connections.iterator();
				while (iter.hasNext()) {
					ConnectionNodeEditPart srcConnection = (ConnectionNodeEditPart)iter.next();
					apexGetLinkedEditPartList((ConnectionNodeEditPart)srcConnection, findConnection, findExecSpec, findFront, list);
				}
			}
		}
		
		return list;
	}

	/**
	 * Message에 링크된 EditPart들 중 가장 하위에 있는 EditPart를 반환 (bottom값이 가장 하위)
	 * 
	 * @param agep
	 * @return
	 */
	public static IGraphicalEditPart apexGetBottomEditPartInLinked(IGraphicalEditPart agep) {
		List editPartList = apexGetLinkedEditPartList(agep, true, true, false, new ArrayList());
		Iterator iter = editPartList.iterator();
		int bottom = Integer.MIN_VALUE;
		IGraphicalEditPart bottomEditPart = null;
		while (iter.hasNext()) {
			Object next = iter.next();
			if (next instanceof IGraphicalEditPart) {
				int b = apexGetAbsolutePosition((IGraphicalEditPart)next, SWT.BOTTOM);
				if (b > bottom) {
					bottom = b;
					bottomEditPart = (IGraphicalEditPart)next;
				}
			}
		}
		return bottomEditPart;
	}
	
	public static Rectangle apexGetAbsoluteRectangle(IGraphicalEditPart gep) {
		Rectangle bounds = null;
		if (gep instanceof ConnectionNodeEditPart) {
			Connection conn = ((ConnectionNodeEditPart)gep).getConnectionFigure();
			Point p2 = conn.getTargetAnchor().getReferencePoint();
			Point p1 = conn.getSourceAnchor().getLocation(p2);
			bounds = new Rectangle(p1.x(), p1.y, p2.x() - p1.x(), p2.y() - p1.y());
		} else {
			IFigure figure = gep.getFigure();
			bounds = figure.getBounds().getCopy();
			figure.translateToAbsolute(bounds);
		}
		
		return bounds;
	}	
	
	public static void apexTestCoordinateSystem(IGraphicalEditPart agep) {
		IFigure thisFigure = agep.getFigure();
		Rectangle origRect = thisFigure.getBounds().getCopy();
		Rectangle copyRect1 = thisFigure.getBounds().getCopy();
		Rectangle copyRect2 = thisFigure.getBounds().getCopy();
		Rectangle copyRect3 = thisFigure.getBounds().getCopy();
		Rectangle copyRect4 = thisFigure.getBounds().getCopy();
		
		System.out.println("----------------------");
		System.out.println("agep.getNotationView() :                        : " + agep.resolveSemanticElement());
		System.out.println("agep.getParent() :                              : " + agep.getParent());
		System.out.println("agep.getParent().getFigure()                    : " + ((AbstractGraphicalEditPart)agep.getParent()).getFigure());
		System.out.println("");
		System.out.println("thisFigure                                      : " + thisFigure);
		System.out.println("thisFigure.getParent()                          : " + thisFigure.getParent());
		System.out.println("");
		System.out.println("thisFigure.getInset() :                         : " + thisFigure.getInsets());
		System.out.println("thisFigure.getBounds().getCopy()                : " + origRect);	
		
		thisFigure.translateFromParent(copyRect1);
//		System.out.println("thisFigure.translateFromParent(copyRect1)       : " + copyRect1);		
		
		thisFigure.translateToParent(copyRect3);
//		System.out.println("thisFigure.translateToParent(copyRect3)         : " + copyRect3);
		
		thisFigure.translateToAbsolute(copyRect2);
		System.out.println("thisFigure.translateToAbsolute(copyRect2)       : " + copyRect2);
		
		thisFigure.translateToRelative(copyRect4);
//		System.out.println("thisFigure.translateToRelative(copyRect4)       : " + copyRect4);		
		
//		System.out.println("thisFigure.getParent().getClass()               : " + thisFigure.getParent().getClass());
//		System.out.println("thisFigure.getParent().getInsets()              : " + thisFigure.getParent().getInsets());
//		System.out.println("thisFigure.getParent().getBounds()              : " + thisFigure.getParent().getBounds());
		Rectangle parentR = thisFigure.getParent().getBounds().getCopy();
		thisFigure.getParent().translateToAbsolute(parentR);
		
		System.out.println("thisFigure.getParent().translateToAbs(parentF)  : " + parentR);
		
		Rectangle copyRect11 = thisFigure.getBounds().getCopy();
		Rectangle copyRect21 = thisFigure.getBounds().getCopy();
		Rectangle copyRect31 = thisFigure.getBounds().getCopy();
		Rectangle copyRect41 = thisFigure.getBounds().getCopy();
		
		thisFigure.getParent().translateFromParent(copyRect11);
//		System.out.println("parentFigure.translateFromParent(copyRect11)    : " + copyRect11);
		
		thisFigure.getParent().translateToParent(copyRect31);
//		System.out.println("parentFigure.translateToParent(copyRect31)      : " + copyRect31);
				
		thisFigure.getParent().translateToAbsolute(copyRect21);
		System.out.println("parentFigure.translateToAbsolute(copyRect21)    : " + copyRect21);

		
		thisFigure.getParent().translateToRelative(copyRect41);
//		System.out.println("parentFigure.translateToRelative(copyRect41)    : " + copyRect41);
		
//		apexShowChildrenEditPart(agep);
		List children = apexGetChildEditPartList(agep);
		Iterator it = children.iterator();
		
		while ( it.hasNext() ) {
			AbstractGraphicalEditPart agep1 = (AbstractGraphicalEditPart)it.next();
			if ( agep1 instanceof CombinedFragmentEditPart ) {
				CombinedFragmentEditPart cfep = (CombinedFragmentEditPart)agep1;
				
				IFigure childFigure = cfep.getFigure();
				Rectangle origRect2 = childFigure.getBounds().getCopy();
				Rectangle copyRect12 = childFigure.getBounds().getCopy();
				Rectangle copyRect22 = childFigure.getBounds().getCopy();
				Rectangle copyRect32 = childFigure.getBounds().getCopy();
				Rectangle copyRect42 = childFigure.getBounds().getCopy();
				
//				System.out.println("cfep.getNotationView() :                        : " + cfep.resolveSemanticElement());
//				System.out.println("cfep.getParent() :                              : " + cfep.getParent());
//				System.out.println("cfep.getParent().getFigure()                    : " + ((InteractionOperandEditPart)cfep.getParent()).getFigure());
//				System.out.println("");
//				System.out.println("childFigure                                      : " + childFigure);
//				System.out.println("childFigure.getParent()                          : " + childFigure.getParent());
				
				System.out.println("");
				System.out.println("childFigure.getBounds().getCopy()           : " + origRect2);	
				
				childFigure.translateFromParent(copyRect12);
//				System.out.println("childFigure.translateFromParent(copyRect12) : " + copyRect12);
				
				childFigure.translateToParent(copyRect32);
//				System.out.println("childFigure.translateToParent(copyRect32)   : " + copyRect32);
				
				childFigure.translateToAbsolute(copyRect22);
				System.out.println("childFigure.translateToAbsolute(copyRect22) : " + copyRect22);
				
				childFigure.translateToRelative(copyRect42);
//				System.out.println("childFigure.translateToRelative(copyRect42) : " + copyRect42);
				
//				System.out.println("childFigure.getParent().getClass()          : " + childFigure.getParent().getClass());
//				System.out.println("childFigure.getParent().getInsets()         : " + childFigure.getParent().getInsets());
				System.out.println("childFigure.getParent().getBounds()         : " + childFigure.getParent().getBounds());
				
				Rectangle copyRect13 = childFigure.getBounds().getCopy();
				Rectangle copyRect23 = childFigure.getBounds().getCopy();
				Rectangle copyRect33 = childFigure.getBounds().getCopy();
				Rectangle copyRect43 = childFigure.getBounds().getCopy();
				
				childFigure.getParent().translateFromParent(copyRect13);
//				System.out.println("parentFigure.translateFromParent(copyRect13): " + copyRect13);
				
				childFigure.getParent().translateToParent(copyRect33);
//				System.out.println("parentFigure.translateToParent(copyRect33)  : " + copyRect33);
				
				childFigure.getParent().translateToAbsolute(copyRect23);
				System.out.println("parentFigure.translateToAbsolute(copyRect23): " + copyRect23);
				
				childFigure.getParent().translateToRelative(copyRect43);
//				System.out.println("parentFigure.translateToRelative(copyRect43): " + copyRect43);
			}
		}
		
			
		
		
		
/*		
		org.eclipse.draw2d.geometry.Rectangle parentRect = new Rectangle(10, 20, 300, 100);
		IFigure parentFigure = new Figure();
		parentFigure.setBounds(parentRect);
		Rectangle origRect = parentFigure.getBounds().getCopy();
		Rectangle copyRect1 = parentFigure.getBounds().getCopy();
		Rectangle copyRect2 = parentFigure.getBounds().getCopy();
		Rectangle copyRect3 = parentFigure.getBounds().getCopy();
		Rectangle copyRect4 = parentFigure.getBounds().getCopy();
		
		System.out.println("----------------------");
		System.out.println("parentFigure.getBounds().getCopy()           : " + origRect);	
		
		parentFigure.translateFromParent(copyRect1);
		System.out.println("parentFigure.translateFromParent(copyRect1)  : " + copyRect1);
		
		parentFigure.translateToAbsolute(copyRect2);
		System.out.println("parentFigure.translateToAbsolute(copyRect2)  : " + copyRect2);
		
		parentFigure.translateToParent(copyRect3);
		System.out.println("parentFigure.translateToParent(copyRect3)    : " + copyRect3);
		
		parentFigure.translateToRelative(copyRect4);
		System.out.println("parentFigure.translateToRelative(copyRect4)  : " + copyRect4);
		
		org.eclipse.draw2d.geometry.Rectangle childRect = new Rectangle(40, 80, 100, 30);
		IFigure childFigure = new Figure();
		childFigure.setBounds(childRect);
		childFigure.setParent(parentFigure);
		Rectangle origRect1 = childFigure.getBounds().getCopy();
		Rectangle copyRect11 = childFigure.getBounds().getCopy();
		Rectangle copyRect22 = childFigure.getBounds().getCopy();
		Rectangle copyRect33 = childFigure.getBounds().getCopy();
		Rectangle copyRect44 = childFigure.getBounds().getCopy();
		
		System.out.println("childFigure.getBounds().getCopy()            : " + origRect1);	
		
		childFigure.getParent().translateFromParent(copyRect11);		
		System.out.println("parentFigure.translateFromParent(copyRect11) : " + copyRect11);
		
		childFigure.getParent().translateToAbsolute(copyRect22);
		System.out.println("parentFigure.translateToAbsolute(copyRect22) : " + copyRect22);
		
		childFigure.getParent().translateToParent(copyRect33);
		System.out.println("parentFigure.translateToParent(copyRect33)   : " + copyRect33);
		
		childFigure.getParent().translateToRelative(copyRect44);
		System.out.println("parentFigure.translateToRelative(copyRect44) : " + copyRect44);
*/
	}
	
	/**
	 * EditPart의 Bottom값을 반환. Message인 경우 Bendpoint들 중 가장 하위의 값을 반환
	 * 
	 * @param gep
	 * @param type	one of TOP, BOTTOM
	 * @return
	 */
	public static int apexGetAbsolutePosition(IGraphicalEditPart gep, int type) {
		Rectangle bounds = apexGetAbsoluteRectangle(gep);
		switch (type) {
		case SWT.TOP:
			return bounds.getTop().y();
		case SWT.BOTTOM:
			return bounds.getBottom().y();
		}
		return -1;
	}
	
	/**
	 * SequenceUtil에 있던 메서드지만 아무도 호출하지 않아
	 * ApexSequenceUtil 로 가져와서 개조 사용
	 * Property의 covered 설정과 관계없이
	 * 해당 Rectangle에 intersect되는 모든 Lifeline 반환
	 * 절대좌표로 비교
	 * 
	 * @param selectionRect 절대좌표화된 선택영역
	 * @param hostEditPart
	 * @return
	 */
	public static List<LifelineEditPart> apexGetPositionallyCoveredLifelineEditParts(Rectangle selectionRect, AbstractGraphicalEditPart hostEditPart) {
		
		//hostEditPart.getFigure().translateToAbsolute(selectionRect);
		
		List positionallyCoveredLifelineEditParts = new ArrayList();

		// retrieve all the edit parts in the registry
		Set<Entry<Object, EditPart>> allEditPartEntries = hostEditPart.getViewer().getEditPartRegistry().entrySet();
		for(Entry<Object, EditPart> epEntry : allEditPartEntries) {
			EditPart ep = epEntry.getValue();

			if(ep instanceof LifelineEditPart) {
				Rectangle lifelineRect = ApexSequenceUtil.apexGetAbsoluteRectangle((LifelineEditPart)ep);

				if(selectionRect.intersects(lifelineRect)) {
					positionallyCoveredLifelineEditParts.add((LifelineEditPart)ep);
				}
			}

		}
		return positionallyCoveredLifelineEditParts;
	}
	
	
	public static ConnectionNodeEditPart apexGetPrevConnectionEditPart(ConnectionNodeEditPart connectionPart) {
		EditPart sourcePart = connectionPart.getSource();
		if (sourcePart instanceof ActionExecutionSpecificationEditPart ||
				sourcePart instanceof BehaviorExecutionSpecificationEditPart) {
			List<ConnectionNodeEditPart> srcConns = apexGetSortedConnections((IGraphicalEditPart)sourcePart, true, true);
			if (srcConns.size() > 0 && srcConns.indexOf(connectionPart) > 0) {
				int index = srcConns.indexOf(connectionPart);
				return srcConns.get(index - 1);
//				return apexGetLastConnectionEditPart(srcConns.get(index - 1));
			}
			List<ConnectionNodeEditPart> tgtConns = apexGetSortedConnections((IGraphicalEditPart)sourcePart, false, true);
			if (tgtConns.size() > 0) {
				return tgtConns.get(tgtConns.size() - 1);
			}
		}
		return null;
	}
	
	public static ConnectionNodeEditPart apexGetNextConnectionEditPart(ConnectionNodeEditPart connectionPart) {
		EditPart targetPart = connectionPart.getTarget();
		if (targetPart instanceof ActionExecutionSpecificationEditPart ||
				targetPart instanceof BehaviorExecutionSpecificationEditPart) {
			List<ConnectionNodeEditPart> srcConns = apexGetSortedConnections((IGraphicalEditPart)targetPart, true, true);
			if (srcConns.size() > 0) {
				return srcConns.get(0);
			}
		}
		EditPart sourcePart = connectionPart.getSource();
		if (sourcePart instanceof ActionExecutionSpecificationEditPart ||
				sourcePart instanceof BehaviorExecutionSpecificationEditPart) {
			List<ConnectionNodeEditPart> srcConns = apexGetSortedConnections((IGraphicalEditPart)sourcePart, true, true);
			if (srcConns.size() > 0) {
				int index = srcConns.indexOf(connectionPart);
				if (index > -1 && index < srcConns.size() - 1) {
					return srcConns.get(index + 1);
				}
			}
		}
		return null;
	}
	
	public static ConnectionNodeEditPart apexGetLastConnectionEditPart(ConnectionNodeEditPart connectionPart) {
		EditPart targetPart = connectionPart.getTarget();
		if (targetPart instanceof LifelineEditPart) {
			return connectionPart;
		}
		else if (targetPart instanceof ActionExecutionSpecificationEditPart ||
				targetPart instanceof BehaviorExecutionSpecificationEditPart) {
			List<ConnectionNodeEditPart> sourceConns = apexGetSortedConnections((IGraphicalEditPart)targetPart, true, true);
			if (sourceConns.size() > 0) {
				return apexGetLastConnectionEditPart(sourceConns.get(sourceConns.size() - 1));
			}
			else {
				return connectionPart;
			}
		}
		return null;
	}
	
	public static List<ConnectionNodeEditPart> apexGetSortedConnections(IGraphicalEditPart editPart, boolean isSource, final boolean forward) {
		List<ConnectionNodeEditPart> srcConns = new ArrayList<ConnectionNodeEditPart>();
		List<?> conns = isSource ? editPart.getSourceConnections() : editPart.getTargetConnections();
		for (Object conn : conns) {
			if (conn instanceof ConnectionNodeEditPart) {
				srcConns.add((ConnectionNodeEditPart)conn);
			}
		}
		Collections.sort(srcConns, new Comparator<ConnectionNodeEditPart>() {
			public int compare(ConnectionNodeEditPart o1, ConnectionNodeEditPart o2) {
				Point p1 = SequenceUtil.getAbsoluteEdgeExtremity((ConnectionNodeEditPart)o1, true);
				Point p2 = SequenceUtil.getAbsoluteEdgeExtremity((ConnectionNodeEditPart)o2, true);
				
				if (p1 != null && p2 != null) {
					int compare = forward ? p1.y() - p2.y() : p2.y() - p1.y();
					return compare >= 0 ? (compare != 0 ? 1 : 0) : -1;
				}
				return 0;
			}
		});
		
		return srcConns;
	}
	
	public static List<IGraphicalEditPart> apexGetSiblingEditParts(IGraphicalEditPart gep) {
		List<InteractionFragment> fragmentList = new ArrayList<InteractionFragment>();
		
		InteractionFragment fragment = null;
		if (gep instanceof ConnectionNodeEditPart) {
			ConnectionNodeEditPart connection = (ConnectionNodeEditPart)gep;
			Point edge = SequenceUtil.getAbsoluteEdgeExtremity(connection, true);
			fragment = SequenceUtil.findInteractionFragmentContainerAt(edge, gep);
		}
		else {
			Rectangle bounds = SequenceUtil.getAbsoluteBounds(gep);
			fragment = SequenceUtil.findInteractionFragmentContainerAt(bounds, gep);
		}

		if (fragment instanceof Interaction) {
			fragmentList.addAll( ((Interaction) fragment).getFragments() );
		}
		else if (fragment instanceof InteractionOperand) {
			fragmentList.addAll( ((InteractionOperand) fragment).getFragments() );
		}
		else if (fragment instanceof CombinedFragment) {
			fragmentList.addAll( ((CombinedFragment) fragment).getOperands() );
		}
		
		List<IGraphicalEditPart> result = new ArrayList<IGraphicalEditPart>(fragmentList.size());
		for (InteractionFragment itf : fragmentList) {
			EObject parseElement = itf;
			if (itf instanceof MessageOccurrenceSpecification) {
				parseElement = ((MessageOccurrenceSpecification)itf).getMessage();
			}
			else if (itf instanceof ExecutionOccurrenceSpecification) {
				parseElement = ((ExecutionOccurrenceSpecification)itf).getExecution();
			}
			
			List<View> views = DiagramEditPartsUtil.findViews(parseElement, gep.getViewer());
			for (View view : views) {
				EditPart part = DiagramEditPartsUtil.getEditPartFromView(view, gep);
				boolean isCombinedFragment = part instanceof CombinedFragmentEditPart || part instanceof CombinedFragment2EditPart;
				boolean isContinuation = part instanceof ContinuationEditPart;
				boolean isInteractionOperand = part instanceof InteractionOperandEditPart;
				boolean isInteractionUse = part instanceof InteractionUseEditPart;
				boolean isInteraction = part instanceof InteractionEditPart;
				boolean isMessage = part instanceof ConnectionNodeEditPart;
				boolean isActivation = part instanceof AbstractExecutionSpecificationEditPart;
				boolean isSameEditPart = gep.equals(part);
				if(isCombinedFragment || isContinuation || isInteractionOperand || isInteractionUse || isInteraction || isMessage /*|| isActivation*/) {
					if (!result.contains(part) && !isSameEditPart) {
						result.add((IGraphicalEditPart) part);
					}
				}
			}
		}
		
		return result;
	}
	
	public static List<IGraphicalEditPart> apexGetNextSiblingEditParts(IGraphicalEditPart gep) {
		List<IGraphicalEditPart> removeList = new ArrayList<IGraphicalEditPart>();
		List<IGraphicalEditPart> result = apexGetSiblingEditParts(gep);
		int y = ApexSequenceUtil.apexGetAbsolutePosition(gep, SWT.BOTTOM);
		for (IGraphicalEditPart part : result) {
			int top = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.TOP);
			if (y > top) {
				removeList.add(part);
			}
		}
		
		result.removeAll(removeList);
		
		Collections.sort(result, new Comparator<IGraphicalEditPart>() {
			public int compare(IGraphicalEditPart o1, IGraphicalEditPart o2) {
				int i1 = apexGetAbsolutePosition(o1, SWT.TOP);
				int i2 = apexGetAbsolutePosition(o2, SWT.TOP);
				return i1 - i2;
			}
		});
		
		return result;
	}
	
	public static List<IGraphicalEditPart> apexGetPrevSiblingEditParts(IGraphicalEditPart gep) {
		List<IGraphicalEditPart> removeList = new ArrayList<IGraphicalEditPart>();
		List<IGraphicalEditPart> result = apexGetSiblingEditParts(gep);
		int y = ApexSequenceUtil.apexGetAbsolutePosition(gep, SWT.TOP);
		for (IGraphicalEditPart part : result) {
			int bottom = ApexSequenceUtil.apexGetAbsolutePosition(part, SWT.BOTTOM);
			if (y < bottom) {
				removeList.add(part);
			}
		}
		
		result.removeAll(removeList);
		
		Collections.sort(result, new Comparator<IGraphicalEditPart>() {
			public int compare(IGraphicalEditPart o1, IGraphicalEditPart o2) {
				int i1 = apexGetAbsolutePosition(o1, SWT.BOTTOM);
				int i2 = apexGetAbsolutePosition(o2, SWT.BOTTOM);
				return i1 - i2;
			}
		});
		
		return result;
	}
	
	public static String apexGetSimpleClassName(Object object) {
		if ( object != null ) {
			String fullName = object.getClass().toString();
			return fullName.substring(fullName.lastIndexOf('.')+1);	
		} else {
			return null;
		}		
	}
	
	/**
	 * compoundCommand를 분해해서 compositeCommand에 add해주는 메서드
	 * 
	 * @param compoundCommand
	 * @param compositeCommand
	 */
	public static void apexCompoundCommandToCompositeCommand(CompoundCommand compoundCommand, CompositeCommand compositeCommand) {
		List cmdList = compoundCommand.getCommands();
		Iterator itCmd = cmdList.iterator();
		while ( itCmd.hasNext() ) {
			Command aCommand = (Command)itCmd.next();
			if ( aCommand != null && !aCommand.canExecute()) {
				compositeCommand.add(UnexecutableCommand.INSTANCE);
			} else if ( aCommand != null ) {
				if ( aCommand instanceof ICommandProxy ) {
					ICommandProxy iCommandproxy = (ICommandProxy)aCommand;
					ICommand iCommand = iCommandproxy.getICommand();
					compositeCommand.add(iCommand);
				}									
			}
		}
	}
	
	/**
	 * compoundCommand를 분해하여 다른 compoundCommand에 add하는 메서드
	 * null 이나 Unexecutable에 관계없이 있는대로 add함
	 * 
	 * @param inputCompoundCommand
	 * @param resultCompoundCommand
	 */
	public static void apexCompoundCommandToCompoundCommand(CompoundCommand inputCompoundCommand, CompoundCommand resultCompoundCommand) {
		List cmdList = inputCompoundCommand.getCommands();
		Iterator itCmd = cmdList.iterator();
		while ( itCmd.hasNext() ) {
			Command aCommand = (Command)itCmd.next();
			resultCompoundCommand.add(aCommand);
		}
	}
		
	/**
	 * IOEP가 cover하는 Lifeline의 Activation 및 Connection 중 IOEP에 경계가 포함되는 EditPartList 반환
	 * 
	 * @param ioep
	 * @return
	 */
	public static List<EditPart> apexGetIOEPContainedEditParts(InteractionOperandEditPart ioep) {
		return apexGetIOEPContainedEditParts(ioep, true);
	}
	
	/**
	 * IOEP가 cover하는 Lifeline의 Activation 및 Connection 중 IOEP에 경계가 포함되는 EditPartList 반환
	 * coveredOnly가 false인 경우 IOEP의 covered 대신 위치적으로 IOEP와 교차되는 Lifeline의 EditPartList 반환
	 * 
	 * @param ioep
	 * @param coveredOnly
	 * @return
	 */
	public static List<EditPart> apexGetIOEPContainedEditParts(InteractionOperandEditPart ioep, boolean coveredOnly) {
		
		List<EditPart> containedEditParts = new ArrayList<EditPart>();
		
		List<LifelineEditPart> coveredLifelineEditParts = apexGetCoveredLifelineEditParts(ioep, coveredOnly);
		
		Rectangle ioepRect = apexGetAbsoluteRectangle(ioep);
		
		for ( LifelineEditPart lep : coveredLifelineEditParts ) {
			List<EditPart> lepChildren = lep.getChildren();
			
			for ( EditPart ep : lepChildren ) {
				if ( ep instanceof AbstractExecutionSpecificationEditPart ) {
					AbstractExecutionSpecificationEditPart aesep = (AbstractExecutionSpecificationEditPart)ep;
					Rectangle activationRect = apexGetAbsoluteRectangle(aesep);
					
					if ( ioepRect.contains(activationRect) ) {
						containedEditParts.add(aesep);
					}	
				}				
			}
			
			List<EditPart> sourceConnections = lep.getSourceConnections();
			
			for ( EditPart ep : sourceConnections ) {
				if ( ep instanceof ConnectionEditPart ) {
					ConnectionEditPart cep = (ConnectionEditPart)ep;
					Rectangle cepRect = apexGetAbsoluteRectangle(cep);
					
					if ( ioepRect.contains(cepRect) ) {
						containedEditParts.add(cep);
					}	
				}
			}
		}
		
		return containedEditParts;
	}
	
	/**
	 * CombinedFragmentEditPart 나 InteractionOperandEditPart의 coveredLifelineEditParts 반환
	 * 
	 * @param snep
	 * @return
	 */
	public static List<LifelineEditPart> apexGetCoveredLifelineEditParts(ShapeNodeEditPart snep, boolean coveredOnly) {
	
		List<LifelineEditPart> coveredLifelineEditParts = new ArrayList<LifelineEditPart>();
		
		List<Lifeline> coveredLifelines = null;
				
		if ( snep instanceof CombinedFragmentEditPart ) {
			CombinedFragment cf = (CombinedFragment)snep.resolveSemanticElement();
			if ( coveredOnly ) {
				coveredLifelines = cf.getCovereds();	
			} else {
				coveredLifelines = new ArrayList<Lifeline>();				
				List<LifelineEditPart> positionallyCoveredLifelineEditparts = apexGetPositionallyCoveredLifelineEditParts(apexGetAbsoluteRectangle(snep), snep);
			
				for ( LifelineEditPart lep : positionallyCoveredLifelineEditparts ) {
					coveredLifelines.add((Lifeline)lep.resolveSemanticElement());
				}
			}
			
		} else if ( snep instanceof InteractionOperandEditPart ) {
			InteractionOperand io = (InteractionOperand)snep.resolveSemanticElement();
			if ( coveredOnly ) {
				coveredLifelines = io.getCovereds();	
			} else {
				coveredLifelines = new ArrayList<Lifeline>();				
				List<LifelineEditPart> positionallyCoveredLifelineEditparts = apexGetPositionallyCoveredLifelineEditParts(apexGetAbsoluteRectangle(snep), snep);
			
				for ( LifelineEditPart lep : positionallyCoveredLifelineEditparts ) {
					coveredLifelines.add((Lifeline)lep.resolveSemanticElement());
				}
			}
				
		} else {
			return null;
		}
		
		for ( Lifeline ll : coveredLifelines ) {
			coveredLifelineEditParts.add((LifelineEditPart)apexGetEditPart(ll, snep.getViewer()));
		}
		/*
		RootEditPart rootEP = snep.getRoot();
		EditPart contents = rootEP.getContents();
		System.out
				.println("ApexSequenceUtil.apexGetCoveredLifelineEditParts(), line : "
						+ Thread.currentThread().getStackTrace()[1]
								.getLineNumber());
		System.out.println("rootEP.getChildren() : " + rootEP.getChildren());
		
		if ( contents instanceof PackageEditPart ) {
			PackageEditPart pep = (PackageEditPart)contents;
			List interactionEditParts = pep.getChildren();
			
			if ( interactionEditParts.size() > 0 ) {
			
				if ( interactionEditParts.get(0) instanceof InteractionEditPart ) {				
					InteractionEditPart iep = (InteractionEditPart)interactionEditParts.get(0);
					List<EditPart> iepChildren = iep.getChildren();
					
					for ( EditPart ep : iepChildren ) {					
						if ( ep instanceof InteractionInteractionCompartmentEditPart ) {
							InteractionInteractionCompartmentEditPart iicep = (InteractionInteractionCompartmentEditPart)ep;
							List<EditPart> iicepChildren = iicep.getChildren();
						
							for ( EditPart ep1 : iicepChildren ) {
								if ( ep1 instanceof LifelineEditPart ) {
									LifelineEditPart llep = (LifelineEditPart)ep1;									
									Lifeline lifeline = (Lifeline)llep.resolveSemanticElement();
									
									
									if ( coveredLifelines.contains(lifeline) ) {
										coveredLifelineEditParts.add(llep);										
									}
								}
							}
						}
					}
				}
			}
		}
		*/
		return coveredLifelineEditParts;
	}
	
	/**
	 * InteractionOperand의 children 과
	 * InteractionOperand이 cover하는 Lifeline의 Activation 중 InteractionOperand에 위치적으로 포함된 Activation 함께 리턴 
	 * 
	 * @param ioep
	 * @return
	 */
	public static List apexGetInteractionOperandChildrenEditParts(InteractionOperandEditPart ioep) {
		
		List operandChildren = new ArrayList();
		
		operandChildren.addAll(ioep.getChildren());
		operandChildren.addAll(apexGetIOEPContainedEditParts(ioep));
		
		return operandChildren;
	}
	
	/**
	 * CFEP의 모든 최종 childrenEditParts 반환
	 * CombinedFragmentCombinedFragmentCompartmentEditPart 과 InteractionOperand을 제외한
	 * 중첩된 CF나 IOEP 내의 모든 Activation 반환
	 * 
	 * @param cfep
	 * @return
	 */
	public static List apexGetCombinedFragmentChildrenEditParts(CombinedFragmentEditPart cfep) {
		
		List cfChildren = new ArrayList();
		
		List<CombinedFragmentCombinedFragmentCompartmentEditPart> childCompartments = cfep.getChildren();
		
		for ( CombinedFragmentCombinedFragmentCompartmentEditPart compartEP : childCompartments ) {
			List<InteractionOperandEditPart> ioeps = compartEP.getChildren();
			
			for ( InteractionOperandEditPart ioep : ioeps ) {
				cfChildren.addAll(apexGetIOEPContainedEditParts(ioep));
			}
		}		
		
		return cfChildren;
	}
	
	public static EditPart apexGetEditPart(EObject eObject, EditPartViewer viewer) {
		Collection<Setting> settings = CacheAdapter.getInstance().getNonNavigableInverseReferences(eObject);
		for (Setting ref : settings) {
			if(NotationPackage.eINSTANCE.getView_Element().equals(ref.getEStructuralFeature())) {
				View view = (View)ref.getEObject();
				EditPart part = (EditPart)viewer.getEditPartRegistry().get(view);
				return part;
			}
		}
		return null;
	}
}
