/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.breeding.manager.crossingmanager.actions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Triple;
import org.generationcp.breeding.manager.crossingmanager.pojos.CrossParents;
import org.generationcp.breeding.manager.crossingmanager.pojos.CrossesMade;
import org.generationcp.breeding.manager.crossingmanager.pojos.GermplasmListEntry;
import org.generationcp.commons.spring.util.ContextUtil;
import org.generationcp.commons.util.CollectionTransformationUtil;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Creates Germplasm, GermplasmList, GermplasmListData records for crosses defined. Adds a ProjectActivity (Workbench) record for the save
 * action.
 *
 * @author Darla Ani
 */
@Configurable
public class SaveCrossesMadeAction implements Serializable {
	// Save temp list as deleted
	// TODO Refactor liststatus to bit array so a list can have multiple status
	private static final Integer GERMPLASM_LIST_STATUS = 9;

	private static final Integer GERMPLASM_GNPGS = 2;
	private static final Integer GERMPLASM_GRPLCE = 0;
	private static final Integer GERMPLASM_LGID = 0;
	private static final Integer GERMPLASM_MGID = 0;
	private static final Integer GERMPLASM_REFID = 0;

	private static final Integer NAME_REFID = 0;

	private static final Integer LIST_DATA_STATUS = 0;
	private static final Integer LIST_DATA_LRECID = 0;

	private static final String WB_ACTIVITY_NAME = "Created a list of crosses";
	private static final String WB_ACTIVITY_DESCRIPTION = "List cross id = ";

	private static final long serialVersionUID = -6273933938066390358L;

	@Autowired
	private GermplasmListManager germplasmListManager;

	@Autowired
	private GermplasmDataManager germplasmManager;

	@Autowired
	private ContextUtil contextUtil;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private PedigreeService pedigreeService;

	private GermplasmList germplasmList;
	private List<GermplasmListData> existingListEntries = new ArrayList<GermplasmListData>();
	private List<Germplasm> existingGermplasms = new ArrayList<Germplasm>();
	private final Map<Germplasm, GermplasmListData> germplasmToListDataMap = new LinkedHashMap<Germplasm, GermplasmListData>();

	public SaveCrossesMadeAction(final GermplasmList germplasmList) {
		this.germplasmList = germplasmList;
	}

	SaveCrossesMadeAction() {
		super();
	}

	/**
	 * Saves records in Germplasm, GermplasmList and GermplasmListData, ProjectActivity (Workbench).
	 *
	 * @param crossesMade where crosses information is defined
	 * @return id of new Germplasm List created
	 */
	public GermplasmList saveRecords(final CrossesMade crossesMade) {
		final TransactionTemplate transactionTemplate = new TransactionTemplate(this.transactionManager);
		return transactionTemplate.execute(new TransactionCallback<GermplasmList>() {

			@Override
			public GermplasmList doInTransaction(final TransactionStatus transactionStatus) {
				SaveCrossesMadeAction.this.updateConstantFields(crossesMade);

				final List<Integer> germplasmIDs = SaveCrossesMadeAction.this.saveGermplasmsAndNames(crossesMade);

				final GermplasmList list = SaveCrossesMadeAction.this.saveGermplasmListRecord(crossesMade);
				SaveCrossesMadeAction.this.saveGermplasmListDataRecords(crossesMade, germplasmIDs, list);

				// log project activity in Workbench
				if (SaveCrossesMadeAction.this.germplasmList == null) {
					SaveCrossesMadeAction.this.contextUtil.logProgramActivity(SaveCrossesMadeAction.WB_ACTIVITY_NAME,
							SaveCrossesMadeAction.WB_ACTIVITY_DESCRIPTION + list.getId());
				}

				return list;
			}
		});

	}

	// Here is where crossed germplasm is saved.
	List<Integer> saveGermplasmsAndNames(final CrossesMade crossesMade) {
		List<Integer> germplasmIDs = new ArrayList<>();

		final List<Triple<Germplasm, Name, List<Progenitor>>> currentCrossesList = crossesMade.getCrossesList();
		List<Triple<Germplasm, Name, List<Progenitor>>> crossesToInsert = new ArrayList<>();
		if (this.germplasmList == null) {
			crossesToInsert = currentCrossesList;
		}

		if (!crossesToInsert.isEmpty()) {
			germplasmIDs = this.germplasmManager.addGermplasm(crossesToInsert);
		}
		return germplasmIDs;
	}

	private void retrieveGermplasmsOfList() {
		this.germplasmToListDataMap.clear();

		final List<GermplasmListData> allExistingEntries =
				this.germplasmListManager.getGermplasmListDataByListId(this.germplasmList.getId());

		// Add only non deleted list data
		this.existingListEntries = new ArrayList<>();

		for (final GermplasmListData germplasmListData : allExistingEntries) {
			if (germplasmListData.getStatus() != 9) {
				this.existingListEntries.add(germplasmListData);
			}
		}

		final List<Integer> gids = new ArrayList<>();
		for (final GermplasmListData entry : this.existingListEntries) {
			gids.add(entry.getGid());
		}
		this.existingGermplasms = this.germplasmManager.getGermplasms(gids);

		for (final Germplasm germplasm : this.existingGermplasms) {
			for (final GermplasmListData entry : this.existingListEntries) {
				if (entry.getGid().equals(germplasm.getGid())) {
					this.germplasmToListDataMap.put(germplasm, entry);
				}
			}
		}
	}

