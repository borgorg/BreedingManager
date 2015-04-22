package org.generationcp.breeding.manager.listmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.breeding.manager.application.BreedingManagerApplication;
import org.generationcp.breeding.manager.application.BreedingManagerLayout;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.AppConstants;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.customcomponent.ActionButton;
import org.generationcp.breeding.manager.customcomponent.ExportListAsDialog;
import org.generationcp.breeding.manager.customcomponent.HeaderLabelLayout;
import org.generationcp.breeding.manager.customcomponent.IconButton;
import org.generationcp.breeding.manager.customcomponent.SaveListAsDialog;
import org.generationcp.breeding.manager.customcomponent.SaveListAsDialogSource;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.customcomponent.ViewListHeaderWindow;
import org.generationcp.breeding.manager.customcomponent.listinventory.ListManagerInventoryTable;
import org.generationcp.breeding.manager.inventory.ReservationStatusWindow;
import org.generationcp.breeding.manager.inventory.ReserveInventoryAction;
import org.generationcp.breeding.manager.inventory.ReserveInventorySource;
import org.generationcp.breeding.manager.inventory.ReserveInventoryUtil;
import org.generationcp.breeding.manager.inventory.ReserveInventoryWindow;
import org.generationcp.breeding.manager.listeners.InventoryLinkButtonClickListener;
import org.generationcp.breeding.manager.listmanager.dialog.AddEntryDialog;
import org.generationcp.breeding.manager.listmanager.dialog.AddEntryDialogSource;
import org.generationcp.breeding.manager.listmanager.dialog.ListManagerCopyToNewListDialog;
import org.generationcp.breeding.manager.listmanager.listeners.GidLinkButtonClickListener;
import org.generationcp.breeding.manager.listmanager.util.FillWith;
import org.generationcp.breeding.manager.listmanager.util.GermplasmListExporter;
import org.generationcp.breeding.manager.listmanager.util.ListCommonActionsUtil;
import org.generationcp.breeding.manager.listmanager.util.ListDataPropertiesRenderer;
import org.generationcp.breeding.manager.util.BreedingManagerUtil;
import org.generationcp.commons.constant.ColumnLabels;
import org.generationcp.commons.exceptions.GermplasmListExporterException;
import org.generationcp.commons.exceptions.InternationalizableException;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.FileDownloadResource;
import org.generationcp.commons.vaadin.spring.InternationalizableComponent;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.commons.vaadin.theme.Bootstrap;
import org.generationcp.commons.vaadin.ui.BaseSubWindow;
import org.generationcp.commons.vaadin.ui.ConfirmDialog;
import org.generationcp.commons.vaadin.util.MessageNotifier;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.BlurListener;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Reindeer;

