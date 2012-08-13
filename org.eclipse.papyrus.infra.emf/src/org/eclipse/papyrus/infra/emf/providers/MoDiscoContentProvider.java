/*******************************************************************************
 * Copyright (c) 2010 Mia-Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Nicolas Bros (Mia-Software) - initial API and implementation
 *******************************************************************************/
package org.eclipse.papyrus.infra.emf.providers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.facet.infra.browser.uicore.CustomizableModelContentProvider;
import org.eclipse.emf.facet.infra.browser.uicore.internal.model.LinkItem;
import org.eclipse.emf.facet.infra.browser.uicore.internal.model.ModelElementItem;
import org.eclipse.emf.validation.internal.modeled.model.validation.impl.ConstraintImpl;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.ModelUtils;
import org.eclipse.papyrus.infra.core.resource.uml.UmlModel;
import org.eclipse.papyrus.infra.core.resource.uml.UmlUtils;
import org.eclipse.papyrus.infra.core.sasheditor.contentprovider.IPageMngr;
import org.eclipse.papyrus.infra.core.sasheditor.di.contentprovider.DiSashModelMngr;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.emf.Activator;
import org.eclipse.uml2.uml.internal.impl.ActionExecutionSpecificationImpl;
import org.eclipse.uml2.uml.internal.impl.BehaviorExecutionSpecificationImpl;
import org.eclipse.uml2.uml.internal.impl.CombinedFragmentImpl;
import org.eclipse.uml2.uml.internal.impl.ExecutionOccurrenceSpecificationImpl;
import org.eclipse.uml2.uml.internal.impl.LifelineImpl;
import org.eclipse.uml2.uml.internal.impl.MessageImpl;
import org.eclipse.uml2.uml.internal.impl.MessageOccurrenceSpecificationImpl;
import org.eclipse.uml2.uml.internal.impl.TimeConstraintImpl;

/**
 * the content provider that inherits of modisco properties
 * 
 * @deprecated Use SemanticUMLContentProvider instead
 */
@Deprecated
public class MoDiscoContentProvider extends CustomizableModelContentProvider {

	/** The ModelSet containing all the models. This is the initial input. */
	protected ModelSet modelSet;

	/** The list of open pages (diagrams) */
	protected IPageMngr pageMngr;

