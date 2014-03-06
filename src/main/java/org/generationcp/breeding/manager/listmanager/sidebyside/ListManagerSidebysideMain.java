package org.generationcp.breeding.manager.listmanager.sidebyside;

import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalSplitPanel;

@Configurable
public class ListManagerSidebysideMain extends AbsoluteLayout implements
		InternationalizableComponent, InitializingBean, BreedingManagerLayout {

    private static final long serialVersionUID = 5976245899964745758L;
    private static final String VERSION = "1.0.0";
    
    private AbsoluteLayout titleLayout;
    private Label mainTitle;
    private Button buildNewListButton;
    public static final String BUILD_NEW_LIST_BUTTON_DATA = "Build new list";
    private static final ThemeResource ICON_PLUS = new ThemeResource("images/plus_icon.png");
    
    //For Main Tab 
    private HorizontalLayout tabHeaderLayout;
    private Button showBrowseListsButton;
    private Button showSearchListsButton;
    private Panel containerPanel;
    
    private HorizontalSplitPanel hSplitPanel;
    private VerticalSplitPanel vSplitPanel;
    
    //Layouts on every pane
    private HorizontalLayout searchBarLayout;
    private AbsoluteLayout browserSearchLayout;
    private HorizontalLayout buildListLayout;
    
    //Components on every pane
    private ListManagerSearchListBarComponent searchListsBarComponent;
    private ListManagerBrowseListComponent browseListsComponent;
    private ListManagerSearchListComponent searchListsComponent;
    
    
    private Button toggleBuildNewListButton;
	private static Float EXPANDED_SPLIT_POSITION_RIGHT = Float.valueOf(50); //actual width in pixel 650 
	private static Float COLLAPSED_SPLIT_POSITION_RIGHT = Float.valueOf(96); //actual width in pixel 50
	
	private static Float EXPANDED_SPLIT_POSITION_TOP = Float.valueOf(65); //actual width in pixel
	private static Float COLLAPSED_SPLIT_POSITION_TOP = Float.valueOf(0); //actual width in pixel
	
    @Autowired
    private SimpleResourceBundleMessageSource messageSource;
	
    @Override
    public void afterPropertiesSet() throws Exception {
    	
        instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
		
		collapseTop();
		collapseRight();
    }

	@Override
	public void updateLabels() {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("deprecation")
	@Override
	public void instantiateComponents() {
		setSizeFull(); 
		
		//title
		titleLayout = new AbsoluteLayout();
		
        setTitleContent("");
        setTabHeader();
        setContent();		
	}

	private void setTitleContent(String string) {
		titleLayout.removeAllComponents();
        titleLayout.setWidth("100%");
        titleLayout.setHeight("40px");
        
        //TODO put software version in title
        String title =  messageSource.getMessage(Message.LIST_MANAGER_SCREEN_LABEL)+ "  <h2>" + VERSION + "</h2>";
        mainTitle = new Label();
        mainTitle.setStyleName(Bootstrap.Typography.H1.styleName());
        mainTitle.setContentMode(Label.CONTENT_XHTML);
        mainTitle.setValue(title);
        
        buildNewListButton = new Button();
        buildNewListButton.setCaption(messageSource.getMessage(Message.START_A_NEW_LIST));
        buildNewListButton.setData(BUILD_NEW_LIST_BUTTON_DATA);
        buildNewListButton.setStyleName(Bootstrap.Buttons.INFO.styleName());
        buildNewListButton.setIcon(ICON_PLUS);
        
        titleLayout.addComponent(mainTitle,"top:0px;left:0px");
        titleLayout.addComponent(buildNewListButton,"top:10px;right:0px");
	}
	
	private void setTabHeader(){
        showBrowseListsButton = new Button(messageSource.getMessage(Message.BROWSE_LISTS));
        showSearchListsButton = new Button(messageSource.getMessage(Message.SEARCH_LISTS_AND_GERMPLASM));
        showBrowseListsButton.addStyleName("tabStyleButton");
        showSearchListsButton.addStyleName("tabStyleButton");
        showBrowseListsButton.setImmediate(true);
        showSearchListsButton.setImmediate(true);
        
        tabHeaderLayout = new HorizontalLayout();
        tabHeaderLayout.addStyleName("tabHeaderStyle");
        tabHeaderLayout.setSpacing(true);
        tabHeaderLayout.addComponent(showBrowseListsButton);
        tabHeaderLayout.addComponent(showSearchListsButton);
	}
	
	@SuppressWarnings("deprecation")
	private void setContent(){
		
		vSplitPanel = new VerticalSplitPanel();
		vSplitPanel.setWidth("100%");
		vSplitPanel.setHeight("600px");
		vSplitPanel.setMinSplitPosition(COLLAPSED_SPLIT_POSITION_TOP, Sizeable.UNITS_PIXELS);
		vSplitPanel.setMaxSplitPosition(EXPANDED_SPLIT_POSITION_TOP, Sizeable.UNITS_PIXELS);
		vSplitPanel.setImmediate(true);
		
		containerPanel = new Panel();
        containerPanel.setLayout(vSplitPanel);
		
		hSplitPanel = new HorizontalSplitPanel();
		hSplitPanel.setSizeFull();
		hSplitPanel.setMargin(false);
		hSplitPanel.setMaxSplitPosition(COLLAPSED_SPLIT_POSITION_RIGHT, Sizeable.UNITS_PERCENTAGE);
		hSplitPanel.setMinSplitPosition(EXPANDED_SPLIT_POSITION_RIGHT, Sizeable.UNITS_PERCENTAGE);
		hSplitPanel.setImmediate(true);
		
		// Top Pane
		searchListsBarComponent = new ListManagerSearchListBarComponent();
		searchBarLayout = new HorizontalLayout();
		searchBarLayout.setSizeFull();
		searchBarLayout.setMargin(true);
		searchBarLayout.addComponent(searchListsBarComponent);
		
        
		//Left Pane
        browseListsComponent = new ListManagerBrowseListComponent();
        searchListsComponent = new ListManagerSearchListComponent();
        browserSearchLayout = new AbsoluteLayout();
        browserSearchLayout.addStyleName("leftPane");
        browserSearchLayout.addComponent(browseListsComponent,"top:0px;left:0px");
        browserSearchLayout.addComponent(searchListsComponent,"top:0px;left:0px");
        
        //Right Pane
        toggleBuildNewListButton = new Button();
        toggleBuildNewListButton.setDescription("Toggle Build New List Pane");
        
		buildListLayout = new HorizontalLayout();
		buildListLayout.setSpacing(true);
		buildListLayout.addComponent(toggleBuildNewListButton);
		buildListLayout.addComponent(new BuildNewListComponentSidebyside());
		
		hSplitPanel.setFirstComponent(browserSearchLayout);
		hSplitPanel.setSecondComponent(buildListLayout);
		
		vSplitPanel.setFirstComponent(searchListsBarComponent);
		vSplitPanel.setSecondComponent(hSplitPanel);
	}

	@Override
	public void initializeValues() {
		browserSearchLayout.setWidth("100%");
        browserSearchLayout.setHeight("600px");
        searchListsComponent.setVisible(false);
		toggleBuildNewListButton.setCaption(">>");
	}

	@Override
	public void addListeners() {
		
		showBrowseListsButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				showBrowseListPane();
				collapseTop();
			}

		});
		
		showSearchListsButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				expandTop();
				showSearchListPane();
			}
		});
		
		toggleBuildNewListButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				if(hSplitPanel.getSplitPosition() == hSplitPanel.getMinSplitPosition()){
					collapseRight();
				} else {
					expandRight();
				}
			}
		});
		
		buildNewListButton.addListener(new ClickListener(){
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				expandRight();
			}
		});
	}

	protected void showSearchListPane() {
		browseListsComponent.setVisible(false);
		searchListsComponent.setVisible(true);
	}

	protected void showBrowseListPane() {
		browseListsComponent.setVisible(true);
		searchListsComponent.setVisible(false);
	}

	@Override
	public void layoutComponents() {
		addComponent(titleLayout,"top:10px; left:10px");
		addComponent(tabHeaderLayout,"top:50px;left:10px;");
		addComponent(containerPanel,"top:75px;left:10px;");
	}
	
    private void expandRight(){
    	hSplitPanel.setSplitPosition(EXPANDED_SPLIT_POSITION_RIGHT, Sizeable.UNITS_PERCENTAGE);
    	toggleBuildNewListButton.setCaption(">>");
    }

    private void collapseRight(){
    	hSplitPanel.setSplitPosition(COLLAPSED_SPLIT_POSITION_RIGHT, Sizeable.UNITS_PERCENTAGE);
    	toggleBuildNewListButton.setCaption("<<");
    }
    
    private void collapseTop(){
    	vSplitPanel.setSplitPosition(COLLAPSED_SPLIT_POSITION_TOP, Sizeable.UNITS_PIXELS);
    }
    
    private void expandTop(){
    	vSplitPanel.setSplitPosition(EXPANDED_SPLIT_POSITION_TOP, Sizeable.UNITS_PIXELS);
    }
}
