/**
 * 
 *  Copyright (c) 2011 CEA LIST.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Vincent Lorenzo(CEA LIST) - initial API and implementation
 */
package org.eclipse.papyrus.sysml.facets.query.value.setter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.facet.infra.query.core.exception.ModelQueryExecutionException;
import org.eclipse.emf.facet.infra.query.core.java.IJavaModelQueryWithEditingDomain;
import org.eclipse.emf.facet.infra.query.core.java.ParameterValueList;
import org.eclipse.emf.facet.infra.query.runtime.ModelQueryParameterValue;
import org.eclipse.gmf.runtime.common.core.command.CompositeCommand;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateRelationshipRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.DestroyElementRequest;
import org.eclipse.osgi.util.NLS;
import org.eclipse.papyrus.commands.wrappers.GMFtoEMFCommandWrapper;
import org.eclipse.papyrus.gmf.diagram.common.commands.IdentityCommandWithNotification;
import org.eclipse.papyrus.infra.services.edit.service.ElementEditServiceUtils;
import org.eclipse.papyrus.infra.services.edit.service.IElementEditService;
import org.eclipse.papyrus.infra.widgets.toolbox.notification.Type;
import org.eclipse.papyrus.sysml.facets.messages.Messages;
import org.eclipse.papyrus.sysml.requirements.DeriveReqt;
import org.eclipse.papyrus.sysml.requirements.Requirement;
import org.eclipse.papyrus.sysml.service.types.element.SysMLElementTypes;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Dependency;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.util.UMLUtil;

/** Query to set the derived attribute "derived" of the requirement */
public class SetRequirementDerivedFromQuery implements IJavaModelQueryWithEditingDomain<Class, EObject> {


	/**
	 * 
	 * @see org.eclipse.emf.facet.infra.query.core.java.IJavaModelQuery#evaluate(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.facet.infra.query.core.java.ParameterValueList)
	 * 
	 * @param context
	 * @param parameterValues
	 * @return
	 * @throws ModelQueryExecutionException
	 */
	public EObject evaluate(final Class context, final ParameterValueList parameterValues) throws ModelQueryExecutionException {
		//nothing to do
		return null;
	}

	/**
	 * 
	 * @see org.eclipse.emf.facet.infra.query.core.java.IJavaModelQueryWithEditingDomain#evaluate(org.eclipse.emf.ecore.EObject,
	 *      org.eclipse.emf.facet.infra.query.core.java.ParameterValueList, org.eclipse.emf.edit.domain.EditingDomain)
	 * 
	 * @param arg0
	 * @param parameter
	 * @param arg2
	 * @return
	 * @throws ModelQueryExecutionException
	 */
	public EObject evaluate(final Class context, final ParameterValueList parameter, final EditingDomain editingDomain) throws ModelQueryExecutionException {
		CompositeCommand cmd = new CompositeCommand("Edit the feature /DerivedFrom"); //$NON-NLS-1$
		/*
		 * we need to do this test, because, the facets can be applied on a default table.
		 * In this case, we can't be sure the edited element is a Requirement
		 */
		if(UMLUtil.getStereotypeApplication(context, Requirement.class) != null) {
			Requirement req = UMLUtil.getStereotypeApplication(context, Requirement.class);
			EList<Dependency> dependencies = context.getClientDependencies();
			EList<Requirement> currentDerivedFrom = req.getDerivedFrom();
			List<Class> currentDerivedFrom_base_Class = new ArrayList<Class>();
			for(Requirement currentReq : currentDerivedFrom) {
				currentDerivedFrom_base_Class.add(currentReq.getBase_Class());
			}


			ModelQueryParameterValue object = parameter.get(0);
			Object values = object.getValue();
			Assert.isTrue(values instanceof List<?>);
			List<?> newDerivedFrom = (List<?>)values;

			//we destroy the unnecessary Derive_Reqt
			for(Dependency current : dependencies) {
				if(UMLUtil.getStereotypeApplication(current, DeriveReqt.class) != null) {
					EList<NamedElement> suppliers = current.getSuppliers();
					//we assume that there is only one supplier
					if(suppliers.size() == 1) {
						NamedElement supplier = suppliers.get(0);
						if(!newDerivedFrom.contains(supplier)) {//we destroy this dependencies
							IElementEditService provider = ElementEditServiceUtils.getCommandProvider(current);
							DestroyElementRequest request = new DestroyElementRequest(current, false);
							ICommand desroyCommand = provider.getEditCommand(request);
							cmd.add(desroyCommand);
						}
					}
				}
			}

			for(Object current : (List<?>)values) {
				//we create the derive_reqt only if it doesn't exist

				if(!currentDerivedFrom_base_Class.contains(current)) {
					if(UMLUtil.getStereotypeApplication(context, Requirement.class) != null) {
						IElementEditService provider = ElementEditServiceUtils.getCommandProvider(context);
						CreateRelationshipRequest createRequest = new CreateRelationshipRequest(context.getNearestPackage(), context, (EObject)current, SysMLElementTypes.DERIVE_REQT);
						cmd.add(provider.getEditCommand(createRequest));
					} else {

						cmd.add(new IdentityCommandWithNotification(Messages.SetRequirementTextQuery_AssignmentCantBeDone, NLS.bind(Messages.SetRequirementDerivedFromQuery_DeriveReqtCantBeCreated, context.getName(), ((NamedElement)current).getName()), Type.ERROR));
					}
				}
			}
		} else {
			cmd.add(new IdentityCommandWithNotification(Messages.SetRequirementTextQuery_AssignmentCantBeDone, Messages.SetRequirementTextQuery_NotASysMLRequirement, Type.ERROR));
		}
		editingDomain.getCommandStack().execute(new GMFtoEMFCommandWrapper(cmd));
		return null;
	}

}
