/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.breeding.manager.listmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.generationcp.breeding.manager.application.BreedingManagerApplication;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listimport.listeners.GidLinkButtonClickListener;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListButtonClickListener;
import org.generationcp.breeding.manager.listmanager.util.FillWith;
import org.generationcp.breeding.manager.listmanager.util.GermplasmListExporter;
import org.generationcp.breeding.manager.listmanager.util.GermplasmListExporterException;
import org.generationcp.breeding.manager.util.GermplasmDetailModel;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.util.FileDownloadResource;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ListDataComponent extends VerticalLayout implements InitializingBean, InternationalizableComponent {

	private static final long serialVersionUID = -2847082090222842504L;
	private static final Logger LOG = LoggerFactory.getLogger(ListDataComponent.class);

    private static final String GID = "gid";
    private static final String GID_VALUE = "gidValue";
    private static final String ENTRY_ID = "entryId";
    private static final String ENTRY_CODE = "entryCode";
    private static final String SEED_SOURCE = "seedSource";
    private static final String DESIGNATION = "designation";
    private static final String GROUP_NAME = "groupName";
    private static final String STATUS = "status";
    private static final String PREFERRED_NAME="preferrred name";
    private static final String PREFERRED_ID="preferrred id";
    private static final String LOCATION_NAME="location name";
    
    public final static String SORTING_BUTTON_ID = "GermplasmListDataComponent Save Sorting Button";
    public static final String DELETE_LIST_ENTRIES_BUTTON_ID="Delete list entries";
    public final static String EXPORT_BUTTON_ID = "GermplasmListDataComponent Export List Button";
    public final static String EXPORT_FOR_GENOTYPING_BUTTON_ID = "GermplasmListDataComponent Export For Genotyping Order Button";
    public final static String COPY_TO_NEW_LIST_BUTTON_ID = "GermplasmListDataComponent Copy to New List Button";
    public final static String ADD_ENTRIES_BUTTON_ID = "GermplasmListDataComponent Add Entries Button";
    
    private Table listDataTable;
    private int germplasmListId;
    private String listName;
    private List<GermplasmListData> listDatas;
    private String designationOfListEntriesDeleted="";
    
    private String MENU_SELECT_ALL="Select All"; 
    private String MENU_EXPORT_LIST="Export List"; 
    private String MENU_EXPORT_LIST_FOR_GENOTYPING_ORDER="Export List for Genotyping Order"; 
    private String MENU_COPY_TO_NEW_LIST="Copy to New List"; 
    private String MENU_ADD_ENTRY="Add Entry"; 
    private String MENU_SAVE_CHANGES="Save Changes"; 
    private String MENU_DELETE_SELECTED_ENTRIES="Delete Selected Entries"; 
    static final Action ACTION_VIEW_GERMPLASM_PREFERRED_NAME = new Action("Replace Entry Code with Preferred Name");
    static final Action ACTION_VIEW_GERMPLASM_PREFERRED_ID = new Action("Replace Entry Code with Preferred ID");
    static final Action ACTION_VIEW_GERMPLASM_LOCATION_NAME = new Action("Replace Seed Source with Germplasm Location Name");
    
    static final Action ACTION_SELECT_ALL = new Action("Select All");
    static final Action ACTION_DELETE = new Action("Delete selected entries");
    static final Action[] ACTIONS_TABLE_CONTEXT_MENU = new Action[] { ACTION_SELECT_ALL, ACTION_DELETE,ACTION_VIEW_GERMPLASM_PREFERRED_NAME,ACTION_VIEW_GERMPLASM_PREFERRED_ID,ACTION_VIEW_GERMPLASM_LOCATION_NAME};
    static final Action[] ACTIONS_TABLE_CONTEXT_MENU_WITHOUT_DELETE = new Action[] { ACTION_SELECT_ALL};
    
    public ListManagerTreeMenu listManagerTreeMenu;

    private boolean fromUrl;    //this is true if this component is created by accessing the Germplasm List Details page directly from the URL
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    @Autowired
    private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private GermplasmListManager germplasmListManager;
    
    @Autowired
    private GermplasmDataManager germplasmDataManager;
    
    @Autowired
    private PedigreeDataManager pedigreeDataManager;
    
    private boolean forGermplasmListWindow;
    private Integer germplasmListStatus;
    private GermplasmList germplasmList;
	private int germplasListUserId;
	private Button toolsButton;
	private ContextMenu menu;
	private ContextMenuItem menuSelectAll;
	private ContextMenuItem menuExportList;
	private ContextMenuItem menuExportForGenotypingOrder;
	private ContextMenuItem menuCopyToList;
	private ContextMenuItem menuAddEntry;
	private ContextMenuItem menuSaveChanges;
	private ContextMenuItem menuDeleteEntries;

	private Window listManagerCopyToNewListDialog;
	private GermplasmDetailModel germplasmDetail;
	 private static final ThemeResource ICON_TOOLS = new ThemeResource("images/tools.png");
	 public static String TOOLS_BUTTON_ID = "Tools";
	  private static String TOOLS_TOOLTIP = "Tools";
    
    public ListDataComponent(int germplasmListId,String listName,int germplasListUserId, boolean fromUrl,boolean forGermplasmListWindow, Integer germplasmListStatus,ListManagerTreeMenu listManagerTreeMenu){
        this.germplasmListId = germplasmListId;
        this.fromUrl = fromUrl;
        this.listName=listName;
        this.germplasListUserId=germplasListUserId;
        this.forGermplasmListWindow=forGermplasmListWindow;
        this.germplasmListStatus=germplasmListStatus;
        this.listManagerTreeMenu = listManagerTreeMenu;
    }

    @Override
    public void afterPropertiesSet() throws Exception{
    	
		menu = new ContextMenu();

		// Generate main level items
		menuSelectAll = menu.addItem(MENU_SELECT_ALL);
		menuExportList = menu.addItem(MENU_EXPORT_LIST);
		menuExportForGenotypingOrder = menu.addItem(MENU_EXPORT_LIST_FOR_GENOTYPING_ORDER);
		menuCopyToList = menu.addItem(MENU_COPY_TO_NEW_LIST);
		menuAddEntry = menu.addItem(MENU_ADD_ENTRY);
		menuSaveChanges = menu.addItem(MENU_SAVE_CHANGES);
		menuDeleteEntries = menu.addItem(MENU_DELETE_SELECTED_ENTRIES);
		
		menu.addListener(new ContextMenu.ClickListener() {
			   public void contextItemClick(ClickEvent event) {
			      // Get reference to clicked item
			      ContextMenuItem clickedItem = event.getClickedItem();
			      if(clickedItem.getName().equals(MENU_SELECT_ALL)){
			    	  listDataTable.setValue(listDataTable.getItemIds());
			      }else if(clickedItem.getName().equals(MENU_EXPORT_LIST)){
			    	  exportListAction();
			      }else if(clickedItem.getName().equals(MENU_EXPORT_LIST_FOR_GENOTYPING_ORDER)){
			    	  exportListForGenotypingOrderAction();
			      }else if(clickedItem.getName().equals(MENU_COPY_TO_NEW_LIST)){
			    	  copyToNewListAction();
			      }else if(clickedItem.getName().equals(MENU_ADD_ENTRY)){	  
			    
			      }else if(clickedItem.getName().equals(MENU_SAVE_CHANGES)){	  
			    	  saveChangesAction();
			      }else if(clickedItem.getName().equals(MENU_DELETE_SELECTED_ENTRIES)){	 
			    	  deleteListButtonClickAction();
			    	  
			      }
			   }
			});

    	
    	 toolsButton = new Button("Tools");
    	 toolsButton.setData(TOOLS_BUTTON_ID);
    	 toolsButton.setIcon(ICON_TOOLS);
    	 toolsButton.setWidth("200px");
    	 toolsButton.setDescription(TOOLS_TOOLTIP);
    	 toolsButton.setStyleName(Reindeer.BUTTON_LINK);
    	 toolsButton.addListener(new GermplasmListButtonClickListener(this, germplasmList));
 		
    	 toolsButton.addListener(new ClickListener() {

    		 @Override
    		 public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
    			 menu.show(event.getClientX(), event.getClientY());
    			 
    			 if(fromUrl){
    			  menuExportForGenotypingOrder.setVisible(false);
    			  menuExportList.setVisible(false);
    			  menuCopyToList.setVisible(false);
    			 }
    			 
    			// Show "Save Sorting" button only when Germplasm List open is a local IBDB record (negative ID).
                 // and when not accessed directly from URL or popup window
    			 if (germplasmListId < 0
    					 && !fromUrl) {
    				 if(germplasmListStatus>=100){
    					 menuDeleteEntries.setVisible(false);
    					 menuSaveChanges.setVisible(false);
    					 menuAddEntry.setVisible(false);
    				 }else{
    					 menuDeleteEntries.setVisible(true); 
    					 menuSaveChanges.setVisible(true);
    					 menuAddEntry.setVisible(true);
    				 }
		 
    			 }else{
    				 menuDeleteEntries.setVisible(false);
					 menuSaveChanges.setVisible(false);
					 menuAddEntry.setVisible(false);
    			 }

    		 }
    	 });
    	 
    	 listManagerTreeMenu.addComponent(menu);
    	 addComponent(toolsButton);
    	
    	 
    	 listDatas = new ArrayList<GermplasmListData>();
         long listDataCount = this.germplasmListManager.countGermplasmListDataByListId(germplasmListId);

         if (listDataCount == 0) {
             addComponent(new Label(messageSource.getMessage(Message.NO_LISTDATA_RETRIEVED_LABEL))); // "No Germplasm List Data retrieved."
         } else {
        	 
        	 // create the Vaadin Table to display the Germplasm List Data
             listDataTable = new Table("");
             listDataTable.setSelectable(true);
             listDataTable.setMultiSelect(true);
             listDataTable.setColumnCollapsingAllowed(true);
             listDataTable.setColumnReorderingAllowed(true);
//             listDataTable.setPageLength(15); // number of rows to display in the Table
             listDataTable.setWidth("95%");
             listDataTable.setHeight("95%");
             
             
             if(!fromUrl){
                     listDataTable.addActionHandler(new Action.Handler() {
                         public Action[] getActions(Object target, Object sender) {
                         if (germplasmListId < 0 &&  germplasmListStatus < 100){
                         return ACTIONS_TABLE_CONTEXT_MENU;
                         }else{
                         return ACTIONS_TABLE_CONTEXT_MENU_WITHOUT_DELETE;
                         }
                     }
         
                     public void handleAction(Action action, Object sender, Object target) {
                     	if (ACTION_DELETE == action) {
                     		//deleteListButtonClickAction();
                     	}else if(ACTION_SELECT_ALL == action) {
                     		listDataTable.setValue(listDataTable.getItemIds());
                     	}else if(ACTION_VIEW_GERMPLASM_PREFERRED_ID==action){
                    		setListDataTableColumnWithOtherInfo(PREFERRED_ID);
                    	}else if(ACTION_VIEW_GERMPLASM_PREFERRED_NAME==action){
                    		setListDataTableColumnWithOtherInfo(PREFERRED_NAME);
                    	}else if(ACTION_VIEW_GERMPLASM_LOCATION_NAME==action){
                    		setListDataTableColumnWithOtherInfo(LOCATION_NAME);
                    	}
                     }
                     });
             }

             //make GID as link only if the page wasn't directly accessed from the URL
             if (!fromUrl) {
                 listDataTable.addContainerProperty(GID, Button.class, null);
             } else {
                 listDataTable.addContainerProperty(GID, Integer.class, null);
             }

             listDataTable.addContainerProperty(GID_VALUE, Integer.class, null);
             listDataTable.addContainerProperty(ENTRY_ID, Integer.class, null);
             listDataTable.addContainerProperty(ENTRY_CODE, String.class, null);
             listDataTable.addContainerProperty(SEED_SOURCE, String.class, null);
             listDataTable.addContainerProperty(DESIGNATION, String.class, null);
             listDataTable.addContainerProperty(GROUP_NAME, String.class, null);
             listDataTable.addContainerProperty(STATUS, String.class, null);
         
             messageSource.setColumnHeader(listDataTable, GID, Message.LISTDATA_GID_HEADER);
             messageSource.setColumnHeader(listDataTable, ENTRY_ID, Message.LISTDATA_ENTRY_ID_HEADER);
             messageSource.setColumnHeader(listDataTable, ENTRY_CODE, Message.LISTDATA_ENTRY_CODE_HEADER);
             messageSource.setColumnHeader(listDataTable, SEED_SOURCE, Message.LISTDATA_SEEDSOURCE_HEADER);
             messageSource.setColumnHeader(listDataTable, DESIGNATION, Message.LISTDATA_DESIGNATION_HEADER);
             messageSource.setColumnHeader(listDataTable, GROUP_NAME, Message.LISTDATA_GROUPNAME_HEADER);
             messageSource.setColumnHeader(listDataTable, STATUS, Message.LISTDATA_STATUS_HEADER);
             
             populateTable();
             
             if(germplasmListId < 0){
            
	             List<String> propertyIdsEnabled = new ArrayList<String>();
	             propertyIdsEnabled.add(ENTRY_CODE);
	             propertyIdsEnabled.add(SEED_SOURCE);
	             
	           	 @SuppressWarnings("unused")
	           	 FillWith fillWith = new FillWith(listManagerTreeMenu, messageSource, listDataTable, GID, propertyIdsEnabled);
             }
             setSpacing(true);
             addComponent(listDataTable);
             
         }
    }


    @Override
    public void attach() {
        super.attach();
        updateLabels();
    }

    @Override
    public void updateLabels() {
    }

    
    private void populateTable() throws MiddlewareQueryException {
        listDataTable.removeAllItems();
        long listDataCount = this.germplasmListManager.countGermplasmListDataByListId(germplasmListId);
        listDatas = this.germplasmListManager.getGermplasmListDataByListId(germplasmListId, 0, (int) listDataCount);
        for (GermplasmListData data : listDatas) {
            Object gidObject;

            if (!fromUrl) {
                // make GID as link only if the page wasn't directly accessed from the URL
                String gid = String.format("%s", data.getGid().toString());
                Button gidButton = new Button(gid, new GidLinkButtonClickListener(gid,true));
                gidButton.setStyleName(BaseTheme.BUTTON_LINK);
                gidButton.setDescription("Click to view Germplasm information");
                gidObject = gidButton;
                //item.addItemProperty(columnId, new ObjectProperty<Button>(gidButton));
            } else {
                gidObject = data.getGid();
            }

            listDataTable.addItem(new Object[] {
                    gidObject,data.getGid(),data.getEntryId(), data.getEntryCode(), data.getSeedSource(),
                    data.getDesignation(), data.getGroupName(), data.getStatusString()
            }, data.getId());
        }

        listDataTable.sort(new Object[]{"entryId"}, new boolean[]{true});
        listDataTable.setVisibleColumns(new String[] {GID,ENTRY_ID,ENTRY_CODE,SEED_SOURCE,DESIGNATION,GROUP_NAME,STATUS});
    }

    
    protected void setListDataTableColumnWithOtherInfo(String gInfo) {
    	for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
            //iterate through the table elements' IDs
            int listDataId = (Integer) i.next();
            Item item = listDataTable.getItem(listDataId);
            Object gidObject = item.getItemProperty(GID).getValue();
            Button b= (Button) gidObject;
            String gid=b.getCaption();
            GermplasmDetailModel gModel=getGermplasmDetails(Integer.valueOf(gid));
            if(gInfo.equals(PREFERRED_NAME)){
            	item.getItemProperty(ENTRY_CODE).setValue(gModel.getGermplasmPreferredName());
            }else if(gInfo.equals(ENTRY_CODE)){
            	item.getItemProperty(ENTRY_CODE).setValue(gModel.getPrefID());
            }else if(gInfo.equals(LOCATION_NAME)){
            	item.getItemProperty(SEED_SOURCE).setValue(gModel.getGermplasmLocation());
            }
    	}
		
	}
    
    public GermplasmDetailModel getGermplasmDetails(int gid) throws InternationalizableException {
        try {
            germplasmDetail = new GermplasmDetailModel();
            Germplasm g = germplasmDataManager.getGermplasmByGID(new Integer(gid));
            Name name = germplasmDataManager.getPreferredNameByGID(gid);

            if (g != null) {
                germplasmDetail.setGid(g.getGid());
                germplasmDetail.setGermplasmMethod(germplasmDataManager.getMethodByID(g.getMethodId()).getMname());
                germplasmDetail.setGermplasmPreferredName(name == null ? "" : name.getNval());
                germplasmDetail.setPrefID(getGermplasmPrefID(g.getGid()));
            }
            return germplasmDetail;
        } catch (MiddlewareQueryException e) {
          
        }
		return germplasmDetail;
    }
    
    private String getGermplasmPrefID(int gid) throws InternationalizableException {
    	 String prefId = "";
    	try {
            ArrayList<Name> names = (ArrayList<Name>) germplasmDataManager.getNamesByGID(gid, 8, null);
           
            for (Name n : names) {
                if (n.getNstat() == 8) {
                    prefId = n.getNval();
                    break;
                }
            }
            return prefId;
        } catch (MiddlewareQueryException e) {
//            throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_GETTING_NAMES_BY_GERMPLASM_ID);
        }
		return prefId;
    }

    public void saveChangesAction() throws InternationalizableException {
        int entryId = 1;
        //re-assign "Entry ID" field based on table's sorting
        for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
            //iterate through the table elements' IDs
            int listDataId = (Integer) i.next();

            //update table item's entryId
            Item item = listDataTable.getItem(listDataId);
            item.getItemProperty(ENTRY_ID).setValue(entryId);

            //then find the corresponding ListData and assign a new entryId to it
            for (GermplasmListData listData : listDatas) {
                if (listData.getId().equals(listDataId)) {
                    listData.setEntryId(entryId);
                    listData.setEntryCode(item.getItemProperty(ENTRY_CODE).getValue().toString());
                    listData.setSeedSource(item.getItemProperty(SEED_SOURCE).getValue().toString());
                    break;
                }
            }
            entryId += 1;
        }
        //save the list of Germplasm List Data to the database
        try {
            germplasmListManager.updateGermplasmListData(listDatas);
            listDataTable.requestRepaint();
            MessageNotifier.showMessage(this.getWindow(), 
                    messageSource.getMessage(Message.SUCCESS), 
                    messageSource.getMessage(Message.SAVE_GERMPLASMLIST_DATA_SAVING_SUCCESS)
                    ,3000, Notification.POSITION_CENTERED);
        } catch (MiddlewareQueryException e) {
            throw new InternationalizableException(e, Message.ERROR_DATABASE, Message.ERROR_IN_SAVING_GERMPLASMLIST_DATA_CHANGES);
        }

    }

    //called by GermplasmListButtonClickListener
    public void exportListAction() throws InternationalizableException {

        if(germplasmListId>0 || (germplasmListId<0 && germplasmListStatus>=100)){
        
            String tempFileName = System.getProperty( "user.home" ) + "/temp.xls";
    
            GermplasmListExporter listExporter = new GermplasmListExporter(germplasmListId);
    
            try {
                listExporter.exportGermplasmListExcel(tempFileName);
                FileDownloadResource fileDownloadResource = new FileDownloadResource(new File(tempFileName), this.getApplication());
                fileDownloadResource.setFilename(listName + ".xls");
    
                //Window downloadWindow = new Window();
                //downloadWindow.setWidth(0);
                //downloadWindow.setHeight(0);
                //downloadWindow.open(fileDownloadResource);
                //this.getWindow().addWindow(downloadWindow);
                this.getWindow().open(fileDownloadResource);
    
                //TODO must figure out other way to clean-up file because deleting it here makes it unavailable for download
                    //File tempFile = new File(tempFileName);
                    //tempFile.delete();
            } catch (GermplasmListExporterException e) {
                    LOG.error("Error with exporting list.", e);
                MessageNotifier.showError(this.getApplication().getWindow(listManagerTreeMenu.getBreedingManagerApplication().LIST_MANAGER_WINDOW_NAME)
                            , "Error with exporting list."    
                            , e.getMessage() + " .Please report to Workbench developers.", Notification.POSITION_CENTERED);
            }
        } else {
//            MessageNotifier.showError(this.getApplication().getWindow(GermplasmStudyBrowserApplication.GERMPLASMLIST_WINDOW_NAME), "Germplasm List must be locked before exporting it", "");
            ConfirmDialog.show(this.getWindow(), "Export List", "Before exporting, the list should be locked first. Would you like to lock it?",
                "Yes", "No", new ConfirmDialog.Listener() {

            public void onClose(ConfirmDialog dialog) {
                if (dialog.isConfirmed()) {
                try {
                lockList();
                germplasmListStatus=germplasmList.getStatus();
                exportListAction();
            } catch (MiddlewareQueryException e) {
                LOG.error("Error with exporting list.", e);
                e.printStackTrace();
            }
                
                }else{

                }
            }
            });
    }
        }

    //called by GermplasmListButtonClickListener
    public void exportListForGenotypingOrderAction() throws InternationalizableException {
        if(germplasmListId>0 || (germplasmListId<0 && germplasmListStatus>=100)){
            String tempFileName = System.getProperty( "user.home" ) + "/tempListForGenotyping.xls";
            
                GermplasmListExporter listExporter = new GermplasmListExporter(germplasmListId);
    
                try {
                        listExporter.exportListForKBioScienceGenotypingOrder(tempFileName, 96);
                        FileDownloadResource fileDownloadResource = new FileDownloadResource(new File(tempFileName), this.getApplication());
                        fileDownloadResource.setFilename(listName + "ForGenotyping.xls");
    
                        this.getWindow().open(fileDownloadResource);
    
                        //TODO must figure out other way to clean-up file because deleting it here makes it unavailable for download
                        //File tempFile = new File(tempFileName);
                        //tempFile.delete();
                } catch (GermplasmListExporterException e) {
                        MessageNotifier.showError(this.getApplication().getWindow(listManagerTreeMenu.getBreedingManagerApplication().LIST_MANAGER_WINDOW_NAME) 
                                    , "Error with exporting list."
                                    , e.getMessage(), Notification.POSITION_CENTERED);
                }
        } else {
            MessageNotifier.showError(this.getApplication().getWindow(listManagerTreeMenu.getBreedingManagerApplication().LIST_MANAGER_WINDOW_NAME)
                        , "Error with exporting list."    
                        , "Germplasm List must be locked before exporting it", Notification.POSITION_CENTERED);
                    
        }
    }
    
    public void deleteListButtonClickAction()  throws InternationalizableException {
        final Collection<?> selectedIds = (Collection<?>)listDataTable.getValue();
        if(selectedIds.size() > 0){
            ConfirmDialog.show(this.getWindow(), "Delete List Entries:", "Are you sure you want to delete the selected list entries?",
                    "OK", "Cancel", new ConfirmDialog.Listener() {

            			private static final long serialVersionUID = 1L;

						public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                            	performListEntriesDeletion(selectedIds);

				            } else {
				                // User did not confirm
				            }
                        }
        		}
            );
            
        }else{
            MessageNotifier.showError(this.getWindow(), "Error with deleteting entries." 
                    , messageSource.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED), Notification.POSITION_CENTERED);
        }
    }
    
    private void performListEntriesDeletion(Collection<?> selectedIds){
		try {
            if(getCurrentUserLocalId()==germplasListUserId) {
                designationOfListEntriesDeleted="";
                final ArrayList<Integer> gidsWithoutChildren = getGidsToDeletedWithOutChildren();
                for (final Object itemId : selectedIds) {
                	Property pEntryId = listDataTable.getItem(itemId).getItemProperty(ENTRY_ID);
                	Property pDesignation = listDataTable.getItem(itemId).getItemProperty(DESIGNATION);
                	try {
					    int entryId=Integer.valueOf(pEntryId.getValue().toString());
					    designationOfListEntriesDeleted+=String.valueOf(pDesignation.getValue()).toString()+",";
					    germplasmListManager.deleteGermplasmListDataByListIdEntryId(germplasmListId,entryId);
					    listDataTable.removeItem(itemId);
					} catch (MiddlewareQueryException e) {
						e.printStackTrace();
					}
                }
                
                deleteGermplasmDialogBox(gidsWithoutChildren);
                
                designationOfListEntriesDeleted=designationOfListEntriesDeleted.substring(0,designationOfListEntriesDeleted.length()-1);
    
                //Change entry IDs on listData
                listDatas = germplasmListManager.getGermplasmListDataByListId(germplasmListId, 0
                            , (int) germplasmListManager.countGermplasmListDataByListId(germplasmListId));
                Integer entryId = 1;
                for (GermplasmListData listData : listDatas) {
                    listData.setEntryId(entryId);
                    entryId++;
                }
                germplasmListManager.updateGermplasmListData(listDatas);
                
                //Change entry IDs on table
                entryId = 1;
                for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
                    int listDataId = (Integer) i.next();
                    Item item = listDataTable.getItem(listDataId);
                    item.getItemProperty(ENTRY_ID).setValue(entryId);
                    for (GermplasmListData listData : listDatas) {
                        if (listData.getId().equals(listDataId)) {
                            listData.setEntryId(entryId);
                            break;
                        }
                    }
                    entryId += 1;
                }
                listDataTable.requestRepaint();
                
                try {
                    logDeletedListEntriesToWorkbenchProjectActivity();
                } catch (MiddlewareQueryException e) {
                    LOG.error("Error logging workbench activity.", e);
                    e.printStackTrace();
                }

            
                
            } else {
            	showMessageInvalidDeletingListEntries();
            }
            
		} catch (NumberFormatException e) {
			LOG.error("Error with deleting list entries.", e);
			e.printStackTrace();
		} catch (MiddlewareQueryException e) {
			LOG.error("Error with deleting list entries.", e);
			e.printStackTrace();
		}
        
