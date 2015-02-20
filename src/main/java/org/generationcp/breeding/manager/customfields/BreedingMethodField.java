package org.generationcp.breeding.manager.customfields;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.service.BreedingManagerService;
import org.generationcp.breeding.manager.util.BreedingManagerUtil;
import org.generationcp.breeding.manager.util.Util;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configurable
public class BreedingMethodField extends AbsoluteLayout
		implements InitializingBean, InternationalizableComponent, BreedingManagerLayout {

	private static final long serialVersionUID = 4506866031376540836L;
	private final static Logger LOG = LoggerFactory.getLogger(BreedingMethodField.class);
	private static String DEFAULT_METHOD = "UDM";

	private Label captionLabel;
	private String caption;
	private ComboBox breedingMethodComboBox;
	private boolean isMandatory = true;
	private boolean hasDefaultValue = true;
	private boolean changed;
	private int leftIndentPixels = 130;

	private Map<String, String> methodMap;
	private List<Method> methods;
	private CheckBox showFavoritesCheckBox;
	private Button manageFavoritesLink;

	private Window attachToWindow;

	private Label methodDescription;
	private PopupView popup;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private BreedingManagerService breedingManagerService;

	@Resource
	private ContextUtil contextUtil;

	private String programUniqueId;

	public BreedingMethodField() {
		this.caption = "Breeding Method: ";
		this.changed = false;
	}

	public BreedingMethodField(Window attachToWindow) {
		this();
		this.attachToWindow = attachToWindow;
		this.isMandatory = true;
		this.hasDefaultValue = true;
	}

	public BreedingMethodField(Window attachToWindow, int pixels) {
		this();
		this.attachToWindow = attachToWindow;
		this.leftIndentPixels = pixels;
		this.isMandatory = true;
		this.hasDefaultValue = true;
	}

	public BreedingMethodField(int pixels) {
		this();
		this.leftIndentPixels = pixels;
		this.isMandatory = true;
		this.hasDefaultValue = true;
	}

	public BreedingMethodField(Window attachToWindow, boolean isMandatory,
			boolean hasDefaultValue) {
		this();
		this.attachToWindow = attachToWindow;
		this.isMandatory = isMandatory;
		this.hasDefaultValue = hasDefaultValue;
	}

	public BreedingMethodField(Window attachToWindow, int pixels, boolean isMandatory,
			boolean hasDefaultValue) {
		this();
		this.attachToWindow = attachToWindow;
		this.leftIndentPixels = pixels;
		this.isMandatory = isMandatory;
		this.hasDefaultValue = hasDefaultValue;
	}

	public BreedingMethodField(int pixels, boolean isMandatory, boolean hasDefaultValue) {
		this();
		this.leftIndentPixels = pixels;
		this.isMandatory = isMandatory;
		this.hasDefaultValue = hasDefaultValue;
	}

	@Override
	public void instantiateComponents() {
		setHeight("250px");

		captionLabel = new Label(caption);
		captionLabel.addStyleName("bold");

		breedingMethodComboBox = new ComboBox();
		breedingMethodComboBox.setWidth("320px");
		breedingMethodComboBox.setImmediate(true);

		if (isMandatory) {
			breedingMethodComboBox.setNullSelectionAllowed(false);
			breedingMethodComboBox.setRequired(true);
			breedingMethodComboBox.setRequiredError("Please specify the method.");
		} else {
			breedingMethodComboBox.setNullSelectionAllowed(true);
			breedingMethodComboBox.setInputPrompt("Please Choose");
		}

		showFavoritesCheckBox = new CheckBox();
		showFavoritesCheckBox
				.setCaption(messageSource.getMessage(Message.SHOW_ONLY_FAVORITE_METHODS));
		showFavoritesCheckBox.setImmediate(true);

		manageFavoritesLink = new Button();
		manageFavoritesLink.setStyleName(BaseTheme.BUTTON_LINK);
		manageFavoritesLink.setCaption(messageSource.getMessage(Message.MANAGE_METHODS));

		methodDescription = new Label();
		methodDescription.setWidth("300px");
		popup = new PopupView(" ? ", methodDescription);
		popup.setStyleName("gcp-popup-view");

		try {
			programUniqueId = breedingManagerService.getCurrentProject().getUniqueID();
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void initializeValues() {
		populateMethods(programUniqueId);
		enableMethodHelp(hasDefaultValue);
		initPopulateFavMethod(programUniqueId);
	}

	public boolean initPopulateFavMethod(String programUUID) {
		boolean hasFavorite = false;
		if (!hasDefaultValue && BreedingManagerUtil
				.hasFavoriteMethods(germplasmDataManager, programUUID)) {
			showFavoritesCheckBox.setValue(true);
			hasFavorite = true;
			populateMethods(true, programUniqueId);
		}
		return hasFavorite;
	}

	@Override
	public void addListeners() {

		breedingMethodComboBox.addListener(new ComboBox.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateComboBoxDescription();
				changed = true;
			}
		});

		breedingMethodComboBox.addListener(new ComboBox.ItemSetChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				updateComboBoxDescription();
				changed = true;
			}
		});

		showFavoritesCheckBox.addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				populateMethods(((Boolean) event.getProperty().getValue()).equals(true),
						programUniqueId);
				updateComboBoxDescription();
			}
		});

		manageFavoritesLink.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				launchManageWindow();
			}
		});

	}

	@Override
	public void layoutComponents() {
		addComponent(captionLabel, "top:3px; left:0;");
		addComponent(breedingMethodComboBox, "top:0; left:" + leftIndentPixels + "px");

		int pixels = leftIndentPixels + 325;
		addComponent(popup, "top:0; left:" + pixels + "px");

		addComponent(showFavoritesCheckBox, "top:30px; left:" + leftIndentPixels + "px");

		pixels = leftIndentPixels + 220;
		addComponent(manageFavoritesLink, "top:33px; left:" + pixels + "px");
	}

	@Override
	public void updateLabels() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();
	}

	public ComboBox getBreedingMethodComboBox() {
		return breedingMethodComboBox;
	}

	public void setBreedingMethodComboBox(ComboBox breedingMethodComboBox) {
		this.breedingMethodComboBox = breedingMethodComboBox;
	}

	public void setValue(String value) {
		breedingMethodComboBox.select(value);
	}

	public String getValue() {
		return (String) breedingMethodComboBox.getValue();
	}

	public void validate() throws InvalidValueException {
		breedingMethodComboBox.validate();
	}

	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	private void updateComboBoxDescription() {
		Object breedingMethodComboBoxValue = breedingMethodComboBox.getValue();

		final Boolean methodSelected = breedingMethodComboBoxValue != null;
		enableMethodHelp(methodSelected);

		if (methodSelected) {
			methodDescription.setValue(methodMap.get(breedingMethodComboBoxValue.toString()));
		}
	}

	private void enableMethodHelp(final Boolean enable) {
		methodDescription.setEnabled(enable);
		popup.setEnabled(enable);
	}

	private Map<String, String> populateMethods(String programUUID) {
		try {
			methods = germplasmDataManager.getMethodsByUniqueID(programUUID);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		if (methods == null) {
			methods = new ArrayList<Method>();
		}

		methodMap = new HashMap<String, String>();

		Method defaultMethod = null;
		for (Method method : methods) {
			breedingMethodComboBox.addItem(method.getMid());
			breedingMethodComboBox.setItemCaption(method.getMid(), method.getMname());

			if (DEFAULT_METHOD.equalsIgnoreCase(method.getMcode())) {
				defaultMethod = method;
			}

			methodMap.put(method.getMid().toString(), method.getMdesc());
		}

		if (hasDefaultValue) {
			if (defaultMethod != null) {
				breedingMethodComboBox.setValue(defaultMethod.getMid());
				methodDescription.setValue(defaultMethod.getMdesc());
			} else {
				//if the list of methods has no default method, just select the first item from the list
				if (breedingMethodComboBox.getValue() == null && methods.size() > 0
						&& methods.get(0) != null) {
					breedingMethodComboBox.setValue(methods.get(0).getMid());
					breedingMethodComboBox.setDescription(methods.get(0).getMdesc());
				}
			}
		}

		return methodMap;
	}

	private void populateMethods(boolean showOnlyFavorites, String programUUID) {
		breedingMethodComboBox.removeAllItems();
		if (showOnlyFavorites) {
			try {
				BreedingManagerUtil.populateWithFavoriteMethods(workbenchDataManager,
						germplasmDataManager, breedingMethodComboBox, null, programUUID);
			} catch (MiddlewareQueryException e) {
				LOG.error(e.getMessage(), e);
				MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.ERROR),
						"Error getting favorite methods!");
			}
		} else {
			populateMethods(programUUID);
		}

	}

	private void launchManageWindow() {
		try {
			Project project = contextUtil.getProjectInContext();
			Window window = attachToWindow != null ? attachToWindow : getWindow();
			Window manageFavoriteMethodsWindow = Util
					.launchMethodManager(workbenchDataManager, project.getProjectId(), window,
							messageSource.getMessage(Message.MANAGE_METHODS));
			manageFavoriteMethodsWindow.addListener(new CloseListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void windowClose(CloseEvent e) {
					Object lastValue = breedingMethodComboBox.getValue();
					populateMethods(((Boolean) showFavoritesCheckBox.getValue()).equals(true),
							programUniqueId);
					breedingMethodComboBox.setValue(lastValue);
				}
			});
		} catch (MiddlewareQueryException e) {
			LOG.error("Error on manageFavoriteMethods click", e);
		}
	}

	public void setCaption(String caption) {
		this.caption = caption;
		if (this.captionLabel != null) {
			this.captionLabel.setValue(this.caption);
		}
	}

	protected int getLeftIndentPixels() {
		return leftIndentPixels;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return germplasmDataManager;
	}

	public void setGermplasmDataManager(GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public boolean isHasDefaultValue() {
		return hasDefaultValue;
	}

	public void setHasDefaultValue(boolean hasDefaultValue) {
		this.hasDefaultValue = hasDefaultValue;
	}

	public void setBreedingManagerService(
			BreedingManagerService breedingManagerService) {
		this.breedingManagerService = breedingManagerService;
	}
}
