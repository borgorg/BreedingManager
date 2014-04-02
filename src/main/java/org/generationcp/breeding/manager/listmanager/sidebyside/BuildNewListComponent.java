package org.generationcp.breeding.manager.listmanager.sidebyside;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.customfields.BreedingManagerListDetailsComponent;
import org.generationcp.breeding.manager.listmanager.constants.ListDataTablePropertyID;
import org.generationcp.breeding.manager.listmanager.listeners.ResetListButtonClickListener;
import org.generationcp.breeding.manager.listmanager.sidebyside.listeners.SaveListButtonClickListener;
import org.generationcp.breeding.manager.listmanager.util.AddColumnContextMenu;
import org.generationcp.breeding.manager.listmanager.util.BuildNewListDropHandler;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import com.vaadin.data.Item;
import com.vaadin.event.Action;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;


@Configurable
public class BuildNewListComponent extends VerticalLayout implements InitializingBean, BreedingManagerLayout {

    private static final Logger LOG = LoggerFactory.getLogger(BuildNewListComponent.class);
    
    private static final long serialVersionUID = -7736422783255724272L;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private GermplasmDataManager germplasmDataManager;
    
    @Autowired
    private GermplasmListManager germplasmListManager;
    
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
        
    
    public static final String GERMPLASMS_TABLE_DATA = "Germplasms Table Data";
    static final Action ACTION_SELECT_ALL = new Action("Select All");
    static final Action ACTION_DELETE_SELECTED_ENTRIES = new Action("Delete Selected Entries");
    static final Action[] GERMPLASMS_TABLE_CONTEXT_MENU = new Action[] { ACTION_SELECT_ALL, ACTION_DELETE_SELECTED_ENTRIES };
    
    public static final String DATE_AS_NUMBER_FORMAT = "yyyyMMdd";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    
    //Components
    private Label buildNewListTitle;
    private Label buildNewListDesc;
    private BreedingManagerListDetailsComponent breedingManagerListDetailsComponent;
    private TableWithSelectAllLayout tableWithSelectAllLayout;
    private Button toolsButton;
    private Button saveButton;
    private Button resetButton;
    
    private BuildNewListDropHandler dropHandler;
    
    //Tools Button Context Menu
    private ContextMenu menu;
    private ContextMenuItem menuExportList;
    private ContextMenuItem menuExportForGenotypingOrder;
    private ContextMenuItem menuCopyToList;
    
    public static String TOOLS_BUTTON_ID = "Tools";
    
    //For Saving
    private ListManagerMain source;
    private GermplasmList currentlySavedGermplasmList;
    private boolean changed = false;
    private Integer saveInListId;    
    
    private AddColumnContextMenu addColumnContextMenu;
    
    //Listener
    SaveListButtonClickListener saveListButtonListener;
    
    public BuildNewListComponent() {
        super();
    }
    
    public BuildNewListComponent(ListManagerMain source) {
        super();
        this.source = source;
        this.currentlySavedGermplasmList = null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    	instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
		initializeHandlers();
    }
    
