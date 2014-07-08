package org.generationcp.breeding.manager.crossingmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.customcomponent.ViewListHeaderWindow;
import org.generationcp.breeding.manager.customcomponent.listinventory.CrossingManagerInventoryTable;
import org.generationcp.breeding.manager.customcomponent.listinventory.ListInventoryTable;
import org.generationcp.breeding.manager.inventory.ReservationStatusWindow;
import org.generationcp.breeding.manager.inventory.ReserveInventoryAction;
import org.generationcp.breeding.manager.inventory.ReserveInventorySource;
import org.generationcp.breeding.manager.inventory.ReserveInventoryUtil;
import org.generationcp.breeding.manager.inventory.ReserveInventoryWindow;
import org.generationcp.breeding.manager.listeners.InventoryLinkButtonClickListener;
import org.generationcp.breeding.manager.listimport.listeners.GidLinkClickListener;
import org.generationcp.breeding.manager.listmanager.constants.ListDataTablePropertyID;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
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
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class SelectParentsListDataComponent extends VerticalLayout implements InitializingBean, 
							InternationalizableComponent, BreedingManagerLayout, ReserveInventorySource {

	private static final Logger LOG = LoggerFactory.getLogger(SelectParentsListDataComponent.class);
	private static final long serialVersionUID = 7907737258051595316L;
	private static final String CHECKBOX_COLUMN_ID="Checkbox Column ID";
	
	public static final String LIST_DATA_TABLE_ID = "SelectParentsListDataComponent List Data Table ID";
	
	private static final Action ACTION_ADD_TO_FEMALE_LIST = new Action("Add to Female List");
	private static final Action ACTION_ADD_TO_MALE_LIST = new Action("Add to Male List");
	private static final Action[] LIST_DATA_TABLE_ACTIONS = new Action[] {ACTION_ADD_TO_FEMALE_LIST, ACTION_ADD_TO_MALE_LIST};
	
	private Integer germplasmListId;
	private GermplasmList germplasmList;
	private Long count;
	private Label listEntriesLabel;
	private Label totalListEntriesLabel;

	private Table listDataTable;
	private Button viewListHeaderButton;
	private String listName;
	
	private Button actionButton;
	private ContextMenu actionMenu;
	
	private Button inventoryViewActionButton;
	private ContextMenu inventoryViewActionMenu;
	private ContextMenuItem menuCopyToNewListFromInventory;
	private ContextMenuItem menuInventorySaveChanges;
	private ContextMenuItem menuListView;
	private ContextMenuItem menuReserveInventory;
	
	public static String ACTIONS_BUTTON_ID = "Actions";
	
	private ViewListHeaderWindow viewListHeaderWindow;
	
	private TableWithSelectAllLayout tableWithSelectAllLayout;
	private CrossingManagerInventoryTable listInventoryTable;
	
	//Layout variables
	private HorizontalLayout headerLayout;
	private HorizontalLayout subHeaderLayout;
	
	private boolean hasChanges = false;
	
    //Inventory Related Variables
    private ReserveInventoryWindow reserveInventory;
    private ReservationStatusWindow reservationStatus;
    private ReserveInventoryUtil reserveInventoryUtil;
    private ReserveInventoryAction reserveInventoryAction;
    private Map<ListEntryLotDetails, Double> validReservationsToSave;
	
	private MakeCrossesParentsComponent makeCrossesParentsComponent;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;

	@Autowired
    private GermplasmListManager germplasmListManager;
	
	@Autowired
	private InventoryDataManager inventoryDataManager;
	
	public SelectParentsListDataComponent(Integer germplasmListId, String listName, MakeCrossesParentsComponent makeCrossesParentsComponent){
		super();
		this.germplasmListId = germplasmListId;
		this.listName = listName;
		this.makeCrossesParentsComponent = makeCrossesParentsComponent;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
	}

	@Override
	public void updateLabels() {
		
	}

	@Override
	public void instantiateComponents() {
		retrieveListDetails();
		
		listEntriesLabel = new Label(messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
		listEntriesLabel.setStyleName(Bootstrap.Typography.H4.styleName());
		listEntriesLabel.setWidth("160px");
		
		totalListEntriesLabel = new Label(messageSource.getMessage(Message.TOTAL_LIST_ENTRIES) + ": " 
       		 + "  <b>" + count + "</b>", Label.CONTENT_XHTML);
       	totalListEntriesLabel.setWidth("135px");
		
		viewListHeaderWindow = new ViewListHeaderWindow(germplasmList);
		
		viewListHeaderButton = new Button(messageSource.getMessage(Message.VIEW_HEADER));
		viewListHeaderButton.addStyleName(Reindeer.BUTTON_LINK);
		viewListHeaderButton.setDescription(viewListHeaderWindow.getListHeaderComponent().toString());
		
		actionButton = new Button(messageSource.getMessage(Message.ACTIONS));
		actionButton.setData(ACTIONS_BUTTON_ID);
		actionButton.setIcon(AppConstants.Icons.ICON_TOOLS);
		actionButton.setWidth("110px");
		actionButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
		
		inventoryViewActionButton = new Button(messageSource.getMessage(Message.ACTIONS));
		inventoryViewActionButton.setData(ACTIONS_BUTTON_ID);
		inventoryViewActionButton.setIcon(AppConstants.Icons.ICON_TOOLS);
		inventoryViewActionButton.setWidth("110px");
		inventoryViewActionButton.addStyleName(Bootstrap.Buttons.INFO.styleName());
		
		actionMenu = new ContextMenu();
		actionMenu.setWidth("250px");
		actionMenu.addItem(messageSource.getMessage(Message.ADD_TO_MALE_LIST));
		actionMenu.addItem(messageSource.getMessage(Message.ADD_TO_FEMALE_LIST));
		actionMenu.addItem(messageSource.getMessage(Message.INVENTORY_VIEW));
		actionMenu.addItem(messageSource.getMessage(Message.SELECT_ALL));
		
		inventoryViewActionMenu = new ContextMenu();
		inventoryViewActionMenu.setWidth("295px");
		menuCopyToNewListFromInventory = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.COPY_TO_NEW_LIST));
        menuReserveInventory = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.RESERVE_INVENTORY));
        menuListView = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.RETURN_TO_LIST_VIEW));
        menuInventorySaveChanges = inventoryViewActionMenu.addItem(messageSource.getMessage(Message.SAVE_CHANGES));
        inventoryViewActionMenu.addItem(messageSource.getMessage(Message.SELECT_ALL));
        resetInventoryMenuOptions();
        
		initializeListDataTable();
		initializeListInventoryTable(); //listInventoryTable
		
		viewListHeaderButton = new Button(messageSource.getMessage(Message.VIEW_LIST_HEADERS));
		viewListHeaderButton.setStyleName(BaseTheme.BUTTON_LINK);
		
	    //Inventory Related Variables
        validReservationsToSave = new HashMap<ListEntryLotDetails, Double>();
	}
	
	private void resetInventoryMenuOptions() {
        //disable the save button at first since there are no reservations yet
        menuInventorySaveChanges.setEnabled(false);
        
        //Temporarily disable to Copy to New List in InventoryView TODO implement the function
        menuCopyToNewListFromInventory.setEnabled(false);
	}
	
	private void initializeListDataTable(){
		tableWithSelectAllLayout = new TableWithSelectAllLayout(count.intValue(),9,CHECKBOX_COLUMN_ID);
		tableWithSelectAllLayout.setWidth("100%");
		
		listDataTable = tableWithSelectAllLayout.getTable();
		listDataTable.setWidth("100%");
		listDataTable.setData(LIST_DATA_TABLE_ID);
		listDataTable.setSelectable(true);
		listDataTable.setMultiSelect(true);
		listDataTable.setColumnCollapsingAllowed(true);
		listDataTable.setColumnReorderingAllowed(true);
		listDataTable.setImmediate(true);
		listDataTable.setDragMode(TableDragMode.MULTIROW);
		
		listDataTable.addContainerProperty(CHECKBOX_COLUMN_ID, CheckBox.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.ENTRY_ID.getName(), Integer.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.DESIGNATION.getName(), Button.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.AVAILABLE_INVENTORY.getName(), Button.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.SEED_RESERVATION.getName(), String.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.PARENTAGE.getName(), String.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.ENTRY_CODE.getName(), String.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.GID.getName(), Button.class, null);
		listDataTable.addContainerProperty(ListDataTablePropertyID.SEED_SOURCE.getName(), String.class, null);
		
		listDataTable.setColumnHeader(CHECKBOX_COLUMN_ID, messageSource.getMessage(Message.CHECK_ICON));
		listDataTable.setColumnHeader(ListDataTablePropertyID.ENTRY_ID.getName(), messageSource.getMessage(Message.HASHTAG));
		listDataTable.setColumnHeader(ListDataTablePropertyID.DESIGNATION.getName(), messageSource.getMessage(Message.LISTDATA_DESIGNATION_HEADER));
		listDataTable.setColumnHeader(ListDataTablePropertyID.AVAILABLE_INVENTORY.getName(), messageSource.getMessage(Message.AVAIL_INV));
		listDataTable.setColumnHeader(ListDataTablePropertyID.SEED_RESERVATION.getName(), messageSource.getMessage(Message.SEED_RES));
		listDataTable.setColumnHeader(ListDataTablePropertyID.PARENTAGE.getName(), messageSource.getMessage(Message.LISTDATA_GROUPNAME_HEADER));
		listDataTable.setColumnHeader(ListDataTablePropertyID.ENTRY_CODE.getName(), messageSource.getMessage(Message.LISTDATA_ENTRY_CODE_HEADER));
		listDataTable.setColumnHeader(ListDataTablePropertyID.GID.getName(), messageSource.getMessage(Message.LISTDATA_GID_HEADER));
		listDataTable.setColumnHeader(ListDataTablePropertyID.SEED_SOURCE.getName(), messageSource.getMessage(Message.LISTDATA_SEEDSOURCE_HEADER));
		
		listDataTable.setColumnWidth(CHECKBOX_COLUMN_ID, 25);
		listDataTable.setColumnWidth(ListDataTablePropertyID.ENTRY_ID.getName(), 25);
		listDataTable.setColumnWidth(ListDataTablePropertyID.DESIGNATION.getName(), 130);
		listDataTable.setColumnWidth(ListDataTablePropertyID.AVAILABLE_INVENTORY.getName(), 70);
		listDataTable.setColumnWidth(ListDataTablePropertyID.SEED_RESERVATION.getName(), 70);
		listDataTable.setColumnWidth(ListDataTablePropertyID.PARENTAGE.getName(), 130);
		listDataTable.setColumnWidth(ListDataTablePropertyID.ENTRY_CODE.getName(), 100);
		listDataTable.setColumnWidth(ListDataTablePropertyID.GID.getName(), 60);
		listDataTable.setColumnWidth(ListDataTablePropertyID.SEED_SOURCE.getName(), 110);
		
		listDataTable.setVisibleColumns(new String[] { 
        		CHECKBOX_COLUMN_ID
        		,ListDataTablePropertyID.ENTRY_ID.getName()
        		,ListDataTablePropertyID.DESIGNATION.getName()
        		,ListDataTablePropertyID.AVAILABLE_INVENTORY.getName()
        		,ListDataTablePropertyID.SEED_RESERVATION.getName()
        		,ListDataTablePropertyID.PARENTAGE.getName()
        		,ListDataTablePropertyID.ENTRY_CODE.getName()
        		,ListDataTablePropertyID.GID.getName()
        		,ListDataTablePropertyID.SEED_SOURCE.getName()});
	}
	
	private void initializeListInventoryTable(){
		listInventoryTable = new CrossingManagerInventoryTable(germplasmList.getId());
		listInventoryTable.setVisible(false);
		listInventoryTable.setMaxRows(9);
	}

	private void retrieveListDetails() {
		try {
			germplasmList = germplasmListManager.getGermplasmListById(this.germplasmListId);
			count = germplasmListManager.countGermplasmListDataByListId(this.germplasmListId);
		} catch (MiddlewareQueryException e) {
			LOG.error("Error getting list details" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void initializeValues() {
		try{
			List<GermplasmListData> listEntries = inventoryDataManager.getLotCountsForList(germplasmListId, 0, Integer.MAX_VALUE);
			
			for(GermplasmListData entry : listEntries){
				String gid = String.format("%s", entry.getGid().toString());
                Button gidButton = new Button(gid, new GidLinkClickListener(gid,true));
                gidButton.setStyleName(BaseTheme.BUTTON_LINK);
                gidButton.setDescription("Click to view Germplasm information");
                
                Button desigButton = new Button(entry.getDesignation(), new GidLinkClickListener(gid,true));
                desigButton.setStyleName(BaseTheme.BUTTON_LINK);
                desigButton.setDescription("Click to view Germplasm information");
                
                CheckBox itemCheckBox = new CheckBox();
                itemCheckBox.setData(entry.getId());
                itemCheckBox.setImmediate(true);
    	   		itemCheckBox.addListener(new ClickListener() {
    	 			private static final long serialVersionUID = 1L;
    	 			@Override
    	 			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
    	 				CheckBox itemCheckBox = (CheckBox) event.getButton();
    	 				if(((Boolean) itemCheckBox.getValue()).equals(true)){
    	 					listDataTable.select(itemCheckBox.getData());
    	 				} else {
    	 					listDataTable.unselect(itemCheckBox.getData());
    	 				}
    	 			}
    	 		});
    	   		
    			
    			//#1 Available Inventory
    			String avail_inv = "-"; //default value
    			if(entry.getInventoryInfo().getLotCount().intValue() != 0){
    				avail_inv = entry.getInventoryInfo().getActualInventoryLotCount().toString().trim();
    			}
    			
    			InventoryLinkButtonClickListener inventoryLinkButtonClickListener = new InventoryLinkButtonClickListener(this,germplasmList.getId(),entry.getId(), entry.getGid());
    			Button inventoryButton = new Button(avail_inv, inventoryLinkButtonClickListener);
    			inventoryButton.setData(inventoryLinkButtonClickListener);
    			inventoryButton.setStyleName(BaseTheme.BUTTON_LINK);
    			inventoryButton.setDescription("Click to view Inventory Details");
    			
    			if(avail_inv.equals("-")){
    				inventoryButton.setEnabled(false);
    				inventoryButton.setDescription("No Lot for this Germplasm");
    			}
    			else{
    				inventoryButton.setDescription("Click to view Inventory Details");
    			}
    			
    			// Seed Reserved
    	   		String seed_res = "-"; //default value
    	   		if(entry.getInventoryInfo().getReservedLotCount().intValue() != 0){
    	   			seed_res = entry.getInventoryInfo().getReservedLotCount().toString().trim();
    	   		}
    	   		
    	   		listDataTable.addItem(new Object[] {
                        itemCheckBox, entry.getEntryId(), desigButton, inventoryButton, seed_res, entry.getGroupName(), entry.getEntryCode(), gidButton, entry.getSeedSource()}
    	   			, entry.getId());
			}
		} catch(MiddlewareQueryException ex){
			LOG.error("Error with getting list entries for list: " + germplasmListId);
			MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR_DATABASE), "Error in getting list entries."
					, Notification.POSITION_CENTERED);
		}
	}

	@Override
	public void addListeners() {
		
		actionButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				actionMenu.show(event.getClientX(), event.getClientY());
			}
		});
		
		actionMenu.addListener(new ContextMenu.ClickListener() {
			private static final long serialVersionUID = -2343109406180457070L;
			@Override
			public void contextItemClick(ClickEvent event) {
			  // Get reference to clicked item
			  ContextMenuItem clickedItem = event.getClickedItem();
			  if(clickedItem.getName().equals(messageSource.getMessage(Message.SELECT_ALL))){
				  listDataTable.setValue(listDataTable.getItemIds());
			  }else if(clickedItem.getName().equals(messageSource.getMessage(Message.ADD_TO_FEMALE_LIST))){
				  makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getFemaleTable(), null);
				  makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getFemaleTable());
				  makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(0);
			  }else if(clickedItem.getName().equals(messageSource.getMessage(Message.ADD_TO_MALE_LIST))){
				  makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getMaleTable(), null);
				  makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getMaleTable());
				  makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(1);
			  }else if(clickedItem.getName().equals(messageSource.getMessage(Message.INVENTORY_VIEW))){
				  viewInventoryAction();
			  }
		   }
		});
		
		inventoryViewActionButton.addListener(new ClickListener() {
	   		 private static final long serialVersionUID = 272707576878821700L;
	
				 @Override
	   		 public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
					 inventoryViewActionMenu.show(event.getClientX(), event.getClientY());
	   		 }
	   	 });
		
		inventoryViewActionMenu.addListener(new ContextMenu.ClickListener() {
			private static final long serialVersionUID = -2343109406180457070L;
	
			@Override
			public void contextItemClick(ClickEvent event) {
			      // Get reference to clicked item
			      ContextMenuItem clickedItem = event.getClickedItem();
			      if(clickedItem.getName().equals(messageSource.getMessage(Message.SAVE_CHANGES))){	  
			    	  saveReservationChangesAction();
                  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.RETURN_TO_LIST_VIEW))){
                	  viewListAction();
                  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.COPY_TO_NEW_LIST))){
                	  //copyToNewListFromInventoryViewAction();
				  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.RESERVE_INVENTORY))){
		          	  reserveInventoryAction();
                  } else if(clickedItem.getName().equals(messageSource.getMessage(Message.SELECT_ALL))){
                	  listInventoryTable.getTable().setValue(listInventoryTable.getTable().getItemIds());
		          }
		    }
		});
		
		viewListHeaderButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 329434322390122057L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				openViewListHeaderWindow();
			}
		});
		
		listDataTable.addActionHandler(new Action.Handler() {
			private static final long serialVersionUID = -2173636726748988046L;

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if(action.equals(ACTION_ADD_TO_FEMALE_LIST)){
					makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getFemaleTable(), null);
					makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getFemaleTable());
					makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(0);
				} else if(action.equals(ACTION_ADD_TO_MALE_LIST)){
					makeCrossesParentsComponent.dropToFemaleOrMaleTable(listDataTable, makeCrossesParentsComponent.getMaleTable(), null);
					makeCrossesParentsComponent.assignEntryNumber(makeCrossesParentsComponent.getMaleTable());
					makeCrossesParentsComponent.getParentTabSheet().setSelectedTab(1);
				}
			}
			
			@Override
			public Action[] getActions(Object target, Object sender) {
				return LIST_DATA_TABLE_ACTIONS;
			}
		});
	}

	@Override
	public void layoutComponents() {
		setMargin(true);
		setSpacing(true);
		
		addComponent(actionMenu);
		addComponent(inventoryViewActionMenu);
		
		headerLayout = new HorizontalLayout();
		headerLayout.setWidth("100%");
		
		HeaderLabelLayout headingLayout = new HeaderLabelLayout(AppConstants.Icons.ICON_LIST_TYPES, listEntriesLabel);
		headerLayout.addComponent(headingLayout);
		headerLayout.addComponent(viewListHeaderButton);
		headerLayout.setComponentAlignment(headingLayout, Alignment.MIDDLE_LEFT);
		headerLayout.setComponentAlignment(viewListHeaderButton, Alignment.MIDDLE_RIGHT);
		
		addComponent(headerLayout);
		
		subHeaderLayout = new HorizontalLayout();
		subHeaderLayout.setWidth("100%");
		subHeaderLayout.addComponent(totalListEntriesLabel);
		subHeaderLayout.addComponent(actionButton);
		subHeaderLayout.setComponentAlignment(totalListEntriesLabel, Alignment.MIDDLE_LEFT);
		subHeaderLayout.setComponentAlignment(actionButton, Alignment.MIDDLE_RIGHT);
		
		addComponent(subHeaderLayout);
		addComponent(tableWithSelectAllLayout);
		addComponent(listInventoryTable);

	}
	
	/*--------------------------------------INVENTORY RELATED FUNCTIONS---------------------------------------*/
	
	private void viewListAction(){
		changeToListView();
	}	
	
	public void changeToListView(){
		if(listInventoryTable.isVisible()){
			tableWithSelectAllLayout.setVisible(true);
			listInventoryTable.setVisible(false);
			
			subHeaderLayout.removeComponent(inventoryViewActionButton);
			subHeaderLayout.addComponent(actionButton);
			subHeaderLayout.setComponentAlignment(actionButton, Alignment.MIDDLE_RIGHT);
			
			listEntriesLabel.setValue(messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
	        totalListEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LIST_ENTRIES) + ": " 
	       		 + "  <b>" + count + "</b>");
	        
	        this.removeComponent(listInventoryTable);
	        this.addComponent(tableWithSelectAllLayout);
	        
	        this.requestRepaint();
		}
	}
	
	private void viewInventoryAction(){
		viewInventoryActionConfirmed();
	}
	
	public void viewInventoryActionConfirmed(){
		listInventoryTable.loadInventoryData();
		changeToInventoryView();
	}
	
	public void changeToInventoryView(){
		if(tableWithSelectAllLayout.isVisible()){
			tableWithSelectAllLayout.setVisible(false);
			listInventoryTable.setVisible(true);
			
			subHeaderLayout.removeComponent(actionButton);
	        subHeaderLayout.addComponent(inventoryViewActionButton);
	        subHeaderLayout.setComponentAlignment(inventoryViewActionButton, Alignment.MIDDLE_RIGHT);
	        
	        listEntriesLabel.setValue(messageSource.getMessage(Message.LOTS));
	        totalListEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LOTS) + ": " 
	          		 + "  <b>" + listInventoryTable.getTable().getItemIds().size() + "</b>");
	        
	        this.removeComponent(tableWithSelectAllLayout);
	        this.addComponent(listInventoryTable);
	        
	        this.requestRepaint();
		}
	}
	
	public void reserveInventoryAction() {
		if(!inventoryViewActionMenu.isVisible()){//checks if the screen is in the inventory view
			MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.WARNING), 
					"Please change to Inventory View first.");
		}
		else{
			List<ListEntryLotDetails> lotDetailsGid = listInventoryTable.getSelectedLots();
			
			if( lotDetailsGid == null || lotDetailsGid.size() == 0){
				MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.WARNING), 
						"Please select at least 1 lot to reserve.");
			}
			else{
		        //this util handles the inventory reservation related functions
		        reserveInventoryUtil = new ReserveInventoryUtil(this,lotDetailsGid);
				reserveInventoryUtil.viewReserveInventoryWindow();
			}
		}
	}

	@Override
	public void updateListInventoryTable(
			Map<ListEntryLotDetails, Double> validReservations,
			boolean withInvalidReservations) {
		for(Map.Entry<ListEntryLotDetails, Double> entry: validReservations.entrySet()){
			ListEntryLotDetails lot = entry.getKey();
			Double new_res = entry.getValue();
			
			Item itemToUpdate = listInventoryTable.getTable().getItem(lot);
			itemToUpdate.getItemProperty(ListInventoryTable.NEWLY_RESERVED_COLUMN_ID).setValue(new_res);
		}
		
		removeReserveInventoryWindow(reserveInventory);
		
		//update lot reservatios to save
		updateLotReservationsToSave(validReservations);
		
		//enable now the Save Changes option
		menuInventorySaveChanges.setEnabled(true);
		
		if(validReservations.size() == 0){//if there are no valid reservations
			MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.INVALID_INPUT), 
					messageSource.getMessage(Message.COULD_NOT_MAKE_ANY_RESERVATION_ALL_SELECTED_LOTS_HAS_INSUFFICIENT_BALANCES) + ".");
		
		} else if(!withInvalidReservations){
			MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), 
					"All selected entries will be reserved in their respective lots.", 
					3000);
		}		
	}

	private void updateLotReservationsToSave(
			Map<ListEntryLotDetails, Double> validReservations) {
		for(Map.Entry<ListEntryLotDetails, Double> entry : validReservations.entrySet()){
			ListEntryLotDetails lot = entry.getKey();
			Double amountToReserve = entry.getValue();
			
			if(validReservationsToSave.containsKey(lot)){
				validReservationsToSave.remove(lot);
				
			}
			
			validReservationsToSave.put(lot,amountToReserve);
		}
		
		if(validReservationsToSave.size() > 0){
			setHasUnsavedChanges(true);
		}
	}

	@Override
	public void addReserveInventoryWindow(
			ReserveInventoryWindow reserveInventory) {
		this.reserveInventory = reserveInventory;
		makeCrossesParentsComponent.getWindow().addWindow(this.reserveInventory);
	}

	@Override
	public void addReservationStatusWindow(
			ReservationStatusWindow reservationStatus) {
		this.reservationStatus = reservationStatus;
		removeReserveInventoryWindow(reserveInventory);
		makeCrossesParentsComponent.getWindow().addWindow(this.reservationStatus);
	}

	@Override
	public void removeReserveInventoryWindow(
			ReserveInventoryWindow reserveInventory) {
		this.reserveInventory = reserveInventory;
		makeCrossesParentsComponent.getWindow().removeWindow(this.reserveInventory);
	}

	@Override
	public void removeReservationStatusWindow(
			ReservationStatusWindow reservationStatus) {
		this.reservationStatus = reservationStatus;
		makeCrossesParentsComponent.getWindow().removeWindow(this.reservationStatus);
	}
	
	public void saveReservationChangesAction() {
		if(hasUnsavedChanges()){
			reserveInventoryAction = new ReserveInventoryAction(this);
			boolean success = reserveInventoryAction.saveReserveTransactions(getValidReservationsToSave(), germplasmList.getId());
			if(success){
				refreshInventoryColumns(getValidReservationsToSave());
				resetListInventoryTableValues();
				MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS), 
						"All reservations were saved.");
			}
		}
	}
	
	public void refreshInventoryColumns(
			Map<ListEntryLotDetails, Double> validReservationsToSave2) {
		
		Set<Integer> entryIds = new HashSet<Integer>();
		for(Entry<ListEntryLotDetails, Double> details : validReservationsToSave.entrySet()){
			entryIds.add(details.getKey().getId());
		 }
		
		List<GermplasmListData> germplasmListDataEntries = new ArrayList<GermplasmListData>();
		
		try {
			if (!entryIds.isEmpty())
				germplasmListDataEntries = this.inventoryDataManager.getLotCountsForListEntries(germplasmList.getId(), new ArrayList<Integer>(entryIds));
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
		
		for (GermplasmListData listData : germplasmListDataEntries){
			Item item = listDataTable.getItem(listData.getId());
			
			//#1 Available Inventory
			String avail_inv = "-"; //default value
			if(listData.getInventoryInfo().getLotCount().intValue() != 0){
				avail_inv = listData.getInventoryInfo().getActualInventoryLotCount().toString().trim();
			}
			Button inventoryButton = new Button(avail_inv, new InventoryLinkButtonClickListener(makeCrossesParentsComponent, germplasmList.getId(),listData.getId(), listData.getGid()));
			inventoryButton.setStyleName(BaseTheme.BUTTON_LINK);
			inventoryButton.setDescription("Click to view Inventory Details");
			
			if(avail_inv.equals("-")){
				inventoryButton.setEnabled(false);
				inventoryButton.setDescription("No Lot for this Germplasm");
			}
			else{
				inventoryButton.setDescription("Click to view Inventory Details");
			}
			item.getItemProperty(ListDataTablePropertyID.AVAIL_INV.getName()).setValue(inventoryButton);
			
		
			// Seed Reserved
	   		String seed_res = "-"; //default value
	   		if(listData.getInventoryInfo().getReservedLotCount().intValue() != 0){
	   			seed_res = listData.getInventoryInfo().getReservedLotCount().toString().trim();
	   		}
			
	   		item.getItemProperty(ListDataTablePropertyID.SEED_RES.getName()).setValue(seed_res);
		}		
	}
	
    public void resetListInventoryTableValues() {
    	if(germplasmList != null){
    		listInventoryTable.updateListInventoryTableAfterSave();
    	}
    	else{
    		listInventoryTable.reset();
    	}
		
		resetInventoryMenuOptions();
		
		validReservationsToSave.clear();//reset the reservations to save. 
		
		resetUnsavedChangesFlag();
	}
	
	public void resetUnsavedChangesFlag() {
		hasChanges = false;
	}
	
	/*--------------------------------END OF INVENTORY RELATED FUNCTIONS--------------------------------------*/
	
	public Map<ListEntryLotDetails, Double> getValidReservationsToSave(){
		return validReservationsToSave;
	}

	public boolean hasUnsavedChanges() {	
		// TODO mark other unsaved changes in dropHandler, listDataTable, inventoryTable
		
		return hasChanges;
	}

	private void openViewListHeaderWindow(){
		this.getWindow().addWindow(viewListHeaderWindow);
	}
	
	public Table getListDataTable(){
		return this.listDataTable;
	}

	public String getListName() {
		return this.listName;
	}
	
	public GermplasmList getGermplasmList(){
		return germplasmList;
	}
	
	public void setHasUnsavedChanges(Boolean hasChanges) {
		this.hasChanges = hasChanges;
		//setHasChangesMain(this.hasChanges);
	}

}
