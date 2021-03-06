/*****************************************************************************
 * Copyright (c) 2009 CEA LIST.
 *
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Yann Tanguy (CEA LIST) yann.tanguy@cea.fr - Initial API and implementation
 *
 *****************************************************************************/
package org.eclipse.papyrus.sysml.allocations.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;
import org.eclipse.papyrus.sysml.allocations.Allocate;
import org.eclipse.papyrus.sysml.allocations.AllocateActivityPartition;
import org.eclipse.papyrus.sysml.allocations.Allocated;
import org.eclipse.papyrus.sysml.allocations.AllocationsPackage;
import org.eclipse.uml2.uml.Abstraction;
import org.eclipse.uml2.uml.ActivityPartition;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.util.UMLUtil;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance
 * hierarchy. It supports the call {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the
 * inheritance hierarchy until a non-null result is returned, which is the
 * result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.papyrus.sysml.allocations.AllocationsPackage
 * @generated
 */
public class AllocationsSwitch<T> extends Switch<T> {

	/**
	 * The cached model package
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected static AllocationsPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	public AllocationsSwitch() {
		if(modelPackage == null) {
			modelPackage = AllocationsPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @parameter ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		/**
		 * ePackage == UMLPackage.eINSTANCE in order to accept UML element
		 */
		return ePackage == modelPackage || ePackage == UMLPackage.eINSTANCE;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Allocate</em>'.
	 * <!-- begin-user-doc --> This implementation returns
	 * null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Allocate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAllocate(Allocate object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Allocate Activity Partition</em>'.
	 * <!-- begin-user-doc --> This
	 * implementation returns null; returning a non-null result will terminate
	 * the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Allocate Activity Partition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAllocateActivityPartition(AllocateActivityPartition object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Allocated</em>'.
	 * <!-- begin-user-doc --> This implementation returns
	 * null; returning a non-null result will terminate the switch. <!--
	 * end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Allocated</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAllocated(Allocated object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc --> This implementation returns
	 * null; returning a non-null result will terminate the switch, but this is
	 * the last case anyway. <!-- end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

	/**
	 * Tell if the Abstraction is a Allocate implementation
	 * 
	 * @param Abstraction
	 * @return
	 * @generated
	 */
	protected Boolean isAllocateFromAbstraction(Abstraction abstraction_) {
		if(UMLUtil.getStereotypeApplication(abstraction_, Allocate.class) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Allocate</em>' from Abstraction object. <!--
	 * begin-user-doc --> This implementation returns null; returning a non-null result will
	 * terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Allocate</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAbstractionStereotypedByAllocate(Abstraction abstraction_) {
		return null;
	}

	/**
	 * Tell if the NamedElement is a Allocated implementation
	 * 
	 * @param NamedElement
	 * @return
	 * @generated
	 */
	protected Boolean isAllocatedFromNamedElement(NamedElement namedElement_) {
		if(UMLUtil.getStereotypeApplication(namedElement_, Allocated.class) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Allocated</em>' from NamedElement object. <!--
	 * begin-user-doc --> This implementation returns null; returning a non-null result will
	 * terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Allocated</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNamedElementStereotypedByAllocated(NamedElement namedElement_) {
		return null;
	}

	/**
	 * Tell if the ActivityPartition is a AllocateActivityPartition implementation
	 * 
	 * @param ActivityPartition
	 * @return
	 * @generated
	 */
	protected Boolean isAllocateActivityPartitionFromActivityPartition(ActivityPartition activityPartition_) {
		if(UMLUtil.getStereotypeApplication(activityPartition_, AllocateActivityPartition.class) != null) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>AllocateActivityPartition</em>' from ActivityPartition object. <!--
	 * begin-user-doc --> This implementation returns null; returning a non-null result will
	 * terminate the switch. <!-- end-user-doc -->
	 * 
	 * @param object
	 *        the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>AllocateActivityPartition</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseActivityPartitionStereotypedByAllocateActivityPartition(ActivityPartition activityPartition_) {
		return null;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		if(theEObject.eClass().getEPackage() == UMLPackage.eINSTANCE) {
			switch(classifierID) {





			case UMLPackage.ABSTRACTION:
			{
				Abstraction umlElement = (Abstraction)theEObject;
				T result;

				result = caseAbstractionStereotypedByAllocate(umlElement);
				if(result != null) {
					return result;
				}

				return null;
			}





			case UMLPackage.ACTIVITY_PARTITION:
			{
				ActivityPartition umlElement = (ActivityPartition)theEObject;
				T result;

				result = caseActivityPartitionStereotypedByAllocateActivityPartition(umlElement);
				if(result != null) {
					return result;
				}

				return null;
			}





			case UMLPackage.NAMED_ELEMENT:
			{
				NamedElement umlElement = (NamedElement)theEObject;
				T result;

				result = caseNamedElementStereotypedByAllocated(umlElement);
				if(result != null) {
					return result;
				}

				return null;
			}




			default:
				return defaultCase(theEObject);
			}




		} else {
			switch(classifierID) {

			case AllocationsPackage.ALLOCATE:
			{
				Allocate allocate = (Allocate)theEObject;
				T result = caseAllocate(allocate);

				if(result == null)
					result = defaultCase(theEObject);
				return result;
			}


			case AllocationsPackage.ALLOCATED:
			{
				Allocated allocated = (Allocated)theEObject;
				T result = caseAllocated(allocated);

				if(result == null)
					result = defaultCase(theEObject);
				return result;
			}


			case AllocationsPackage.ALLOCATE_ACTIVITY_PARTITION:
			{
				AllocateActivityPartition allocateActivityPartition = (AllocateActivityPartition)theEObject;
				T result = caseAllocateActivityPartition(allocateActivityPartition);

				if(result == null)
					result = defaultCase(theEObject);
				return result;
			}

			default:
				return defaultCase(theEObject);
			}
		}
	}

} // AllocationsSwitch