@Configurable
public class ListComponent extends VerticalLayout implements InitializingBean,
		InternationalizableComponent, BreedingManagerLayout, AddEntryDialogSource,
		SaveListAsDialogSource, ReserveInventorySource {

	private static final long serialVersionUID = -3367108805414232721L;

	private static final Logger LOG = LoggerFactory.getLogger(ListComponent.class);

	// String Literals
	private static final String CLICK_TO_VIEW_INVENTORY_DETAILS = "Click to view Inventory Details";
	private static final String DATABASE_ERROR = "Database Error!";

	private static final int MINIMUM_WIDTH = 10;
	private final Map<Object, Map<Object, Field>> fields = new HashMap<Object, Map<Object, Field>>();

	private final ListManagerMain source;
	private final ListTabComponent parentListDetailsComponent;
	private GermplasmList germplasmList;
	private List<GermplasmListData> listEntries;
	private long listEntriesCount;
	private String designationOfListEntriesDeleted = "";

	private Label topLabel;
	private Button viewHeaderButton;
	private Label totalListEntriesLabel;
	private Label totalSelectedListEntriesLabel;
	private Button actionsButton;
	private Table listDataTable;
	private TableWithSelectAllLayout listDataTableWithSelectAll;

	private HorizontalLayout headerLayout;
	private HorizontalLayout subHeaderLayout;

	// Menu for tools button
	private ContextMenu menu;
	private ContextMenuItem menuExportList;
	private ContextMenuItem menuExportForGenotypingOrder;
	private ContextMenuItem menuCopyToList;
	private ContextMenuItem menuAddEntry;
	private ContextMenuItem menuSaveChanges;
	private ContextMenuItem menuDeleteEntries;
	private ContextMenuItem menuEditList;
	private ContextMenuItem menuDeleteList;
	@SuppressWarnings("unused")
	private ContextMenuItem menuInventoryView;
	private AddColumnContextMenu addColumnContextMenu;

	private ContextMenu inventoryViewMenu;
	private ContextMenuItem menuCopyToNewListFromInventory;
	private ContextMenuItem menuInventorySaveChanges;
	@SuppressWarnings("unused")
	private ContextMenuItem menuListView;
	@SuppressWarnings("unused")
	private ContextMenuItem menuReserveInventory;
	@SuppressWarnings("unused")
	private ContextMenuItem menuCancelReservation;

	// Tooltips
	public static final String TOOLS_BUTTON_ID = "Actions";
	public static final String LIST_DATA_COMPONENT_TABLE_DATA = "List Data Component Table";

	// this is true if this component is created by accessing the Germplasm List
	// Details page directly from the URL
	private boolean fromUrl;

	// Theme Resource
	private BaseSubWindow listManagerCopyToNewListDialog;
	private static final String USER_HOME = "user.home";

	private Object selectedColumn = "";
	private Object selectedItemId;
	private String lastCellvalue = "";
	private final List<Integer> gidsWithoutChildrenToDelete;
	private final Map<Object, String> itemsToDelete;

	private Button lockButton;
	private Button unlockButton;
	private Button editHeaderButton;

	private ViewListHeaderWindow viewListHeaderWindow;

	private HorizontalLayout toolsMenuContainer;

	private Button inventoryViewToolsButton;

	public static final String LOCK_BUTTON_ID = "Lock Germplasm List";
	public static final String UNLOCK_BUTTON_ID = "Unlock Germplasm List";

	private static final String LOCK_TOOLTIP = "Click to lock or unlock this germplasm list.";

	private ContextMenu tableContextMenu;

	@SuppressWarnings("unused")
	private ContextMenuItem tableContextMenuSelectAll;

	private ContextMenuItem tableContextMenuCopyToNewList;
	private ContextMenuItem tableContextMenuDeleteEntries;
	private ContextMenuItem tableContextMenuEditCell;

	// Value change event is fired when table is populated, so we need a flag
	private Boolean doneInitializing = false;

	// Inventory Related Variables
	private ListManagerInventoryTable listInventoryTable;
	private ReserveInventoryWindow reserveInventory;
	private ReservationStatusWindow reservationStatus;
	private ReserveInventoryUtil reserveInventoryUtil;
	private ReserveInventoryAction reserveInventoryAction;
	private Map<ListEntryLotDetails, Double> validReservationsToSave;
	private Boolean hasChanges;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	@Autowired
	private PedigreeDataManager pedigreeDataManager;

	@Autowired
	private InventoryDataManager inventoryDataManager;

	@Autowired
	private OntologyDataManager ontologyDataManager;

	@Resource
	private ContextUtil contextUtil;

	private Integer localUserId = null;

	private FillWith fillWith;

	private SaveListAsDialog dialog;

	public ListComponent(ListManagerMain source, ListTabComponent parentListDetailsComponent,
			GermplasmList germplasmList) {
		super();
		this.source = source;
		this.parentListDetailsComponent = parentListDetailsComponent;
		this.germplasmList = germplasmList;
		this.gidsWithoutChildrenToDelete = new ArrayList<Integer>();
		this.itemsToDelete = new HashMap<Object, String>();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instantiateComponents();
		initializeValues();
		addListeners();
		layoutComponents();

		if (source.getModeView().equals(ModeView.LIST_VIEW)) {
			changeToListView();
		} else if (source.getModeView().equals(ModeView.INVENTORY_VIEW)) {
			viewInventoryActionConfirmed();
		}
	}

	@Override
	public void instantiateComponents() {
		topLabel = new Label(messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
		topLabel.setWidth("120px");
		topLabel.setStyleName(Bootstrap.Typography.H4.styleName());

		viewListHeaderWindow = new ViewListHeaderWindow(germplasmList);

		viewHeaderButton = new Button(messageSource.getMessage(Message.VIEW_HEADER));
		viewHeaderButton.addStyleName(Reindeer.BUTTON_LINK);
		viewHeaderButton.setDescription(viewListHeaderWindow.getListHeaderComponent().toString());

		editHeaderButton = new IconButton(
				"<span class='glyphicon glyphicon-pencil' style='left: 2px; top:10px; color: #7c7c7c;font-size: 16px; font-weight: bold;'></span>",
				"Edit List Header");

		actionsButton = new ActionButton();
		actionsButton.setData(TOOLS_BUTTON_ID);

		inventoryViewToolsButton = new ActionButton();
		inventoryViewToolsButton.setData(TOOLS_BUTTON_ID);

		try {
			listEntriesCount = germplasmListManager.countGermplasmListDataByListId(germplasmList
					.getId());
		} catch (MiddlewareQueryException ex) {
			LOG.error(
					"Error with retrieving count of list entries for list: "
							+ germplasmList.getId(), ex);
			listEntriesCount = 0;
		}

		totalListEntriesLabel = new Label("", Label.CONTENT_XHTML);
		totalListEntriesLabel.setWidth("120px");

		if (listEntriesCount == 0) {
			totalListEntriesLabel.setValue(messageSource
					.getMessage(Message.NO_LISTDATA_RETRIEVED_LABEL));
		} else {
			updateNoOfEntries(listEntriesCount);
		}

		totalSelectedListEntriesLabel = new Label("", Label.CONTENT_XHTML);
		totalSelectedListEntriesLabel.setWidth("95px");
		updateNoOfSelectedEntries(0);

		unlockButton = new IconButton(
				"<span class='bms-locked' style='position: relative; top:5px; left: 2px; color: #666666;font-size: 16px; font-weight: bold;'></span>",
				LOCK_TOOLTIP);
		unlockButton.setData(UNLOCK_BUTTON_ID);

		lockButton = new IconButton(
				"<span class='bms-lock-open' style='position: relative; top:5px; left: 2px; left: 2px; color: #666666;font-size: 16px; font-weight: bold;'></span>",
				LOCK_TOOLTIP);
		lockButton.setData(LOCK_BUTTON_ID);

		menu = new ContextMenu();
		menu.setWidth("295px");

		// Add Column menu will be initialized after list data table is created
		initializeListDataTable(); // listDataTable
		initializeListInventoryTable(); // listInventoryTable

		// Generate main level items
		menuAddEntry = menu.addItem(messageSource.getMessage(Message.ADD_ENTRIES));
		menuCopyToList = menu.addItem(messageSource.getMessage(Message.COPY_TO_NEW_LIST));
		menuDeleteList = menu.addItem(messageSource.getMessage(Message.DELETE_LIST));
		menuDeleteEntries = menu.addItem(messageSource.getMessage(Message.DELETE_SELECTED_ENTRIES));
		menuEditList = menu.addItem(messageSource.getMessage(Message.EDIT_LIST));
		menuExportList = menu.addItem(messageSource.getMessage(Message.EXPORT_LIST));
		menuExportForGenotypingOrder = menu.addItem(messageSource
				.getMessage(Message.EXPORT_LIST_FOR_GENOTYPING_ORDER));
		menuInventoryView = menu.addItem(messageSource.getMessage(Message.INVENTORY_VIEW));
		menuSaveChanges = menu.addItem(messageSource.getMessage(Message.SAVE_CHANGES));
		menu.addItem(messageSource.getMessage(Message.SELECT_ALL));

		inventoryViewMenu = new ContextMenu();
		inventoryViewMenu.setWidth("295px");
		menuCancelReservation = inventoryViewMenu.addItem(messageSource
				.getMessage(Message.CANCEL_RESERVATIONS));
		menuCopyToNewListFromInventory = inventoryViewMenu.addItem(messageSource
				.getMessage(Message.COPY_TO_NEW_LIST));
		menuReserveInventory = inventoryViewMenu.addItem(messageSource
				.getMessage(Message.RESERVE_INVENTORY));
		menuListView = inventoryViewMenu.addItem(messageSource
				.getMessage(Message.RETURN_TO_LIST_VIEW));
		menuInventorySaveChanges = inventoryViewMenu.addItem(messageSource
				.getMessage(Message.SAVE_RESERVATIONS));
		inventoryViewMenu.addItem(messageSource.getMessage(Message.SELECT_ALL));

		resetInventoryMenuOptions();

		tableContextMenu = new ContextMenu();
		tableContextMenu.setWidth("295px");
		tableContextMenuSelectAll = tableContextMenu.addItem(messageSource
				.getMessage(Message.SELECT_ALL));
		tableContextMenuDeleteEntries = tableContextMenu.addItem(messageSource
				.getMessage(Message.DELETE_SELECTED_ENTRIES));
		tableContextMenuEditCell = tableContextMenu.addItem(messageSource
				.getMessage(Message.EDIT_VALUE));
		tableContextMenuCopyToNewList = tableContextMenu.addItem(messageSource
				.getMessage(Message.ADD_SELECTED_ENTRIES_TO_NEW_LIST));

		// Inventory Related Variables
		validReservationsToSave = new HashMap<ListEntryLotDetails, Double>();

		// Keep Track the changes in ListDataTable and/or ListInventoryTable
		hasChanges = false;

		// ListSelectionComponent is null when tool launched from BMS dashboard
		if (source != null && source.getListSelectionComponent() != null) {
			ListSelectionLayout listSelection = source.getListSelectionComponent()
					.getListDetailsLayout();
			listSelection.addUpdateListStatusForChanges(this, this.hasChanges);
		}
	}

	private void resetInventoryMenuOptions() {
		// disable the save button at first since there are no reservations yet
		menuInventorySaveChanges.setEnabled(false);

		// Temporarily disable to Copy to New List in InventoryView
		// implement the function
		menuCopyToNewListFromInventory.setEnabled(false);
	}

	protected void initializeListDataTable() {

		setListDataTableWithSelectAll(new TableWithSelectAllLayout(Long.valueOf(listEntriesCount)
				.intValue(), getNoOfEntries(), ColumnLabels.TAG.getName()));

		listDataTable = getListDataTableWithSelectAll().getTable();
		listDataTable.setSelectable(true);
		listDataTable.setMultiSelect(true);
		listDataTable.setColumnCollapsingAllowed(true);
		listDataTable.setWidth("100%");
		listDataTable.setDragMode(TableDragMode.ROW);
		listDataTable.setData(LIST_DATA_COMPONENT_TABLE_DATA);
		listDataTable.setColumnReorderingAllowed(false);
		listDataTable.setDebugId("vaadin-listdata-tbl");

		listDataTable.addContainerProperty(ColumnLabels.TAG.getName(), CheckBox.class,
				null);
		listDataTable.addContainerProperty(ColumnLabels.ENTRY_ID.getName(),
				Integer.class, null);
		listDataTable.addContainerProperty(ColumnLabels.DESIGNATION.getName(),
				Button.class, null);
		listDataTable.addContainerProperty(ColumnLabels.PARENTAGE.getName(),
				String.class, null);
		listDataTable.addContainerProperty(ColumnLabels.AVAILABLE_INVENTORY.getName(),
				Button.class, null);
		listDataTable.addContainerProperty(ColumnLabels.SEED_RESERVATION.getName(),
				String.class, null);
		listDataTable.addContainerProperty(ColumnLabels.ENTRY_CODE.getName(),
				String.class, null);
		listDataTable.addContainerProperty(ColumnLabels.GID.getName(), Button.class,
				null);
		listDataTable.addContainerProperty(ColumnLabels.STOCKID.getName(), String.class,
				null);
		listDataTable.addContainerProperty(ColumnLabels.SEED_SOURCE.getName(),
				String.class, null);

		listDataTable.setColumnHeader(ColumnLabels.TAG.getName(),
				messageSource.getMessage(Message.CHECK_ICON));
		listDataTable.setColumnHeader(ColumnLabels.ENTRY_ID.getName(),
				messageSource.getMessage(Message.HASHTAG));
		listDataTable.setColumnHeader(ColumnLabels.DESIGNATION.getName(),
				getTermNameFromOntology(ColumnLabels.DESIGNATION));
		listDataTable.setColumnHeader(ColumnLabels.PARENTAGE.getName(),
				getTermNameFromOntology(ColumnLabels.PARENTAGE));
		listDataTable.setColumnHeader(ColumnLabels.AVAILABLE_INVENTORY.getName(),
				getTermNameFromOntology(ColumnLabels.AVAILABLE_INVENTORY));
		listDataTable.setColumnHeader(ColumnLabels.SEED_RESERVATION.getName(),
				getTermNameFromOntology(ColumnLabels.SEED_RESERVATION));
		listDataTable.setColumnHeader(ColumnLabels.ENTRY_CODE.getName(),
				getTermNameFromOntology(ColumnLabels.ENTRY_CODE));
		listDataTable.setColumnHeader(ColumnLabels.GID.getName(),
				getTermNameFromOntology(ColumnLabels.GID));
		listDataTable.setColumnHeader(ColumnLabels.STOCKID.getName(),
				getTermNameFromOntology(ColumnLabels.STOCKID));
		listDataTable.setColumnHeader(ColumnLabels.SEED_SOURCE.getName(),
				getTermNameFromOntology(ColumnLabels.SEED_SOURCE));

		initializeAddColumnContextMenu();

	}

	protected void initializeAddColumnContextMenu() {
		addColumnContextMenu = new AddColumnContextMenu(parentListDetailsComponent, menu,
				listDataTable, ColumnLabels.GID.getName());
	}

	public void initializeListInventoryTable() {
		listInventoryTable = new ListManagerInventoryTable(source, germplasmList.getId(), true,
				false);
		listInventoryTable.setVisible(false);
	}

	public int getNoOfEntries() {
		// browse list component is null at this point when tool launched from
		// Workbench dashboard
		ListSelectionComponent browseListsComponent = source.getListSelectionComponent();
		if (browseListsComponent == null || browseListsComponent.isVisible()) {
			return 8;
		}

		return 18;
	}

	@Override
	public void initializeValues() {

		try {
			localUserId = contextUtil.getCurrentUserLocalId();
		} catch (MiddlewareQueryException e) {
			LOG.error("Error with retrieving local user ID", e);
			LOG.error("\n" + e.getStackTrace());
		}

		loadEntriesToListDataTable();
	}

	public void resetListDataTableValues() {
		listDataTable.setEditable(false);
		listDataTable.removeAllItems();

		loadEntriesToListDataTable();

		listDataTable.refreshRowCache();
		listDataTable.setImmediate(true);
		listDataTable.setEditable(true);
		listDataTable.requestRepaint();
	}

	public void loadEntriesToListDataTable() {
		if (listEntriesCount > 0) {
			listEntries = new ArrayList<GermplasmListData>();

			getAllListEntries();

			for (GermplasmListData entry : listEntries) {
				addListEntryToTable(entry);
			}

			listDataTable.sort(new Object[] { ColumnLabels.ENTRY_ID.getName() },
					new boolean[] { true });

			// render additional columns
			ListDataPropertiesRenderer newColumnsRenderer = new ListDataPropertiesRenderer(
					germplasmList.getId(), listDataTable);
			try {
				newColumnsRenderer.render();
			} catch (MiddlewareQueryException ex) {
				LOG.error("Error with displaying added columns for entries of list: "
						+ germplasmList.getId(), ex);
			}

		}

	}

	private void addListEntryToTable(GermplasmListData entry) {
		String gid = String.format("%s", entry.getGid().toString());
		Button gidButton = new Button(gid, new GidLinkButtonClickListener(source, gid, true, true));
		gidButton.setStyleName(BaseTheme.BUTTON_LINK);
		gidButton.setDescription("Click to view Germplasm information");

		Button desigButton = new Button(entry.getDesignation(), new GidLinkButtonClickListener(
				source, gid, true, true));
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
				if (((Boolean) itemCheckBox.getValue()).equals(true)) {
					listDataTable.select(itemCheckBox.getData());
				} else {
					listDataTable.unselect(itemCheckBox.getData());
				}
			}

		});

		Item newItem = listDataTable.getContainerDataSource().addItem(entry.getId());
		newItem.getItemProperty(ColumnLabels.TAG.getName()).setValue(itemCheckBox);
		newItem.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(
				entry.getEntryId());
		newItem.getItemProperty(ColumnLabels.DESIGNATION.getName())
				.setValue(desigButton);
		newItem.getItemProperty(ColumnLabels.PARENTAGE.getName()).setValue(
				entry.getGroupName());
		newItem.getItemProperty(ColumnLabels.ENTRY_CODE.getName()).setValue(
				entry.getEntryCode());
		newItem.getItemProperty(ColumnLabels.GID.getName()).setValue(gidButton);
		newItem.getItemProperty(ColumnLabels.SEED_SOURCE.getName()).setValue(
				entry.getSeedSource());

		// Inventory Related Columns

		// #1 Available Inventory
		// default value
		String availInv = "-";
		if (entry.getInventoryInfo().getLotCount().intValue() != 0) {
			availInv = entry.getInventoryInfo().getActualInventoryLotCount().toString().trim();
		}
		Button inventoryButton = new Button(availInv, new InventoryLinkButtonClickListener(
				parentListDetailsComponent, germplasmList.getId(), entry.getId(), entry.getGid()));
		inventoryButton.setStyleName(BaseTheme.BUTTON_LINK);
		inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
		newItem.getItemProperty(ColumnLabels.AVAILABLE_INVENTORY.getName()).setValue(
				inventoryButton);

		if ("-".equals(availInv)) {
			inventoryButton.setEnabled(false);
			inventoryButton.setDescription("No Lot for this Germplasm");
		} else {
			inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
		}

		// #2 Seed Reserved
		// default value
		String seedRes = "-";
		if (entry.getInventoryInfo().getReservedLotCount().intValue() != 0) {
			seedRes = entry.getInventoryInfo().getReservedLotCount().toString().trim();
		}
		newItem.getItemProperty(ColumnLabels.SEED_RESERVATION.getName()).setValue(
				seedRes);
		
		newItem.getItemProperty(ColumnLabels.STOCKID.getName()).setValue(entry.getInventoryInfo().getStockIDs());
		
	}

	private void getAllListEntries() {
		List<GermplasmListData> entries = null;
		try {
			entries = inventoryDataManager.getLotCountsForList(germplasmList.getId(), 0, Long
					.valueOf(listEntriesCount).intValue());

			listEntries.addAll(entries);
		} catch (MiddlewareQueryException ex) {
			LOG.error("Error with retrieving list entries for list: " + germplasmList.getId(), ex);
			listEntries = new ArrayList<GermplasmListData>();
		}
	}

	@Override
	public void addListeners() {
		viewHeaderButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 329434322390122057L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				openViewListHeaderWindow();
			}
		});

		setFillWith();

		makeTableEditable();

		actionsButton.addListener(new ToolsButtonClickListener());

		inventoryViewToolsButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 272707576878821700L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				inventoryViewMenu.show(event.getClientX(), event.getClientY());
			}
		});

		menu.addListener(new MenuClickListener());

		inventoryViewMenu.addListener(new InventoryViewMenuClickListner());

		editHeaderButton.addListener(new ClickListener() {
			private static final long serialVersionUID = -788407324474054727L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				openSaveListAsDialog();
			}
		});

		lockButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				lockGermplasmList(germplasmList);
			}
		});

		unlockButton.addListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				unlockGermplasmList();
			}
		});

		tableContextMenu.addListener(new TableContextMenuClickListener());

		getListDataTableWithSelectAll().getTable().addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateNoOfSelectedEntries();
			}
		});

		listInventoryTable.getTable().addListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				updateNoOfSelectedEntries();
			}
		});

	}

	private void setFillWith() {
		if (!germplasmList.isLockedList()) {
			fillWith = new FillWith(parentListDetailsComponent, parentListDetailsComponent,
					messageSource, listDataTable, ColumnLabels.GID.getName());
		} else {
			fillWith = null;
		}
	}

	@Override
	public void layoutComponents() {
		headerLayout = new HorizontalLayout();
		headerLayout.setWidth("100%");
		headerLayout.setSpacing(true);

		HeaderLabelLayout headingLayout = new HeaderLabelLayout(AppConstants.Icons.ICON_LIST_TYPES,
				topLabel);
		headerLayout.addComponent(headingLayout);
		headerLayout.addComponent(viewHeaderButton);
		headerLayout.setComponentAlignment(viewHeaderButton, Alignment.BOTTOM_RIGHT);

		headerLayout.addComponent(editHeaderButton);
		headerLayout.setComponentAlignment(editHeaderButton, Alignment.BOTTOM_LEFT);

		if (localUserIsListOwner()) {
			headerLayout.addComponent(lockButton);
			headerLayout.setComponentAlignment(lockButton, Alignment.BOTTOM_LEFT);

			headerLayout.addComponent(unlockButton);
			headerLayout.setComponentAlignment(unlockButton, Alignment.BOTTOM_LEFT);
		}

		setLockedState(germplasmList.isLockedList());

		headerLayout.setExpandRatio(headingLayout, 1.0f);

		toolsMenuContainer = new HorizontalLayout();
		toolsMenuContainer.setWidth("90px");
		toolsMenuContainer.setHeight("27px");
		toolsMenuContainer.addComponent(actionsButton);

		HorizontalLayout leftSubHeaderLayout = new HorizontalLayout();
		leftSubHeaderLayout.setSpacing(true);
		leftSubHeaderLayout.addComponent(totalListEntriesLabel);
		leftSubHeaderLayout.addComponent(totalSelectedListEntriesLabel);
		leftSubHeaderLayout.setComponentAlignment(totalListEntriesLabel, Alignment.MIDDLE_LEFT);
		leftSubHeaderLayout.setComponentAlignment(totalSelectedListEntriesLabel,
				Alignment.MIDDLE_LEFT);

		subHeaderLayout = new HorizontalLayout();
		subHeaderLayout.setWidth("100%");
		subHeaderLayout.setSpacing(true);
		subHeaderLayout.addStyleName("lm-list-desc");
		subHeaderLayout.addComponent(leftSubHeaderLayout);
		subHeaderLayout.addComponent(toolsMenuContainer);
		subHeaderLayout.setComponentAlignment(leftSubHeaderLayout, Alignment.MIDDLE_LEFT);
		subHeaderLayout.setComponentAlignment(toolsMenuContainer, Alignment.MIDDLE_RIGHT);

		addComponent(headerLayout);
		addComponent(subHeaderLayout);

		listDataTable.setHeight("480px");

		addComponent(getListDataTableWithSelectAll());
		addComponent(listInventoryTable);
		addComponent(tableContextMenu);

		parentListDetailsComponent.addComponent(menu);
		parentListDetailsComponent.addComponent(inventoryViewMenu);
	}

	@Override
	public void updateLabels() {
		// not yet implemented
	}

	private boolean localUserIsListOwner() {
		return germplasmList.getUserId().equals(localUserId);
	}

	public void makeTableEditable() {

		listDataTable.addListener(new ListDataTableItemClickListener());

		listDataTable.setTableFieldFactory(new ListDataTableFieldFactory());

		listDataTable.setEditable(true);
	}

	private final class ListDataTableFieldFactory implements TableFieldFactory {
		private static final long serialVersionUID = 1L;

		@Override
		public Field createField(Container container, final Object itemId,
				final Object propertyId, Component uiContext) {

			if (propertyId.equals(ColumnLabels.GID.getName())
					|| propertyId.equals(ColumnLabels.ENTRY_ID.getName())
					|| propertyId.equals(ColumnLabels.DESIGNATION.getName())
					|| propertyId.equals(ColumnLabels.AVAILABLE_INVENTORY.getName())
					|| propertyId.equals(ColumnLabels.SEED_RESERVATION.getName())) {
				return null;
			}

			final TextField tf = new TextField();
			tf.setData(new ItemPropertyId(itemId, propertyId));

			// set the size of textfield based on text of cell
			String value = (String) container.getItem(itemId).getItemProperty(propertyId)
					.getValue();
			Double d = computeTextFieldWidth(value);
			tf.setWidth(d.floatValue(), UNITS_EM);

			// Needed for the generated column
			tf.setImmediate(true);

			// Manage the field in the field storage
			Map<Object, Field> itemMap = fields.get(itemId);
			if (itemMap == null) {
				itemMap = new HashMap<Object, Field>();
				fields.put(itemId, itemMap);
			}
			itemMap.put(propertyId, tf);

			tf.setReadOnly(true);

			tf.addListener(new FocusListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void focus(FocusEvent event) {
					listDataTable.select(itemId);
				}
			});

			tf.addListener(new FocusListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void focus(FocusEvent event) {
					lastCellvalue = ((TextField) event.getComponent()).getValue().toString();
				}
			});

			tf.addListener(new TextFieldBlurListener(tf, itemId));
			// this area can be used for validation
			tf.addListener(new Property.ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					Double d = computeTextFieldWidth(tf.getValue().toString());
					tf.setWidth(d.floatValue(), UNITS_EM);
					tf.setReadOnly(true);

					if (doneInitializing && !tf.getValue().toString().equals(lastCellvalue)) {
						setHasUnsavedChanges(true);
					}
				}
			});

			tf.addShortcutListener(new ShortcutListener("ENTER", ShortcutAction.KeyCode.ENTER,
					null) {
				private static final long serialVersionUID = 1L;

				@Override
				public void handleAction(Object sender, Object target) {
					Double d = computeTextFieldWidth(tf.getValue().toString());
					tf.setWidth(d.floatValue(), UNITS_EM);
					tf.setReadOnly(true);
					listDataTable.focus();

				}
			});

			return tf;
		}

		private Double computeTextFieldWidth(String value) {
			double multiplier = 0.55;
			int length = 1;
			if (value != null && !value.isEmpty()) {
				length = value.length();
				if (value.equalsIgnoreCase(value)) {
					// if all caps, provide bigger space
					multiplier = 0.75;
				}
			}
			Double d = length * multiplier;
			// set a minimum textfield width
			return NumberUtils.max(new double[] { MINIMUM_WIDTH, d });
		}
	}

	private final class InventoryViewMenuClickListner implements ContextMenu.ClickListener {
		private static final long serialVersionUID = -2343109406180457070L;

		@Override
		public void contextItemClick(ClickEvent event) {
			// Get reference to clicked item
			ContextMenuItem clickedItem = event.getClickedItem();
			if (clickedItem.getName().equals(
					messageSource.getMessage(Message.SAVE_RESERVATIONS))) {
				saveReservationChangesAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.RETURN_TO_LIST_VIEW))) {
				viewListAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.COPY_TO_NEW_LIST))) {
				copyToNewListFromInventoryViewAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.RESERVE_INVENTORY))) {
				reserveInventoryAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.SELECT_ALL))) {
				listInventoryTable.getTable().setValue(
						listInventoryTable.getTable().getItemIds());
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.CANCEL_RESERVATIONS))) {
				cancelReservationsAction();
			}
		}
	}

	private final class MenuClickListener implements ContextMenu.ClickListener {
		private static final long serialVersionUID = -2343109406180457070L;

		@Override
		public void contextItemClick(ClickEvent event) {
			// Get reference to clicked item
			ContextMenuItem clickedItem = event.getClickedItem();
			if (clickedItem.getName().equals(messageSource.getMessage(Message.SELECT_ALL))) {
				listDataTable.setValue(listDataTable.getItemIds());
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.EXPORT_LIST))) {
				exportListAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.EXPORT_LIST_FOR_GENOTYPING_ORDER))) {
				exportListForGenotypingOrderAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.COPY_TO_NEW_LIST))) {
				copyToNewListAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.ADD_ENTRIES))) {
				addEntryButtonClickAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.SAVE_CHANGES))) {
				saveChangesAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.DELETE_SELECTED_ENTRIES))) {
				deleteEntriesButtonClickAction();
			} else if (clickedItem.getName()
					.equals(messageSource.getMessage(Message.EDIT_LIST))) {
				editListButtonClickAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.DELETE_LIST))) {
				deleteListButtonClickAction();
			} else if (clickedItem.getName().equals(
					messageSource.getMessage(Message.INVENTORY_VIEW))) {
				viewInventoryAction();
			}

		}
	}

	private final class ToolsButtonClickListener implements ClickListener {
		private static final long serialVersionUID = 272707576878821700L;

		@Override
		public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
			addColumnContextMenu.refreshAddColumnMenu();
			menu.show(event.getClientX(), event.getClientY());

			if (fromUrl) {
				menuExportForGenotypingOrder.setVisible(false);
				menuExportList.setVisible(false);
				menuCopyToList.setVisible(false);
			}

			if (source != null) {
				menuCopyToList.setVisible(!source.listBuilderIsLocked());
			}

			// when the Germplasm List is not locked, and when not accessed
			// directly from URL or popup window
			if (!germplasmList.isLockedList() && !fromUrl) {
				menuEditList.setVisible(true);
				// show only Delete List when user is owner
				menuDeleteList.setVisible(localUserIsListOwner());
				menuDeleteEntries.setVisible(true);
				menuSaveChanges.setVisible(true);
				menuAddEntry.setVisible(true);
				addColumnContextMenu.showHideAddColumnMenu(true);
			} else {
				menuEditList.setVisible(false);
				menuDeleteList.setVisible(false);
				menuDeleteEntries.setVisible(false);
				menuSaveChanges.setVisible(false);
				menuAddEntry.setVisible(false);
				addColumnContextMenu.showHideAddColumnMenu(false);
			}

		}
	}

	private final class ListDataTableItemClickListener implements ItemClickListener {
		private static final long serialVersionUID = 1L;

		@Override
		public void itemClick(ItemClickEvent event) {
			selectedColumn = event.getPropertyId();
			selectedItemId = event.getItemId();

			if (event.getButton() == ItemClickEvent.BUTTON_RIGHT) {

				tableContextMenu.show(event.getClientX(), event.getClientY());

				if (selectedColumn.equals(ColumnLabels.TAG.getName())
						|| selectedColumn.equals(ColumnLabels.GID.getName())
						|| selectedColumn.equals(ColumnLabels.ENTRY_ID.getName())
						|| selectedColumn.equals(ColumnLabels.DESIGNATION.getName())
						|| selectedColumn.equals(ColumnLabels.SEED_RESERVATION
						.getName())
						|| selectedColumn.equals(ColumnLabels.AVAILABLE_INVENTORY
						.getName())) {
					tableContextMenuDeleteEntries.setVisible(!germplasmList.isLockedList());
					tableContextMenuEditCell.setVisible(false);
					if (source != null) {
						tableContextMenuCopyToNewList.setVisible(!source.listBuilderIsLocked());
					}
				} else if (!germplasmList.isLockedList()) {
					tableContextMenuDeleteEntries.setVisible(true);
					tableContextMenuEditCell.setVisible(true);
					if (source != null) {
						tableContextMenuCopyToNewList.setVisible(!source.listBuilderIsLocked());
					}
					doneInitializing = true;
				} else {
					tableContextMenuDeleteEntries.setVisible(false);
					tableContextMenuEditCell.setVisible(false);
					if (source != null) {
						tableContextMenuCopyToNewList.setVisible(!source.listBuilderIsLocked());
					}
				}
			}
		}
	}

	private final class TableContextMenuClickListener implements ContextMenu.ClickListener {
		private static final long serialVersionUID = -2343109406180457070L;

		public void contextItemClick(ClickEvent event) {
			String action = event.getClickedItem().getName();
			if (action.equals(messageSource.getMessage(Message.DELETE_SELECTED_ENTRIES))) {
				deleteEntriesButtonClickAction();
			} else if (action.equals(messageSource.getMessage(Message.SELECT_ALL))) {
				listDataTable.setValue(listDataTable.getItemIds());
			} else if (action.equals(messageSource.getMessage(Message.EDIT_VALUE))) {

				Map<Object, Field> itemMap = fields.get(selectedItemId);

				// go through each field, set previous edited fields to
				// blurred/readonly
				for (Map.Entry<Object, Field> entry : itemMap.entrySet()) {
					Field f = entry.getValue();
					Object fieldValue = f.getValue();
					if (!f.isReadOnly()) {
						f.setReadOnly(true);

						if (!fieldValue.equals(lastCellvalue)) {
							setHasUnsavedChanges(true);
						}
					}
				}

				// Make the entire item editable

				if (itemMap != null) {
					for (Map.Entry<Object, Field> entry : itemMap.entrySet()) {
						Object column = entry.getKey();
						if (column.equals(selectedColumn)) {
							Field f = entry.getValue();
							if (f.isReadOnly()) {
								Object fieldValue = f.getValue();
								lastCellvalue = (fieldValue != null) ? fieldValue.toString()
										: "";
								f.setReadOnly(false);
								f.focus();
							}
						}
					}
				}

				listDataTable.select(selectedItemId);
			} else if (action.equals(messageSource
					.getMessage(Message.ADD_SELECTED_ENTRIES_TO_NEW_LIST))) {
				source.addSelectedPlantsToList(listDataTable);
			}
		}
	}

	private final class TextFieldBlurListener implements BlurListener {
		private final TextField tf;
		private final Object itemId;
		private static final long serialVersionUID = 1L;

		private TextFieldBlurListener(TextField tf, Object itemId) {
			this.tf = tf;
			this.itemId = itemId;
		}

		@Override
		public void blur(BlurEvent event) {
			Map<Object, Field> itemMap = fields.get(itemId);

			// go through each field, set previous edited fields to
			// blurred/readonly
			for (Map.Entry<Object, Field> entry : itemMap.entrySet()) {
				Field f = entry.getValue();
				Object fieldValue = f.getValue();
				if (!f.isReadOnly()) {
					f.setReadOnly(true);
					if (!fieldValue.equals(lastCellvalue)) {
						setHasUnsavedChanges(true);
					}
				}
			}

			for (Map.Entry<Object, Field> entry : itemMap.entrySet()) {
				Object column = entry.getKey();
				Field f = entry.getValue();
				Object fieldValue = f.getValue();

				// mark list as changed if value for the cell was
				// changed
				if (column.equals(selectedColumn) && !f.isReadOnly()
						&& !fieldValue.toString().equals(lastCellvalue)) {
					setHasUnsavedChanges(true);
				}

				// validate for designation
				if (column.equals(selectedColumn)
						&& selectedColumn.equals(ColumnLabels.DESIGNATION
						.getName())) {
					Object eventSource = event.getSource();
					String designation = eventSource.toString();

					// retrieve item id at event source
					ItemPropertyId itemProp = (ItemPropertyId) ((TextField) eventSource)
							.getData();
					Object sourceItemId = itemProp.getItemId();

					String[] items = listDataTable.getItem(sourceItemId).toString()
							.split(" ");
					int gid = Integer.valueOf(items[2]);

					if (isDesignationValid(designation, gid)) {
						Double d = computeTextFieldWidth(f.getValue().toString());
						f.setWidth(d.floatValue(), UNITS_EM);
						f.setReadOnly(true);
						listDataTable.focus();
					} else {
						ConfirmDialog
								.show(getWindow(),
										"Update Designation",
										"The value you entered is not one of the germplasm names. "
												+ "Are you sure you want to update Designation with new value?",
										"Yes", "No", new ConfirmDialog.Listener() {
											private static final long serialVersionUID = 1L;

											@Override
											public void onClose(ConfirmDialog dialog) {
												if (!dialog.isConfirmed()) {
													tf.setReadOnly(false);
													tf.setValue(lastCellvalue);
												} else {
													Double d = computeTextFieldWidth(tf
															.getValue().toString());
													tf.setWidth(d.floatValue(),
															UNITS_EM);
												}
												tf.setReadOnly(true);
												listDataTable.focus();
											}
										});
					}
				} else {
					Double d = computeTextFieldWidth(f.getValue().toString());
					f.setWidth(d.floatValue(), UNITS_EM);
					f.setReadOnly(true);
				}
			}

		}

		private Double computeTextFieldWidth(String value) {
			double multiplier = 0.55;
			int length = 1;
			if (value != null && !value.isEmpty()) {
				length = value.length();
				if (value.equalsIgnoreCase(value)) {
					// if all caps, provide bigger space
					multiplier = 0.75;
				}
			}
			Double d = length * multiplier;
			// set a minimum textfield width
			return NumberUtils.max(new double[] { MINIMUM_WIDTH, d });
		}
	}

	// This is needed for storing back-references
	class ItemPropertyId {
		Object itemId;
		Object propertyId;

		public ItemPropertyId(Object itemId, Object propertyId) {
			this.itemId = itemId;
			this.propertyId = propertyId;
		}

		public Object getItemId() {
			return itemId;
		}

		public Object getPropertyId() {
			return propertyId;
		}
	}

	public boolean isDesignationValid(String designation, int gid) {
		List<Name> germplasms = new ArrayList<Name>();
		List<String> designations = new ArrayList<String>();

		try {
			germplasms = germplasmDataManager.getNamesByGID(gid, null, null);

			for (Name germplasm : germplasms) {
				designations.add(germplasm.getNval());
			}

			for (String nameInDb : designations) {
				if (GermplasmDataManagerUtil.compareGermplasmNames(designation, nameInDb)) {
					return true;
				}
			}

		} catch (Exception e) {
			LOG.error("Database error!", e);
			MessageNotifier.showError(
					getWindow(),
					DATABASE_ERROR,
					"Error with validating designation."
							+ messageSource.getMessage(Message.ERROR_REPORT_TO));
		}

		return false;
	}

	public void deleteEntriesButtonClickAction() {
		Collection<?> selectedIdsToDelete = (Collection<?>) listDataTable.getValue();

		if (!selectedIdsToDelete.isEmpty()) {
			if (listDataTable.size() == selectedIdsToDelete.size()) {
				ConfirmDialog.show(this.getWindow(),
						messageSource.getMessage(Message.DELETE_ALL_ENTRIES),
						messageSource.getMessage(Message.DELETE_ALL_ENTRIES_CONFIRM),
						messageSource.getMessage(Message.YES),
						messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {
							private static final long serialVersionUID = 1L;

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									removeRowsInListDataTable((Collection<?>) listDataTable
											.getValue());
								}
							}

						});
			} else {
				removeRowsInListDataTable(selectedIdsToDelete);
			}

		} else {
			MessageNotifier.showError(this.getWindow(),
					messageSource.getMessage(Message.ERROR_DELETING_LIST_ENTRIES),
					messageSource.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED));
		}
	}

	private void removeRowsInListDataTable(Collection<?> selectedIds) {
		// marks that there is a change in listDataTable
		setHasUnsavedChanges(true);

		// Marks the Local Germplasm to be deleted
		try {
			final List<Integer> gidsWithoutChildren = getGidsToDeletedWithoutChildren(selectedIds);
			if (!gidsWithoutChildren.isEmpty()) {
				ConfirmDialog.show(this.getWindow(), "Delete Germplasm from Database",
						"Would you like to delete the germplasm(s) from the database also?", "Yes",
						"No", new ConfirmDialog.Listener() {
							private static final long serialVersionUID = 1L;

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									gidsWithoutChildrenToDelete.addAll(gidsWithoutChildren);
								}
							}

						});
			}
		} catch (NumberFormatException e) {
			LOG.error(e.getMessage(), e);
			MessageNotifier.showError(getWindow(),
					messageSource.getMessage(Message.ERROR_DATABASE),
					"Error with deleting list entries.");
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
			MessageNotifier.showError(getWindow(),
					messageSource.getMessage(Message.ERROR_DATABASE),
					"Error with deleting list entries.");
		}

		if (listDataTable.getItemIds().size() == selectedIds.size()) {
			listDataTable.getContainerDataSource().removeAllItems();
		} else {
			// marks the entryId and designationId of the list entries to delete
			for (final Object itemId : selectedIds) {
				Button desigButton = (Button) listDataTable.getItem(itemId)
						.getItemProperty(ColumnLabels.DESIGNATION.getName()).getValue();
				String designation = String.valueOf(desigButton.getCaption().toString());
				itemsToDelete.put(itemId, designation);
				listDataTable.getContainerDataSource().removeItem(itemId);
			}
		}
		// reset selection
		listDataTable.setValue(null);

		renumberEntryIds();
		listDataTable.requestRepaint();
		updateNoOfEntries();
	}

	private List<Integer> getGidsToDeletedWithoutChildren(Collection<?> selectedIds)
			throws MiddlewareQueryException {
		List<Integer> gids = new ArrayList<Integer>();
		for (final Object itemId : selectedIds) {
			Button gidButton = (Button) listDataTable.getItem(itemId)
					.getItemProperty(ColumnLabels.GID.getName()).getValue();
			Integer germplasmID = Integer.parseInt(gidButton.getCaption());

			long count = pedigreeDataManager.countDescendants(germplasmID);
			if (count == 0) {
				gids.add(germplasmID);
			}
		}

		return gids;
	}

	private void renumberEntryIds() {
		Integer entryId = 1;
		for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
			int listDataId = (Integer) i.next();
			Item item = listDataTable.getItem(listDataId);
			item.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(entryId);
			entryId += 1;
		}
	}

	/* MENU ACTIONS */
	private void editListButtonClickAction() {
		final ListBuilderComponent listBuilderComponent = source.getListBuilderComponent();

		if (listBuilderComponent.hasUnsavedChanges()) {
			String message = "";

			String buildNewListTitle = listBuilderComponent.getBuildNewListTitle().getValue()
					.toString();
			if (buildNewListTitle.equals(messageSource.getMessage(Message.BUILD_A_NEW_LIST))) {
				message = "You have unsaved changes to the current list you are building. Do you want to save your changes before proceeding to your next list to edit?";
			} else {
				message = "You have unsaved changes to the list you are editing. Do you want to save your changes before proceeding to your next list to edit?";
			}

			ConfirmDialog.show(getWindow(), "Unsaved Changes", message, "Yes", "No",
					new ConfirmDialog.Listener() {

						private static final long serialVersionUID = 1L;

						@Override
						public void onClose(ConfirmDialog dialog) {
							if (dialog.isConfirmed()) {
								// save the existing list
								listBuilderComponent.getSaveButton().click();
							}

							source.loadListForEditing(getGermplasmList());
						}
					});
		} else {
			source.loadListForEditing(getGermplasmList());
		}
	}

	private void exportListAction() {
		ExportListAsDialog exportListAsDialog = new ExportListAsDialog(source, germplasmList,
				listDataTable);
		this.getWindow().addWindow(exportListAsDialog);
	}

	protected void setLockedState(boolean locked) {
		lockButton.setVisible(!locked);
		unlockButton.setVisible(locked);

		editHeaderButton.setVisible(!locked);

		if (fillWith != null) {
			fillWith.setContextMenuEnabled(!locked);
		}
	}

	private void exportListForGenotypingOrderAction() {
		if (germplasmList.isLockedList()) {
			String tempFileName = System.getProperty(USER_HOME) + "/tempListForGenotyping.xls";
			GermplasmListExporter listExporter = new GermplasmListExporter(germplasmList.getId());

			try {
				listExporter.exportKBioScienceGenotypingOrderXLS(tempFileName, 96);
				FileDownloadResource fileDownloadResource = new FileDownloadResource(new File(
						tempFileName), source.getApplication());
				String listName = germplasmList.getName();
				fileDownloadResource.setFilename(FileDownloadResource.getDownloadFileName(listName, BreedingManagerUtil.getApplicationRequest()).replace(" ", "_") + "ForGenotyping.xls");

				source.getWindow().open(fileDownloadResource);

			} catch (GermplasmListExporterException e) {
				LOG.error(e.getMessage(), e);
				MessageNotifier.showError(source.getWindow(),
						messageSource.getMessage(Message.ERROR_EXPORTING_LIST), e.getMessage());
			}
		} else {
			MessageNotifier.showError(source.getWindow(),
					messageSource.getMessage(Message.ERROR_EXPORTING_LIST),
					messageSource.getMessage(Message.ERROR_EXPORT_LIST_MUST_BE_LOCKED));
		}
	}

	private void copyToNewListAction() {
		Collection<?> newListEntries = (Collection<?>) listDataTable.getValue();
		if (newListEntries == null || newListEntries.isEmpty()) {
			MessageNotifier.showRequiredFieldError(this.getWindow(),
					messageSource.getMessage(Message.ERROR_LIST_ENTRIES_MUST_BE_SELECTED));

		} else {
			listManagerCopyToNewListDialog = new BaseSubWindow(
					messageSource.getMessage(Message.COPY_TO_NEW_LIST_WINDOW_LABEL));
			listManagerCopyToNewListDialog.setOverrideFocus(true);
			listManagerCopyToNewListDialog.setModal(true);
			listManagerCopyToNewListDialog.setWidth("617px");
			listManagerCopyToNewListDialog.setHeight("230px");
			listManagerCopyToNewListDialog.setResizable(false);
			listManagerCopyToNewListDialog.addStyleName(Reindeer.WINDOW_LIGHT);

			try {
				listManagerCopyToNewListDialog.addComponent(new ListManagerCopyToNewListDialog(
						parentListDetailsComponent.getWindow(), listManagerCopyToNewListDialog,
						germplasmList.getName(), listDataTable, contextUtil.getCurrentUserLocalId(), source));
				parentListDetailsComponent.getWindow().addWindow(listManagerCopyToNewListDialog);
				listManagerCopyToNewListDialog.center();
			} catch (MiddlewareQueryException e) {
				LOG.error("Error copying list entries.", e);
				LOG.error("\n" + e.getStackTrace());
			}
		}
	}

	private void copyToNewListFromInventoryViewAction() {
		// do nothing
	}

	private void addEntryButtonClickAction() {
		Window parentWindow = this.getWindow();
		AddEntryDialog addEntriesDialog = new AddEntryDialog(this, parentWindow);
		addEntriesDialog.addStyleName(Reindeer.WINDOW_LIGHT);
		addEntriesDialog.focusOnSearchField();
		parentWindow.addWindow(addEntriesDialog);
	}

	@Override
	public void finishAddingEntry(Integer gid) {
		finishAddingEntry(gid, true);
	}

	public Boolean finishAddingEntry(Integer gid, Boolean showSuccessMessage) {

		Germplasm germplasm = null;

		try {
			germplasm = germplasmDataManager.getGermplasmWithPrefName(gid);
		} catch (MiddlewareQueryException ex) {
			LOG.error("Error with getting germplasm with id: " + gid, ex);
			MessageNotifier.showError(
					getWindow(),
					DATABASE_ERROR,
					"Error with getting germplasm with id: " + gid + ". "
							+ messageSource.getMessage(Message.ERROR_REPORT_TO));
			return false;
		}

		Integer maxEntryId = Integer.valueOf(0);
		if (listDataTable != null) {
			for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
				// iterate through the table elements' IDs
				int listDataId = (Integer) i.next();

				// update table item's entryId
				Item item = listDataTable.getItem(listDataId);
				Integer entryId = (Integer) item.getItemProperty(
						ColumnLabels.ENTRY_ID.getName()).getValue();
				if (maxEntryId < entryId) {
					maxEntryId = entryId;
				}
			}
		}

		GermplasmListData listData = new GermplasmListData();
		listData.setList(germplasmList);
		if (germplasm.getPreferredName() != null) {
			listData.setDesignation(germplasm.getPreferredName().getNval());
		} else {
			listData.setDesignation("-");
		}
		listData.setEntryId(maxEntryId + 1);
		listData.setGid(gid);
		listData.setLocalRecordId(Integer.valueOf(0));
		listData.setStatus(Integer.valueOf(0));
		listData.setEntryCode(listData.getEntryId().toString());
		listData.setSeedSource("From Add Entry Feature of List Manager");

		String groupName = "-";
		try {
			groupName = this.germplasmDataManager.getCrossExpansion(gid, 1);
		} catch (MiddlewareQueryException ex) {
			LOG.error(ex.getMessage(), ex);
			groupName = "-";
		}
		listData.setGroupName(groupName);

		Integer listDataId = null;
		try {
			listDataId = this.germplasmListManager.addGermplasmListData(listData);

			// create table if added entry is first listdata record
			if (listDataTable == null) {
				initializeListDataTable();
				initializeValues();
			} else {
				listDataTable.setEditable(false);
				List<GermplasmListData> inventoryData = this.inventoryDataManager
						.getLotCountsForListEntries(this.germplasmList.getId(),
								new ArrayList<Integer>(Collections.singleton(listDataId)));
				if (inventoryData != null) {
					listData = inventoryData.get(0);
				}
				addListEntryToTable(listData);

				Object[] visibleColumns = listDataTable.getVisibleColumns();
				if (isColumnVisible(visibleColumns, ColumnLabels.PREFERRED_ID.getName())) {
					addColumnContextMenu.setPreferredIdColumnValues(false);
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.GERMPLASM_LOCATION.getName())) {
					addColumnContextMenu.setLocationColumnValues(false);
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.PREFERRED_NAME.getName())) {
					addColumnContextMenu.setPreferredNameColumnValues(false);
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.GERMPLASM_DATE.getName())) {
					addColumnContextMenu.setGermplasmDateColumnValues(false);
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_NAME.getName())) {
					addColumnContextMenu.setMethodInfoColumnValues(false,
							ColumnLabels.BREEDING_METHOD_NAME.getName());
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName())) {
					addColumnContextMenu.setMethodInfoColumnValues(false,
							ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName());
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_NUMBER.getName())) {
					addColumnContextMenu.setMethodInfoColumnValues(false,
							ColumnLabels.BREEDING_METHOD_NUMBER.getName());
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.BREEDING_METHOD_GROUP.getName())) {
					addColumnContextMenu.setMethodInfoColumnValues(false,
							ColumnLabels.BREEDING_METHOD_GROUP.getName());
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.CROSS_FEMALE_GID.getName())) {
					addColumnContextMenu.setCrossFemaleInfoColumnValues(false,
							ColumnLabels.CROSS_FEMALE_GID.getName());
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName())) {
					addColumnContextMenu.setCrossFemaleInfoColumnValues(false,
							ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName());
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.CROSS_MALE_GID.getName())) {
					addColumnContextMenu.setCrossMaleGIDColumnValues(false);
				}
				if (isColumnVisible(visibleColumns, ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName())) {
					addColumnContextMenu.setCrossMalePrefNameColumnValues(false);
				}

				saveChangesAction(this.getWindow(), false);
				listDataTable.refreshRowCache();
				listDataTable.setImmediate(true);
				listDataTable.setEditable(true);
			}

			if (showSuccessMessage) {
				setHasUnsavedChanges(false);
				MessageNotifier.showMessage(this.getWindow(),
						messageSource.getMessage(Message.SUCCESS),
						"Successful in adding list entries.", 3000);
			}

			doneInitializing = true;
			return true;

		} catch (MiddlewareQueryException ex) {
			LOG.error("Error with adding list entry.", ex);
			MessageNotifier.showError(getWindow(), DATABASE_ERROR, "Error with adding list entry. "
					+ messageSource.getMessage(Message.ERROR_REPORT_TO));
			return false;
		}

	}

	private boolean isColumnVisible(Object[] columns, String columnName) {

		for (Object col : columns) {
			if (col.equals(columnName)) {
				return true;
			}
		}

		return false;
	}

	public void saveChangesAction() {
		saveChangesAction(this.getWindow());
	}

	public Boolean saveChangesAction(Window window) {
		return saveChangesAction(window, true);
	}

	public Boolean saveChangesAction(Window window, Boolean showSuccessMessage) {

		// selected entries to entries
		if (!itemsToDelete.isEmpty()) {
			performListEntriesDeletion(itemsToDelete);
		}

		try {
			listEntriesCount = this.germplasmListManager
					.countGermplasmListDataByListId(germplasmList.getId());
			listEntries = this.germplasmListManager.getGermplasmListDataByListId(
					germplasmList.getId(), 0, (int) listEntriesCount);
		} catch (MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE,
					Message.ERROR_IN_SAVING_GERMPLASMLIST_DATA_CHANGES);
		}

		int entryId = 1;
		// re-assign "Entry ID" field based on table's sorting
		for (Iterator<?> i = listDataTable.getItemIds().iterator(); i.hasNext();) {
			// iterate through the table elements' IDs
			int listDataId = (Integer) i.next();

			// update table item's entryId
			Item item = listDataTable.getItem(listDataId);
			item.getItemProperty(ColumnLabels.ENTRY_ID.getName()).setValue(entryId);

			// then find the corresponding ListData and assign a new entryId to
			// it
			for (GermplasmListData listData : listEntries) {
				if (listData.getId().equals(listDataId)) {
					listData.setEntryId(entryId);

					String entryCode = (String) item.getItemProperty(
							ColumnLabels.ENTRY_CODE.getName()).getValue();
					if (entryCode != null && entryCode.length() != 0) {
						listData.setEntryCode(entryCode);
					} else {
						listData.setEntryCode(Integer.valueOf(entryId).toString());
					}

					String seedSource = (String) item.getItemProperty(
							ColumnLabels.SEED_SOURCE.getName()).getValue();
					if (seedSource != null && seedSource.length() != 0) {
						listData.setSeedSource(seedSource);
					} else {
						listData.setSeedSource("-");
					}

					Button desigButton = (Button) (item
							.getItemProperty(ColumnLabels.DESIGNATION.getName()))
							.getValue();
					String designation = String.valueOf(desigButton.getCaption().toString());
					if (designation != null && designation.length() != 0) {
						listData.setDesignation(designation);
					} else {
						listData.setDesignation("-");
					}

					String parentage = (String) item.getItemProperty(
							ColumnLabels.PARENTAGE.getName()).getValue();
					if (parentage != null && parentage.length() != 0) {
						if (parentage.length() > 255) {
							parentage = parentage.substring(0, 255);
						}
						listData.setGroupName(parentage);
					} else {
						listData.setGroupName("-");
					}

					break;
				}
			}
			entryId += 1;
		}
		// save the list of Germplasm List Data to the database
		try {

			germplasmListManager.updateGermplasmListData(listEntries);
			germplasmListManager.saveListDataColumns(addColumnContextMenu
					.getListDataCollectionFromTable(listDataTable));

			listDataTable.requestRepaint();
			// reset flag to indicate unsaved changes
			setHasUnsavedChanges(true);

			if (showSuccessMessage) {
				MessageNotifier.showMessage(window, messageSource.getMessage(Message.SUCCESS),
						messageSource.getMessage(Message.SAVE_GERMPLASMLIST_DATA_SAVING_SUCCESS),
						3000);
			}

		} catch (MiddlewareQueryException e) {
			throw new InternationalizableException(e, Message.ERROR_DATABASE,
					Message.ERROR_IN_SAVING_GERMPLASMLIST_DATA_CHANGES);
		}

		// Update counter
		updateNoOfEntries();

		setHasUnsavedChanges(false);

		refreshTreeOnSave();

		return true;
	}

	// saveChangesAction()
	// might be possible to eliminate this method altogether and reduce the
	// number of middleware calls
	private void performListEntriesDeletion(Map<Object, String> itemsToDelete) {
		try {
			designationOfListEntriesDeleted = "";

			for (Map.Entry<Object, String> item : itemsToDelete.entrySet()) {

				Object sLRecId = item.getKey();
				String sDesignation = item.getValue();
				int lrecId = Integer.valueOf(sLRecId.toString());
				designationOfListEntriesDeleted += sDesignation + ",";
				deleteGermplasmListDataByListIdLrecId(germplasmList.getId(), lrecId);

			}

			deleteGermplasmDialogBox(gidsWithoutChildrenToDelete);
			designationOfListEntriesDeleted = designationOfListEntriesDeleted.substring(0,
					designationOfListEntriesDeleted.length() - 1);

			// Change entry IDs on listData
			List<GermplasmListData> listDatas = germplasmListManager.getGermplasmListDataByListId(
					germplasmList.getId(), 0, (int) germplasmListManager
							.countGermplasmListDataByListId(germplasmList.getId()));
			Integer entryId = 1;
			for (GermplasmListData listData : listDatas) {
				listData.setEntryId(entryId);
				entryId++;
			}
			germplasmListManager.updateGermplasmListData(listDatas);

			contextUtil.logProgramActivity("Deleted list entries.",
					"Deleted list entries from the list id " + germplasmList.getId() + " - "
							+ germplasmList.getName());

			// reset items to delete in listDataTable
			itemsToDelete.clear();

		} catch (NumberFormatException e) {
			LOG.error("Error with deleting list entries.", e);
			LOG.error("\n" + e.getStackTrace());
		} catch (MiddlewareQueryException e) {
			LOG.error("Error with deleting list entries.", e);
			LOG.error("\n" + e.getStackTrace());
		}
		// end of performListEntriesDeletion
	}

	private void deleteGermplasmListDataByListIdLrecId(int listId, int lrecId){
		try {
			germplasmListManager.deleteGermplasmListDataByListIdLrecId(
					germplasmList.getId(), lrecId);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	protected void deleteGermplasmDialogBox(final List<Integer> gidsWithoutChildren)
			throws MiddlewareQueryException {

		if (gidsWithoutChildren != null && !gidsWithoutChildren.isEmpty()) {
			List<Germplasm> gList = new ArrayList<Germplasm>();
			try {
				for (Integer gid : gidsWithoutChildren) {
					Germplasm g = germplasmDataManager.getGermplasmByGID(gid);
					g.setGrplce(gid);
					gList.add(g);
				}
				// end loop

				germplasmDataManager.updateGermplasm(gList);

			} catch (MiddlewareQueryException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public void deleteListButtonClickAction() {
		ConfirmDialog.show(this.getWindow(), "Delete Germplasm List:",
				"Are you sure that you want to delete this list?", "Yes", "No",
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = -6641772458404494412L;

					@Override
					public void onClose(ConfirmDialog dialog) {
						if (dialog.isConfirmed()) {
							deleteGermplasmListConfirmed();
						}
					}
				});
	}

	public void deleteGermplasmListConfirmed() {
		if (!germplasmList.isLockedList()) {
			try {
				ListCommonActionsUtil.deleteGermplasmList(germplasmListManager, germplasmList,
						contextUtil, getWindow(), messageSource, "list");

				source.getListSelectionComponent().getListTreeComponent()
						.removeListFromTree(germplasmList);
				source.updateUIForDeletedList(germplasmList);
			} catch (MiddlewareQueryException e) {
				getWindow().showNotification("Error",
						"There was a problem deleting the germplasm list",
						Notification.TYPE_ERROR_MESSAGE);
				LOG.error("Error with deleting germplasmlist.", e);
			}
		}
	}

	/* SETTERS AND GETTERS */
	public GermplasmList getGermplasmList() {
		return germplasmList;
	}

	public Integer getGermplasmListId() {
		return germplasmList.getId();
	}

	public void lockGermplasmList(GermplasmList list) {
		Integer listId = list.getId();
		try {
			list = germplasmListManager.getGermplasmListById(listId);
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		germplasmList = list;

		if (source.lockGermplasmList(germplasmList)) {
			setLockedState(germplasmList.isLockedList());
		}
	}

	public void unlockGermplasmList() {
		if (germplasmList.isLockedList()) {
			germplasmList.setStatus(germplasmList.getStatus() - 100);

			setFillWith();
			
			try {
				germplasmListManager.updateGermplasmList(germplasmList);

				setLockedState(germplasmList.isLockedList());

				contextUtil.logProgramActivity("Unlocked a germplasm list.", "Unlocked list " + germplasmList.getId()
						+ " - " + germplasmList.getName());

			} catch (MiddlewareQueryException e) {
				LOG.error("Error with unlocking list.", e);
				MessageNotifier.showError(
						getWindow(),
						DATABASE_ERROR,
						"Error with unlocking list. "
								+ messageSource.getMessage(Message.ERROR_REPORT_TO));
			}
		}
	}

	public void openSaveListAsDialog() {
		dialog = new SaveListAsDialog(this, germplasmList,
				messageSource.getMessage(Message.EDIT_LIST_HEADER));
		this.getWindow().addWindow(dialog);
	}

	public SaveListAsDialog getSaveListAsDialog() {
		return dialog;
	}

	public GermplasmList getCurrentListInSaveDialog() {
		return dialog.getGermplasmListToSave();
	}

	@Override
	public void saveList(GermplasmList list) {

		GermplasmList savedList = ListCommonActionsUtil.overwriteList(list,
				this.germplasmListManager, this.source, messageSource, true);
		if (savedList != null) {
			if (!savedList.getId().equals(germplasmList.getId())) {
				ListCommonActionsUtil.overwriteListEntries(savedList, listEntries, germplasmList
								.getId().intValue() != savedList.getId().intValue(), germplasmListManager,
						source, messageSource, true);
				source.closeList(savedList);
			} else {
				germplasmList = savedList;
				viewListHeaderWindow = new ViewListHeaderWindow(savedList);
				if (viewHeaderButton != null) {
					viewHeaderButton.setDescription(viewListHeaderWindow.getListHeaderComponent()
							.toString());
				}
			}
		}
		// Refresh tree on save
		refreshTreeOnSave();

	}

	protected void refreshTreeOnSave() {
		((BreedingManagerApplication) getApplication()).refreshListManagerTree();
	}

	public void openViewListHeaderWindow() {
		this.getWindow().addWindow(viewListHeaderWindow);
	}

	@Override
	public void finishAddingEntry(List<Integer> gids) {
		Boolean allSuccessful = true;
		for (Integer gid : gids) {
			if (finishAddingEntry(gid, false).equals(false)) {
				allSuccessful = false;
			}
		}
		if (allSuccessful) {
			MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS),
					messageSource.getMessage(Message.SAVE_GERMPLASMLIST_DATA_SAVING_SUCCESS), 3000);
		}
	}

	private void updateNoOfEntries(long count) {
		String countLabel = "  <b>" + count + "</b>";
		if (source.getModeView().equals(ModeView.LIST_VIEW)) {
			if (count == 0) {
				totalListEntriesLabel.setValue(messageSource
						.getMessage(Message.NO_LISTDATA_RETRIEVED_LABEL));
			} else {
				totalListEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LIST_ENTRIES)
						+ ": " + countLabel);
			}
		} else {
			// Inventory View
			totalListEntriesLabel.setValue(messageSource.getMessage(Message.TOTAL_LOTS) + ": "
					+ countLabel);
		}
	}

	protected void updateNoOfEntries(){
		int count = 0;
		if (source.getModeView().equals(ModeView.LIST_VIEW)) {
			count = listDataTable.getItemIds().size();
		} else {
			// Inventory View
			count = listInventoryTable.getTable().size();
		}
		updateNoOfEntries(count);
	}

	private void updateNoOfSelectedEntries(int count) {
		totalSelectedListEntriesLabel.setValue("<i>" + messageSource.getMessage(Message.SELECTED)
				+ ": " + "  <b>" + count + "</b></i>");
	}

	private void updateNoOfSelectedEntries() {
		int count = 0;

		if (source.getModeView().equals(ModeView.LIST_VIEW)) {
			Collection<?> selectedItems = (Collection<?>) getListDataTableWithSelectAll()
					.getTable().getValue();
			count = selectedItems.size();
		} else {
			Collection<?> selectedItems = (Collection<?>) listInventoryTable.getTable().getValue();
			count = selectedItems.size();
		}

		updateNoOfSelectedEntries(count);
	}

	@Override
	public void setCurrentlySavedGermplasmList(GermplasmList list) {
		// not yet implemented
	}

	/*-------------------------------------LIST INVENTORY RELATED METHODS-------------------------------------*/

	private void viewListAction() {
		if (!hasUnsavedChanges()) {
			source.setModeView(ModeView.LIST_VIEW);
		} else {
			String message = "You have unsaved reservations for this list. "
					+ "You will need to save them before changing views. "
					+ "Do you want to save your changes?";

			source.showUnsavedChangesConfirmDialog(message, ModeView.LIST_VIEW);

		}
	}

	public void changeToListView() {
		if (listInventoryTable.isVisible()) {
			getListDataTableWithSelectAll().setVisible(true);
			listInventoryTable.setVisible(false);
			toolsMenuContainer.addComponent(actionsButton);
			toolsMenuContainer.removeComponent(inventoryViewToolsButton);

			topLabel.setValue(messageSource.getMessage(Message.LIST_ENTRIES_LABEL));
			updateNoOfEntries();
			updateNoOfSelectedEntries();
			setHasUnsavedChanges(false);
		}
	}

	public void changeToInventoryView() {
		if (getListDataTableWithSelectAll().isVisible()) {
			getListDataTableWithSelectAll().setVisible(false);
			listInventoryTable.setVisible(true);
			toolsMenuContainer.removeComponent(actionsButton);
			toolsMenuContainer.addComponent(inventoryViewToolsButton);

			topLabel.setValue(messageSource.getMessage(Message.LOTS));
			updateNoOfEntries();
			updateNoOfSelectedEntries();
			setHasUnsavedChanges(false);
		}
	}

	public void setHasUnsavedChanges(Boolean hasChanges) {
		this.hasChanges = hasChanges;

		ListSelectionLayout listSelection = source.getListSelectionComponent()
				.getListDetailsLayout();
		listSelection.addUpdateListStatusForChanges(this, this.hasChanges);
	}

	public Boolean hasUnsavedChanges() {
		return hasChanges;
	}

	private void viewInventoryAction() {
		if (!hasUnsavedChanges()) {
			source.setModeView(ModeView.INVENTORY_VIEW);
		} else {
			String message = "You have unsaved changes to the list you are currently editing.. "
					+ "You will need to save them before changing views. "
					+ "Do you want to save your changes?";
			source.showUnsavedChangesConfirmDialog(message, ModeView.INVENTORY_VIEW);
		}
	}

	public void viewInventoryActionConfirmed() {
		listInventoryTable.loadInventoryData();

		changeToInventoryView();
	}

	public void reserveInventoryAction() {
		// checks if the screen is in the inventory view
		if (!inventoryViewMenu.isVisible()) {
			MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.WARNING),
					"Please change to Inventory View first.");
		} else {
			List<ListEntryLotDetails> lotDetailsGid = listInventoryTable.getSelectedLots();

			if (lotDetailsGid == null || lotDetailsGid.isEmpty()) {
				MessageNotifier.showError(getWindow(), messageSource.getMessage(Message.WARNING),
						"Please select at least 1 lot to reserve.");
			} else {
				// this util handles the inventory reservation related functions
				reserveInventoryUtil = new ReserveInventoryUtil(this, lotDetailsGid);
				reserveInventoryUtil.viewReserveInventoryWindow();
			}
		}
	}

	// end of reserveInventoryAction

	public void saveReservationChangesAction() {
		if (hasUnsavedChanges()) {
			reserveInventoryAction = new ReserveInventoryAction(this);
			boolean success = reserveInventoryAction.saveReserveTransactions(
					getValidReservationsToSave(), germplasmList.getId());
			if (success) {
				refreshInventoryColumns(getValidReservationsToSave());
				resetListInventoryTableValues();
				MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS),
						"All reservations were saved.");
			}
		}
	}

	public void cancelReservationsAction() {
		List<ListEntryLotDetails> lotDetailsGid = listInventoryTable.getSelectedLots();

		if (lotDetailsGid == null || lotDetailsGid.isEmpty()) {
			MessageNotifier.showWarning(getWindow(), messageSource.getMessage(Message.WARNING),
					"Please select at least 1 lot to cancel reservations.");
		} else {
			if (!listInventoryTable.isSelectedEntriesHasReservation(lotDetailsGid)) {
				MessageNotifier.showWarning(getWindow(), messageSource.getMessage(Message.WARNING),
						"There are no reservations on the current selected lots.");
			} else {
				ConfirmDialog.show(getWindow(),
						messageSource.getMessage(Message.CANCEL_RESERVATIONS),
						"Are you sure you want to cancel the selected reservations?",
						messageSource.getMessage(Message.YES),
						messageSource.getMessage(Message.NO), new ConfirmDialog.Listener() {
							private static final long serialVersionUID = 1L;

							@Override
							public void onClose(ConfirmDialog dialog) {
								if (dialog.isConfirmed()) {
									cancelReservations();
								}
							}
						});
			}
		}
	}

	public void cancelReservations() {
		List<ListEntryLotDetails> lotDetailsGid = listInventoryTable.getSelectedLots();
		reserveInventoryAction = new ReserveInventoryAction(this);
		try {
			reserveInventoryAction.cancelReservations(lotDetailsGid);
		} catch (MiddlewareQueryException e) {
			LOG.error("Error with canceling reservations.", e);
		}

		refreshInventoryColumns(getLrecIds(lotDetailsGid));
		listInventoryTable.resetRowsForCancelledReservation(lotDetailsGid, germplasmList.getId());

		MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS),
				"All selected reservations were cancelled successfully.");
	}

	private Set<Integer> getLrecIds(List<ListEntryLotDetails> lotDetails) {
		Set<Integer> lrecIds = new HashSet<Integer>();

		for (ListEntryLotDetails lotDetail : lotDetails) {
			if (!lrecIds.contains(lotDetail.getId())) {
				lrecIds.add(lotDetail.getId());
			}
		}
		return lrecIds;
	}

	private void refreshInventoryColumns(Set<Integer> entryIds) {
		List<GermplasmListData> germplasmListDataEntries = new ArrayList<GermplasmListData>();
		try {
			if (!entryIds.isEmpty()) {
				germplasmListDataEntries = this.inventoryDataManager.getLotCountsForListEntries(
						germplasmList.getId(), new ArrayList<Integer>(entryIds));
			}
		} catch (MiddlewareQueryException e) {
			LOG.error(e.getMessage(), e);
		}

		for (GermplasmListData listData : germplasmListDataEntries) {
			Item item = listDataTable.getItem(listData.getId());

			// #1 Available Inventory
			// default value
			String availInv = "-";
			if (listData.getInventoryInfo().getLotCount().intValue() != 0) {
				availInv = listData.getInventoryInfo().getActualInventoryLotCount().toString()
						.trim();
			}
			Button inventoryButton = new Button(availInv, new InventoryLinkButtonClickListener(
					parentListDetailsComponent, germplasmList.getId(), listData.getId(),
					listData.getGid()));
			inventoryButton.setStyleName(BaseTheme.BUTTON_LINK);
			inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);

			if ("-".equals(availInv)) {
				inventoryButton.setEnabled(false);
				inventoryButton.setDescription("No Lot for this Germplasm");
			} else {
				inventoryButton.setDescription(CLICK_TO_VIEW_INVENTORY_DETAILS);
			}
			item.getItemProperty(ColumnLabels.AVAILABLE_INVENTORY.getName()).setValue(
					inventoryButton);

			Button gidButton = (Button) item.getItemProperty(ColumnLabels.GID.getName())
					.getValue();
			String gidString = "";

			if (gidButton != null) {
				gidString = gidButton.getCaption();
			}

			updateAvailInvValues(Integer.valueOf(gidString), availInv);

			// Seed Reserved
			// default value
			String seedRes = "-";
			if (listData.getInventoryInfo().getReservedLotCount().intValue() != 0) {
				seedRes = listData.getInventoryInfo().getReservedLotCount().toString().trim();
			}

			item.getItemProperty(ColumnLabels.SEED_RESERVATION.getName()).setValue(
					seedRes);
		}
	}

	private void refreshInventoryColumns(Map<ListEntryLotDetails, Double> validReservationsToSave) {

		Set<Integer> entryIds = new HashSet<Integer>();
		for (Entry<ListEntryLotDetails, Double> details : validReservationsToSave.entrySet()) {
			entryIds.add(details.getKey().getId());
		}

		refreshInventoryColumns(entryIds);
	}

	@Override
	public void updateListInventoryTable(Map<ListEntryLotDetails, Double> validReservations,
			boolean withInvalidReservations) {
		for (Map.Entry<ListEntryLotDetails, Double> entry : validReservations.entrySet()) {
			ListEntryLotDetails lot = entry.getKey();
			Double newRes = entry.getValue();

			Item itemToUpdate = listInventoryTable.getTable().getItem(lot);
			itemToUpdate.getItemProperty(ColumnLabels.NEWLY_RESERVED.getName()).setValue(
					newRes);
		}

		removeReserveInventoryWindow(reserveInventory);

		// update lot reservatios to save
		updateLotReservationsToSave(validReservations);

		// enable now the Save Changes option
		menuInventorySaveChanges.setEnabled(true);

		// if there are no valid reservations
		if (validReservations.isEmpty()) {
			MessageNotifier
					.showRequiredFieldError(
							getWindow(),
							messageSource
									.getMessage(Message.COULD_NOT_MAKE_ANY_RESERVATION_ALL_SELECTED_LOTS_HAS_INSUFFICIENT_BALANCES)
									+ ".");

		} else if (!withInvalidReservations) {
			MessageNotifier.showMessage(getWindow(), messageSource.getMessage(Message.SUCCESS),
					"All selected entries will be reserved in their respective lots.", 3000);
		}

	}

	private void updateLotReservationsToSave(Map<ListEntryLotDetails, Double> validReservations) {

		for (Map.Entry<ListEntryLotDetails, Double> entry : validReservations.entrySet()) {
			ListEntryLotDetails lot = entry.getKey();
			Double amountToReserve = entry.getValue();

			if (validReservationsToSave.containsKey(lot)) {
				validReservationsToSave.remove(lot);

			}

			validReservationsToSave.put(lot, amountToReserve);
		}

		if (!validReservationsToSave.isEmpty()) {
			setHasUnsavedChanges(true);
		}
	}

	public Map<ListEntryLotDetails, Double> getValidReservationsToSave() {
		return validReservationsToSave;
	}

	@Override
	public void addReserveInventoryWindow(ReserveInventoryWindow reserveInventory) {
		this.reserveInventory = reserveInventory;
		source.getWindow().addWindow(this.reserveInventory);
	}

	@Override
	public void removeReserveInventoryWindow(ReserveInventoryWindow reserveInventory) {
		this.reserveInventory = reserveInventory;
		source.getWindow().removeWindow(this.reserveInventory);
	}

	@Override
	public void addReservationStatusWindow(ReservationStatusWindow reservationStatus) {
		this.reservationStatus = reservationStatus;
		removeReserveInventoryWindow(reserveInventory);
		source.getWindow().addWindow(this.reservationStatus);
	}

	@Override
	public void removeReservationStatusWindow(ReservationStatusWindow reservationStatus) {
		this.reservationStatus = reservationStatus;
		source.getWindow().removeWindow(this.reservationStatus);
	}

	public void resetListInventoryTableValues() {
		listInventoryTable.updateListInventoryTableAfterSave();

		resetInventoryMenuOptions();

		// reset the reservations to save.
		validReservationsToSave.clear();

		setHasUnsavedChanges(false);
	}

	@Override
	public Component getParentComponent() {
		return source;
	}

	public AddColumnContextMenu getAddColumnContextMenu() {
		return addColumnContextMenu;
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getItemIds(Table table) {
		List<Integer> itemIds = new ArrayList<Integer>();
		itemIds.addAll((Collection<? extends Integer>) table.getItemIds());

		return itemIds;
	}

	private void updateAvailInvValues(Integer gid, String availInv) {
		List<Integer> itemIds = getItemIds(listDataTable);
		for (Integer itemId : itemIds) {
			Item item = listDataTable.getItem(itemId);
			Button gidButton = (Button) item.getItemProperty(ColumnLabels.GID.getName())
					.getValue();

			String currentGid = "";
			if (gidButton != null) {
				currentGid = gidButton.getCaption();
			}

			if (currentGid.equals(gid)) {
				((Button) item.getItemProperty(
						ColumnLabels.AVAILABLE_INVENTORY.getName()).getValue())
						.setCaption(availInv);
			}
		}
		listDataTable.requestRepaint();
	}

	public ViewListHeaderWindow getViewListHeaderWindow() {
		return viewListHeaderWindow;
	}

	public void setViewListHeaderWindow(ViewListHeaderWindow viewListHeaderWindow) {
		this.viewListHeaderWindow = viewListHeaderWindow;
	}

	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setGermplasmListManager(GermplasmListManager germplasmListManager) {
		this.germplasmListManager = germplasmListManager;
	}

	public void setListEntries(List<GermplasmListData> listEntries) {
		this.listEntries = listEntries;
	}

	public Table getListDataTable() {
		return listDataTable;
	}

	public void setListDataTable(Table listDataTable) {
		this.listDataTable = listDataTable;
	}

	public void setAddColumnContextMenu(AddColumnContextMenu addColumnContextMenu) {
		this.addColumnContextMenu = addColumnContextMenu;
	}

	protected TableWithSelectAllLayout getListDataTableWithSelectAll() {
		return listDataTableWithSelectAll;
	}

	protected void setListDataTableWithSelectAll(TableWithSelectAllLayout listDataTableWithSelectAll) {
		this.listDataTableWithSelectAll = listDataTableWithSelectAll;
	}

	protected String getTermNameFromOntology(ColumnLabels columnLabels) {
		return columnLabels.getTermNameFromOntology(ontologyDataManager);
	}

}