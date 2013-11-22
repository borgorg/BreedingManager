package org.generationcp.breeding.manager.listmanager.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listmanager.ListManagerTreeMenu;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.HeaderClickEvent;
import com.vaadin.ui.Window.Notification;

@Configurable
public class FillWith implements InternationalizableComponent  {

	//@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
    @Autowired
    private GermplasmDataManager germplasmDataManager;

    private ListManagerTreeMenu listManagerTreeMenu;
    private AbsoluteLayout absoluteLayout;
    
    private Table targetTable;
    private String GIDPropertyId;
    private List<String> propertyIdsContextMenuAvailableTo;
    
    
	private ContextMenu fillWithMenu;
	private ContextMenuItem menuFillWithGermplasmDate;
	private ContextMenuItem menuFillWithPrefName;
	private ContextMenuItem menuFillWithPrefID;
	private ContextMenuItem menuFillWithLocationName;
	private ContextMenuItem menuFillWithBreedingMethodInfo;
	private ContextMenuItem menuFillWithBreedingMethodName;
	private ContextMenuItem menuFillWithBreedingMethodID;
	private ContextMenuItem menuFillWithBreedingMethodGroup;
	private ContextMenuItem menuFillWithBreedingMethodNumber;
	private ContextMenuItem menuFillWithBreedingMethodAbbreviation;
	private ContextMenuItem menuFillWithCrossFemaleInformation;
	private ContextMenuItem menuFillWithCrossFemaleGID;
	private ContextMenuItem menuFillWithCrossFemalePreferredName;
	private ContextMenuItem menuFillWithCrossMaleInformation;
	private ContextMenuItem menuFillWithCrossMaleGID;
	private ContextMenuItem menuFillWithCrossMalePreferredName;
    
