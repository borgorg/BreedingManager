package org.generationcp.breeding.manager.listmanager.listeners;

import java.util.Date;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listmanager.BuildNewListComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Table;

public class ResetListButtonClickListener implements Button.ClickListener{

	private static final long serialVersionUID = -2641642996209640461L;
	
	private BuildNewListComponent source;
	
	private SimpleResourceBundleMessageSource messageSource;
	
	public ResetListButtonClickListener(BuildNewListComponent source, SimpleResourceBundleMessageSource messageSource){
		this.source = source;
		this.messageSource = messageSource;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {

		ConfirmDialog.show(source.getWindow(), messageSource.getMessage(Message.SAVE_CROSSES_MADE), 
            messageSource.getMessage(Message.CONFIRM_RESET_LIST_BUILDER_FIELDS), 
            messageSource.getMessage(Message.OK), messageSource.getMessage(Message.CANCEL_LABEL), 
            new ConfirmDialog.Listener() {
				private static final long serialVersionUID = 1L;
				public void onClose(ConfirmDialog dialog) {
                    if (dialog.isConfirmed()) {
                    	resetListBuilder();
                    }
                }
            }
        );
	}

	public void resetListBuilder(){
		//Reset fields
		source.getListTypeComboBox().setValue(source.DEFAULT_LIST_TYPE);
		source.getListDateField().setValue(new Date());
		source.getListNameText().setValue("");
		source.getDescriptionText().setValue("");
		source.getNotesTextArea().setValue("");
		
		//Recreate table, why? The user might have added columns
		source.removeComponent(source.getGermplasmsTable());
		source.createGermplasmTable();
		source.setupDragSources();
		source.setupDropHandlers();
		source.setupTableHeadersContextMenu();
		source.setupAddColumnContextMenu();
		source.setupSaveButtonClickListener();
		
		//Clear flag, this is used for saving logic (to save new list or update)
		source.setCurrentlySavedGermplasmList(null);
	}
    
}