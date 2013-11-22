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

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.listmanager.listeners.GermplasmTreeExpandListener;
import org.generationcp.breeding.manager.listmanager.util.germplasm.GermplasmIndexContainer;
import org.generationcp.breeding.manager.listmanager.util.germplasm.GermplasmQueries;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;

@Configurable
public class GermplasmPedigreeTreeComponent extends Tree implements InitializingBean, InternationalizableComponent{

    private static final long serialVersionUID = 1L;
    
//    private ListManagerDetailsLayout detailsLayout;
    private GermplasmPedigreeTree germplasmPedigreeTree;
    private GermplasmQueries qQuery;
    private VerticalLayout mainLayout;
    private TabSheet tabSheet;
    private GermplasmIndexContainer dataIndexContainer;
    private Boolean includeDerivativeLines;
    @SuppressWarnings("unused")
    private final static Logger LOG = LoggerFactory.getLogger(GermplasmPedigreeTreeComponent.class);
    
    private Integer rootGid;

    @Autowired
    private SimpleResourceBundleMessageSource messageSource;

    public GermplasmPedigreeTreeComponent(int gid, GermplasmQueries qQuery, GermplasmIndexContainer dataResultIndexContainer,
            VerticalLayout mainLayout, TabSheet tabSheet) throws InternationalizableException {

        super();

        this.mainLayout = mainLayout;
        this.tabSheet = tabSheet;
        this.qQuery = qQuery;
        this.dataIndexContainer = dataResultIndexContainer;
        // this.gDetailModel = this.qQuery.getGermplasmDetails(gid);
        this.includeDerivativeLines = false;

        this.setSizeFull();
        germplasmPedigreeTree = qQuery.generatePedigreeTree(Integer.valueOf(gid), 1); // throws QueryException
        addNode(germplasmPedigreeTree.getRoot(), 1);
        this.setImmediate(false);
        
//        this.addListener(new GermplasmItemClickListener(this));
        this.addListener(new GermplasmTreeExpandListener(this));

        this.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

            private static final long serialVersionUID = 3442425534732855473L;

            @Override
            public String generateDescription(Component source, Object itemId, Object propertyId) {
                return messageSource.getMessage(Message.CLICK_TO_VIEW_GERMPLASM_DETAILS);
            }
        });
    }

    public GermplasmPedigreeTreeComponent(int gid, GermplasmQueries qQuery, GermplasmIndexContainer dataResultIndexContainer,
            VerticalLayout mainLayout, ListManagerDetailsLayout detailsLayout, Boolean includeDerivativeLines) throws InternationalizableException {

        super();

        this.mainLayout = mainLayout;
//        this.detailsLayout = detailsLayout;
//        this.tabSheet = tabSheet;
        
        this.qQuery = qQuery;
        this.dataIndexContainer = dataResultIndexContainer;
        // this.gDetailModel = this.qQuery.getGermplasmDetails(gid);
        this.includeDerivativeLines = includeDerivativeLines;

        this.setSizeFull();
        rootGid = Integer.valueOf(gid);
        germplasmPedigreeTree = qQuery.generatePedigreeTree(Integer.valueOf(gid), 1, includeDerivativeLines); // throws QueryException
        addNode(germplasmPedigreeTree.getRoot(), 1);
        this.setImmediate(false);

//        this.addListener(new SearchResultsItemClickListener(
//        		SearchResultsComponent.MATCHING_GEMRPLASMS_TABLE_DATA, this.detailsLayout));
        this.addListener(new GermplasmTreeExpandListener(this));

        this.setItemDescriptionGenerator(new AbstractSelect.ItemDescriptionGenerator() {

            private static final long serialVersionUID = 3442425534732855473L;

            @Override
            public String generateDescription(Component source, Object itemId, Object propertyId) {
                return messageSource.getMessage(Message.CLICK_TO_VIEW_GERMPLASM_DETAILS);
            }
        });
    }
   
    
    private void addNode(GermplasmPedigreeTreeNode node, int level) {
        if (level == 1) {
            String preferredName="";
            try{
                preferredName= node.getGermplasm().getPreferredName().getNval();
            }catch(Exception e){
                preferredName=String.valueOf(node.getGermplasm().getGid());
            }
            String leafNodeLabel = preferredName + "(" + node.getGermplasm().getGid() + ")";
            String leafNodeId = node.getGermplasm().getGid().toString();
            this.addItem(leafNodeId);
            this.setItemCaption(leafNodeId, leafNodeLabel);
            this.setParent(leafNodeId, leafNodeId);
            this.setChildrenAllowed(leafNodeId, true);
            // pedigreeTree.expandItemsRecursively(leafNode);
        }

        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
            String leafNodeId = node.getGermplasm().getGid().toString();
            String preferredName="";
            try{
                preferredName= parent.getGermplasm().getPreferredName().getNval();
            }catch(Exception e){
                preferredName=String.valueOf(parent.getGermplasm().getGid());
            }

            String parentNodeLabel = preferredName + "(" + parent.getGermplasm().getGid() + ")";
            String parentNodeId = node.getGermplasm().getGid() + "@" + parent.getGermplasm().getGid();
            this.addItem(parentNodeId);
            this.setItemCaption(parentNodeId, parentNodeLabel);
            this.setParent(parentNodeId, leafNodeId);
            this.setChildrenAllowed(parentNodeId, true);
            // pedigreeTree.expandItemsRecursively(parentNode);

            addNode(parent, level + 1);
        }
    }

    private void addNode(GermplasmPedigreeTreeNode node, String itemIdOfParent){
        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
            String leafNodeId = itemIdOfParent;
            String preferredName="";
            try{
                preferredName= parent.getGermplasm().getPreferredName().getNval();
            }catch(Exception e){
                preferredName=String.valueOf(parent.getGermplasm().getGid());
            }

            String parentNodeLabel = preferredName + "(" + parent.getGermplasm().getGid() + ")";
            String parentNodeId = node.getGermplasm().getGid() + "@" + parent.getGermplasm().getGid();
            this.addItem(parentNodeId);
            this.setItemCaption(parentNodeId, parentNodeLabel);
            this.setParent(parentNodeId, leafNodeId);
            this.setChildrenAllowed(parentNodeId, true);
        }
    }
    
    public void pedigreeTreeExpandAction(String itemId) throws InternationalizableException {
        if(itemId.contains("@")){
            String gidString = itemId.substring(itemId.indexOf("@")+1, itemId.length());
            germplasmPedigreeTree = qQuery.generatePedigreeTree(Integer.valueOf(gidString), 2, includeDerivativeLines);
            addNode(germplasmPedigreeTree.getRoot(), itemId);
        } else {
            germplasmPedigreeTree = qQuery.generatePedigreeTree(Integer.valueOf(itemId), 2, includeDerivativeLines);
            addNode(germplasmPedigreeTree.getRoot(), 2);
        }

    }

//    public void displayNewGermplasmDetailTab(int gid) throws InternationalizableException {
//        if(this.mainLayout != null && this.tabSheet != null) {
//            VerticalLayout detailLayout = new VerticalLayout();
//            detailLayout.setSpacing(true);
//    
//            if (!Util.isTabExist(tabSheet, String.valueOf(gid))) {
//                detailLayout.addComponent(new GermplasmDetail(gid, qQuery, dataIndexContainer, mainLayout, tabSheet, false));
//                Tab tab = tabSheet.addTab(detailLayout, String.valueOf(gid), null);
//                tab.setDescription(String.valueOf(gid));
//                tab.setClosable(true);
//                tabSheet.setSelectedTab(detailLayout);
//                mainLayout.addComponent(tabSheet);
//    
//            } else {
//                Tab tab = Util.getTabAlreadyExist(tabSheet, String.valueOf(gid));
//                tabSheet.setSelectedTab(tab.getComponent());
//            }
//        }
//    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void attach() {

        super.attach();

        updateLabels();
    }

    @Override
    public void updateLabels() {

    }

}