	@Override
	public void instantiateComponents() {
    	buildNewListTitle = new Label(messageSource.getMessage(Message.BUILD_A_NEW_LIST));
        buildNewListTitle.addStyleName(Bootstrap.Typography.H3.styleName());
        
        buildNewListDesc = new Label();
        buildNewListDesc.setValue(messageSource.getMessage(Message.CLICK_AND_DRAG_ON_PANEL_EDGES_TO_RESIZE));
        buildNewListDesc.setWidth("300px");
        
        breedingManagerListDetailsComponent = new BreedingManagerListDetailsComponent();
        
        menu = new ContextMenu();
        menu.setWidth("255px");
        menu.addItem(messageSource.getMessage(Message.SELECT_ALL));
        menu.addItem(messageSource.getMessage(Message.DELETE_SELECTED_ENTRIES));
        menuExportList = menu.addItem(messageSource.getMessage(Message.EXPORT_LIST));
        menuExportForGenotypingOrder = menu.addItem(messageSource.getMessage(Message.EXPORT_LIST_FOR_GENOTYPING));
        menuCopyToList = menu.addItem(messageSource.getMessage(Message.COPY_TO_NEW_LIST_WINDOW_LABEL));
        
        resetMenuOptions();
        
        toolsButton = new Button(messageSource.getMessage(Message.TOOLS));
        toolsButton.setIcon(AppConstants.Icons.ICON_TOOLS);
        toolsButton.setStyleName(Bootstrap.Buttons.INFO.styleName());
             
        tableWithSelectAllLayout = new TableWithSelectAllLayout(ListDataTablePropertyID.TAG.getName());
        createGermplasmTable(tableWithSelectAllLayout.getTable());
        
        addColumnContextMenu = new AddColumnContextMenu(tableWithSelectAllLayout.getTable(), ListDataTablePropertyID.GID.getName(), true);
        
        dropHandler = new BuildNewListDropHandler(germplasmDataManager, germplasmListManager, tableWithSelectAllLayout.getTable());
        
        saveButton = new Button();
        saveButton.setCaption(messageSource.getMessage(Message.SAVE_LABEL));
        saveButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
        
        resetButton = new Button();
        resetButton.setCaption(messageSource.getMessage(Message.RESET_LIST));
        resetButton.addStyleName(Bootstrap.Buttons.DEFAULT.styleName());
	}
	
    public void resetMenuOptions(){
        //initially disabled when the current list building is not yet save or being reset
        menuExportList.setEnabled(false);
        menuExportForGenotypingOrder.setEnabled(false);
        menuCopyToList.setEnabled(false);
    }

	@Override
	public void initializeValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addListeners() {
		menu.addListener(new ContextMenu.ClickListener() {

            private static final long serialVersionUID = -2331333436994090161L;

            @Override
            public void contextItemClick(ClickEvent event) {
                ContextMenuItem clickedItem = event.getClickedItem();
                Table germplasmsTable = tableWithSelectAllLayout.getTable();
                if(clickedItem.getName().equals(messageSource.getMessage(Message.SELECT_ALL))){
                      germplasmsTable.setValue(germplasmsTable.getItemIds());
                }else if(clickedItem.getName().equals(messageSource.getMessage(Message.DELETE_SELECTED_ENTRIES))){
                      deleteSelectedEntries();
                }else if(clickedItem.getName().equals(messageSource.getMessage(Message.EXPORT_LIST))){
                    //exportListAction();
                }else if(clickedItem.getName().equals(messageSource.getMessage(Message.EXPORT_LIST_FOR_GENOTYPING))){
                    //exportListForGenotypingOrderAction();
                }else if(clickedItem.getName().equals(messageSource.getMessage(Message.COPY_TO_NEW_LIST_WINDOW_LABEL))){
                    //copyToNewListAction();
                }                
            }
            
        });
		
		toolsButton.addListener(new ClickListener() {

            private static final long serialVersionUID = 1345004576139547723L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if(isCurrentListSave()){
                    enableMenuOptionsAfterSave();
                }
                menu.show(event.getClientX(), event.getClientY());
            }
         });
		
