package org.generationcp.breeding.manager.listmanager;

import com.google.common.collect.Lists;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import junit.framework.Assert;
import org.generationcp.breeding.manager.application.BreedingManagerApplication;
import org.generationcp.breeding.manager.application.Message;
import org.generationcp.breeding.manager.constants.ModeView;
import org.generationcp.breeding.manager.customcomponent.TableWithSelectAllLayout;
import org.generationcp.breeding.manager.customcomponent.listinventory.CloseLotDiscardInventoryAction;
import org.generationcp.breeding.manager.customcomponent.listinventory.ListInventoryTable;
import org.generationcp.breeding.manager.customcomponent.listinventory.ListManagerInventoryTable;
import org.generationcp.breeding.manager.data.initializer.ImportedGermplasmListDataInitializer;
import org.generationcp.breeding.manager.inventory.ReserveInventoryAction;
import org.generationcp.breeding.manager.inventory.SeedInventoryListExporter;
import org.generationcp.breeding.manager.inventory.exception.SeedInventoryExportException;
import org.generationcp.breeding.manager.listmanager.dialog.AssignCodesDialog;
import org.generationcp.breeding.manager.listmanager.dialog.GermplasmGroupingComponent;
import org.generationcp.breeding.manager.listmanager.listcomponent.InventoryViewActionMenu;
import org.generationcp.breeding.manager.listmanager.util.ListDataPropertiesRenderer;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.ListInventoryDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListNewColumnsInfo;
import org.generationcp.middleware.domain.gms.ListDataColumnValues;
import org.generationcp.middleware.domain.gms.ListDataInfo;
import org.generationcp.middleware.domain.inventory.ListEntryLotDetails;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.vaadin.peter.contextmenu.ContextMenu;


import com.google.common.collect.Lists;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@RunWith(MockitoJUnitRunner.class)
public class ListComponentTest {

	private static final String STOCKID = "STOCKID";
	private static final String TOTAL_AVAILBALE = "AVAILABLE";
	private static final String AVAIL_INV = "AVAIL_INV";
	private static final String HASH = "#";
	private static final String CHECK = "CHECK";
	private static final String SEED_SOURCE = "SEED_SOURCE";
	private static final String CROSS = "CROSS";
	private static final String DESIG = "DESIG";
	private static final String ENTRY_CODE = "ENTRY_CODE";
	private static final String GID = "GID";

	private static final String UPDATED_GERMPLASM_LIST_NOTE = "UPDATED Germplasm List Note";
	private static final String UPDATED_GERMPLASM_LIST_NAME = "UPDATED Germplasm List Name";
	private static final String UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE = "UPDATED Germplasm List Description Value";
	private static final long UPDATED_GERMPLASM_LIST_DATE = 20141205;
	private static final String UPDATED_GERMPLASM_LIST_TYPE = "F1 LST";

	private static final Integer EXPECTED_USER_ID = 1;
	private static final Integer TEST_GERMPLASM_LIST_ID = 111;
	private static final Integer TEST_GERMPLASM_NO_OF_ENTRIES = 5;

	@Mock
	private ListManagerMain source;

	@Mock
	private ListTabComponent parentListDetailsComponent;

	@Mock
	private GermplasmListManager germplasmListManager;

	@Mock
	private WorkbenchDataManager workbenchDataManager;

	@Mock
	private InventoryViewActionMenu inventoryViewMenu;

	@Mock
	private OntologyDataManager ontologyDataManager;

	@Mock
	private InventoryDataManager inventoryDataManager;

	@Mock
	private SimpleResourceBundleMessageSource messageSource;

	@Mock
	private Window window;

	@Mock
	private Component parentComponent;

	@Mock
	private AddColumnContextMenu addColumnContextMenu;

	@Mock
	private ContextUtil contextUtil;

	@Mock
	private ListSelectionComponent listSelectionComponent;

	@Mock
	private ListSelectionLayout listDetailsLayout;

	@Mock
	private BreedingManagerApplication breedingManagerApplication;

	@Mock
	private ListDataPropertiesRenderer newColumnsRenderer;

	@Mock
	private CrossExpansionProperties crossExpansionProperties;

	@Mock
	public ListInventoryTable listInventoryTable;

	@Mock
	public ListManagerInventoryTable listManagerInventoryTable;

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private UserDataManager userDataManager;

	@Mock
	private CloseLotDiscardInventoryAction closeLotDiscardInventoryAction;

	@InjectMocks
	private final ListComponent listComponent = new ListComponent();

	private GermplasmList germplasmList;
	private ImportedGermplasmListDataInitializer importedGermplasmListInitializer;

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@Before
	public void setUp() throws Exception {

		this.setUpWorkbenchDataManager();
		this.setUpOntologyManager();
		this.setUpListComponent();
		this.setUpGermplasmDataManager();
		this.importedGermplasmListInitializer = new ImportedGermplasmListDataInitializer();
	}

