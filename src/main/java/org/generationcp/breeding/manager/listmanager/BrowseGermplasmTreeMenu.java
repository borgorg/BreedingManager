package org.generationcp.breeding.manager.listmanager;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listimport.listeners.GidLinkClickListener;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmListManagerButtonClickListener;
import org.generationcp.breeding.manager.listmanager.util.germplasm.GermplasmIndexContainer;
import org.generationcp.breeding.manager.listmanager.util.germplasm.GermplasmQueries;
import org.generationcp.breeding.manager.util.GermplasmDetailModel;
import org.generationcp.breeding.manager.util.Util;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.ComponentTree;
import org.generationcp.commons.vaadin.ui.ComponentTreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Deprecated
@Configurable
public class BrowseGermplasmTreeMenu extends VerticalLayout implements
		InitializingBean, InternationalizableComponent {
	
	public static final String SAVE_TO_LIST = "Germplasm Details - Save to List";
	public static final String MORE_DETAILS = "Germplasm Details - More Details";

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(BrowseGermplasmTreeMenu.class);
	private static final long serialVersionUID = -6789065165873336884L;
	
	@Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
    private GermplasmQueries qQuery;
    private GermplasmDetailModel gDetailModel;
    private GermplasmHeaderInfoComponent basicDetailsComponent;
    private GermplasmPedigreeComponent pedigreeComponent;
    private GermplasmAttributesComponent germplasmAttributesComponent;
    private ListManagerMain listManagerMain;
    
    private Button saveToListLink;
    private Button moreDetailsLink;
    
    private Integer germplasmId;
    
    private ComponentTreeItem basicDetails;
    private ComponentTreeItem attributesDetails;
    private ComponentTreeItem pedigreeDetails;
    
    private static final ThemeResource ICON_PLUS = new ThemeResource("images/plus_icon.png");
    
    public BrowseGermplasmTreeMenu(ListManagerMain listManagerMain, Integer germplasmId){
    	this.listManagerMain = listManagerMain;
    	this.germplasmId = germplasmId;
    }


	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		qQuery = new GermplasmQueries();
		gDetailModel = qQuery.getGermplasmDetails(this.germplasmId);
		
		basicDetailsComponent = new GermplasmHeaderInfoComponent(gDetailModel);
		pedigreeComponent = new GermplasmPedigreeComponent(this.germplasmId);
		germplasmAttributesComponent = new GermplasmAttributesComponent(new GermplasmIndexContainer(qQuery), gDetailModel);
		
		ComponentTree content = new ComponentTree();
        content.setWidth("95%");
                
        basicDetails = content.addChild(createBasicDetailsHeader(messageSource.getMessage(Message.BASIC_DETAILS)));
        basicDetails.showChild();
        basicDetails.addChild(basicDetailsComponent);
        basicDetails.addListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if(event.getRelativeY()< ListManagerTreeMenu.TOGGABLE_Y_COORDINATE){
					basicDetails.toggleChild();
				}
			}
        });
        
        attributesDetails = content.addChild(ComponentTreeItem.createHeaderComponent(messageSource.getMessage(Message.ATTRIBUTES)));
        VerticalLayout layoutForAttributes = new VerticalLayout();
        layoutForAttributes.addComponent(germplasmAttributesComponent);
        attributesDetails.addChild(layoutForAttributes);
        attributesDetails.addListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if(event.getRelativeY()< ListManagerTreeMenu.TOGGABLE_Y_COORDINATE){
					attributesDetails.toggleChild();
				}
			}
        });
        
        pedigreeDetails = content.addChild(ComponentTreeItem.createHeaderComponent(messageSource.getMessage(Message.PEDIGREE_TREE)));
        pedigreeDetails.addChild(this.pedigreeComponent);
        pedigreeDetails.addListener(new LayoutClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void layoutClick(LayoutClickEvent event) {
				if(event.getRelativeY()< ListManagerTreeMenu.TOGGABLE_Y_COORDINATE){
					pedigreeDetails.toggleChild();
				}
			}
        });
		
		addComponent(content);
	}
	
	private Component createBasicDetailsHeader (String header) {
		HorizontalLayout mainLayout = new HorizontalLayout();
		mainLayout.setWidth("85%");
		mainLayout.setHeight("30px");
		
        CssLayout layout = new CssLayout();
        layout.setWidth("130px");
        
        Label l1 = new Label("<b>" + header + "</b>",Label.CONTENT_XHTML);
        l1.setStyleName(Bootstrap.Typography.H4.styleName());
        layout.addComponent(l1);
        
        saveToListLink = new Button(messageSource.getMessage(Message.SAVE_TO_LIST));
		saveToListLink.setData(SAVE_TO_LIST);
		saveToListLink.setImmediate(true);
		saveToListLink.setStyleName(Bootstrap.Buttons.INFO.styleName());
		saveToListLink.setIcon(ICON_PLUS);
		saveToListLink.addListener(new GermplasmListManagerButtonClickListener(this, this.germplasmId));
		
		moreDetailsLink = new Button(messageSource.getMessage(Message.MORE_DETAILS));
		moreDetailsLink.setData(MORE_DETAILS);
		moreDetailsLink.setImmediate(true);
		moreDetailsLink.setStyleName(BaseTheme.BUTTON_LINK);
		moreDetailsLink.addListener(new GidLinkClickListener(this.germplasmId.toString(), true));
        
		HorizontalLayout leftLayout = new HorizontalLayout();
		leftLayout.addComponent(layout);
		leftLayout.addComponent(saveToListLink);
		
        mainLayout.addComponent(leftLayout);
        mainLayout.addComponent(moreDetailsLink);
        mainLayout.setComponentAlignment(leftLayout, Alignment.BOTTOM_LEFT);
        mainLayout.setComponentAlignment(moreDetailsLink, Alignment.BOTTOM_RIGHT);

        return mainLayout;
	}
	
	public ListManagerMain getListManagerMain(){
		return this.listManagerMain;
	}

}