//		gidsWithoutChildren=getGidsToDeletedWithOutChildren();
//		try {
//			if(gidsWithoutChildren.size() > 0){
//				deleteGermplasmDialogBox(gidsWithoutChildren);
//			}
//		} catch (NumberFormatException e1) {
//			e1.printStackTrace();
//		} catch (MiddlewareQueryException e1) {
//			e1.printStackTrace();
//		}
		
    }

    private int getCurrentUserLocalId() throws MiddlewareQueryException {
        Integer workbenchUserId = this.workbenchDataManager.getWorkbenchRuntimeData().getUserId();
        Project lastProject = this.workbenchDataManager.getLastOpenedProject(workbenchUserId);
        Integer localIbdbUserId = this.workbenchDataManager.getLocalIbdbUserId(workbenchUserId,lastProject.getProjectId());
        if (localIbdbUserId != null) {
            return localIbdbUserId;
        } else {
            return -1; // TODO: verify actual default value if no workbench_ibdb_user_map was found
        }
    }

    private void logDeletedListEntriesToWorkbenchProjectActivity() throws MiddlewareQueryException {
//        GermplasmStudyBrowserApplication app = GermplasmStudyBrowserApplication.get();
        BreedingManagerApplication app = listManagerTreeMenu.getBreedingManagerApplication();

        User user = (User) workbenchDataManager.getUserById(workbenchDataManager.getWorkbenchRuntimeData().getUserId());

        ProjectActivity projAct = new ProjectActivity(new Integer(workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue()), 
                workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()), 
                "Deleted list entries.", 
                "Deleted list entries from the list id " + germplasmListId + " - " + listName,user,new Date());
        try {
            workbenchDataManager.addProjectActivity(projAct);
        } catch (MiddlewareQueryException e) {
            LOG.error("Error with logging workbench activity.", e);
            e.printStackTrace();
        }
    }

    private void showMessageInvalidDeletingListEntries(){
    MessageNotifier.showError(this.getWindow()
        , messageSource.getMessage(Message.INVALID_DELETING_LIST_ENTRIES) 
        , messageSource.getMessage(Message.INVALID_USER_DELETING_LIST_ENTRIES)
        , Notification.POSITION_CENTERED);
    }

    public void copyToNewListAction(){
        Collection<?> listEntries = (Collection<?>) listDataTable.getValue();
        if (listEntries == null || listEntries.isEmpty()){
            MessageNotifier.showError(this.getWindow(), messageSource.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED), "", Notification.POSITION_CENTERED);
            
        } else {
            listManagerCopyToNewListDialog = new Window(messageSource.getMessage(Message.COPY_TO_NEW_LIST_WINDOW_LABEL));
            listManagerCopyToNewListDialog.setModal(true);
            listManagerCopyToNewListDialog.setWidth(700);
            listManagerCopyToNewListDialog.setHeight(350);
            
            try {
                if(forGermplasmListWindow) {
                    listManagerCopyToNewListDialog.addComponent(new ListManagerCopyToNewListDialog(this.getApplication().getWindow(listManagerTreeMenu.getBreedingManagerApplication().LIST_MANAGER_WINDOW_NAME), listManagerCopyToNewListDialog,listName,listDataTable,getCurrentUserLocalId()));
                    this.getApplication().getWindow(listManagerTreeMenu.getBreedingManagerApplication().LIST_MANAGER_WINDOW_NAME).addWindow(listManagerCopyToNewListDialog);
                 
                } else {
                    
//                  listManagerCopyToNewListDialog.addComponent(new ListManagerCopyToNewListDialog(this.getApplication().getMainWindow(), listManagerCopyToNewListDialog,listName,listDataTable,getCurrentUserLocalId()));
//                  this.getApplication().getMainWindow().addWindow(listManagerCopyToNewListDialog);
                    listManagerCopyToNewListDialog.addComponent(new ListManagerCopyToNewListDialog(listManagerTreeMenu.getWindow(), listManagerCopyToNewListDialog,listName,listDataTable,getCurrentUserLocalId()));
                    listManagerTreeMenu.getWindow().addWindow(listManagerCopyToNewListDialog);
                }
            } catch (MiddlewareQueryException e) {
                LOG.error("Error copying list entries.", e);
                e.printStackTrace();
            }
        }
        
    
    }
    
    public void lockList() throws MiddlewareQueryException{
        germplasmList = germplasmListManager.getGermplasmListById(germplasmListId);
        germplasmList.setStatus(germplasmList.getStatus()+100);
        try {
        germplasmListManager.updateGermplasmList(germplasmList);
    
        User user = (User) workbenchDataManager.getUserById(workbenchDataManager.getWorkbenchRuntimeData().getUserId());
        ProjectActivity projAct = new ProjectActivity(new Integer(workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue()), 
                workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()), 
            "Locked a germplasm list.", 
            "Locked list "+germplasmList.getId()+" - "+germplasmList.getName(), user, new Date());
        workbenchDataManager.addProjectActivity(projAct);
        
//        	deleteSelectedEntriesButton.setEnabled(false); 
//            saveSortingButton.setEnabled(false);
//            addEntriesButton.setEnabled(false);
            
            menuDeleteEntries.setVisible(false);
            menuSaveChanges.setVisible(false);
            menuAddEntry.setVisible(false);
            
           
        }catch (MiddlewareQueryException e) {
            LOG.error("Error with locking list.", e);
            MessageNotifier.showError(getWindow(), "Database Error!", "Error with locking list. Please report to IBWS developers."
                    , Notification.POSITION_CENTERED);
            return;
        }
    }

    public void finishAddingEntry(Integer gid) {
        GermplasmList list = null; 
        Germplasm germplasm = null;
        try {
            list = germplasmListManager.getGermplasmListById(germplasmListId);
        } catch(MiddlewareQueryException ex){
            LOG.error("Error with getting germplasm list with id: " + germplasmListId, ex);
            MessageNotifier.showError(getWindow(), "Database Error!", "Error with getting germplasm list with id: " + germplasmListId  
                    + ". Please report to IBWS developers."
                    , Notification.POSITION_CENTERED);
            return;
        }
        
        try {
            germplasm = germplasmDataManager.getGermplasmWithPrefName(gid);
        } catch(MiddlewareQueryException ex){
            LOG.error("Error with getting germplasm with id: " + gid, ex);
            MessageNotifier.showError(getWindow(), "Database Error!", "Error with getting germplasm with id: " + gid  
                    + ". Please report to IBWS developers."
                    , Notification.POSITION_CENTERED);
            return;
        }
        
        Integer maxEntryId = Integer.valueOf(1);
        for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
            //iterate through the table elements' IDs
            int listDataId = (Integer) i.next();

            //update table item's entryId
            Item item = listDataTable.getItem(listDataId);
            Integer entryId = (Integer) item.getItemProperty(ENTRY_ID).getValue();
            if(maxEntryId < entryId){
                maxEntryId = entryId;
            }
        }
        
        
        GermplasmListData listData = new GermplasmListData();
        listData.setList(list);
        if(germplasm.getPreferredName() != null){
            listData.setDesignation(germplasm.getPreferredName().getNval());
        } else {
            listData.setDesignation("-");
        }
        listData.setEntryId(maxEntryId+1);
        listData.setGid(gid);
        listData.setLocalRecordId(Integer.valueOf(0));
        listData.setStatus(Integer.valueOf(0));
        
        String preferredId = "-";
        try{
            Name nameRecord = this.germplasmDataManager.getPreferredIdByGID(gid);
            if(nameRecord != null){
                preferredId = nameRecord.getNval();
            }
        } catch(MiddlewareQueryException ex){
            preferredId = "-";
        }
        listData.setEntryCode(preferredId);
        
        listData.setSeedSource("From Add Entry Feature of Germplasm List Browser");
        
        String groupName = "-";
        try{
            groupName = this.germplasmDataManager.getCrossExpansion(gid, 1);
        } catch(MiddlewareQueryException ex){
            groupName = "-";
        }
        listData.setGroupName(groupName);
            
        Integer listDataId = null;
        try {
            listDataId = this.germplasmListManager.addGermplasmListData(listData);
            
            Object gidObject;

            if (!fromUrl) {
                    // make GID as link only if the page wasn't directly accessed from the URL
                    String gidString = String.format("%s", gid.toString());
                    Button gidButton = new Button(gidString, new GidLinkButtonClickListener(gidString,false));
                    gidButton.setStyleName(BaseTheme.BUTTON_LINK);
                    gidButton.setDescription("Click to view Germplasm information");
                    gidObject = gidButton;
            } else {
                    gidObject = gid;
            }

            listDataTable.addItem(new Object[] {
                            gidObject,gid,listData.getEntryId(), listData.getEntryCode(), listData.getSeedSource(),
                            listData.getDesignation(), listData.getGroupName(), listData.getStatusString()
                    }, listDataId);
            listDataTable.requestRepaint();
            listDataTable.setImmediate(true);
            MessageNotifier.showMessage(this.getWindow(), 
                    messageSource.getMessage(Message.SUCCESS), 
                    "Successful in adding a list entry.", 3000, Notification.POSITION_CENTERED);
            
            User user = (User) workbenchDataManager.getUserById(workbenchDataManager.getWorkbenchRuntimeData().getUserId());

            ProjectActivity projAct = new ProjectActivity(new Integer(workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()).getProjectId().intValue()), 
                            workbenchDataManager.getLastOpenedProject(workbenchDataManager.getWorkbenchRuntimeData().getUserId()), 
                            "Added list entry.", 
                            "Added " + gid + " as list entry to " + list.getId() + ":" + list.getName(),user,new Date());
            try {
                workbenchDataManager.addProjectActivity(projAct);
            } catch (MiddlewareQueryException e) {
                LOG.error("Error with adding workbench activity log.", e);
                MessageNotifier.showError(getWindow(), "Database Error!", "Error with adding workbench activity log. Please report to IBWS developers."
                        , Notification.POSITION_CENTERED);
            }
            //populateTable();
            //listDataTable.requestRepaint();
