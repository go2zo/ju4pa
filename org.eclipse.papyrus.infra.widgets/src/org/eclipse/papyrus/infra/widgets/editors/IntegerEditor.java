/*****************************************************************************
 * Copyright (c) 2010 CEA LIST.
 *    
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Camille Letavernier (CEA LIST) camille.letavernier@cea.fr - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.infra.widgets.editors;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.conversion.NumberToStringConverter;
import org.eclipse.core.databinding.conversion.StringToNumberConverter;
import org.eclipse.papyrus.infra.widgets.Activator;
import org.eclipse.swt.widgets.Composite;

/**
 * A Property Editor representing an Integer value
 * as a text box.
 * 
 * @author Camille Letavernier
 * 
 */
public class IntegerEditor extends StringEditor {

	/**
	 * The IConverter for converting data from the widget to the model
	 */
	private IConverter targetToModelConverter;

	/**
	 * Constructs an editor for Integer values. The widget is a Text field.
	 * 
	 * @param parent
	 *        The Composite in which this editor is created
	 * @param style
	 *        The Text's style
	 */
	public IntegerEditor(Composite parent, int style) {
		this(parent, style, null);
	}

	/**
	 * Constructs an editor for Integer values. The widget is a Text field.
	 * 
	 * @param parent
	 *        The Composite in which this editor is created
	 * @param style
	 *        The Text's style
	 * @param label
	 *        The editor's label
	 */
	public IntegerEditor(Composite parent, int style, String label) {
		super(parent, style, label);

		targetToModelConverter = StringToNumberConverter.toInteger(false);

		setConverters(targetToModelConverter, NumberToStringConverter.fromInteger(false));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getEditableType() {
		return Integer.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getValue() {
		try {
			return (Integer)targetToModelConverter.convert(super.getValue());
		} catch (Exception ex) {
			Activator.log.error(ex);
			return null;
		}
	}
}