	private boolean haveSameParents(final Germplasm g1, final Germplasm g2) {
		return g1.getGpid1().equals(g2.getGpid1()) && g1.getGpid2().equals(g2.getGpid2());
	}

	GermplasmList saveGermplasmListRecord(final CrossesMade crossesMade) {
		int listId;
		final GermplasmList listToSave = crossesMade.getGermplasmList();
		listToSave.setProgramUUID(this.contextUtil.getCurrentProgramUUID());
		if (this.germplasmList == null) {
			listToSave.setProgramUUID(this.contextUtil.getCurrentProgramUUID());
			listId = this.germplasmListManager.addGermplasmList(listToSave);
		} else {
			// GCP-8225 : set the updates manually on List object so that list entries are not deleted
			this.germplasmList = this.germplasmListManager.getGermplasmListById(this.germplasmList.getId());

			this.germplasmList.setName(listToSave.getName());
			this.germplasmList.setDescription(listToSave.getDescription());
			this.germplasmList.setType(listToSave.getType());
			this.germplasmList.setDate(listToSave.getDate());
			this.germplasmList.setNotes(listToSave.getNotes());
			this.germplasmList.setProgramUUID(this.contextUtil.getCurrentProgramUUID());

			listId = this.germplasmListManager.updateGermplasmList(this.germplasmList);
		}

		return this.germplasmListManager.getGermplasmListById(listId);
	}

	void saveGermplasmListDataRecords(final CrossesMade crossesMade, final List<Integer> germplasmIDs, final GermplasmList list) {

		this.deleteRemovedListData(crossesMade);
		this.addNewGermplasmListData(crossesMade, germplasmIDs, list);
	}

	private void deleteRemovedListData(final CrossesMade crossesMade) {
		final List<GermplasmListData> retainedCrosses = new ArrayList<>();
		final List<Triple<Germplasm, Name, List<Progenitor>>> crossesList = crossesMade.getCrossesList();
		for (int i = 0; i < this.existingGermplasms.size(); i++) {
			final Germplasm existingGermplasm = this.existingGermplasms.get(i);
			for (final Triple<Germplasm, Name, List<Progenitor>> triple: crossesList) {
				final Germplasm currentGermplasm = triple.getLeft();
				if (this.haveSameParents(currentGermplasm, existingGermplasm)) {
					final GermplasmListData germplasmListData = this.germplasmToListDataMap.get(existingGermplasm);
					retainedCrosses.add(germplasmListData);
					break;
				}
			}
		}

		final List<GermplasmListData> listToDelete = new ArrayList<>(this.existingListEntries);
		listToDelete.removeAll(retainedCrosses);

		if (!listToDelete.isEmpty()) {
			this.germplasmListManager.deleteGermplasmListData(listToDelete);
		}

		// Update "exsitingListEntries", this is used to assign the entry id
		this.existingListEntries = new ArrayList<>();

		final List<GermplasmListData> allExistingEntries = new ArrayList<>();

		if (this.germplasmList != null) {
			allExistingEntries.addAll(this.germplasmListManager.getGermplasmListDataByListId(this.germplasmList.getId()));
			Integer entryId = 1;
			for (final GermplasmListData germplasmListData : allExistingEntries) {
				if (germplasmListData.getStatus() != 9) {
					germplasmListData.setEntryId(entryId);
					this.existingListEntries.add(germplasmListData);
					entryId++;
				}
			}
		}
		this.germplasmListManager.updateGermplasmListData(this.existingListEntries);
	}

	private Map<Integer, String> updateWithActualPedigree(final Set<Germplasm> gids) {
		final ImmutableSet<Integer> allGidsFromGermplasmListDataList = CollectionTransformationUtil.getAllGidsFromGermplasmList(gids);

		final Iterable<List<Integer>> partition = Iterables.partition(allGidsFromGermplasmListDataList, 5000);
		final Map<Integer, String> resultMap = new HashMap<>();
		for (final List<Integer> partitionedList : partition) {
			resultMap.putAll(
					this.pedigreeService.getCrossExpansions(new HashSet<Integer>(partitionedList), null, this.crossExpansionProperties));
		}
		return resultMap;
	}

