package org.eclipse.papyrus.views.modelexplorer.provider;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.utils.EditorUtils;
import org.eclipse.papyrus.infra.services.decoration.DecorationService;
import org.eclipse.papyrus.infra.services.decoration.util.IDecoration;
import org.eclipse.papyrus.infra.services.decoration.util.Decoration.PreferedPosition;
import org.eclipse.papyrus.views.modelexplorer.Activator;
import org.eclipse.papyrus.views.modelexplorer.Messages;
import org.eclipse.papyrus.views.modelexplorer.MoDiscoLabelProvider;
import org.eclipse.papyrus.views.modelexplorer.core.ui.pagebookview.ModelExplorerDecorationAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Search 시 CTRL+T 처럼 동작하도록
 * @author hanmomhanda
 *
 */
public class ApexUMLLabelProvider extends MoDiscoLabelProvider {
	
	/** Decoration Service *. */
	private DecorationService decorationService;

	/**
	 * Creates a new MoDiscoLabelProvider.
	 */
	public ApexUMLLabelProvider() {
		super();
		try {
			decorationService = EditorUtils.getServiceRegistry().getService(DecorationService.class);
			/* OR : decorationService = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry().getService(DecorationService.class); */
		} catch (ServiceException ex) {
			Activator.log.error(ex);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		String text = null;
		if(element instanceof Diagram) {
			Diagram diagram = (Diagram)element;
			text = diagram.getName();
		} else if(element instanceof IAdaptable) {
			EObject obj = (EObject)((IAdaptable)element).getAdapter(EObject.class);
			if(obj instanceof InternalEObject && obj.eIsProxy()) {
				InternalEObject internal = (InternalEObject)obj;
				text = NLS.bind(Messages.MoDiscoLabelProvider_ProxyLabel, obj.getClass().getSimpleName(), internal.eProxyURI().trimFragment());
				// Messages.MoDiscoLabelProvider_0 +  + Messages.MoDiscoLabelProvider_1 + ;;
			} else {
				text = super.getText(element);
			}
		} else {
			text = super.getText(element);
		}
		return text;
	}
	
	/**
	 * return the image of an element in the model browser
	 * evaluates error markers.
	 * 
	 * @param element
	 *        the element
	 * @return the image
	 */
	@Override
	public Image getImage(Object element) {

		// Get the Model Explorer Adapter
		ModelExplorerDecorationAdapter adapter = new ModelExplorerDecorationAdapter(null);


		//Set the decoration target
		/**
		 * Useless since EMF Facet integration with bug 358732
		 */
		if(element instanceof Diagram) {
			adapter.setDecoratorTarget(getEditorRegistry().getEditorIcon(element));
		} else {
			adapter.setDecoratorTarget(super.getImage(element));
		}

		//Set the decoration with default position
		if(element != null) {
			if(element instanceof EObject || (element instanceof IAdaptable && ((IAdaptable)element).getAdapter(EObject.class) != null)) {
				IDecoration decoration = decorationService.getDecoration(element, true);
				decoration.setDecorationImage(getImageDescriptor(decoration.getSeverity()));
				adapter.setDecoration(decoration.getDecorationImage(), PreferedPosition.DEFAULT);
			}
		}

		//return the target decorated
		return adapter.getDecoratedImage();

	}
	
	private ImageDescriptor getImageDescriptor(int severity) {

		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		ImageDescriptor overlay = null;
		switch(severity) {
		case 2://Error
			overlay = sharedImages.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR);
			break;
		case 1://Warning
			overlay = sharedImages.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING);
			break;
		}

		return overlay;
	}
}
