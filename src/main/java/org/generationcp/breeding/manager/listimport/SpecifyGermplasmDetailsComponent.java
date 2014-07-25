package org.generationcp.breeding.manager.listimport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmName;
import org.generationcp.breeding.manager.customcomponent.SaveListAsDialog;
import org.generationcp.breeding.manager.customcomponent.SaveListAsDialogSource;
import org.generationcp.breeding.manager.listimport.actions.ProcessImportedGermplasmAction;
import org.generationcp.breeding.manager.listimport.actions.SaveGermplasmListAction;
import org.generationcp.breeding.manager.listimport.listeners.GermplasmImportButtonClickListener;
import org.generationcp.breeding.manager.listimport.util.GermplasmListUploader;
import org.generationcp.breeding.manager.pojos.ImportedGermplasm;
import org.generationcp.breeding.manager.pojos.ImportedGermplasmList;
import org.generationcp.breeding.manager.util.BreedingManagerUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ConversionException;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class SpecifyGermplasmDetailsComponent extends VerticalLayout implements InitializingBean, 
		InternationalizableComponent, BreedingManagerLayout, SaveListAsDialogSource {

    private static final long serialVersionUID = 2762965368037453497L;
    
    public static final String NEXT_BUTTON_ID = "next button";
    public static final String BACK_BUTTON_ID = "back button";

    private GermplasmImportMain source;
    
    private GermplasmFieldsComponent germplasmFieldsComponent;
    private Table germplasmDetailsTable;
    
    private Label reviewImportDetailsLabel;
    private Label totalEntriesLabel;
    private Label selectPedigreeOptionsLabel;
    private Label pedigreeOptionsLabel;
    
    private ComboBox pedigreeOptionComboBox;
    
    private Button backButton;
    private Button nextButton;


    private CheckBox automaticallyAcceptSingleMatchesCheckbox;

    private List<ImportedGermplasm> importedGermplasms;
    private GermplasmListUploader germplasmListUploader;

	private GermplasmList germplasmList;
	
	private SaveListAsDialog saveListAsDialog;
	private ProcessImportedGermplasmAction processGermplasmAction;
    
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
    
    @Autowired
    private GermplasmDataManager germplasmDataManager;
    
    @Autowired
    private GermplasmListManager germplasmListManager;
        
    @Autowired
    private WorkbenchDataManager workbenchDataManager;
    
    private Boolean viaToolURL;
    
	public SpecifyGermplasmDetailsComponent(GermplasmImportMain source, Boolean viaToolURL){
        this.source = source;
        this.viaToolURL = viaToolURL;
    }

    public Table getGermplasmDetailsTable(){
        return germplasmDetailsTable;
    }
    
    public List<ImportedGermplasm> getImportedGermplasms() {
        return importedGermplasms;
    }

    public void setImportedGermplasms(List<ImportedGermplasm> importedGermplasms) {
        this.importedGermplasms = importedGermplasms;
    }

    public GermplasmListUploader getGermplasmListUploader() {
        return germplasmListUploader;
    }

    public void setGermplasmListUploader(GermplasmListUploader germplasmListUploader) {
        this.germplasmListUploader = germplasmListUploader;
    }
    
    public Boolean getViaToolURL() {
		return viaToolURL;
	}

    @Override
    public void afterPropertiesSet() throws Exception {
        instantiateComponents();
        initializeValues();
        addListeners();
        layoutComponents();
    }
    
	
    
    public GermplasmFieldsComponent getGermplasmFieldsComponent() {
		return germplasmFieldsComponent;
	}


	@Override
    public void attach() {
        super.attach();
        updateLabels();
    }
    
    @Override
    public void updateLabels() {
        messageSource.setCaption(backButton, Message.BACK);
        messageSource.setCaption(nextButton, Message.FINISH);
    }
    
    
        
    public void nextButtonClickAction(){
        if (validateLocation() && validatePedigreeOption()) {
            processGermplasmAction.processGermplasm();
        }
    }
    
    public void popupSaveAsDialog(){

		germplasmList = new GermplasmList();
		
	    SimpleDateFormat formatter = new SimpleDateFormat(GermplasmImportMain.DATE_FORMAT);
	    String sDate = formatter.format(germplasmListUploader.getListDate());
	
	    Long dataLongValue = Long.parseLong(sDate.replace("-", ""));
	    germplasmList.setName(germplasmListUploader.getListName());
	    germplasmList.setDate(dataLongValue);
	    germplasmList.setType(germplasmListUploader.getListType());
	    germplasmList.setDescription(germplasmListUploader.getListTitle());
	    germplasmList.setStatus(1);
	     
	    List<GermplasmName> germplasmNameObjects = getGermplasmNameObjects();
	    List<GermplasmName> germplasmNameObjectsToBeSaved = new ArrayList<GermplasmName>();
	     
	    for(int i = 0 ; i < germplasmNameObjects.size() ; i++){
	        Integer gid = germplasmNameObjects.get(i).getGermplasm().getGid();
			if(processGermplasmAction.getMatchedGermplasmIds().contains(gid)){
	            //Get germplasm using temporarily set GID, then create map
	            Germplasm germplasmToBeUsed;
				try {
					germplasmToBeUsed = germplasmDataManager.getGermplasmByGID(gid);
					germplasmNameObjectsToBeSaved.add(new GermplasmName(germplasmToBeUsed, germplasmNameObjects.get(i).getName()));
				} catch (MiddlewareQueryException e) {
					e.printStackTrace();
				}
	        } else {	        	 
	           	germplasmNameObjectsToBeSaved.add(new GermplasmName(germplasmNameObjects.get(i).getGermplasm(), germplasmNameObjects.get(i).getName()));
	        }
	    }
	     
	    saveListAsDialog = new SaveListAsDialog(this, germplasmList);
	    this.getWindow().addWindow(saveListAsDialog);
         
    }
    
    private boolean validatePedigreeOption() {
        return BreedingManagerUtil.validateRequiredField(getWindow(), pedigreeOptionComboBox,
                messageSource, messageSource.getMessage(Message.PEDIGREE_OPTIONS_LABEL));
    }
    
    private boolean validateLocation() {
        return BreedingManagerUtil.validateRequiredField(getWindow(), germplasmFieldsComponent.getLocationComboBox(),
                messageSource, messageSource.getMessage(Message.GERMPLASM_LOCATION_LABEL));
    }
  
    private void updateTotalEntriesLabel(){
    	int count = germplasmDetailsTable.getItemIds().size();
		if(count == 0) {
			totalEntriesLabel.setValue(messageSource.getMessage(Message.NO_LISTDATA_RETRIEVED_LABEL));
		} else {
			totalEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LIST_ENTRIES) + ": " 
	        		 + "  <b>" + count + "</b>");
        }
    }
    
    public void backButtonClickAction(){
        source.backStep();
    }
    
    public GermplasmImportMain getSource() {
        return source;
    }
    
    public void setGermplasmBreedingMethod(String breedingMethod){
    	germplasmFieldsComponent.setGermplasmBreedingMethod(breedingMethod);
    }
    
    public void setGermplasmDate(Date germplasmDate) throws ReadOnlyException, ConversionException, ParseException{
        germplasmFieldsComponent.setGermplasmDate(germplasmDate);
    }
    public void setGermplasmLocation(String germplasmLocation){
        germplasmFieldsComponent.setGermplasmLocation(germplasmLocation);
    }
    public void setGermplasmListType(String germplasmListType){
        germplasmFieldsComponent.setGermplasmListType(germplasmListType);
    }

    protected void initializePedigreeOptions() {
		pedigreeOptionComboBox.addItem(1);
        pedigreeOptionComboBox.addItem(2);
        pedigreeOptionComboBox.addItem(3);
        pedigreeOptionComboBox.setItemCaption(1, messageSource.getMessage(Message.IMPORT_PEDIGREE_OPTION_ONE));
        pedigreeOptionComboBox.setItemCaption(2, messageSource.getMessage(Message.IMPORT_PEDIGREE_OPTION_TWO));
        pedigreeOptionComboBox.setItemCaption(3, messageSource.getMessage(Message.IMPORT_PEDIGREE_OPTION_THREE));
	}
    
    private void showFirstPedigreeOption(boolean visible){
    	Item firstOption = pedigreeOptionComboBox.getItem(1);
    	if (firstOption == null && visible){
    		pedigreeOptionComboBox.removeAllItems();
    		initializePedigreeOptions();
    	} else if (!visible){
    		pedigreeOptionComboBox.removeItem(1);
    	}
    }
    
    public Integer getPedigreeOptionGroupValue(){
    	return (Integer) pedigreeOptionComboBox.getValue();
    }
    
    public String getPedigreeOption(){
    	return pedigreeOptionComboBox.getValue().toString();
    }
    
    public List<GermplasmName> getGermplasmNameObjects(){
    	return processGermplasmAction.getGermplasmNameObjects();
    }

	@Override
	public void instantiateComponents() {
		germplasmFieldsComponent = new GermplasmFieldsComponent(200);
		
        reviewImportDetailsLabel = new Label(messageSource.getMessage(Message.GERMPLASM_DETAILS_LABEL).toUpperCase());
        reviewImportDetailsLabel.addStyleName(Bootstrap.Typography.H4.styleName());
        
        totalEntriesLabel = new Label("Total Entries: 0", Label.CONTENT_XHTML);
		
        germplasmDetailsTable = new Table();
        germplasmDetailsTable.addContainerProperty(1, Integer.class, null);
        germplasmDetailsTable.addContainerProperty(2, String.class, null);
        germplasmDetailsTable.addContainerProperty(3, String.class, null);
        germplasmDetailsTable.addContainerProperty(4, String.class, null);
        germplasmDetailsTable.addContainerProperty(5, Integer.class, null);
        germplasmDetailsTable.addContainerProperty(6, String.class, null);
        germplasmDetailsTable.setColumnCollapsingAllowed(true);
        germplasmDetailsTable.setColumnHeaders(new String[]{"Entry_No", "Entry_Code", "Designation", "Parentage", "GID", "Source"});
        germplasmDetailsTable.setHeight("200px");
        germplasmDetailsTable.setWidth("700px");
        
        selectPedigreeOptionsLabel = new Label(messageSource.getMessage(Message.SELECT_PEDIGREE_OPTIONS).toUpperCase());
        selectPedigreeOptionsLabel.addStyleName(Bootstrap.Typography.H4.styleName());
        
        pedigreeOptionsLabel = new Label(messageSource.getMessage(Message.PEDIGREE_OPTIONS_LABEL) + ":");
        pedigreeOptionsLabel.addStyleName(AppConstants.CssStyles.BOLD);
        pedigreeOptionsLabel.setWidth("250px");
        
        pedigreeOptionComboBox = new ComboBox();
        pedigreeOptionComboBox.setImmediate(true);
        pedigreeOptionComboBox.setRequired(true);
        pedigreeOptionComboBox.setWidth("450px");
        pedigreeOptionComboBox.setInputPrompt("Please Choose");
        
        automaticallyAcceptSingleMatchesCheckbox = new CheckBox(messageSource.getMessage(Message.AUTOMATICALLY_ACCEPT_SINGLE_MATCHES_WHENEVER_FOUND));
        automaticallyAcceptSingleMatchesCheckbox.setVisible(false);
        
        GermplasmImportButtonClickListener clickListener = new GermplasmImportButtonClickListener(this);
        
        backButton = new Button();
        backButton.setData(BACK_BUTTON_ID);
        backButton.addListener(clickListener);
        
        nextButton = new Button();
        nextButton.setData(NEXT_BUTTON_ID);
        nextButton.addListener(clickListener);
        nextButton.addStyleName(Bootstrap.Buttons.PRIMARY.styleName());
        
	}

	@Override
	public void initializeValues() {
        // 2nd section
        initializePedigreeOptions();
	}

	@Override
	public void addListeners() {
		processGermplasmAction = new ProcessImportedGermplasmAction(this);
		pedigreeOptionComboBox.addListener(new Property.ValueChangeListener(){

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(pedigreeOptionComboBox.getValue()!=null){
					if(pedigreeOptionComboBox.getValue().equals(3)){
						automaticallyAcceptSingleMatchesCheckbox.setVisible(true);
						automaticallyAcceptSingleMatchesCheckbox.setValue(false);
					} else {
						automaticallyAcceptSingleMatchesCheckbox.setVisible(false);
						automaticallyAcceptSingleMatchesCheckbox.setValue(false);
					}
				}
			}
			
		});
		
	}

	@Override
	public void layoutComponents() {
		setWidth("800px");
        
		// Review Import Details Layout
		VerticalLayout importDetailsLayout = new VerticalLayout();
		importDetailsLayout.setSpacing(true);
		importDetailsLayout.addComponent(reviewImportDetailsLabel);
		importDetailsLayout.addComponent(totalEntriesLabel);
		importDetailsLayout.addComponent(germplasmDetailsTable);
		
		
		// Pedigree Options Layout
		VerticalLayout pedigreeOptionsLayout = new VerticalLayout();
		pedigreeOptionsLayout.setSpacing(true);

		VerticalLayout pedigreeControlsLayoutVL = new VerticalLayout();
		pedigreeControlsLayoutVL.addComponent(pedigreeOptionComboBox);
		pedigreeControlsLayoutVL.addComponent(automaticallyAcceptSingleMatchesCheckbox);

		HorizontalLayout pedigreeControlsLayout = new HorizontalLayout();
		pedigreeControlsLayout.addComponent(pedigreeOptionsLabel);
		pedigreeControlsLayout.addComponent(pedigreeControlsLayoutVL);

		pedigreeOptionsLayout.addComponent(selectPedigreeOptionsLabel);
		pedigreeOptionsLayout.addComponent(pedigreeControlsLayout);
		
		// Buttons Layout
		HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidth("100%");
        buttonLayout.setHeight("40px");
        buttonLayout.setSpacing(true);
        
        buttonLayout.addComponent(backButton);
        buttonLayout.addComponent(nextButton);
        buttonLayout.setComponentAlignment(backButton, Alignment.BOTTOM_RIGHT);
        buttonLayout.setComponentAlignment(nextButton, Alignment.BOTTOM_LEFT);
		
        VerticalLayout spacerLayout = new VerticalLayout();
        spacerLayout.setHeight("30px");
        VerticalLayout spacerLayout2 = new VerticalLayout();
        spacerLayout2.setHeight("30px");
        
		addComponent(germplasmFieldsComponent);
		addComponent(importDetailsLayout);
		addComponent(spacerLayout);
		addComponent(pedigreeOptionsLayout);
		addComponent(spacerLayout2);
        addComponent(buttonLayout);
	}
	
	public void initializeFromImportFile(ImportedGermplasmList importedGermplasmList){
		//Clear table contents first (possible that it has some rows in it from previous uploads, and then user went back to upload screen)
		getGermplasmDetailsTable().removeAllItems();
        String source;
        for(int i = 0 ; i < importedGermplasms.size() ; i++){
            ImportedGermplasm importedGermplasm  = importedGermplasms.get(i);
            if(importedGermplasm.getSource()==null){
            	source = importedGermplasmList.getFilename()+":"+(i+1);
            }else{
            	source=importedGermplasm.getSource();
            }
            getGermplasmDetailsTable().addItem(new Object[]{importedGermplasm.getEntryId(), 
            		importedGermplasm.getEntryCode(),  importedGermplasm.getDesig(), importedGermplasm.getCross(), importedGermplasm.getGid(), source}, new Integer(i+1));
        }
        updateTotalEntriesLabel();

        if(germplasmListUploader.importFileIsAdvanced()){
        	showFirstPedigreeOption(false);
        } else {
        	showFirstPedigreeOption(true);
        }
	}

	@Override
	public void saveList(GermplasmList list) {
			
		SaveGermplasmListAction saveGermplasmListAction = new SaveGermplasmListAction();
		
		try {
			saveGermplasmListAction.saveRecords(list, getGermplasmNameObjects(), 
					germplasmListUploader.getOriginalFilename(), processGermplasmAction.getMatchedGermplasmIds(), importedGermplasms);
			
			MessageNotifier.showMessage(this.source.getWindow(), messageSource.getMessage(Message.SUCCESS), 
					messageSource.getMessage(Message.GERMPLASM_LIST_SAVED_SUCCESSFULLY), 3000);
			
			source.backStep();
			source.reset();
		} catch (MiddlewareQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void setCurrentlySavedGermplasmList(GermplasmList list) {
		this.germplasmList = list;
	}

	@Override
	public Component getParentComponent() {
		return source;
	}
	
	public GermplasmList getGermplasmList(){
		return germplasmList;
	}
	
	public void closeSaveListAsDialog(){
		if (saveListAsDialog != null){
			getWindow().removeWindow(saveListAsDialog);
		}
	}

	public Boolean automaticallyAcceptSingleMatchesCheckbox(){
		return (Boolean) automaticallyAcceptSingleMatchesCheckbox.getValue();
	}
	
}
