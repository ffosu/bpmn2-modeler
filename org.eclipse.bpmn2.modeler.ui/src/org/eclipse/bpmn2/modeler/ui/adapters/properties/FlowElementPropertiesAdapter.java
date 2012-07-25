/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Bob Brodt
 ******************************************************************************/

package org.eclipse.bpmn2.modeler.ui.adapters.properties;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.FeatureDescriptor;
import org.eclipse.bpmn2.modeler.core.adapters.ObjectDescriptor;
import org.eclipse.bpmn2.modeler.core.utils.ModelUtil;
import org.eclipse.bpmn2.modeler.ui.util.PropertyUtil;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Bob Brodt
 *
 */
public class FlowElementPropertiesAdapter extends ItemAwareElementPropertiesAdapter {

	/**
	 * @param adapterFactory
	 * @param object
	 */
	public FlowElementPropertiesAdapter(AdapterFactory adapterFactory, EObject object) {
		super(adapterFactory, object);
    	EStructuralFeature f = Bpmn2Package.eINSTANCE.getFlowElement_Name();
		final FeatureDescriptor fd = new FeatureDescriptor(adapterFactory,object, f) {

			@Override
			public void setText(String text) {
				int i = text.lastIndexOf("/");
				if (i>=0)
					text = text.substring(i+1);
				text = text.trim();
				((FlowElement)object).setName(text);
			}

			@Override
			public String getValueText(Object context) {
				FlowElement flowElement = (FlowElement)(context instanceof FlowElement ? context : this.object);
				String text = flowElement.getName();
				if (text==null || text.isEmpty())
					text = flowElement.getId();
				
				EObject container = flowElement.eContainer();
				while (container!=null) {
					if (container instanceof Participant) {
						container = ((Participant)container).getProcessRef();
						if (container==null)
							break;
					}
					if (container instanceof Activity || container instanceof Process) {
						text = PropertyUtil.getText(container) + "/" + text;
					}
					container = container.eContainer();
				}
				return text;
			}
			
		};
		setFeatureDescriptor(f, fd);
		
		setObjectDescriptor(new ObjectDescriptor(adapterFactory, object) {

			@Override
			public void setText(String text) {
				fd.setText(text);
				ModelUtil.setID(object);
			}

			@Override
			public String getText(Object context) {
				return fd.getValueText(context);
			}
		});
	}

}