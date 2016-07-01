
package org.generationcp.breeding.manager.customfields;

import java.util.List;

import javax.annotation.Resource;

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
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.workbench.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.themes.BaseTheme;

@Configurable
public class BreedingLocationField extends AbsoluteLayout implements InitializingBean, InternationalizableComponent, BreedingManagerLayout {

	private static final long serialVersionUID = 4506866031376540836L;
	private static final Logger LOG = LoggerFactory.getLogger(BreedingLocationField.class);

	private Label captionLabel;
	private String caption;
	private ComboBox breedingLocationComboBox;
	private static final String DEFAULT_LOCATION = "Unknown";
	private boolean changed;
	private int leftIndentPixels = 130;

	private List<Location> locations;
	private CheckBox showFavoritesCheckBox;
	private OptionGroup breedingLocationsRadioBtn;
	private Button manageFavoritesLink;

	private Window attachToWindow;

	private Integer locationType = 0;

	// flags
	private boolean displayFavoriteMethodsFilter = true;
	private boolean displayManageMethodLink = true;

	private BreedingLocationFieldSource source;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private LocationDataManager locationDataManager;

	@Autowired
	private BreedingManagerService breedingManagerService;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	@Resource
	private ContextUtil contextUtil;

	private String programUniqueId;

	public BreedingLocationField() {

	}

	public BreedingLocationField(BreedingLocationFieldSource source) {
		this.source = source;
		this.caption = "Location: ";
		this.changed = false;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, Window attachToWindow) {
		this(source);
		this.attachToWindow = attachToWindow;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, Window attachToWindow, int pixels) {
		this(source, attachToWindow);
		this.leftIndentPixels = pixels;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, int pixels) {
		this.source = source;
		this.leftIndentPixels = pixels;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, Window attachToWindow, int pixels, Integer locationType) {
		this(source, attachToWindow);
		this.leftIndentPixels = pixels;
		this.locationType = locationType;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, int pixels, Integer locationType) {
		this.source = source;
		this.leftIndentPixels = pixels;
		this.locationType = locationType;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, Window attachToWindow, int pixels,
			boolean displayFavoriteMethodsFilter, boolean displayManageMethodLink, Integer locationType) {
		this(source, attachToWindow);
		this.leftIndentPixels = pixels;
		this.displayFavoriteMethodsFilter = displayFavoriteMethodsFilter;
		this.displayManageMethodLink = displayManageMethodLink;
		this.locationType = locationType;
	}

	public BreedingLocationField(BreedingLocationFieldSource source, int pixels, boolean displayFavoriteMethodsFilter,
			boolean displayManageMethodLink, Integer locationType) {
		this.source = source;
		this.leftIndentPixels = pixels;
		this.displayFavoriteMethodsFilter = displayFavoriteMethodsFilter;
		this.displayManageMethodLink = displayManageMethodLink;
		this.locationType = locationType;
	}

