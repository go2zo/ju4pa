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
package org.eclipse.papyrus.sysml.portandflows.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptorDecorator;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.papyrus.sysml.edit.provider.IComposableAdapterFactory;
import org.eclipse.papyrus.sysml.edit.provider.SysMLItemProviderAdapter;
import org.eclipse.papyrus.sysml.portandflows.FlowDirection;
import org.eclipse.papyrus.sysml.portandflows.FlowProperty;
import org.eclipse.papyrus.sysml.portandflows.PortandflowsPackage;
import org.eclipse.papyrus.sysml.provider.SysmlEditPlugin;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.util.UMLUtil;

/**
 * This is the item provider adapter for a {@link org.eclipse.papyrus.sysml.portandflows.FlowProperty} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * 
 * @generated
 */
public class FlowPropertyItemProvider extends SysMLItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource

{

	/**
	 * Pattern prefix of flowProperty
	 * 
	 * @generated
	 */
	private static Pattern FLOW_PROPERTY_PREFIX_PATTERN = Pattern.compile("(flowProperty, |<<flowProperty>>|, flowProperty)");

	/**
	 * Get the prefix pattern of PROPERTY_PREFIX_PATTERN
	 * 
	 * @generated
	 */

	private static Pattern PROPERTY_PREFIX_PATTERN = Pattern.compile("Property");

	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public FlowPropertyItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if(object instanceof FlowProperty) {
			if(itemPropertyDescriptors == null) {
				super.getPropertyDescriptors(object);

				addBase_PropertyPropertyDescriptor(object);
				addDirectionPropertyDescriptor(object);
			}
		}

		/**
		 * Handle Property stereotyped by FlowProperty
		 */
		if(object instanceof org.eclipse.uml2.uml.Property) {
			org.eclipse.uml2.uml.Property element = (org.eclipse.uml2.uml.Property)object;
			/**
			 * This is used to store all the property descriptors for a class stereotyped with a block.
			 * Derived classes should add descriptors to this vector.
			 */

			List<IItemPropertyDescriptor> itemPropertyDescriptorsForproperty = new ArrayList<IItemPropertyDescriptor>();
			ItemProviderAdapter ite = ((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory().getItemProvider(UMLPackage.Literals.PROPERTY);
			final List<IItemPropertyDescriptor> propertyDescriptors = ite.getPropertyDescriptors(this);

			itemPropertyDescriptorsForproperty.addAll(propertyDescriptors);
			FlowProperty steApplication = UMLUtil.getStereotypeApplication(element, FlowProperty.class);
			if(steApplication != null) {

				itemPropertyDescriptorsForproperty.add(createBase_PropertyPropertyDescriptorForProperty(steApplication));

				itemPropertyDescriptorsForproperty.add(createDirectionPropertyDescriptorForProperty(steApplication));

			}
			return itemPropertyDescriptorsForproperty;

		}

		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Base Property feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addBase_PropertyPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_FlowProperty_base_Property_feature"), getString("_UI_PropertyDescriptor_description", "_UI_FlowProperty_base_Property_feature", "_UI_FlowProperty_type"), PortandflowsPackage.Literals.FLOW_PROPERTY__BASE_PROPERTY, true, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Base Property feature for the UML element Property.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ItemPropertyDescriptorDecorator createBase_PropertyPropertyDescriptorForProperty(Object object) {

		return new ItemPropertyDescriptorDecorator(object, createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_FlowProperty_base_Property_feature"),

		getString("_UI_PropertyDescriptor_description", "_UI_FlowProperty_base_Property_feature", "_UI_FlowProperty_type"),

		PortandflowsPackage.Literals.FLOW_PROPERTY__BASE_PROPERTY, true, false, true,

		null,

		null,

		null));

	}

