package org.eclipse.papyrus.views.modelexplorer.dialog;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.text.AbstractInformationControl;
import org.eclipse.jdt.internal.ui.typehierarchy.TypeHierarchyMessages;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.emf.providers.MoDiscoContentProvider;
import org.eclipse.papyrus.views.modelexplorer.provider.ApexUMLContentProvider;
import org.eclipse.papyrus.views.modelexplorer.provider.ApexUMLLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.keys.KeySequence;
import org.eclipse.ui.keys.SWTKeySupport;

public class ApexHierarchyInformationControl extends
		AbstractInformationControl {
	private KeyAdapter fKeyAdapter;
	
	private TreeViewer treeViewer;
	private ServicesRegistry serviceRegistry;
	private IContentProvider contentProvider;
	
	public ApexHierarchyInformationControl(Shell parent, int shellStyle, int treeStyle) {
		super(parent, shellStyle, treeStyle, IJavaEditorActionDefinitionIds.OPEN_HIERARCHY, true);		
	}
	
	private KeyAdapter getKeyAdapter() {
		if (fKeyAdapter == null) {
			fKeyAdapter= new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					int accelerator = SWTKeySupport.convertEventToUnmodifiedAccelerator(e);
					KeySequence keySequence = KeySequence.getInstance(SWTKeySupport.convertAcceleratorToKeyStroke(accelerator));
					KeySequence[] sequences= getInvokingCommandKeySequences();
					if (sequences == null)
						return;

					for (int i= 0; i < sequences.length; i++) {
						if (sequences[i].equals(keySequence)) {
							e.doit= false;
//							toggleHierarchy();
							return;
						}
					}
				}
			};
		}
		return fKeyAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean hasHeader() {
		return true;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.text.JavaOutlineInformationControl#createTreeViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	protected TreeViewer createTreeViewer(Composite parent, int style) {
		
		Tree tree= new Tree(parent, SWT.SINGLE);
		GridData gd= new GridData(GridData.FILL_BOTH);
		gd.heightHint= tree.getItemHeight() * 12;
		tree.setLayoutData(gd);

		treeViewer= new TreeViewer(tree);
		
		treeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof IType;
			}
		});
		
		contentProvider = new ApexUMLContentProvider();
		
		treeViewer.setContentProvider(contentProvider);

		treeViewer.setLabelProvider(new ApexUMLLabelProvider());
		
		treeViewer.getTree().addKeyListener(getKeyAdapter());
		
//		treeViewer.setInput(null);

		return treeViewer;
		/*
		EObject[] rootElements = null;
		try {
			serviceRegistry = ServiceUtilsForActionHandlers.getInstance().getServiceRegistry();
			rootElements = ((MoDiscoContentProvider)contentProvider).getRootElements(serviceRegistry);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		
		for ( EObject eObj : rootElements ) {
			System.out.println("rootElements : " + eObj);
			TreeIterator treeIt = eObj.eAllContents();
			while( treeIt.hasNext() ) {
				Object obj = treeIt.next();
				System.out.println("treeItObj : " + obj);
			}
		}
		*/
	}

	private String getHeaderLabel(IJavaElement input) {
		/*
		if (input instanceof IMethod) {
			String[] args= { JavaElementLabels.getElementLabel(input.getParent(), JavaElementLabels.ALL_DEFAULT), JavaElementLabels.getElementLabel(input, JavaElementLabels.ALL_DEFAULT) };
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_methodhierarchy_label, args);
		} else if (input != null) {
			String arg= JavaElementLabels.getElementLabel(input, JavaElementLabels.DEFAULT_QUALIFIED);
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_hierarchy_label, arg);
		} else {
			return ""; //$NON-NLS-1$
		}
		*/
		System.out.println(TypeHierarchyMessages.HierarchyInformationControl_hierarchy_label);
		return "Type to filter";
	}

	/*
	@Override
	protected String getStatusFieldText() {
		KeySequence[] sequences= getInvokingCommandKeySequences();
		String keyName= ""; //$NON-NLS-1$
		if (sequences != null && sequences.length > 0)
			keyName= sequences[0].format();

		if (fOtherContentProvider instanceof TraditionalHierarchyContentProvider) {
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_toggle_traditionalhierarchy_label, keyName);
		} else {
			return Messages.format(TypeHierarchyMessages.HierarchyInformationControl_toggle_superhierarchy_label, keyName);
		}
	}
	*/

	/*
	 * @see org.eclipse.jdt.internal.ui.text.AbstractInformationControl#getId()
	 */
	protected String getId() {
		return "org.eclipse.jdt.internal.ui.typehierarchy.QuickHierarchy"; //$NON-NLS-1$
	}

	@Override
	public void setInput(Object information) {
		Object[] rootElements = ((MoDiscoContentProvider)contentProvider).getRootElements(information);
		if ( rootElements.length > 0 ) {
			inputChanged(information, rootElements[0]);
		} else {
			inputChanged(information, null);
		}
			
	}
	
	@Override
	protected String getStatusFieldText() {
		return "Status Field Text";
	}
	
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
}

	

	
