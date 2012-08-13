package org.eclipse.papyrus.views.modelexplorer.provider;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.emf.providers.MoDiscoContentProvider;
/**
 * Search 시 CTRL+T 처럼 동작하도록
 * @author hanmomhanda
 *
 */
public class ApexUMLContentProvider extends MoDiscoContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private ServicesRegistry serviceRegistry;
	
	public ApexUMLContentProvider() {
		super();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.serviceRegistry = (ServicesRegistry) newInput;
	}

	public Object getParent(Object element) {
		return null;
	}
}