	private void addNewGermplasmListData(final CrossesMade crossesMade, final List<Integer> germplasmIDs, final GermplasmList list) {
		final Iterator<Integer> germplasmIdIterator = germplasmIDs.iterator();
		final List<GermplasmListData> listToSave = new ArrayList<>();

		int ctr = 0;
		int entryId = this.existingListEntries.size() + 1;

		final List<Triple<Germplasm, Name, List<Progenitor>>> crossesList = crossesMade.getCrossesList();
		final Set<Germplasm> germplasm = new LinkedHashSet<>();
		final Set<Name> names = new LinkedHashSet<>();
		for(final Triple<Germplasm, Name, List<Progenitor>> triple: crossesList) {
			germplasm.add(triple.getLeft());
			names.add(triple.getMiddle());
		}
		final Map<Integer, String> pedigreeMap = this.updateWithActualPedigree(germplasm);

		for (final Name name : names) {
			if (this.germplasmList == null) {
				final Integer gid = germplasmIdIterator.next();
				final String designation = name.getNval();
				final String seedsource = this.getFemaleMaleCrossName(crossesMade, designation, ctr);

				final GermplasmListData germplasmListData =
						this.buildGermplasmListData(list, gid, entryId, designation, seedsource, pedigreeMap);

				listToSave.add(germplasmListData);
				entryId++;

			}
			ctr++;
		}

		if (!listToSave.isEmpty()) {
			this.germplasmListManager.addGermplasmListData(listToSave);
		}
	}

	/*
	 * If current names were generated using prefix in tool, retrieve the <female parent>/<male parent> cross names from oldCrossNames in
	 * CrossesMade
	 */
	private String getFemaleMaleCrossName(final CrossesMade crossesMade, final String designation, final Integer ctr) {

		final List<GermplasmListEntry> oldCrossNames = crossesMade.getOldCrossNames();
		if (oldCrossNames != null) {
			return oldCrossNames.get(ctr).getDesignation();
		}
		return designation;
	}

	private GermplasmListData buildGermplasmListData(final GermplasmList list, final Integer gid, final int entryId,
			final String designation, String seedSource, final Map<Integer, String> pedigreeMap) {
		final GermplasmListData germplasmListData = new GermplasmListData();
		germplasmListData.setList(list);
		germplasmListData.setGid(gid);
		germplasmListData.setEntryId(entryId);
		germplasmListData.setEntryCode(String.valueOf(entryId));
		if(seedSource.length() > 255) {
			seedSource = seedSource.substring(0, 255);
		}
		germplasmListData.setSeedSource(seedSource);
		germplasmListData.setDesignation(designation);
		germplasmListData.setStatus(SaveCrossesMadeAction.LIST_DATA_STATUS);
		germplasmListData.setGroupName(pedigreeMap.get(gid));
		germplasmListData.setLocalRecordId(SaveCrossesMadeAction.LIST_DATA_LRECID);

		return germplasmListData;
	}

	private void updateConstantFields(final CrossesMade crossesMade) {
		final Integer ibdbUserId = this.contextUtil.getCurrentUserLocalId();

		for (final Triple<Germplasm, Name, List<Progenitor>> triple : crossesMade.getCrossesList()) {
			final Germplasm g = triple.getLeft();
			g.setGnpgs(SaveCrossesMadeAction.GERMPLASM_GNPGS);
			g.setGrplce(SaveCrossesMadeAction.GERMPLASM_GRPLCE);
			g.setLgid(SaveCrossesMadeAction.GERMPLASM_LGID);
			g.setMgid(SaveCrossesMadeAction.GERMPLASM_MGID);
			g.setUserId(ibdbUserId);
			g.setReferenceId(SaveCrossesMadeAction.GERMPLASM_REFID);

			final Name n = triple.getMiddle();
			n.setReferenceId(SaveCrossesMadeAction.NAME_REFID);
			n.setUserId(ibdbUserId);
		}

		final GermplasmList list = crossesMade.getGermplasmList();
		if (list != null) {
			list.setStatus(SaveCrossesMadeAction.GERMPLASM_LIST_STATUS);
			list.setUserId(ibdbUserId);
		}

	}

	public void updateSeedSource(final Collection<CrossParents> crossParents) {
		this.retrieveGermplasmsOfList();
		for (final CrossParents parents : crossParents) {
			final Germplasm currentGermplasm = new Germplasm();
			currentGermplasm.setGpid1(parents.getFemaleParent().getGid());
			currentGermplasm.setGpid2(parents.getMaleParent().getGid());

			for (final Germplasm existingGermplasm : this.existingGermplasms) {
				if (this.haveSameParents(currentGermplasm, existingGermplasm)) {
					final GermplasmListData germplasmListData = this.germplasmToListDataMap.get(existingGermplasm);
					if (germplasmListData != null) {
						germplasmListData.setSeedSource(parents.getSeedSource());
					}
				}
			}
		}
		this.germplasmListManager.updateGermplasmListData(this.existingListEntries);
	}

	/**
	 * For Test Only
	 *
	 * @param contextUtil
	 */
	void setContextUtil(final ContextUtil contextUtil) {
		this.contextUtil = contextUtil;
	}

	/**
	 * For Test Only
	 *
	 * @param germplasmListManager
	 */
	void setGermplasmListManager(final GermplasmListManager germplasmListManager) {
		this.germplasmListManager = germplasmListManager;
	}

	protected void setTransactionManager(final PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	void setCrossExpansionProperties(final CrossExpansionProperties crossExpansionProperties) {
		this.crossExpansionProperties = crossExpansionProperties;
	}
}