	/**
	 * This adds a property descriptor for the Direction feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected void addDirectionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_FlowProperty_direction_feature"), getString("_UI_PropertyDescriptor_description", "_UI_FlowProperty_direction_feature", "_UI_FlowProperty_type"), PortandflowsPackage.Literals.FLOW_PROPERTY__DIRECTION, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Direction feature for the UML element Property.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ItemPropertyDescriptorDecorator createDirectionPropertyDescriptorForProperty(Object object) {

		return new ItemPropertyDescriptorDecorator(object, createItemPropertyDescriptor(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(), getResourceLocator(), getString("_UI_FlowProperty_direction_feature"),

		getString("_UI_PropertyDescriptor_description", "_UI_FlowProperty_direction_feature", "_UI_FlowProperty_type"),

		PortandflowsPackage.Literals.FLOW_PROPERTY__DIRECTION, true, false, false,

		ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,

		null,

		null));

	}

	/**
	 * This returns FlowProperty.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		Object composedImage = overlayImage(object, getResourceLocator().getImage("full/obj16/FlowProperty"));
		if(object instanceof NamedElement) {
			ComposedImage aux = new ComposedImage(Collections.singletonList(composedImage));
			return (Object)composeVisibilityImage(object, aux);
		}
		return composedImage;
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		/**
		 * Handle Stereotype item and stereoted element
		 */
		FlowProperty flowProperty_ = null;

		if(object instanceof org.eclipse.uml2.uml.Property) {
			EObject steApplication = UMLUtil.getStereotypeApplication((org.eclipse.uml2.uml.Property)object, FlowProperty.class);
			Stereotype ste = UMLUtil.getStereotype(steApplication);
			if(ste != null) {
				IItemLabelProvider ite = (IItemLabelProvider)((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory().getItemProvider(UMLPackage.Literals.PROPERTY);
				String result = ite.getText(object);
				result = FLOW_PROPERTY_PREFIX_PATTERN.matcher(result).replaceFirst("");
				return PROPERTY_PREFIX_PATTERN.matcher(result).replaceFirst("FlowProperty");
			}

		}

		if(flowProperty_ == null) {
			flowProperty_ = (FlowProperty)object;
		}

		FlowDirection labelValue = ((FlowProperty)flowProperty_).getDirection();
		String label = labelValue == null ? null : labelValue.toString();
		return label == null || label.length() == 0 ? getString("_UI_FlowProperty_type") : getString("_UI_FlowProperty_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch(notification.getFeatureID(FlowProperty.class)) {
		case PortandflowsPackage.FLOW_PROPERTY__DIRECTION:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		}

		/**
		 * Notify UML element
		 */
		if(((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory() != null) {

			/**
			 * Handle Property stereotyped by FlowProperty
			 */

			if(notification.getFeatureID(org.eclipse.uml2.uml.Property.class) != Notification.NO_FEATURE_ID) {
				ItemProviderAdapter ite = ((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory().getItemProvider(UMLPackage.Literals.PROPERTY);
				ite.notifyChanged(notification);
				return;

			}

		}

		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return SysmlEditPlugin.INSTANCE;
	}

	/**
	 * Override in order to handle has children for based class
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Collection<?> getChildren(Object object) {
		Collection<Object> result = (Collection<Object>)super.getChildren(object);
		if(object instanceof EObject) {
			EObject eObject = (EObject)object;
			/**
			 * Handle based elements type
			 */
			if(((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory() != null) {

				/**
				 * Handle Property stereotyped by FlowProperty
				 */
				if(UMLPackage.Literals.PROPERTY.equals(eObject.eClass())) {
					ItemProviderAdapter ite = ((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory().getItemProvider(UMLPackage.Literals.PROPERTY);
					result.addAll((Collection<Object>)ite.getChildren(object));
					return result;
				}

			}
		}
		return result;
	}

	/**
	 * Override in order to handle has children for based class
	 * 
	 * @generated
	 */
	@Override
	public boolean hasChildren(Object object) {
		if(object instanceof EObject) {
			EObject eObject = (EObject)object;
			/**
			 * Handle based elements type
			 */
			if(((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory() != null) {

				/**
				 * Handle Property stereotyped by FlowProperty
				 */
				if(UMLPackage.Literals.PROPERTY.equals(eObject.eClass())) {
					ItemProviderAdapter ite = ((IComposableAdapterFactory)adapterFactory).getIRootAdapterFactory().getItemProvider(UMLPackage.Literals.PROPERTY);
					return super.hasChildren(object) || ite.hasChildren(object);
				}

			}
		}
		return super.hasChildren(object);
	}

}