	/**
	 * Creates a new MoDiscoContentProvider.
	 */
	public MoDiscoContentProvider() {
		super(Activator.getDefault().getCustomizationManager());
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/**
	 * apex updated
	 */
	@Override
	public Object[] getChildren(final Object parentElement) {

		/* apex improved start */

		// 무한호출 유발하는 요소는 제외된 List
		ArrayList<Object> result = new ArrayList<Object>();
		
		// 무한호출 유발하는 요소 포함된 List, 추후 Filtering 시 활용
		ArrayList<Object> fullResult = new ArrayList<Object>();

		Object[] arrayObject = super.getChildren(parentElement);
/*8
System.out.println("MoDiscoContentProvider.getChildren(), line : "
		+ Thread.currentThread().getStackTrace()[1].getLineNumber());
System.out.println("parentElement : " + parentElement);
for ( int i = 0 ; i < arrayObject.length ; i++ ) {
	System.out.println("  arrayObject["+i+"] : " + arrayObject[i]);
}
//*/
		

		if (arrayObject != null) {
			for (int i = 0; i < arrayObject.length; i++) {
				boolean canBeAdded = true;
				
/*8				
System.out.println("\n** in super.getChildren loop, arrayObject["+i+"] being added : " + arrayObject[i]);
//*/
				if ( arrayObject[i] instanceof ModelElementItem) {
					ModelElementItem mei = (ModelElementItem)arrayObject[i];
/*8
System.out.println("      mei.getEObject() being added : " + mei.getEObject());
//*/

				} else if ( arrayObject[i] instanceof LinkItem) {
					LinkItem li = (LinkItem)arrayObject[i];

/*8					
System.out.println("      li.getText() : " + li.getText());
System.out.println("      li.getReference() : " + li.getReference());	
System.out.println("      li.getParent() : " + li.getParent());
System.out.println("      li.getParent().getClass().toString() : " + li.getParent().getClass().toString());
System.out.println("      li.getTreeParent() : " + li.getTreeParent());
//*/
					
					if (li.getReference().getName().startsWith("enclosing")) {
						canBeAdded = false;
					} else if (li.getParent() instanceof LifelineImpl ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof MessageImpl ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof MessageOccurrenceSpecificationImpl ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof ExecutionOccurrenceSpecificationImpl ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof BehaviorExecutionSpecificationImpl ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof ActionExecutionSpecificationImpl ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof ConstraintImpl && li.getReference().getName().equals("context") ) {
						canBeAdded = false;
					} else if (li.getParent() instanceof TimeConstraintImpl && li.getReference().getName().equals("context") ) {
						canBeAdded = false;
					}
					
/*8
if ( canBeAdded ) {
	System.out.println("      li.getText() being added : " + li.getText());
}
*/
				
				}
				if ( canBeAdded ) {						
					result.add(arrayObject[i]);
				}
				fullResult.add(arrayObject[i]);
			}
		}

		/* apex improved end */
		/* apex replaced
		Object[] arrayObject = super.getChildren(parentElement);
		if(arrayObject != null) {
			for(int i = 0; i < arrayObject.length; i++) {
				result.add(arrayObject[i]);
			}
		}
		*/
		/**
		 * Refactoring with bug 358732
		 */
		//
		//		if (parentElement instanceof IAdaptable) {
		//			EObject eObject = (EObject)((IAdaptable)parentElement).getAdapter(EObject.class);
		//			if(eObject !=null) {
		//				List<Diagram> diagramList = findAllExistingDiagrams(eObject);
		//					Iterator<Diagram> iterator = diagramList.iterator();
		//					while (iterator.hasNext()) {
		//						result.add(iterator.next());
		//					}
		//			}
		//
		//		}

		return result.toArray();
	}
	
	/* 
	// org.eclipse.emf.facet.infra.browser.uicore.CustomizableModelContentProvider
	public Object[] getChildren(final Object parentElement) {
		if (parentElement instanceof ITreeElement) {
			final ITreeElement treeElement = (ITreeElement) parentElement;
			return treeElement.getChildren().toArray();
		}
		return null;
	}

	// org.eclipse.emf.facet.infra.browser.uicore.internal.model.ModelElementItem
	public List<Object> getChildren() {

		final ArrayList<Object> children = new ArrayList<Object>();

		// show a link for the container
		if (this.appearanceConfiguration.isShowContainer()) {
			addContainer(this.fModelElement, children);
		}

		// show a virtual attribute for the URI
		if (this.appearanceConfiguration.isShowURI()) {
			addURIAttribute(children);
		}

		addVirtualElements(children);

		if (this.appearanceConfiguration.isShowAttributes()) {
			final List<AttributeItem> attributes = createAttributes();
			// sort attributes by name
			// TODO: separate preference for attributes?
			if (this.appearanceConfiguration.isSortLinks()) {
				sortAttributes(attributes);
			}
			children.addAll(attributes);
		}

		final ArrayList<Object> elementsInCollapsedLinks = new ArrayList<Object>();
		final ArrayList<LinkItem> links = createLinks(elementsInCollapsedLinks);
			final CustomizationEngine customizationEngine = this.appearanceConfiguration
					.getCustomizationEngine();
			// filter out invisible elements
			// cf Bug 329037 - [UICustom] "Collapse Link" option does not
			// exploit the "Visible" option
			ListIterator<Object> listIterator = elementsInCollapsedLinks.listIterator();
			while (listIterator.hasNext()) {
				Object next = listIterator.next();
				if (next instanceof ModelElementItem) {
					ModelElementItem modelElementItem = (ModelElementItem) next;
					EObject eObject = modelElementItem.getEObject();
					if (!customizationEngine.isTypeVisible(eObject.eClass(), eObject)) {
						listIterator.remove();
					}
				}
			}
		if (this.appearanceConfiguration.isSortInstances()) {
			BigListItem.sortElements(elementsInCollapsedLinks);
		}
		children.addAll(elementsInCollapsedLinks);

		// sort links by name
		if (this.appearanceConfiguration.isSortLinks()) {
			sortLinks(links);
		}
		if (this.appearanceConfiguration.isSortLinksByType()) {
			// counting on the fact that sorting preserves the order of equal
			// elements
			sortLinksByType(links);
		}
		children.addAll(links);

		return children;
	}
	
	/**
	 * @param owner
	 *        the owner of the diagrams
	 * @return the list of diagrams contained by the given owner
	 */
	private List<Diagram> findAllExistingDiagrams(EObject owner) {
		ArrayList<Diagram> diagrams = new ArrayList<Diagram>();

		// Walk on page (Diagram) references
		for(Object page : pageMngr.allPages()) {
			if(!(page instanceof Diagram)) {
				continue;
			}
			// We have a GMF Diagram
			Diagram diagram = (Diagram)page;
			if(owner.equals(diagram.getElement())) {
				diagrams.add(diagram);
			}

		}

		return diagrams;
	}

	/**
	 * Return the initial values from the input.
	 * Input should be of type {@link UmlModel}.
	 * 
	 * @see org.eclipse.gmt.modisco.infra.browser.uicore.CustomizableModelContentProvider#getRootElements(java.lang.Object)
	 * 
	 * @param inputElement
	 * @return
	 */
	@Override
	public EObject[] getRootElements(Object inputElement) {

		try {
			if(!(inputElement instanceof ServicesRegistry)) {
				return null;
			}

			ServicesRegistry servicesRegistry = (ServicesRegistry)inputElement;

			modelSet = ModelUtils.getModelSetChecked(servicesRegistry);
			pageMngr = servicesRegistry.getService(DiSashModelMngr.class).getIPageMngr();

			return getRootElements(modelSet);
		} catch (Exception e) {
			Activator.log.error(e);
		}

		return new EObject[0];
	}

	/**
	 * Get the roots elements from the {@link ModelSet} provided as input.
	 * 
	 * @return
	 */
	protected EObject[] getRootElements(ModelSet modelSet) {
		UmlModel umlModel = (UmlUtils.getUmlModel(modelSet));

		if(umlModel == null) {
			return null;
		}

		EList<EObject> contents = umlModel.getResource().getContents();
		ArrayList<EObject> result = new ArrayList<EObject>();
		Iterator<EObject> iterator = contents.iterator();
		while(iterator.hasNext()) {
			EObject eObject = iterator.next();
			result.add(eObject);
		}
		return result.toArray(new EObject[result.size()]);
	}
}