	private void setUpGermplasmDataManager() {
		List<UserDefinedField> lattributeList = new ArrayList<>();
		Mockito.when(this.germplasmDataManager.getAttributeTypesByGIDList(Mockito.anyList())).thenReturn(lattributeList);
	}

	@Test
	public void testSaveListOverwriteExistingGermplasmList() {

		final GermplasmList germplasmListToBeSaved = new GermplasmList();
		germplasmListToBeSaved.setId(ListComponentTest.TEST_GERMPLASM_LIST_ID);
		germplasmListToBeSaved.setDescription(ListComponentTest.UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE);
		germplasmListToBeSaved.setName(ListComponentTest.UPDATED_GERMPLASM_LIST_NAME);
		germplasmListToBeSaved.setNotes(ListComponentTest.UPDATED_GERMPLASM_LIST_NOTE);
		germplasmListToBeSaved.setDate(ListComponentTest.UPDATED_GERMPLASM_LIST_DATE);
		germplasmListToBeSaved.setType(ListComponentTest.UPDATED_GERMPLASM_LIST_TYPE);
		germplasmListToBeSaved.setStatus(1);

		try {
			Mockito.doReturn(this.germplasmList).when(this.germplasmListManager).getGermplasmListById(this.germplasmList.getId());

			this.listComponent.saveList(germplasmListToBeSaved);

			final GermplasmList savedList = this.listComponent.getGermplasmList();

			Assert.assertEquals(savedList.getId(), germplasmListToBeSaved.getId());
			Assert.assertEquals(savedList.getDescription(), germplasmListToBeSaved.getDescription());
			Assert.assertEquals(savedList.getName(), germplasmListToBeSaved.getName());
			Assert.assertEquals(savedList.getNotes(), germplasmListToBeSaved.getNotes());
			Assert.assertEquals(savedList.getDate(), germplasmListToBeSaved.getDate());
			Assert.assertEquals(savedList.getType(), germplasmListToBeSaved.getType());
			Assert.assertEquals(savedList.getStatus(), germplasmListToBeSaved.getStatus());

			Assert.assertSame(savedList, this.germplasmList);

		} catch (final Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSaveListOverwriteExistingGermplasmListWithDifferentID() {

		final GermplasmList germplasmListToBeSaved = new GermplasmList();
		germplasmListToBeSaved.setId(1000);
		germplasmListToBeSaved.setDescription(ListComponentTest.UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE);
		germplasmListToBeSaved.setName(ListComponentTest.UPDATED_GERMPLASM_LIST_NAME);
		germplasmListToBeSaved.setNotes(ListComponentTest.UPDATED_GERMPLASM_LIST_NOTE);
		germplasmListToBeSaved.setDate(ListComponentTest.UPDATED_GERMPLASM_LIST_DATE);
		germplasmListToBeSaved.setType(ListComponentTest.UPDATED_GERMPLASM_LIST_TYPE);
		germplasmListToBeSaved.setStatus(1);

		try {
			Mockito.doNothing().when(this.source).closeList(germplasmListToBeSaved);
			Mockito.doReturn(germplasmListToBeSaved).when(this.germplasmListManager).getGermplasmListById(Matchers.anyInt());

			// this will overwrite the list entries of the current germplasm list. Germplasm List Details will not be updated.
			this.listComponent.saveList(germplasmListToBeSaved);

			final GermplasmList savedList = this.listComponent.getGermplasmList();

			Assert.assertFalse(germplasmListToBeSaved.getId().equals(savedList.getId()));
			Assert.assertFalse(germplasmListToBeSaved.getDescription().equals(savedList.getDescription()));
			Assert.assertFalse(germplasmListToBeSaved.getName().equals(savedList.getName()));
			Assert.assertFalse(germplasmListToBeSaved.getNotes().equals(savedList.getNotes()));
			Assert.assertFalse(germplasmListToBeSaved.getDate().equals(savedList.getDate()));
			Assert.assertFalse(germplasmListToBeSaved.getType().equals(savedList.getType()));

			Assert.assertSame(savedList, this.germplasmList);

		} catch (final Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testSaveListOverwriteNonExistingGermplasmList() {

		final GermplasmList germplasmListToBeSaved = new GermplasmList();
		germplasmListToBeSaved.setId(ListComponentTest.TEST_GERMPLASM_LIST_ID);
		germplasmListToBeSaved.setDescription(ListComponentTest.UPDATED_GERMPLASM_LIST_DESCRIPTION_VALUE);
		germplasmListToBeSaved.setName(ListComponentTest.UPDATED_GERMPLASM_LIST_NAME);
		germplasmListToBeSaved.setNotes(ListComponentTest.UPDATED_GERMPLASM_LIST_NOTE);
		germplasmListToBeSaved.setDate(ListComponentTest.UPDATED_GERMPLASM_LIST_DATE);
		germplasmListToBeSaved.setType(ListComponentTest.UPDATED_GERMPLASM_LIST_TYPE);
		germplasmListToBeSaved.setStatus(1);

		Mockito.doReturn(null).when(this.germplasmListManager).getGermplasmListById(ListComponentTest.TEST_GERMPLASM_LIST_ID);

		this.listComponent.saveList(germplasmListToBeSaved);

		final GermplasmList savedList = this.listComponent.getGermplasmList();

		Assert.assertTrue(germplasmListToBeSaved.getId().equals(savedList.getId()));
		Assert.assertFalse(germplasmListToBeSaved.getDescription().equals(savedList.getDescription()));
		Assert.assertFalse(germplasmListToBeSaved.getName().equals(savedList.getName()));
		Assert.assertFalse(germplasmListToBeSaved.getNotes().equals(savedList.getNotes()));
		Assert.assertFalse(germplasmListToBeSaved.getDate().equals(savedList.getDate()));
		Assert.assertFalse(germplasmListToBeSaved.getType().equals(savedList.getType()));

		Assert.assertSame(savedList, this.germplasmList);

	}

	@Test
	public void testInitializeListDataTable() {

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.listComponent.instantiateComponents();
		this.listComponent.initializeListDataTable(tableWithSelectAll);

		final Table table = tableWithSelectAll.getTable();

		Assert.assertEquals(ListComponentTest.CHECK, table.getColumnHeader(ColumnLabels.TAG.getName()));
		Assert.assertEquals(ListComponentTest.HASH, table.getColumnHeader(ColumnLabels.ENTRY_ID.getName()));
		Assert.assertEquals(ListComponentTest.AVAIL_INV, table.getColumnHeader(ColumnLabels.AVAILABLE_INVENTORY.getName()));
		Assert.assertEquals(ListComponentTest.TOTAL_AVAILBALE, table.getColumnHeader(ColumnLabels.TOTAL.getName()));
		Assert.assertEquals(ListComponentTest.STOCKID, table.getColumnHeader(ColumnLabels.STOCKID.getName()));
		Assert.assertEquals(ListComponentTest.GID, table.getColumnHeader(ColumnLabels.GID.getName()));
		Assert.assertEquals(ListComponentTest.ENTRY_CODE, table.getColumnHeader(ColumnLabels.ENTRY_CODE.getName()));
		Assert.assertEquals(ListComponentTest.DESIG, table.getColumnHeader(ColumnLabels.DESIGNATION.getName()));
		Assert.assertEquals(ListComponentTest.CROSS, table.getColumnHeader(ColumnLabels.PARENTAGE.getName()));
		Assert.assertEquals(ListComponentTest.SEED_SOURCE, table.getColumnHeader(ColumnLabels.SEED_SOURCE.getName()));

	}

	@Test
	public void testUserSelectedLotEntriesToCancelReservations(){
		List<ListEntryLotDetails> userSelectedLotEntriesToCancel = ListInventoryDataInitializer.createLotDetails(1);
		this.listComponent.setValidReservationsToSave(this.importedGermplasmListInitializer.createReservations(2));
		Mockito.doReturn(userSelectedLotEntriesToCancel).when(this.listManagerInventoryTable).getSelectedLots();
		this.listComponent.userSelectedLotEntriesToCancelReservations();
		Assert.assertEquals("Expecting Valid reservation to save should have size 0 ", 0,this.listComponent.getValidReservationsToSave().size());
		Assert.assertEquals("Expecting Cancel reservation should have size 3 ", 3,this.listComponent.getValidReservationsToCancel().size());
	}

	@Test
	public void testLockGermplasmList() {
		final ContextUtil contextUtil = Mockito.mock(ContextUtil.class);
		this.listComponent.setContextUtil(contextUtil);
		Mockito.doNothing().when(contextUtil).logProgramActivity(Matchers.anyString(), Matchers.anyString());
		Mockito.doReturn("Test").when(this.messageSource).getMessage(Matchers.any(Message.class));
		Mockito.doReturn(this.germplasmList).when(this.germplasmListManager).getGermplasmListById(this.germplasmList.getId());
		this.listComponent.instantiateComponents();
		this.listComponent.getViewListHeaderWindow().instantiateComponents();

		this.listComponent.toggleGermplasmListStatus();

		Assert.assertEquals(
				"Expecting the that the germplasmList status was changed to locked(101) but returned (" + this.germplasmList.getStatus()
						+ ")", Integer.valueOf(101), this.germplasmList.getStatus());
		Assert.assertEquals(Integer.valueOf(101), this.listComponent.getViewListHeaderWindow().getGermplasmList().getStatus());
		Assert.assertEquals(Integer.valueOf(101),
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getGermplasmList().getStatus());
		Assert.assertEquals("Locked List",
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getStatusValueLabel().toString());
	}

	@Test
	public void testUnlockGermplasmList() {

		Mockito.when(this.messageSource.getMessage(Matchers.any(Message.class))).thenReturn("");

		this.germplasmList.setStatus(101);
		this.listComponent.setListDataTable(new Table());
		this.listComponent.instantiateComponents();
		this.listComponent.getViewListHeaderWindow().instantiateComponents();

		this.listComponent.toggleGermplasmListStatus();

		Assert.assertEquals(
				"Expecting the that the germplasmList status was changed to unlocked(1) but returned (" + this.germplasmList.getStatus()
						+ ")", Integer.valueOf(1), this.germplasmList.getStatus());
		Assert.assertEquals(Integer.valueOf(1), this.listComponent.getViewListHeaderWindow().getGermplasmList().getStatus());
		Assert.assertEquals(Integer.valueOf(1), this.listComponent.getViewListHeaderWindow().getGermplasmList().getStatus());
		Assert.assertEquals(Integer.valueOf(1),
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getGermplasmList().getStatus());
		Assert.assertEquals("Unlocked List",
				this.listComponent.getViewListHeaderWindow().getListHeaderComponent().getStatusValueLabel().toString());
	}

	@Test
	public void testSaveChangesActionVerifyIfTheListTreeIsRefreshedAfterSavingList() {

		Mockito.when(this.messageSource.getMessage(Matchers.any(Message.class))).thenReturn("");

		final Table listDataTable = new Table();
		this.listComponent.setAddColumnContextMenu(this.addColumnContextMenu);
		this.listComponent.instantiateComponents();

		Mockito.when(this.addColumnContextMenu.getListDataCollectionFromTable(listDataTable)).thenReturn(new ArrayList<ListDataInfo>());

		this.listComponent.setListDataTable(listDataTable);
		this.listComponent.saveChangesAction(this.window, false);

	}

	@Test
	public void testSaveReservationChangesAction() {

		this.initializeTableWithTestData();
		List<ListEntryLotDetails> lotDetailsGid = ListInventoryDataInitializer.createLotDetails(1);
		this.listComponent.setHasUnsavedChanges(true);
		this.listComponent.setValidReservationsToSave(this.importedGermplasmListInitializer.createReservations(2));
		this.listComponent.setPersistedReservationToCancel(lotDetailsGid);
		final ContextUtil contextUtil = Mockito.mock(ContextUtil.class);
		this.listComponent.getReserveInventoryAction().setContextUtil(contextUtil);
		this.listComponent.getReserveInventoryAction().setUserDataManager(this.userDataManager);
		this.listComponent.getReserveInventoryAction().setInventoryDataManager(this.inventoryDataManager);
		this.listComponent.getListInventoryTable().setInventoryDataManager(this.inventoryDataManager);
		this.listComponent.setListInventoryTable(this.listManagerInventoryTable);
		this.listComponent.setInventoryViewMenu(this.inventoryViewMenu);
		final User user = new User();
		user.setUserid(12);
		user.setPersonid(123);
		Mockito.doReturn(user).when(this.userDataManager).getUserById(Matchers.anyInt());
		Mockito.when(this.contextUtil.getCurrentUserLocalId()).thenReturn(1);

		List<GermplasmListData> germplasmListData = ListInventoryDataInitializer.createGermplasmListDataWithInventoryDetails();

		Mockito.when(this.inventoryDataManager.getLotDetailsForList(Mockito.isA(Integer.class), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(germplasmListData);

		this.listComponent.saveReservationChangesAction(this.window);

		Assert.assertEquals("Expecting Valid reservation to save should have size 0 ", 0,
				this.listComponent.getValidReservationsToSave().size());
		Assert.assertEquals("Expecting Cancel reservation should have size 0 ", 0,
				this.listComponent.getValidReservationsToCancel().size());

	}

	@Test
	public void testIsInventoryColumn() {
		Assert.assertTrue("Expecting AVAILABLE_INVENTORY as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.AVAILABLE_INVENTORY.getName()));
		Assert.assertTrue("Expecting SEED_RESERVATION as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.SEED_RESERVATION.getName()));
		Assert.assertTrue("Expecting STOCKID as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.STOCKID.getName()));
		Assert.assertFalse("Expecting ENTRY_ID as an inventory column.",
				this.listComponent.isInventoryColumn(ColumnLabels.ENTRY_ID.getName()));
	}

	@Test
	public void testDeleteRemovedGermplasmEntriesFromTableAllEntries() {

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();
		this.listComponent.instantiateComponents();
		this.listComponent.initializeListDataTable(tableWithSelectAll);

		this.listComponent.deleteRemovedGermplasmEntriesFromTable();

		Mockito.verify(this.germplasmListManager).deleteGermplasmListDataByListId(ListComponentTest.TEST_GERMPLASM_LIST_ID);

	}

	@Test
	public void testDeleteRemovedGermplasmEntriesFromTableOnlySelectedEntries() {

		this.initializeTableWithTestData();

		// Add one item to delete from list data table
		this.listComponent.getItemsToDelete().putAll(this.createItemsToDelete(this.listComponent.getListDataTable()));

		this.listComponent.deleteRemovedGermplasmEntriesFromTable();

		// deleteGermplasmListDataByListIdLrecId should only be called once
		Mockito.verify(this.germplasmListManager, Mockito.times(1))
				.deleteGermplasmListDataByListIdLrecId(Mockito.eq(TEST_GERMPLASM_LIST_ID), Mockito.anyInt());

		Assert.assertTrue(this.listComponent.getItemsToDelete().isEmpty());

	}

	@Test
	public void testMarkLinesAsFixedActionWithSelectedEntries() {

		this.initializeTableWithTestData();

		// This selects all items in the table
		final Table table = this.listComponent.getListDataTable();
		table.setValue(table.getItemIds());

		this.listComponent.markLinesAsFixedAction();

		Mockito.verify(window).addWindow(Mockito.any(GermplasmGroupingComponent.class));

	}

	@Test
	public void testMarkLinesAsFixedActionWithoutSelectedEntries() {

		this.initializeTableWithTestData();

		// This removes the selected items in the table
		final Table table = this.listComponent.getListDataTable();
		table.setValue(null);

		this.listComponent.markLinesAsFixedAction();

		Mockito.verify(messageSource).getMessage(Message.ERROR_MARK_LINES_AS_FIXED_NOTHING_SELECTED);
		Mockito.verify(window).showNotification(Mockito.any(Window.Notification.class));

	}

	@Test
	public void testAssignCodesActionWithSelectedEntries() {

		this.initializeTableWithTestData();

		// This selects all items in the table
		final Table table = this.listComponent.getListDataTable();
		table.setValue(table.getItemIds());

		this.listComponent.assignCodesAction();

		Mockito.verify(window).addWindow(Mockito.any(AssignCodesDialog.class));

	}

	@Test
	public void testAssignCodesActionWithoutSelectedEntries() {

		this.initializeTableWithTestData();

		// This removes the selected items in the table
		final Table table = this.listComponent.getListDataTable();
		table.setValue(null);

		this.listComponent.assignCodesAction();

		Mockito.verify(messageSource).getMessage(Message.ERROR_ASSIGN_CODES_NOTHING_SELECTED);
		Mockito.verify(window).showNotification(Mockito.any(Window.Notification.class));

	}

	@Test
	public void testExtractGidListFromListDataTable() {

		this.initializeTableWithTestData();

		// This selects all items in the table
		final Table table = this.listComponent.getListDataTable();
		table.setValue(table.getItemIds());

		final Set<Integer> result = this.listComponent.extractGidListFromListDataTable(this.listComponent.getListDataTable());

		Assert.assertEquals(TEST_GERMPLASM_NO_OF_ENTRIES.intValue(), result.size());

		Collection<Integer> selectedRows = (Collection<Integer>) table.getValue();
		Iterator<Integer> selectedRowsIterator = selectedRows.iterator();
		for (Integer gid : result) {
			final Item selectedRowItem = table.getItem(selectedRowsIterator.next());
			final Button gidCell = (Button) selectedRowItem.getItemProperty(ColumnLabels.GID.getName()).getValue();
			Assert.assertEquals("The order of extracted GIDs should be same order as the entries in the table.",
					Integer.valueOf(gidCell.getCaption()), gid);
		}

	}

	@Test
	public void testExtractGidListFromListDataTableWithoutSelectedEntries() {

		this.initializeTableWithTestData();

		// This removes all items in the table
		final Table table = this.listComponent.getListDataTable();
		table.setValue(null);

		final Set<Integer> result = this.listComponent.extractGidListFromListDataTable(this.listComponent.getListDataTable());

		Assert.assertEquals(0, result.size());

	}

	@Test
	public void testCreateLabelsActionWithNoReservationForAnyListEntries() {
		this.listComponent.createLabelsAction();
		Mockito.verify(this.messageSource).getMessage(Message.ERROR_COULD_NOT_CREATE_LABELS_WITHOUT_RESERVATION);
		Mockito.verify(this.messageSource).getMessage(Message.PRINT_LABELS);
	}

	private void initializeTableWithTestData() {

		Mockito.when(inventoryDataManager.getLotCountsForList(TEST_GERMPLASM_LIST_ID, 0, TEST_GERMPLASM_NO_OF_ENTRIES))
				.thenReturn(ListInventoryDataInitializer.createGermplasmListDataWithInventoryDetails());

		final TableWithSelectAllLayout tableWithSelectAll = new TableWithSelectAllLayout(ColumnLabels.TAG.getName());
		tableWithSelectAll.instantiateComponents();

		this.listComponent.instantiateComponents();
		this.listComponent.initializeListDataTable(tableWithSelectAll);
		this.listComponent.setListDataTable(tableWithSelectAll.getTable());
		this.listComponent.initializeValues();

	}

	private void setUpOntologyManager() {

		Mockito.when(this.ontologyDataManager.getTermById(TermId.AVAILABLE_INVENTORY.getId()))
				.thenReturn(this.createTerm(TermId.AVAILABLE_INVENTORY.getId(), ListComponentTest.AVAIL_INV));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.TOTAL_INVENTORY.getId()))
				.thenReturn(this.createTerm(TermId.TOTAL_INVENTORY.getId(), ListComponentTest.TOTAL_AVAILBALE));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.GID.getId()))
				.thenReturn(this.createTerm(TermId.GID.getId(), ListComponentTest.GID));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.ENTRY_CODE.getId()))
				.thenReturn(this.createTerm(TermId.ENTRY_CODE.getId(), ListComponentTest.ENTRY_CODE));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.DESIG.getId()))
				.thenReturn(this.createTerm(TermId.DESIG.getId(), ListComponentTest.DESIG));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.CROSS.getId()))
				.thenReturn(this.createTerm(TermId.CROSS.getId(), ListComponentTest.CROSS));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.SEED_SOURCE.getId()))
				.thenReturn(this.createTerm(TermId.SEED_SOURCE.getId(), ListComponentTest.SEED_SOURCE));

		Mockito.when(this.ontologyDataManager.getTermById(TermId.STOCKID.getId()))
				.thenReturn(this.createTerm(TermId.STOCKID.getId(), ListComponentTest.STOCKID));

	}

	private void setUpWorkbenchDataManager() {
		final Project dummyProject = new Project();
		dummyProject.setProjectId((long) 5);

		final Integer userId = 5;
		try {
			Mockito.when(this.workbenchDataManager.getLastOpenedProject(userId)).thenReturn(dummyProject);
			Mockito.when(this.workbenchDataManager.getLocalIbdbUserId(userId, dummyProject.getProjectId()))
					.thenReturn(EXPECTED_USER_ID);

		} catch (final MiddlewareQueryException e) {
			Assert.fail("Failed to create an ibdbuser instance.");
		}
	}

	private Term createTerm(final int id, final String name) {
		final Term term = new Term(id, name, "");
		return term;
	}

	private GermplasmListNewColumnsInfo createGermplasmListNewColumnInfo(final int listId) {
		final GermplasmListNewColumnsInfo germplasmListNewColumnsInfo = new GermplasmListNewColumnsInfo(listId);
		germplasmListNewColumnsInfo.setColumnValuesMap(new HashMap<String, List<ListDataColumnValues>>());
		return germplasmListNewColumnsInfo;
	}

	private Map<Object, String> createItemsToDelete(final Table table) {

		final Map<Object, String> itemsToDelete = new HashMap<>();

		// delete the first record from the germplasm list data table
		itemsToDelete.put(1, "Designation 1");

		return itemsToDelete;
	}

	private void setUpListComponent() {

		this.germplasmList =
				GermplasmListTestDataInitializer.createGermplasmListWithListData(TEST_GERMPLASM_LIST_ID, TEST_GERMPLASM_NO_OF_ENTRIES);
		this.germplasmList.setStatus(1);
		this.listComponent.setGermplasmList(this.germplasmList);
		this.listComponent.setParent(parentComponent);
		this.listComponent.setCloseLotDiscardInventoryAction(closeLotDiscardInventoryAction);

		Mockito.when(this.germplasmListManager.countGermplasmListDataByListId(TEST_GERMPLASM_LIST_ID))
				.thenReturn(Long.valueOf(TEST_GERMPLASM_NO_OF_ENTRIES));
		Mockito.when(this.germplasmListManager.getGermplasmListById(TEST_GERMPLASM_LIST_ID)).thenReturn(this.germplasmList);
		Mockito.when(this.germplasmListManager.getAdditionalColumnsForList(TEST_GERMPLASM_LIST_ID))
				.thenReturn(this.createGermplasmListNewColumnInfo(TEST_GERMPLASM_LIST_ID));

		Mockito.when(this.parentComponent.getWindow()).thenReturn(window);
		Mockito.when(this.source.getModeView()).thenReturn(ModeView.LIST_VIEW);
		Mockito.when(this.source.getListSelectionComponent()).thenReturn(this.listSelectionComponent);
		Mockito.when(this.source.getWindow()).thenReturn(window);
		Mockito.when(this.listSelectionComponent.getListDetailsLayout()).thenReturn(this.listDetailsLayout);
		Mockito.when(this.crossExpansionProperties.getProfile()).thenReturn("");

		Mockito.when(this.messageSource.getMessage(Mockito.any(Message.class))).thenReturn("");
		Mockito.when(this.messageSource.getMessage(Message.CHECK_ICON)).thenReturn(ListComponentTest.CHECK);
		Mockito.when(this.messageSource.getMessage(Message.HASHTAG)).thenReturn(ListComponentTest.HASH);
		Mockito.when(this.contextUtil.getProjectInContext()).thenReturn(this.createProject());

		Mockito.doNothing().when(this.contextUtil).logProgramActivity(Mockito.anyString(), Mockito.anyString());

	}

	@Test
	public void testExportSeedPreparationListWithUnsavedReservations() throws SeedInventoryExportException {
		final Map<ListEntryLotDetails, Double> unsavedReservations = new HashMap<>();
		unsavedReservations.put(new ListEntryLotDetails(), new Double(10));
		this.listComponent.setValidReservationsToSave(unsavedReservations);
		final SeedInventoryListExporter exporterMock = Mockito.mock(SeedInventoryListExporter.class);
		this.listComponent.exportSeedPreparationList(exporterMock);
		Mockito.verify(this.messageSource).getMessage(Message.UNSAVED_RESERVATION_WARNING);
		Mockito.verify(exporterMock).exportSeedPreparationList();
	}

	@Test
	public void testExportSeedPreparationListWithNoUnsavedReservations() throws SeedInventoryExportException {
		this.listComponent.setValidReservationsToSave(null);
		final SeedInventoryListExporter exporterMock = Mockito.mock(SeedInventoryListExporter.class);
		this.listComponent.exportSeedPreparationList(exporterMock);
		Mockito.verify(this.messageSource, Mockito.never()).getMessage(Message.UNSAVED_RESERVATION_WARNING);
		Mockito.verify(exporterMock).exportSeedPreparationList();
	}

	@Test
	public void testSaveReservationContextItemClickWithConcurrentUsersFailToSave() throws Exception {
		final ListComponent.InventoryViewMenuClickListner inner = this.listComponent.new InventoryViewMenuClickListner();
		final ContextMenu.ClickEvent clickEventMock = Mockito.mock(ContextMenu.ClickEvent.class);
		final ReserveInventoryAction reserveInventoryAction = Mockito.mock(ReserveInventoryAction.class);

		ContextMenu.ContextMenuItem contextMenuItem = Mockito.mock(ContextMenu.ContextMenuItem.class);
		Mockito.when(contextMenuItem.getName()).thenReturn("Save Changes");

		Mockito.when(clickEventMock.getClickedItem()).thenReturn(contextMenuItem);

		Mockito.when(messageSource.getMessage(Message.SAVE_RESERVATIONS)).thenReturn("Save Changes");

		int threads = 2;

		this.listComponent.setHasUnsavedChanges(true);

		this.listComponent.setReserveInventoryAction(reserveInventoryAction);

		ExecutorService threadPool = Executors.newFixedThreadPool(threads);

		final Map<ListEntryLotDetails, Double> unsavedReservations = new HashMap<>();
		unsavedReservations.put(new ListEntryLotDetails(), new Double(10));
		Mockito.when(reserveInventoryAction.saveReserveTransactions(Mockito.anyMap(), Mockito.anyInt())).thenReturn(false);

		Future<Void> threadOne = threadPool.submit(new Callable<Void>() {

			@Override
			public Void call() {
				inner.contextItemClick(clickEventMock);
				return null;
			}
		});

		Future<Void> threadTwo = threadPool.submit(new Callable<Void>() {

			@Override
			public Void call() {
				inner.contextItemClick(clickEventMock);
				return null;
			}
		});

		threadPool.shutdown();
		while (!threadPool.isTerminated()) {
		}
		Mockito.verify(this.messageSource, Mockito.times(2)).getMessage(Message.SAVE_RESERVATIONS);
		Mockito.verify(this.messageSource, Mockito.times(2)).getMessage(Message.INVENTORY_NOT_AVAILABLE_BALANCE);
		Mockito.verify(this.messageSource, Mockito.never()).getMessage(Message.SAVE_RESERVED_AND_CANCELLED_RESERVATION);
	}

	@Test
	public void testCloseLotsWithNoLotsSelected() throws Exception {
		this.listComponent.closeLotsActions();
		Mockito.verify(this.messageSource).getMessage(Message.NO_LOTS_SELECTED_ERROR);
	}

	@Test
	public void testCloseLotsWithUnCommittedReservation() throws Exception {
		List<ListEntryLotDetails> userSelectedLotEntriesToClose = ListInventoryDataInitializer.createLotDetails(1,1);
		Mockito.doReturn(userSelectedLotEntriesToClose).when(this.listManagerInventoryTable).getSelectedLots();

		this.listComponent.closeLotsActions();
		Mockito.verify(this.messageSource).getMessage(Message.LOTS_HAVE_AVAILABLE_BALANCE_UNCOMMITTED_RESERVATION_ERROR);
	}

	@Test
	public void testCloseLotsWithValidLotsHavingNoBalanceToClose() throws Exception {
		List<ListEntryLotDetails> userSelectedLotEntriesToClose = ListInventoryDataInitializer.createLotDetails(1,1);
		userSelectedLotEntriesToClose.get(0).setReservedTotal(0D);
		userSelectedLotEntriesToClose.get(0).setActualLotBalance(0D);

		final List<GermplasmListData> inventoryDetails = ListInventoryDataInitializer.createGermplasmListDataWithInventoryDetails(1);
		inventoryDetails.get(0).getInventoryInfo().setLotRows(userSelectedLotEntriesToClose);

		Mockito.when(this.inventoryDataManager.getLotDetailsForList(Mockito.isA(Integer.class), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(inventoryDetails);

		Lot activeLot = new Lot();
		activeLot.setStatus(LotStatus.ACTIVE.getIntValue());

		Mockito.when(this.inventoryDataManager.getLotsByIdList(Mockito.isA(List.class))).thenReturn(Lists.newArrayList(activeLot));
		final User user = new User();
		user.setUserid(12);
		user.setPersonid(123);

		Mockito.doReturn(user).when(this.userDataManager).getUserById(Matchers.anyInt());
		Mockito.when(this.contextUtil.getCurrentUserLocalId()).thenReturn(1);

		Mockito.doReturn(userSelectedLotEntriesToClose).when(this.listManagerInventoryTable).getSelectedLots();

		this.listComponent.closeLotsActions();

		Mockito.verify(this.inventoryDataManager, Mockito.times(1)).addTransactions(Matchers.anyList());
		Mockito.verify(this.inventoryDataManager, Mockito.times(1)).updateLots(Matchers.anyList());
		Mockito.verify(this.messageSource).getMessage(Message.LOTS_CLOSED_SUCCESSFULLY);
	}

	@Test
	public void testCloseLotsWithValidLotsHavingActualBalanceToClose() throws Exception {
		List<ListEntryLotDetails> userSelectedLotEntriesToClose = ListInventoryDataInitializer.createLotDetails(1,1);
		userSelectedLotEntriesToClose.get(0).setReservedTotal(0D);
		userSelectedLotEntriesToClose.get(0).setActualLotBalance(5D);

		final List<GermplasmListData> inventoryDetails = ListInventoryDataInitializer.createGermplasmListDataWithInventoryDetails(1);
		inventoryDetails.get(0).getInventoryInfo().setLotRows(userSelectedLotEntriesToClose);

		Mockito.when(this.inventoryDataManager.getLotDetailsForList(Mockito.isA(Integer.class), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(inventoryDetails);

		Lot activeLot = new Lot();
		activeLot.setStatus(LotStatus.ACTIVE.getIntValue());

		Mockito.when(this.inventoryDataManager.getLotsByIdList(Mockito.isA(List.class))).thenReturn(Lists.newArrayList(activeLot));
		final User user = new User();
		user.setUserid(12);
		user.setPersonid(123);

		Mockito.doReturn(user).when(this.userDataManager).getUserById(Matchers.anyInt());
		Mockito.when(this.contextUtil.getCurrentUserLocalId()).thenReturn(1);

		Mockito.doReturn(userSelectedLotEntriesToClose).when(this.listManagerInventoryTable).getSelectedLots();

		this.listComponent.closeLotsActions();

		Mockito.verify(this.closeLotDiscardInventoryAction, Mockito.times(1)).setLotDetails(Matchers.anyList());
		Mockito.verify(this.closeLotDiscardInventoryAction, Mockito.times(1)).processLotCloseWithDiscard();
	}

	@Test
	public void testReserveInventoryActionForLotsWithoutScale() {

		this.listComponent.setInventoryViewMenu(this.inventoryViewMenu);
		this.listComponent.setListInventoryTable(this.listManagerInventoryTable);
		List<ListEntryLotDetails> userSelectedLotEntriesToCancel = ListInventoryDataInitializer.createLotDetails(1);
		userSelectedLotEntriesToCancel.get(0).setScaleId(null);
		Mockito.doReturn(userSelectedLotEntriesToCancel).when(this.listManagerInventoryTable).getSelectedLots();
		Mockito.doReturn(true).when(this.listComponent.getInventoryViewMenu()).isVisible();
		this.listComponent.reserveInventoryAction();

		Mockito.verify(this.messageSource).getMessage(Message.COULD_NOT_MAKE_ANY_RESERVATION_ALL_SELECTED_LOTS_HAS_INSUFFICIENT_BALANCES);

	}

	private Project createProject() {

		final Project project = new Project();
		project.setCropType(new CropType(CropType.CropEnum.MAIZE.name()));
		return project;

	}


	@Test
	public void testRemoveSelectedGermplasmButtonClickAction() {
		this.initializeTableWithTestData();
		this.listComponent.removeSelectedGermplasmButtonClickAction();
		Mockito.verify(this.messageSource).getMessage(Message.ERROR_REMOVING_GERMPLASM);
		Mockito.verify(window).showNotification(Mockito.any(Window.Notification.class));

	}

}