	/**
	 * Add Fill With context menu to a table
	 * @param listManagerTreeMenu - contextMenu will attach to this
	 * @param targetTable - table where data will be manipulated
	 * @param GIDPropertyId - property of GID (button with GID as caption) on that table
	 * @param propertyIdsContextMenuAvailableTo - list of property ID's where context menu will be available for "right clicking"
	 */
    public FillWith(ListManagerTreeMenu listManagerTreeMenu,final SimpleResourceBundleMessageSource messageSource, final Table targetTable, String GIDPropertyId, final List<String> propertyIdsContextMenuAvailableTo){
    	this.GIDPropertyId = GIDPropertyId;
    	this.targetTable = targetTable;
    	this.listManagerTreeMenu = listManagerTreeMenu;
    	this.propertyIdsContextMenuAvailableTo = propertyIdsContextMenuAvailableTo;
    	this.messageSource = messageSource;
    	
    	setupContextMenu();
    	
    }
    
    
	/**
	 * Add Fill With context menu to a table
	 * @param absoluteLayout - contextMenu will attach to this
	 * @param targetTable - table where data will be manipulated
	 * @param GIDPropertyId - property of GID (button with GID as caption) on that table
	 * @param propertyIdsContextMenuAvailableTo - list of property ID's where context menu will be available for "right clicking"
	 */
    public FillWith(AbsoluteLayout absoluteLayout,final SimpleResourceBundleMessageSource messageSource, final Table targetTable, String GIDPropertyId, final List<String> propertyIdsContextMenuAvailableTo){
    	this.GIDPropertyId = GIDPropertyId;
    	this.targetTable = targetTable;
    	this.absoluteLayout = absoluteLayout;
    	this.propertyIdsContextMenuAvailableTo = propertyIdsContextMenuAvailableTo;
    	this.messageSource = messageSource;
    	
    	setupContextMenu();
    	
    }
    
    
    private void setupContextMenu(){
    	
	   	 fillWithMenu = new ContextMenu();
		 
	   	 menuFillWithLocationName = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_LOCATION_NAME));
	   	 menuFillWithPrefID = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_PREF_ID));
	   	 menuFillWithGermplasmDate = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_GERMPLASM_DATE));
	   	 menuFillWithPrefName = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_PREF_NAME));
	   	 
	   	 menuFillWithBreedingMethodInfo = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_INFO));
	   	 menuFillWithBreedingMethodName = menuFillWithBreedingMethodInfo.addItem(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_NAME));
	   	 menuFillWithBreedingMethodAbbreviation = menuFillWithBreedingMethodInfo.addItem(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_ABBREVIATION));
	   	 menuFillWithBreedingMethodNumber = menuFillWithBreedingMethodInfo.addItem(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_NUMBER));
	   	 menuFillWithBreedingMethodGroup = menuFillWithBreedingMethodInfo.addItem(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_GROUP));
	
	   	 menuFillWithCrossFemaleInformation = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_CROSS_FEMALE_INFORMATION));
	   	 menuFillWithCrossFemaleGID = menuFillWithCrossFemaleInformation.addItem(messageSource.getMessage(Message.FILL_WITH_CROSS_FEMALE_GID));
	   	 menuFillWithCrossFemalePreferredName = menuFillWithCrossFemaleInformation.addItem(messageSource.getMessage(Message.FILL_WITH_CROSS_FEMALE_PREFERRED_NAME));
	   	 
	   	 menuFillWithCrossMaleInformation = fillWithMenu.addItem(messageSource.getMessage(Message.FILL_WITH_CROSS_MALE_INFORMATION));
	   	 menuFillWithCrossMaleGID = menuFillWithCrossMaleInformation.addItem(messageSource.getMessage(Message.FILL_WITH_CROSS_MALE_GID));
	   	 menuFillWithCrossMalePreferredName = menuFillWithCrossMaleInformation.addItem(messageSource.getMessage(Message.FILL_WITH_CROSS_MALE_PREFERRED_NAME));
	   	 
	   	 
	   	 
	   	 fillWithMenu.addListener(new ContextMenu.ClickListener() {
	   		private static final long serialVersionUID = -2384037190598803030L;
	
				public void contextItemClick(ClickEvent event) {
	   			 // Get reference to clicked item
	   			 ContextMenuItem clickedItem = event.getClickedItem();
	   			 if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_LOCATION_NAME))){
	   				 MessageNotifier.showMessage(event.getComponent().getWindow(), "Information"
	   						 , "Fill With Location Name was clicked.", 3000, Notification.POSITION_CENTERED);
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_GERMPLASM_DATE))){
	   				 fillWithGermplasmDate(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_PREF_NAME))){
	   				 MessageNotifier.showMessage(event.getComponent().getWindow(), "Information"
	   						 , "Fill With Preferred Name was clicked.", 3000, Notification.POSITION_CENTERED);
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_NAME))){
	   				 fillWithMethodName(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_ABBREVIATION))){
	   				 fillWithMethodAbbreviation(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_NUMBER))){
	   				 fillWithMethodNumber(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_BREEDING_METHOD_GROUP))){
	   				 fillWithMethodGroup(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_CROSS_FEMALE_GID))){
	   				 fillWithCrossFemaleGID(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_CROSS_FEMALE_PREFERRED_NAME))){
	   				 fillWithCrossFemalePreferredName(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_CROSS_MALE_GID))){
	   				 fillWithCrossMaleGID(targetTable, (String) fillWithMenu.getData());
	   			 } else if(clickedItem.getName().equals(messageSource.getMessage(Message.FILL_WITH_CROSS_MALE_PREFERRED_NAME))){
	   				 fillWithCrossMalePreferredName(targetTable, (String) fillWithMenu.getData());
	   			 }
	   			 
	   		 }
	   	 });
	   	 
	   	 if(absoluteLayout!=null){
	   		 absoluteLayout.addComponent(fillWithMenu);
	   	 } else {
	   		 listManagerTreeMenu.addComponent(fillWithMenu);
	   	 }
	   	 
	   	 targetTable.addListener(new Table.HeaderClickListener() {
        	private static final long serialVersionUID = 4792602001489368804L;

			public void headerClick(HeaderClickEvent event) {
        		if(event.getButton() == HeaderClickEvent.BUTTON_RIGHT){
        			String column = (String) event.getPropertyId();
        			fillWithMenu.setData(column);
        			if(propertyIdsContextMenuAvailableTo.contains(column)){
            			menuFillWithLocationName.setVisible(false);
            			menuFillWithPrefID.setVisible(true);
            			menuFillWithPrefName.setVisible(true);
            			fillWithMenu.show(event.getClientX(), event.getClientY());
            		}
        		}
        	}
        });
    }

    
    public List<Integer> getGidsFromTable(Table table){
    	List<Integer> gids = new ArrayList<Integer>();
    	List<Integer> listDataItemIds = getItemIds(table);
    	for(Integer itemId: listDataItemIds){
    		//gids.add((Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue());
    		gids.add(Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString()));
    	}
    	return gids;
    }
   
	@SuppressWarnings("unchecked")
	public List<Integer> getItemIds(Table table){
		List<Integer> itemIds = new ArrayList<Integer>();
    	itemIds.addAll((Collection<? extends Integer>) table.getItemIds());
    	return itemIds;
	}    
    
    public void fillWithGermplasmDate(Table table, String propertyId){
	   try {
		   List<Integer> itemIds = getItemIds(table);
		   List<Integer> gids = getGidsFromTable(table);
		   Map<Integer,Integer> germplasmGidDateMap = germplasmDataManager.getGermplasmDatesByGids(gids);
		   
		   for(Integer itemId: itemIds){
			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
			   table.getItem(itemId).getItemProperty(propertyId).setValue(germplasmGidDateMap.get(gid));
		   }
		   
	   } catch (MiddlewareQueryException e) {
		   e.printStackTrace();
	   }
    }
 
    public void fillWithMethodName(Table table, String propertyId){
	   try {
		   List<Integer> itemIds = getItemIds(table);
		   List<Integer> gids = getGidsFromTable(table);
		   Map<Integer,Object> germplasmGidDateMap = germplasmDataManager.getMethodsByGids(gids);
		   
		   for(Integer itemId: itemIds){
			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
			   table.getItem(itemId).getItemProperty(propertyId).setValue(((Method) germplasmGidDateMap.get(gid)).getMname().toString());
		   }
		   
	   } catch (MiddlewareQueryException e) {
		   e.printStackTrace();
	   }
    }    

    public void fillWithMethodAbbreviation(Table table, String propertyId){
	   try {
		   List<Integer> itemIds = getItemIds(table);
		   List<Integer> gids = getGidsFromTable(table);
		   Map<Integer,Object> germplasmGidDateMap = germplasmDataManager.getMethodsByGids(gids);
		   
		   for(Integer itemId: itemIds){
			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
			   table.getItem(itemId).getItemProperty(propertyId).setValue(((Method) germplasmGidDateMap.get(gid)).getMcode().toString());
		   }
		   
	   } catch (MiddlewareQueryException e) {
		   e.printStackTrace();
	   }
    }   
    
    public void fillWithMethodNumber(Table table, String propertyId){
	   try {
		   List<Integer> itemIds = getItemIds(table);
		   List<Integer> gids = getGidsFromTable(table);
		   Map<Integer,Object> germplasmGidDateMap = germplasmDataManager.getMethodsByGids(gids);
		   
		   for(Integer itemId: itemIds){
			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
			   table.getItem(itemId).getItemProperty(propertyId).setValue(((Method) germplasmGidDateMap.get(gid)).getMid().toString());
		   }
		   
	   } catch (MiddlewareQueryException e) {
		   e.printStackTrace();
	   }
    }       
    
    public void fillWithMethodGroup(Table table, String propertyId){
	   try {
		   List<Integer> itemIds = getItemIds(table);
		   List<Integer> gids = getGidsFromTable(table);
		   Map<Integer,Object> germplasmGidDateMap = germplasmDataManager.getMethodsByGids(gids);
		   
		   for(Integer itemId: itemIds){
			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
			   table.getItem(itemId).getItemProperty(propertyId).setValue(((Method) germplasmGidDateMap.get(gid)).getMgrp().toString());
		   }
		   
	   } catch (MiddlewareQueryException e) {
		   e.printStackTrace();
	   }
    }
    
    public void fillWithCrossFemaleGID(Table table, String propertyId){
 	   try {
		   List<Integer> itemIds = getItemIds(table);
		   List<Integer> gids = getGidsFromTable(table);
		   for(Integer itemId: itemIds){
			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
			   Germplasm germplasm = germplasmDataManager.getGermplasmByGID(gid);
			   table.getItem(itemId).getItemProperty(propertyId).setValue(germplasm.getGpid1());
		   }
	   } catch (MiddlewareQueryException e) {
		   e.printStackTrace();
	   }    	
    }
    
    public void fillWithCrossFemalePreferredName(Table table, String propertyId){
  	   try {
 		   List<Integer> itemIds = getItemIds(table);
 		   List<Integer> gids = getGidsFromTable(table);
 		   for(Integer itemId: itemIds){
 			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
 			  Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
 			   Germplasm germplasm = germplasmDataManager.getGermplasmByGID(gid);
 			   List<Integer> parentGids = new ArrayList<Integer>();
 			   parentGids.add(germplasm.getGpid1());
 			   Map<Integer, String> preferredNames = germplasmDataManager.getPreferredNamesByGids(parentGids);
 			   table.getItem(itemId).getItemProperty(propertyId).setValue(preferredNames.get(germplasm.getGpid1()));
 		   }
 	   } catch (MiddlewareQueryException e) {
 		   e.printStackTrace();
 	   }    	    	
    }
    
    public void fillWithCrossMaleGID(Table table, String propertyId){
  	   try {
 		   List<Integer> itemIds = getItemIds(table);
 		   List<Integer> gids = getGidsFromTable(table);
 		   for(Integer itemId: itemIds){
 			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
 			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
 			   Germplasm germplasm = germplasmDataManager.getGermplasmByGID(gid);
 			   table.getItem(itemId).getItemProperty(propertyId).setValue(germplasm.getGpid2());
 		   }
 	   } catch (MiddlewareQueryException e) {
 		   e.printStackTrace();
 	   }    	    	
    }
    
    public void fillWithCrossMalePreferredName(Table table, String propertyId){
   	   try {
  		   List<Integer> itemIds = getItemIds(table);
  		   List<Integer> gids = getGidsFromTable(table);
  		   for(Integer itemId: itemIds){
  			   //Integer gid = (Integer) table.getItem(itemId).getItemProperty(GID_VALUE).getValue();
  			   Integer gid = Integer.valueOf(((Button) table.getItem(itemId).getItemProperty(GIDPropertyId).getValue()).getCaption().toString());
  			   Germplasm germplasm = germplasmDataManager.getGermplasmByGID(gid);
  			   List<Integer> parentGids = new ArrayList<Integer>();
  			   parentGids.add(germplasm.getGpid2());
  			   Map<Integer, String> preferredNames = germplasmDataManager.getPreferredNamesByGids(parentGids);
  			   table.getItem(itemId).getItemProperty(propertyId).setValue(preferredNames.get(germplasm.getGpid2()));
  		   }
  	   } catch (MiddlewareQueryException e) {
  		   e.printStackTrace();
  	   }        	
    }


	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
		
	}


}