//            if(this.germplasmListAccordionMenu != null)
//                this.germplasmListAccordionMenu.refreshListData();
        } catch (MiddlewareQueryException ex) {
            LOG.error("Error with adding list entry.", ex);
            MessageNotifier.showError(getWindow(), "Database Error!", "Error with adding list entry. Please report to IBWS developers."
                    , Notification.POSITION_CENTERED);
            return;
        }
    }
    
//    public void addEntryButtonClickAction(){
//        Window parentWindow = this.getWindow();
//        AddEntryDialog addEntriesDialog = new AddEntryDialog(this, parentWindow);
//        parentWindow.addWindow(addEntriesDialog);
//    }
    
	protected void deleteGermplasmDialogBox(final List<Integer> gidsWithoutChildren) throws NumberFormatException, MiddlewareQueryException {

        if (gidsWithoutChildren!= null && gidsWithoutChildren.size() > 0){
        	
        	ConfirmDialog.show(this.getWindow(), "Delete Germplasm from Database", "Would you like to delete the germplasm(s) from the database also?",
        			"Yes", "No", new ConfirmDialog.Listener() {
        		private static final long serialVersionUID = 1L;
        		
        		public void onClose(ConfirmDialog dialog) {
        			if (dialog.isConfirmed()) {
        				ArrayList<Germplasm> gList = new ArrayList<Germplasm>();
        				try {
        					for(Integer gid : gidsWithoutChildren){
        						Germplasm g= germplasmDataManager.getGermplasmByGID(gid);
        						g.setGrplce(gid);
        						gList.add(g);
        					}// end loop
        					
        					germplasmDataManager.updateGermplasm(gList);
        					
        				} catch (MiddlewareQueryException e) {
        					e.printStackTrace();
        				}
        				
        			}
        		}
        		
        	});
        	
        }
	}
	
    protected ArrayList<Integer> getGidsToDeletedWithOutChildren() throws NumberFormatException, MiddlewareQueryException{
    	ArrayList<Integer> gids= new ArrayList<Integer>();
    	Collection<?> selectedIds = (Collection<?>)listDataTable.getValue();
	     for (final Object itemId : selectedIds) {
	      
	         Property pGid= listDataTable.getItem(itemId).getItemProperty(GID_VALUE);
	   		 String gid=pGid.getValue().toString();
	   		 // only allow deletions for local germplasms
	   		 if(gid.contains("-")){
	   			 long count = pedigreeDataManager.countDescendants(Integer.valueOf(gid));
	   			 if(count == 0){
	   				 gids.add(Integer.valueOf(gid));
	   			 }
	   		 }
	     }
	    	   			 
	   	return gids;
    }
    
    

}