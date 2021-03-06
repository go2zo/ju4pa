/*****************************************************************************
 * Copyright (c) 2011 CEA LIST.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *		
 *		CEA LIST - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.sysml.diagram.blockdefinition.provider;

import static org.eclipse.papyrus.infra.core.Activator.log;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.gmf.runtime.diagram.core.preferences.PreferencesHint;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateEdgeViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateNodeViewOperation;
import org.eclipse.gmf.runtime.diagram.core.services.view.CreateViewForKindOperation;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.Node;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.gmf.diagram.common.provider.IGraphicalTypeRegistry;
import org.eclipse.papyrus.uml.diagram.clazz.providers.UMLViewProvider;
import org.eclipse.papyrus.uml.diagram.common.commands.SemanticAdapter;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;

public class InheritedClassDiagramViewProvider extends UMLViewProvider {

	/** Local graphical type registry */
	protected IGraphicalTypeRegistry registry = new GraphicalTypeRegistry();

	@Override
	public Edge createEdge(IAdaptable semanticAdapter, View containerView, String semanticHint, int index, boolean persisted, PreferencesHint preferencesHint) {
		Edge createdEdge = null;

		IElementType elementType = (IElementType)semanticAdapter.getAdapter(IElementType.class);
		if(elementType != null) {
			createdEdge = super.createEdge(semanticAdapter, containerView, semanticHint, index, persisted, preferencesHint);
		} else {

			EObject domainElement = (EObject)semanticAdapter.getAdapter(EObject.class);

			String domainElementGraphicalType = semanticHint;
			if(domainElementGraphicalType == null) {
				domainElementGraphicalType = registry.getEdgeGraphicalType(domainElement);
			}

			if((!IGraphicalTypeRegistry.UNDEFINED_TYPE.equals(domainElementGraphicalType)) && (registry.isKnownEdgeType(domainElementGraphicalType))) {
				// Cannot use createEdge from super class as it never take the graphical type (semanticHint) into account.
				// createdEdge = super.createEdge(semanticAdapter, containerView, domainElementGraphicalType, index, persisted, preferencesHint);

				if(ElementTypes.COMMENT_ANNOTATED_ELEMENT.getSemanticHint().equals(domainElementGraphicalType)) {
					createdEdge = createCommentAnnotatedElement_4013(containerView, index, persisted, preferencesHint);
				}
				if(ElementTypes.CONSTRAINT_CONSTRAINED_ELEMENT.getSemanticHint().equals(domainElementGraphicalType)) {
					createdEdge = createConstraintConstrainedElement_4014(containerView, index, persisted, preferencesHint);
				}
			}
		}

		if(createdEdge == null) {
			log.error(new Exception("Could not create Edge."));
		}

		return createdEdge;
	}

	@Override
	protected boolean provides(CreateViewForKindOperation op) {
		// Never use this method (often incorrectly implemented due to GMF Tooling choices).
		String diagramType = op.getContainerView().getDiagram().getType();
		if(!ElementTypes.DIAGRAM_ID.equals(diagramType)) {
			return false;
		}

		throw new UnsupportedOperationException("Should never be called by the "+diagramType+" diagram.");
	}

	@Override
	protected boolean provides(CreateEdgeViewOperation op) {

		// Must have a container
		if(op.getContainerView() == null) {
			return false;
		}

		// This provider is registered for BlockDefinition Diagram only
		String diagramType = op.getContainerView().getDiagram().getType();
		if(!ElementTypes.DIAGRAM_ID.equals(diagramType)) {
			return false;
		}

		IElementType elementType = getSemanticElementType(op.getSemanticAdapter());
		if(elementType == ElementTypes.COMMENT_ANNOTATED_ELEMENT) {
			return true;
		}
		if(elementType == ElementTypes.CONSTRAINT_CONSTRAINED_ELEMENT) {
			return true;
		}

		// /////////////////////////////////////////////////////////////////////
		// Test possibility to provide a view based on the semantic nature and its expected container.
		// /////////////////////////////////////////////////////////////////////

		// IElementType may be null (especially when drop from ModelExplorer).
		// In such a case, test the semantic EObject instead.	
		if(elementType == null) {
			EObject domainElement = (EObject)op.getSemanticAdapter().getAdapter(EObject.class);
			String domainElementGraphicalType = op.getSemanticHint();
			if(domainElementGraphicalType == null) {
				domainElementGraphicalType = registry.getEdgeGraphicalType(domainElement);
			}

			if((!IGraphicalTypeRegistry.UNDEFINED_TYPE.equals(domainElementGraphicalType)) && (registry.isKnownEdgeType(domainElementGraphicalType))) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected boolean provides(CreateNodeViewOperation op) {
		// Must have a container
		if(op.getContainerView() == null) {
			return false;
		}
		// Get the type of the container
		String containerGraphicalType = op.getContainerView().getType();

		// This provider is registered for BlockDefinition Diagram only
		String diagramType = op.getContainerView().getDiagram().getType();
		if(!ElementTypes.DIAGRAM_ID.equals(diagramType)) {
			return false;
		}

		// /////////////////////////////////////////////////////////////////////
		// Test possibility to provide a view based on the ElementType and its expected container.
		// /////////////////////////////////////////////////////////////////////

		IElementType elementType = (IElementType)op.getSemanticAdapter().getAdapter(IElementType.class);
		if(elementType == UMLElementTypes.MODEL) {
			if(ElementTypes.DIAGRAM_ID.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			return false;
		}
		if(elementType == UMLElementTypes.PACKAGE) {
			if(ElementTypes.DIAGRAM_ID.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			return false;
		}
		if(elementType == UMLElementTypes.INSTANCE_SPECIFICATION) {
			if(ElementTypes.DIAGRAM_ID.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			return false;
		}
		if(elementType == UMLElementTypes.CONSTRAINT) {
			if(ElementTypes.DIAGRAM_ID.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			return false;
		}
		if(elementType == UMLElementTypes.COMMENT) {
			if(ElementTypes.DIAGRAM_ID.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.MODEL_CN_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.PACKAGE_COMPARTMENT_PACKAGEABLE_ELEMENT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			return false;
		}
		if(elementType == UMLElementTypes.SLOT) {
			if(ElementTypes.INSTANCE_SPECIFICATION_COMPARTMENT_SLOT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			if(ElementTypes.INSTANCE_SPECIFICATION_CN_COMPARTMENT_SLOT_HINT.equals(containerGraphicalType)) {
				return true;
			}
			return false;
		}

		// /////////////////////////////////////////////////////////////////////
		// Test possibility to provide a view based on the semantic nature and its expected container.
		// /////////////////////////////////////////////////////////////////////

		// IElementType may be null (especially when drop from ModelExplorer).
		// In such a case, test the semantic EObject instead.
		if(elementType == null) {
			EObject domainElement = (EObject)op.getSemanticAdapter().getAdapter(EObject.class);
			String domainElementGraphicalType = op.getSemanticHint();
			if(domainElementGraphicalType == null) {
				domainElementGraphicalType = registry.getNodeGraphicalType(domainElement, containerGraphicalType);
			} else {
				domainElementGraphicalType = registry.getNodeGraphicalType(domainElementGraphicalType, containerGraphicalType);
			}

			if((!IGraphicalTypeRegistry.UNDEFINED_TYPE.equals(domainElementGraphicalType)) && (registry.isKnownNodeType(domainElementGraphicalType))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Node createNode(IAdaptable semanticAdapter, View containerView, String semanticHint, int index, boolean persisted, PreferencesHint preferencesHint) {

		// Use the GraphicalTypeRegistry to find the expected type for a domain element
		// Get the type of the container
		String containerGraphicalType = containerView.getType();
		
		// Get the type of the domain element
		EObject domainElement = (EObject)semanticAdapter.getAdapter(EObject.class);
		
		if(semanticHint != null) {
			// Look for a possible graphicalType replacement
			String graphicalType = registry.getNodeGraphicalType(semanticHint, containerGraphicalType);
			return super.createNode(new SemanticAdapter(domainElement, null), containerView, graphicalType, index, persisted, preferencesHint);
		}

		String domainElementGraphicalType = registry.getNodeGraphicalType(domainElement, containerGraphicalType);

//		if(semanticHint != null) {
//			return super.createNode(semanticAdapter, containerView, semanticHint, index, persisted, preferencesHint);
//		}
//
//		// Use the GraphicalTypeRegistry to find the expected type for a domain element
//		// Get the type of the container
//		String containerGraphicalType = containerView.getType();
//		// Get the type of the domain element
//		EObject domainElement = (EObject)semanticAdapter.getAdapter(EObject.class);
//		String domainElementGraphicalType = registry.getNodeGraphicalType(domainElement, containerGraphicalType);

		// Create the expected node
		if(!IGraphicalTypeRegistry.UNDEFINED_TYPE.equals(domainElementGraphicalType)) {
			return super.createNode(semanticAdapter, containerView, domainElementGraphicalType, index, persisted, preferencesHint);
		}

		log.error(new Exception("Could not create Node."));
		return null;
	}

	@Override
	protected void stampShortcut(View containerView, Node target) {
		if(!ElementTypes.DIAGRAM_ID.equals(containerView.getDiagram().getType())) {
			EAnnotation shortcutAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			shortcutAnnotation.setSource("Shortcut"); //$NON-NLS-1$
			shortcutAnnotation.getDetails().put("modelID", ElementTypes.DIAGRAM_ID); //$NON-NLS-1$
			target.getEAnnotations().add(shortcutAnnotation);
		}
	}
}