		saveListButtonListener = new SaveListButtonClickListener(this, germplasmListManager, tableWithSelectAllLayout.getTable(), messageSource, workbenchDataManager); 
		saveButton.addListener(saveListButtonListener);
		resetButton.addListener(new ResetListButtonClickListener(this, messageSource));
	}

	@Override
	public void layoutComponents() {
		this.setSpacing(true);
		
		HeaderLabelLayout headingLayout = new HeaderLabelLayout(AppConstants.Icons.ICON_BUILD_NEW_LIST,buildNewListTitle);
        this.addComponent(headingLayout);
        
        HorizontalLayout instructionLayout = new HorizontalLayout();
        instructionLayout.setSpacing(true);
        instructionLayout.setWidth("100%");
        instructionLayout.addComponent(buildNewListDesc);
        instructionLayout.addComponent(saveButton);
        instructionLayout.setComponentAlignment(buildNewListDesc, Alignment.MIDDLE_LEFT);
        instructionLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_RIGHT);
        
        this.addComponent(instructionLayout);
        this.addComponent(breedingManagerListDetailsComponent);
        
        this.addComponent(toolsButton);
        this.setComponentAlignment(toolsButton, Alignment.BOTTOM_RIGHT);
        this.addComponent(menu);
        
        this.addComponent(tableWithSelectAllLayout);
        this.addComponent(resetButton);
	}
    
    public void createGermplasmTable(final Table table){
        
    	table.setData(GERMPLASMS_TABLE_DATA);
    	table.addContainerProperty(ListDataTablePropertyID.TAG.getName(), CheckBox.class, null);
    	table.addContainerProperty(ListDataTablePropertyID.GID.getName(), Button.class, null);
    	table.addContainerProperty(ListDataTablePropertyID.ENTRY_ID.getName(), Integer.class, null);
    	table.addContainerProperty(ListDataTablePropertyID.ENTRY_CODE.getName(), String.class, null);
    	table.addContainerProperty(ListDataTablePropertyID.SEED_SOURCE.getName(), String.class, null);
    	table.addContainerProperty(ListDataTablePropertyID.DESIGNATION.getName(), String.class, null);
    	table.addContainerProperty(ListDataTablePropertyID.PARENTAGE.getName(), String.class, null);
        
        messageSource.setColumnHeader(table, ListDataTablePropertyID.TAG.getName(), Message.CHECK_ICON);
        messageSource.setColumnHeader(table, ListDataTablePropertyID.GID.getName(), Message.LISTDATA_GID_HEADER);
        messageSource.setColumnHeader(table, ListDataTablePropertyID.ENTRY_ID.getName(), Message.HASHTAG);
        messageSource.setColumnHeader(table, ListDataTablePropertyID.ENTRY_CODE.getName(), Message.LISTDATA_ENTRY_CODE_HEADER);
        messageSource.setColumnHeader(table, ListDataTablePropertyID.SEED_SOURCE.getName(), Message.LISTDATA_SEEDSOURCE_HEADER);
        messageSource.setColumnHeader(table, ListDataTablePropertyID.DESIGNATION.getName(), Message.LISTDATA_DESIGNATION_HEADER);
        messageSource.setColumnHeader(table, ListDataTablePropertyID.PARENTAGE.getName(), Message.LISTDATA_GROUPNAME_HEADER);
        
        table.setSelectable(true);
        table.setMultiSelect(true);
        table.setWidth("365px");
        table.setHeight("280px");
        
        table.addActionHandler(new Action.Handler() {

            private static final long serialVersionUID = 1884343225476178686L;

            public Action[] getActions(Object target, Object sender) {
                return GERMPLASMS_TABLE_CONTEXT_MENU;
            }

            @Override
            public void handleAction(Action action, Object sender, Object target) {
                if(ACTION_SELECT_ALL == action) {
                	table.setValue(table.getItemIds());
                } else if(ACTION_DELETE_SELECTED_ENTRIES == action) {
                    deleteSelectedEntries();
                }
            }
        });
        
        initializeHandlers();
    }
    
    private void initializeHandlers() {
    	tableWithSelectAllLayout.getTable().setDropHandler(dropHandler);
    }
    
    @SuppressWarnings("unchecked")
    private void deleteSelectedEntries(){
        List<Integer> selectedItemIds = new ArrayList<Integer>();
        selectedItemIds.addAll((Collection<? extends Integer>) tableWithSelectAllLayout.getTable().getValue());
        for(Integer selectedItemId:selectedItemIds){
        	tableWithSelectAllLayout.getTable().removeItem(selectedItemId);
        }
        assignSerializedEntryNumber();
    }
    
    /**
     * Iterates through the whole table, and sets the entry number from 1 to n based on the row position
     */
    private void assignSerializedEntryNumber(){
        List<Integer> itemIds = getItemIds(tableWithSelectAllLayout.getTable());
                
        int id = 1;
        for(Integer itemId : itemIds){
        	tableWithSelectAllLayout.getTable().getItem(itemId).getItemProperty(ListDataTablePropertyID.ENTRY_ID.getName()).setValue(id);
            id++;
        }
    }
    
    /**
     * Get item id's of a table, and return it as a list 
     * @param table
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Integer> getItemIds(Table table){
        List<Integer> itemIds = new ArrayList<Integer>();
        itemIds.addAll((Collection<? extends Integer>) table.getItemIds());

        return itemIds;
    }
    
    public void addFromListDataTable(Table sourceTable){
    	dropHandler.addFromListDataTable(sourceTable);
    }
    
    public boolean isCurrentListSave(){
        boolean isSaved = false;
        
        if(currentlySavedGermplasmList != null){

            isSaved = true;
        }
        
        return isSaved;
    }

    public void enableMenuOptionsAfterSave(){
        menuExportList.setEnabled(true);
        menuExportForGenotypingOrder.setEnabled(true);
        menuCopyToList.setEnabled(true);
    }
    
	public void editList(GermplasmList germplasmList) {
		resetList(); //reset list before placing new one
		
		buildNewListTitle.setValue(messageSource.getMessage(Message.EDIT_LIST));
		breedingManagerListDetailsComponent.setGermplasmListDetails(germplasmList);
		breedingManagerListDetailsComponent.getListNameField().getListNameTextField().focus();
		
		currentlySavedGermplasmList = germplasmList;
		
		//TO DO Update the Parent of the germplamsList, for now just used the current value of germplasmList.getParent()
		this.tableWithSelectAllLayout.getTable().removeAllItems();		
        dropHandler.addGermplasmList(germplasmList.getId());
        
        //reset the change status to false after loading the germplasm list details and list data in the screen
        setChanged(false);
        dropHandler.setChanged(false);
        
	}
	
	public void resetList(){
		
		//list details fields
		breedingManagerListDetailsComponent.resetFields();
		
		//list data table
		resetGermplasmTable();
		
		//disabled the menu options when the build new list table has no rows
		resetMenuOptions();
		
		//Clear flag, this is used for saving logic (to save new list or update)
		setCurrentlySavedGermplasmList(null);

		//Rename the Build New List Header
		buildNewListTitle.setValue(messageSource.getMessage(Message.BUILD_A_NEW_LIST));
		
		//Reset the marker for changes in Build New List
		setChanged(false);
		
		//List Data Table
		dropHandler = new BuildNewListDropHandler(germplasmDataManager, germplasmListManager, tableWithSelectAllLayout.getTable());
		initializeHandlers();
		
		//Reset Save Listener
		saveButton.removeListener(saveListButtonListener);
		saveListButtonListener = new SaveListButtonClickListener(this, germplasmListManager, tableWithSelectAllLayout.getTable(), messageSource, workbenchDataManager); 
		saveButton.addListener(saveListButtonListener);
		
	}
	
	public void resetGermplasmTable(){
		this.removeComponent(tableWithSelectAllLayout);
		this.removeComponent(resetButton);
		
		tableWithSelectAllLayout = new TableWithSelectAllLayout(ListDataTablePropertyID.TAG.getName());
        createGermplasmTable(tableWithSelectAllLayout.getTable());
        
        this.addComponent(tableWithSelectAllLayout);
		this.addComponent(resetButton);
	}
	
    public GermplasmList getCurrentlySetGermplasmListInfo(){
        GermplasmList toreturn = new GermplasmList();
        
        //try {
        //	this.breedingManagerListDetailsComponent.getListNameField().validate();
        
	        Object name = this.breedingManagerListDetailsComponent.getListNameField().getValue();
	        if(name != null){
	            toreturn.setName(name.toString().trim());
	        } else{
	            toreturn.setName(null);
	        }
	        
	    //    this.breedingManagerListDetailsComponent.getListDescriptionField().validate();
	        
	        Object description = this.breedingManagerListDetailsComponent.getListDescriptionField().getValue();
	        if(description != null){
	            toreturn.setDescription(description.toString().trim());
	        } else{
	            toreturn.setDescription(null);
	        }
	        
	    //    breedingManagerListDetailsComponent.getListDateField().validate();
	        
	        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
	        Object dateValue = this.breedingManagerListDetailsComponent.getListDateField().getValue();
	        if(dateValue != null){
	            String sDate = formatter.format(dateValue);
	            Long dataLongValue = Long.parseLong(sDate.replace("-", ""));
	            toreturn.setDate(dataLongValue);
	        } else{
	            toreturn.setDate(null);
	        }
	        
	        toreturn.setType(this.breedingManagerListDetailsComponent.getListTypeField().getValue().toString());
	        toreturn.setNotes(this.breedingManagerListDetailsComponent.getListNotesField().getValue().toString());
	        return toreturn;
        
        //} catch(InvalidValueException e) {
        //	return null;
        //}
    }
    
    public List<GermplasmListData> getListEntriesFromTable(){
        List<GermplasmListData> toreturn = new ArrayList<GermplasmListData>();
        
        assignSerializedEntryNumber();
        
        for(Object id : this.tableWithSelectAllLayout.getTable().getItemIds()){
            Integer entryId = (Integer) id;
            Item item = this.tableWithSelectAllLayout.getTable().getItem(entryId);
            
            GermplasmListData listEntry = new GermplasmListData();
            listEntry.setId(entryId);
            
            Object designation = item.getItemProperty(ListDataTablePropertyID.DESIGNATION.getName()).getValue();
            if(designation != null){
                listEntry.setDesignation(designation.toString());
            } else{
                listEntry.setDesignation("-");
            }
            
            Object entryCode = item.getItemProperty(ListDataTablePropertyID.ENTRY_CODE.getName()).getValue();
            if(entryCode != null){
                listEntry.setEntryCode(entryCode.toString());
            } else{
                listEntry.setEntryCode("-");
            }
            
            Button gidButton = (Button) item.getItemProperty(ListDataTablePropertyID.GID.getName()).getValue();
            listEntry.setGid(Integer.parseInt(gidButton.getCaption()));
            
            Object groupName = item.getItemProperty(ListDataTablePropertyID.PARENTAGE.getName()).getValue();
            if(groupName != null){
                String groupNameString = groupName.toString();
                if(groupNameString.length() > 255){
                    groupNameString = groupNameString.substring(0, 255);
                }
                listEntry.setGroupName(groupNameString);
            } else{
                listEntry.setGroupName("-");
            }
            
            listEntry.setEntryId((Integer) item.getItemProperty(ListDataTablePropertyID.ENTRY_ID.getName()).getValue());
            
            Object seedSource = item.getItemProperty(ListDataTablePropertyID.SEED_SOURCE.getName()).getValue();
            if(seedSource != null){
                listEntry.setSeedSource(seedSource.toString());
            } else{
                listEntry.setSeedSource("-");
            }
            
            toreturn.add(listEntry);
        }
        return toreturn;
    }	
	
	/* SETTERS AND GETTERS */
	public Label getBuildNewListTitle() {
		return buildNewListTitle;
	}

	public void setBuildNewListTitle(Label buildNewListTitle) {
		this.buildNewListTitle = buildNewListTitle;
	}
	
	public BreedingManagerListDetailsComponent getBreedingManagerListDetailsComponent() {
		return breedingManagerListDetailsComponent;
	}
	
    public TableWithSelectAllLayout getTableWithSelectAllLayout() {
		return tableWithSelectAllLayout;
	}

	public void setTableWithSelectAllLayout(TableWithSelectAllLayout tableWithSelectAllLayout) {
		this.tableWithSelectAllLayout = tableWithSelectAllLayout;
	}

	public Table getGermplasmsTable(){
        return tableWithSelectAllLayout.getTable();
    }

	public Button getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(Button saveButton) {
		this.saveButton = saveButton;
	}
	
    public GermplasmList getCurrentlySavedGermplasmList(){
        return this.currentlySavedGermplasmList;
    }
    
    public void setCurrentlySavedGermplasmList(GermplasmList list){
        this.currentlySavedGermplasmList = list;
    }
	
    public ListManagerMain getSource(){
    	return source;
    }
    
    public Integer getSaveInListId(){
    	return saveInListId;
    }
    
    public void setSaveInListId(Integer saveInListId){
    	this.saveInListId = saveInListId;
    }
    
    public AddColumnContextMenu getAddColumnContextMenu(){
    	return addColumnContextMenu;
    }

	public boolean isChanged() {
		
		if(this.breedingManagerListDetailsComponent.isChanged()){
			changed = true;
		}
		
		if(dropHandler.isChanged()){
			changed = true;
		}
		
		//TO DO mark the changes in germplasmListDataTable during fill with and add column functions
		
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
		this.breedingManagerListDetailsComponent.setChanged(changed);
		
		//TO DO mark the changes in germplasmListDataTable during fill with functions
	}
    
}