	@Override
	public void instantiateComponents() {
		this.captionLabel = new Label(this.caption);
		this.captionLabel.addStyleName("bold");

		this.breedingLocationComboBox = new ComboBox();
		this.breedingLocationComboBox.setWidth("320px");
		this.breedingLocationComboBox.setImmediate(true);
		this.breedingLocationComboBox.setNullSelectionAllowed(false);

		this.breedingLocationsRadioBtn = new OptionGroup();
		this.breedingLocationsRadioBtn.setMultiSelect(false);
		this.breedingLocationsRadioBtn.setImmediate(true);
		this.breedingLocationsRadioBtn.setStyleName("v-select-optiongroup-horizontal");
		this.breedingLocationsRadioBtn.addItem(this.messageSource.getMessage(Message.SHOW_ALL_LOCATIONS));
		this.breedingLocationsRadioBtn.addItem(this.messageSource.getMessage(Message.SHOW_STORAGE_LOCATIONS));
		this.breedingLocationsRadioBtn.select(this.messageSource.getMessage(Message.SHOW_STORAGE_LOCATIONS));

		this.showFavoritesCheckBox = new CheckBox();
		this.showFavoritesCheckBox.setCaption(this.messageSource.getMessage(Message.SHOW_ONLY_FAVORITE_LOCATIONS));
		this.showFavoritesCheckBox.setImmediate(true);

		this.manageFavoritesLink = new Button();
		this.manageFavoritesLink.setStyleName(BaseTheme.BUTTON_LINK);
		this.manageFavoritesLink.setCaption(this.messageSource.getMessage(Message.MANAGE_LOCATIONS));

		try {
			this.programUniqueId = this.breedingManagerService.getCurrentProject().getUniqueID();
		} catch (MiddlewareQueryException e) {
			BreedingLocationField.LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void initializeValues() {
		this.populateLocations(this.programUniqueId);
		this.initPopulateFavLocations(this.programUniqueId);
	}

	public boolean initPopulateFavLocations(String programUUID) {
		boolean hasFavorite = false;
		if (BreedingManagerUtil.hasFavoriteLocation(this.germplasmDataManager, 0, programUUID)) {
			this.showFavoritesCheckBox.setValue(true);
			this.populateHarvestLocation(true, this.programUniqueId);
			hasFavorite = true;
		}
		return hasFavorite;
	}

	private boolean isSelectAllLocations() {
		return ((String) this.breedingLocationsRadioBtn.getValue()).equals(this.messageSource.getMessage(Message.SHOW_ALL_LOCATIONS));
	}

	@Override
	public void addListeners() {

		this.breedingLocationComboBox.addListener(new ComboBox.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				BreedingLocationField.this.changed = true;
			}
		});

		this.breedingLocationComboBox.addListener(new ComboBox.ItemSetChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				BreedingLocationField.this.changed = true;
			}
		});

		Property.ValueChangeListener breedingLocationsListener = new Property.ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				BreedingLocationField.this.populateHarvestLocation(((Boolean) BreedingLocationField.this.showFavoritesCheckBox.getValue()),
						BreedingLocationField.this.programUniqueId);
			}
		};
		this.showFavoritesCheckBox.addListener(breedingLocationsListener);
		this.breedingLocationsRadioBtn.addListener(breedingLocationsListener);

		this.manageFavoritesLink.addListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				BreedingLocationField.this.launchManageWindow();
			}
		});

	}

	@Override
	public void layoutComponents() {
		if (this.displayManageMethodLink || this.displayFavoriteMethodsFilter) {
			this.setHeight("250px");
		} else {
			this.setHeight("190px");
		}

		this.addComponent(this.captionLabel, "top:3px; left:0;");
		this.addComponent(this.breedingLocationComboBox, "top:0; left:" + this.leftIndentPixels + "px");

		if (this.displayFavoriteMethodsFilter) {
			this.addComponent(this.breedingLocationsRadioBtn, "top:30px; left:" + this.leftIndentPixels + "px");
			this.addComponent(this.showFavoritesCheckBox, "top:52px; left:" + this.leftIndentPixels + "px");
		}

		if (this.displayManageMethodLink) {
			int pixels = this.leftIndentPixels + 230;
			this.addComponent(this.manageFavoritesLink, "top:33px; left:" + pixels + "px");
		}
	}

	@Override
	public void updateLabels() {
		// do nothing
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.instantiateComponents();
		this.initializeValues();
		this.addListeners();
		this.layoutComponents();
	}

	public ComboBox getBreedingLocationComboBox() {
		return this.breedingLocationComboBox;
	}

	public void setBreedingLocationComboBox(ComboBox breedingLocationComboBox) {
		this.breedingLocationComboBox = breedingLocationComboBox;
	}

	public void setValue(String value) {
		this.breedingLocationComboBox.select(value);
	}

	public String getValue() {
		return (String) this.breedingLocationComboBox.getValue();
	}

	public void validate() {
		this.breedingLocationComboBox.validate();
	}

	public boolean isChanged() {
		return this.changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	public void populateHarvestLocation(Integer selectedLocation, String programUUID) {
		this.populateHarvestLocation(this.showFavoritesCheckBox.getValue().equals(true), programUUID);
		if (selectedLocation != null) {
			this.breedingLocationComboBox.setValue(selectedLocation);
		}
	}

	private void populateHarvestLocation(boolean showOnlyFavorites, String programUUID) {
		this.breedingLocationComboBox.removeAllItems();

		if (showOnlyFavorites) {
			try {
				int ltype = 0;

				if (!isSelectAllLocations()) {
					// get favorites + storage locations (ltype = 1500)
					ltype = 1500;
				}

				BreedingManagerUtil.populateWithFavoriteLocations(this.workbenchDataManager, this.germplasmDataManager,
						this.breedingLocationComboBox, null, ltype, programUUID);

			} catch (MiddlewareQueryException e) {
				BreedingLocationField.LOG.error(e.getMessage(), e);
				MessageNotifier.showError(this.getWindow(), this.messageSource.getMessage(Message.ERROR),
						"Error getting favorite locations!");
			}

		} else {
			this.populateLocations(programUUID);
		}

	}

	/*
	 * Fill with all locations
	 */
	private void populateLocations(String programUUID) {

		try {
			if (isSelectAllLocations()) {
				this.locations = this.locationDataManager.getLocationsByUniqueID(programUUID);
			} else {
				this.locations = this.locationDataManager.getAllSeedingLocations(programUUID);
			}
		} catch (MiddlewareQueryException e) {
			BreedingLocationField.LOG.error(e.getMessage(), e);
		}

		Integer firstId = null;
		boolean hasDefault = false;
		for (Location location : this.locations) {
			if (firstId == null) {
				firstId = location.getLocid();
			}
			this.breedingLocationComboBox.addItem(location.getLocid());
			this.breedingLocationComboBox.setItemCaption(location.getLocid(), BreedingManagerUtil.getLocationNameDisplay(location));
			if (BreedingLocationField.DEFAULT_LOCATION.equalsIgnoreCase(location.getLname())) {
				this.breedingLocationComboBox.setValue(location.getLocid());
				hasDefault = true;
			}
		}
		if (!hasDefault && firstId != null) {
			this.breedingLocationComboBox.setValue(firstId);
		}
	}

	private void launchManageWindow() {
		try {
			Project project = this.contextUtil.getProjectInContext();

			Window window = this.attachToWindow != null ? this.attachToWindow : this.getWindow();
			Window manageFavoriteLocationsWindow =
					Util.launchLocationManager(this.workbenchDataManager, project.getProjectId(), window,
							this.messageSource.getMessage(Message.MANAGE_LOCATIONS));
			manageFavoriteLocationsWindow.addListener(new CloseListener() {

				private static final long serialVersionUID = 1L;

				@Override
				public void windowClose(CloseEvent e) {
					BreedingLocationField.this.source.updateAllLocationFields();
				}
			});
		} catch (MiddlewareQueryException e) {
			BreedingLocationField.LOG.error("Error on manageFavoriteLocations click", e);
		}
	}

	@Override
	public void setCaption(String caption) {
		this.caption = caption;
		if (this.captionLabel != null) {
			this.captionLabel.setValue(this.caption);
		}
	}

	protected int getLeftIndentPixels() {
		return this.leftIndentPixels;
	}

	public SimpleResourceBundleMessageSource getMessageSource() {
		return this.messageSource;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setWorkbenchDataManager(WorkbenchDataManager workbenchDataManager) {
		this.workbenchDataManager = workbenchDataManager;
	}

	public GermplasmDataManager getGermplasmDataManager() {
		return this.germplasmDataManager;
	}

	public void setGermplasmDataManager(GermplasmDataManager germplasmDataManager) {
		this.germplasmDataManager = germplasmDataManager;
	}

	public LocationDataManager getLocationDataManager() {
		return this.locationDataManager;
	}

	public void setLocationDataManager(LocationDataManager locationDataManager) {
		this.locationDataManager = locationDataManager;
	}

	public void setBreedingManagerService(BreedingManagerService breedingManagerService) {
		this.breedingManagerService = breedingManagerService;
	}
}
