package org.generationcp.breeding.manager.listmanager.listeners;

import com.vaadin.ui.Button;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.generationcp.breeding.manager.listmanager.FillWithAttributeWindow;
import org.generationcp.breeding.manager.listmanager.api.AddColumnSource;
import org.generationcp.breeding.manager.listmanager.util.FillWithOption;
import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.commons.vaadin.spring.SimpleResourceBundleMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.vaadin.peter.contextmenu.ContextMenu;
import org.vaadin.peter.contextmenu.ContextMenu.ClickEvent;
import org.vaadin.peter.contextmenu.ContextMenu.ContextMenuItem;

@Configurable
public class AddColumnMenuItemClickListenerForGermplasmSearch implements ContextMenu.ClickListener {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SimpleResourceBundleMessageSource messageSource;

	private final AddColumnSource addColumnSource;

	public AddColumnMenuItemClickListenerForGermplasmSearch(final AddColumnSource addColumnSource) {
		this.addColumnSource = addColumnSource;
	}
	
	@Override
	public void contextItemClick(final ClickEvent event) {
		final ContextMenuItem clickedItem = event.getClickedItem();
		final String clickedOptionName = clickedItem.getName();
		if (this.messageSource.getMessage(FillWithOption.FILL_WITH_PREFERRED_ID.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addPreferredIdColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_PREFERRED_NAME.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addPreferredNameColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_GERMPLASM_DATE.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addGermplasmDateColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_LOCATION.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addLocationColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_BREEDING_METHOD_NAME.getMessageKey()).equals(clickedOptionName)) { 
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addMethodNameColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_BREEDING_METHOD_ABBREV.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addMethodAbbrevColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_BREEDING_METHOD_NUMBER.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addMethodNumberColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_BREEDING_METHOD_GROUP.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addMethodGroupColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_CROSS_FEMALE_GID.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addCrossFemaleGidColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_CROSS_FEMALE_NAME.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addCrossFemalePrefNameColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_CROSS_MALE_GID.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addCrossMaleGIDColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_CROSS_MALE_NAME.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.addCrossMalePrefNameColumn();
		} else if (this.messageSource.getMessage(FillWithOption.FILL_WITH_ATTRIBUTE.getMessageKey()).equals(clickedOptionName)) {
			AddColumnMenuItemClickListenerForGermplasmSearch.this.displayFillWithAttributeWindow();
		}
	}

	private void addPreferredIdColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.PREFERRED_ID.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.PREFERRED_ID);

		}
	}

	private void addPreferredNameColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.PREFERRED_NAME.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.PREFERRED_NAME);
		}
	}

	private void addGermplasmDateColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.GERMPLASM_DATE.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.GERMPLASM_DATE);
		}
	}

	private void addLocationColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.GERMPLASM_LOCATION.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.GERMPLASM_LOCATION);
		}
	}

	private void addMethodNameColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.BREEDING_METHOD_NAME.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.BREEDING_METHOD_NAME);
		}
	}

	private void addMethodAbbrevColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.BREEDING_METHOD_ABBREVIATION);
		}
	}

	private void addMethodNumberColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.BREEDING_METHOD_NUMBER.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.BREEDING_METHOD_NUMBER);
		}
	}

	private void addMethodGroupColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.BREEDING_METHOD_GROUP.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.BREEDING_METHOD_GROUP);
		}
	}

	private void addCrossMaleGIDColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.CROSS_MALE_GID.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.CROSS_MALE_GID);
		}
	}

	private void addCrossMalePrefNameColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.CROSS_MALE_PREFERRED_NAME);
		}
	}

	private void addCrossFemaleGidColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.CROSS_FEMALE_GID.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.CROSS_FEMALE_GID);
		}
	}

	private void addCrossFemalePrefNameColumn() {
		if (!this.addColumnSource.columnExists(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName())) {
			this.addColumnSource.addColumn(ColumnLabels.CROSS_FEMALE_PREFERRED_NAME);
		}
	}

	private void displayFillWithAttributeWindow() {
		final Window mainWindow = this.addColumnSource.getWindow();
		// 2nd parameter is null because user is yet to select the attribute type, which will become column name
		final Window attributeWindow = new FillWithAttributeWindow(this.addColumnSource, null , new Button.ClickListener() {

			@Override
			public void buttonClick(final Button.ClickEvent clickEvent) {

				FillWithAttributeWindow attributeWindow = (FillWithAttributeWindow) ((Button) clickEvent.getSource()).getWindow();

				final Integer attributeTypeId = (Integer) attributeWindow.getAttributeBox().getValue();
				if (attributeTypeId != null) {
					final String attributeType = attributeWindow.getAttributeBox().getItemCaption(attributeTypeId).toUpperCase();
						addColumnSource.addColumn(attributeType);
					}

				attributeWindow.getParent().removeWindow(attributeWindow);

				}

		});
		attributeWindow.setStyleName(Reindeer.WINDOW_LIGHT);
		mainWindow.addWindow(attributeWindow);
	}
	
	public void setMessageSource(SimpleResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	
}
