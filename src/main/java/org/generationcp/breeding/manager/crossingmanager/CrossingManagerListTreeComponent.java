package org.generationcp.breeding.manager.crossingmanager;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.crossingmanager.listeners.CrossingManagerTreeActionsListener;
import org.generationcp.breeding.manager.customfields.ListTreeTableComponent;
import org.generationcp.breeding.manager.listmanager.util.InventoryTableDropHandler;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.pojos.GermplasmList;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class CrossingManagerListTreeComponent extends ListTreeTableComponent {
	
	private static final long serialVersionUID = 8112173851252075693L;
	
	private Button addToFemaleListButton;
	private Button cancelButton;
	private Button addToMaleListButton;
	private Button openForReviewButton;
	private CrossingManagerMakeCrossesComponent source;
	private CrossingManagerTreeActionsListener crossingTreeActionsListener;

	public CrossingManagerListTreeComponent(
			CrossingManagerTreeActionsListener treeActionsListener,
			CrossingManagerMakeCrossesComponent source) {
		super(treeActionsListener);
		this.crossingTreeActionsListener = treeActionsListener;
		this.source = source;
	}

	@Override
	public void addListeners() {
		
		super.addListeners();
	
		
		addToFemaleListButton.addListener(new Button.ClickListener(){
			private static final long serialVersionUID = -3383724866291655410L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				Integer germplasmListId = (Integer) getGermplasmListSource().getValue();
				
				if(source.getModeView().equals(ModeView.INVENTORY_VIEW)){
					if(crossingTreeActionsListener instanceof SelectParentsComponent){
						MakeCrossesParentsComponent parentsComponent = ((SelectParentsComponent) crossingTreeActionsListener).getCrossingManagerMakeCrossesComponent().getParentsComponent();
						InventoryTableDropHandler inventoryTableDropHandler = parentsComponent.getFemaleParentTab().getInventoryTableDropHandler();
						inventoryTableDropHandler.addGermplasmListInventoryData(germplasmListId);
						
						if(parentsComponent.getFemaleTable().getItemIds().isEmpty()){
							crossingTreeActionsListener.addListToFemaleList(germplasmListId);
						} else {
							source.getParentsComponent().getFemaleParentTab().setHasUnsavedChanges(true);
							inventoryTableDropHandler.setHasChanges(true);
						}
						source.getParentsComponent().getParentTabSheet().setSelectedTab(0);
					}
					
					closeTreeWindow(event);
				}else{
					crossingTreeActionsListener.addListToFemaleList(germplasmListId);
					closeTreeWindow(event);
				}
			}

		});
		
		addToMaleListButton.addListener(new Button.ClickListener(){
			private static final long serialVersionUID = -7685621731871659880L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				Integer germplasmListId = (Integer) getGermplasmListSource().getValue();
				
				if(source.getModeView().equals(ModeView.INVENTORY_VIEW)){

					if(crossingTreeActionsListener instanceof SelectParentsComponent){
						MakeCrossesParentsComponent parentsComponent = ((SelectParentsComponent) crossingTreeActionsListener).getCrossingManagerMakeCrossesComponent().getParentsComponent();
						InventoryTableDropHandler inventoryTableDropHandler = parentsComponent.getMaleParentTab().getInventoryTableDropHandler();
						inventoryTableDropHandler.addGermplasmListInventoryData(germplasmListId);
						
						if(parentsComponent.getMaleTable().getItemIds().isEmpty()){
							crossingTreeActionsListener.addListToMaleList(germplasmListId);
						} else {
							source.getParentsComponent().getMaleParentTab().setHasUnsavedChanges(true);
							inventoryTableDropHandler.setHasChanges(true);
						}
						source.getParentsComponent().getParentTabSheet().setSelectedTab(1);
					}
					
					closeTreeWindow(event);
				}else{
					crossingTreeActionsListener.addListToMaleList(germplasmListId);
					closeTreeWindow(event);
				}
			}
			
		});
		
		openForReviewButton.addListener(new Button.ClickListener(){
			private static final long serialVersionUID = 2103866815084444657L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (germplasmList != null){
					getTreeActionsListener().studyClicked(germplasmList);
					closeTreeWindow(event);
				}
	            	
			}
			
		});
		
		cancelButton.addListener(new Button.ClickListener(){
			private static final long serialVersionUID = -3708969669687499248L;

			@Override
			public void buttonClick(ClickEvent event) {
				closeTreeWindow(event);
			}
			
		});
		
	}
	
	protected void closeTreeWindow(ClickEvent event) {
		Window dialog = event.getComponent().getParent().getWindow();
		dialog.getParent().getWindow().removeWindow(dialog);
	}
	
	public void showWarningInInventoryView(){
		String message = "Please switch to list view first before adding entries to parent lists.";
    	MessageNotifier.showError(getWindow(),"Warning!", message);
	}

	@Override
	public void layoutComponents() {
		
		super.layoutComponents();
		
		HorizontalLayout actionButtonsLayout = new HorizontalLayout();
		actionButtonsLayout.setSpacing(true);
		actionButtonsLayout.setStyleName("align-center");
		actionButtonsLayout.setMargin(true, false, false, false);
		

		actionButtonsLayout.addComponent(cancelButton);
		actionButtonsLayout.addComponent(addToFemaleListButton);
		actionButtonsLayout.addComponent(addToMaleListButton);
		actionButtonsLayout.addComponent(openForReviewButton);
	
		addComponent(actionButtonsLayout);
		
		
	}

	@Override
	public void instantiateComponents() {
		
		super.instantiateComponents();
		
		addToFemaleListButton = new Button();
		addToFemaleListButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		addToFemaleListButton.setCaption(messageSource.getMessage(Message.DIALOG_ADD_TO_FEMALE_LABEL));
		addToFemaleListButton.setEnabled(false);
		
		addToMaleListButton = new Button();
		addToMaleListButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		addToMaleListButton.setCaption(messageSource.getMessage(Message.DIALOG_ADD_TO_MALE_LABEL));
		addToMaleListButton.setEnabled(false);
		
		openForReviewButton = new Button();
		openForReviewButton.setStyleName(Bootstrap.Buttons.PRIMARY.styleName());
		openForReviewButton.setCaption(messageSource.getMessage(Message.DIALOG_OPEN_FOR_REVIEW_LABEL));
		openForReviewButton.setEnabled(false);
		
		cancelButton = new Button();
		cancelButton.setStyleName(Bootstrap.Buttons.DEFAULT.styleName());
		cancelButton.setCaption(messageSource.getMessage(Message.CANCEL));
		
	}

	@Override
	protected boolean doIncludeActionsButtons() {
		return true;
	}


	@Override
	protected boolean doIncludeRefreshButton() {
		return false;
	}

	@Override
	protected boolean isTreeItemsDraggable() {
		return true;
	}

	@Override
	protected boolean doIncludeCentralLists() {
		return true;
	}

	@Override
    public boolean doShowFoldersOnly() {
		return false;
	}
	
	@Override
	public String getTreeStyleName() {
		return "crossingManagerTree";
	}
	
	@Override
	public void refreshRemoteTree(){
        //current does not do anything, since there is no remote tree in the screen to be refresh
	}
	
	@Override
	public void studyClickedAction(GermplasmList germplasmList) {
		toggleListSelectionButtons(true);
	}
	
	@Override
	public void folderClickedAction(GermplasmList germplasmList) {
		toggleListSelectionButtons(false);
	}
	
	private void toggleListSelectionButtons(boolean enabled){
		addToFemaleListButton.setEnabled(enabled);
		addToMaleListButton.setEnabled(enabled);
		openForReviewButton.setEnabled(enabled);
	}
	

}
