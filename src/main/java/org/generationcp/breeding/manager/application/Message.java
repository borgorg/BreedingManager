package org.generationcp.breeding.manager.application;


public enum Message{
    MAIN_WINDOW_CAPTION
    ,BACK
    ,DONE
    ,OK
    ,SUCCESS
    ,ERROR_INTERNAL
    ,ERROR_PLEASE_CONTACT_ADMINISTRATOR
    ,ERROR_MUST_BE_SPECIFIED
    ,GERMPLASM_BREEDING_METHOD_LABEL
    ,GERMPLASM_DATE_LABEL
    ,GERMPLASM_DETAILS_LABEL
    ,GERMPLASM_LOCATION_LABEL
    ,GERMPLASM_NAME_TYPE_LABEL
    ,I_WANT_TO_IMPORT_GERMPLASM_LIST
    ,IMPORT_GERMPLASM_LIST_TAB_LABEL
    ,IMPORT_PEDIGREE_OPTION_ONE
    ,IMPORT_PEDIGREE_OPTION_TWO
    ,IMPORT_PEDIGREE_OPTION_THREE
    ,LIST_DATE_LABEL
    ,LIST_DESCRIPTION_LABEL
    ,LIST_NAME_LABEL
    ,LIST_TYPE_LABEL
    ,NEXT
    ,OPEN_GERMPLASM_IMPORT_FILE
    ,PEDIGREE_OPTIONS_LABEL
    ,SAVE_GERMPLASM_LIST
    ,SELECT_GERMPLASM_LIST_FILE
    ,SPECIFY_GERMPLASM_DETAILS
    ,UPLOAD
    ,WELCOME_LABEL
    ,WELCOME_QUESTION_LABEL
    ,WELCOME_TAB_LABEL
    ,CROSSING_MANAGER_LABEL
    ,CROSSING_MANAGER_TAB_LABEL
    ,I_WANT_TO_IMPORT_CROSSING_MANAGER_DATA
    ,SELECT_NURSERY_TEMPLATE
    ,SELECT_CROSSING_TEMPLATE
    ,MAKE_CROSSES
    ,ENTER_ADDITIONAL_DETAILS_OF_GERMPLASM_RECORDS_FOR_CROSSES
    ,ENTER_DETAILS_FOR_LIST_OF_CROSS
    ,SELECT_NURSERY_TEMPLATE_FILE
    ,SELECT_CROSSING_TEMPLATE_FILE
    ,SELECT_AN_OPTION_FOR_SPECIFYING_CROSSES
    ,I_HAVE_ALREADY_DEFINED_CROSSES_IN_THE_NURSERY_TEMPLATE_FILE
    ,I_HAVE_ALREADY_DEFINED_CROSSES_IN_THE_CROSSING_TEMPLATE_FILE
    ,I_WANT_TO_MANUALLY_MAKE_CROSSES
    ,UPLOADED_FILE
    ,AT_LEAST_ONE_FEMALE_AND_ONE_MALE_PARENT_MUST_BE_SELECTED
    
    //Select Germplasm List
    ,CONFIRM_RECORDS_WILL_BE_SAVED_FOR_GERMPLASM
    ,GERMPLASM_LIST_SAVED_SUCCESSFULLY
    ,MAKE_NEW_IMPORT
    ,CONFIRM_REDIRECT_TO_IMPORT_WIZARD
    ,I_WANT_TO_SELECT_GERMPLASM_LIST
    ,DB_LOCAL_TEXT
    ,DB_CENTRAL_TEXT
    ,GERMPLASM_LIST_DETAILS_LABEL
    ,REFRESH_LABEL
    ,CANCEL_LABEL
    ,SELECT_HIGHLIGHTED_GERMPLASM
    ,DONE_LABEL
    ,SELECTED_LIST_LABEL
    ,DESCRIPTION_LABEL
    ,LIST_ENTRIES_LABEL
    ,LISTDATA_GID_HEADER
    ,LISTDATA_ENTRY_ID_HEADER
    ,LISTDATA_ENTRY_CODE_HEADER
    ,LISTDATA_SEEDSOURCE_HEADER
    ,LISTDATA_DESIGNATION_HEADER
    ,LISTDATA_GROUPNAME_HEADER
    ,LISTDATA_STATUS_HEADER
    ,ERROR_DATABASE
    ,ERROR_IN_GETTING_TOP_LEVEL_FOLDERS
    ,ERROR_INVALID_FORMAT
    ,ERROR_IN_CREATING_GERMPLASMLIST_DETAILS_WINDOW
    ,ERROR_IN_GETTING_GERMPLASM_LISTS_BY_PARENT_FOLDER_ID
    ,ERROR_IN_NUMBER_FORMAT
    ,ERROR_IN_GETTING_LAST_SELECTED_LIST
    
    //List Data Inventory
    ,NO_LISTDATA_INVENTORY_RETRIEVED_LABEL
    ,ENTITY_ID_HEADER
    ,LOT_BALANCE_HEADER
    ,LOCATION_HEADER
    ,LOT_COMMENT_HEADER
    
    //Crossing Manager Additional Details
    ,CHOOSE_HOW_CROSSES_WILL_BE_SPECIFIED
    ,SELECT_AN_OPTION
    ,CROSSING_METHOD
    ,SELECT_CROSSING_METHOD
    ,METHOD_DESCRIPTION_LABEL
    ,CROSSING_METHOD_WILL_BE_THE_SAME_FOR_ALL_CROSSES
    ,CROSSING_METHOD_WILL_BE_SET_BASED_ON_STATUS_OF_PARENTAL_LINES
    ,CROSS_CODE
    ,USE_DEFAULT_CROSS_CODE_FOR_ALL
    ,SPECIFY_CROSS_CODE_TEMPLATE_FOR_ALL
    ,SPECIFY_PREFIX_REQUIRED
    ,SEQUENCE_NUMBER_SHOULD_HAVE_LEADING_ZEROS
    ,HOW_MANY_DIGITS
    ,SPECIFY_SUFFIX_OPTIONAL
    ,THE_NEXT_NAME_IN_THE_SEQUENCE_WILL_BE
    ,GENERATE
    ,CROSS_INFO
    ,HARVEST_DATE
    ,HARVEST_LOCATION
    ,ERROR_IN_GETTING_NEXT_NUMBER_IN_CROSS_NAME_SEQUENCE
    ,ERROR_ENTER_PREFIX_FIRST
    ,ERROR_PREFIX_HAS_WHITESPACE
    ,ERROR_IN_GETTING_BREEDING_METHOD_BASED_ON_PARENTAL_LINES
    ,ERROR_NEXT_NAME_MUST_BE_GENERATED_FIRST
    ,ERROR_PREFIX_ENDS_IN_NUMBER
    ,ERROR_WITH_CROSS_CODE
    
    ,GERMPLASM_LIST_NAME
    ,GERMPLASM_LIST_DESCRIPTION
    ,GERMPLASM_LIST_TYPE
    ,GERMPLASM_LIST_DATE
    
    //Saving Crosses action
    ,MAKE_NEW_CROSSES
    ,EXPORT_CROSSES_MADE
    ,SAVE_CROSSES_MADE
    ,CONFIRM_RECORDS_WILL_BE_SAVED_FOR_CROSSES_MADE
    ,CONFIRM_REDIRECT_TO_MAKE_CROSSES_WIZARD
    ,ERROR_IN_SAVING_CROSSES_DEFINED
    ,CROSSES_SAVED_SUCCESSFULLY
    
    //Make Cross Screen
    ,LABEL_FEMALE_PARENTS
    ,LABEL_MALE_PARENTS
    ,LABEL_FEMALE_PARENT
    ,LABEL_MALE_PARENT
    ,FEMALE
    ,MALE
    ,SELECT_FEMALE_LIST_BUTTON_LABEL
    ,SELECT_MALE_LIST_BUTTON_LABEL
    ,MAKE_CROSSES_OPTION_GROUP_ITEM_ONE_LABEL
    ,MAKE_CROSSES_OPTION_GROUP_ITEM_TWO_LABEL
    ,MAKE_CROSSES_CHECKBOX_LABEL
    ,MAKE_CROSSES_BUTTON_LABEL
    ,LABEL_CROSS_MADE
    ,ERROR_CROSS_MUST_BE_SELECTED
    ,ERROR_IN_GETTING_CROSSING_NAME_TYPE
    ,ERROR_MALE_AND_FEMALE_PARENTS_MUST_BE_EQUAL
    ,ERROR_GERMPLASM_LIST_IMPORT_BOTH_ID_REQUIRED
    ,ERROR_GERMPLASM_LIST_IMPORT_MALE_ID_REQUIRED
    ,ERROR_GERMPLASM_LIST_IMPORT_FEMALE_ID_REQUIRED
    ,INVALID_NURSERY_TEMPLATE_FILE
    ,INVALID_CROSSING_TEMPLATE_FILE
    ,NUMBER
    ,REVIEW_CROSSES
    
    //Nursery Template Screens
    ,I_WANT_TO_WRITE_NURSERY_TEMPLATE_FILES
    ,NURSERY_TEMPLATE_TAB_LABEL
    ,NURSERY_TEMPLATE_CAPTION_LABEL
    ,SPECIFY_NURSERY_CONDITIONS_LABEL
    ,CONDITION_HEADER
    ,DESCRIPTION_HEADER
    ,PROPERTY_HEADER
    ,SCALE_HEADER
    ,VALUE_HEADER 
    ,INVALID_SITE_ID
    ,INVALID_BREEDER_ID
    ,INVALID_METHOD_ID
    ,PARENTAGE
    ,CONFIRM_DIALOG_CAPTION_EXPORT_NURSERY_FILE
    ,CONFIRM_DIALOG_MESSAGE_EXPORT_NURSERY_FILE
    ,ADD_SPACE_BETWEEN_PREFIX_AND_CODE
    ,SELECT_A_METHOD
    ,CROSS_NAME
    ,SEQUENCE_NUMBER_SHOULD_HAVE
    ,LEADING_ZEROS
    ,DIGITS
    ,PLEASE_SELECT_A_GERMPLASM_FROM_THE_TABLE
    ,BY_CLICKING_ON_THE_DONE_BUTTON
    ,START_NEW_IMPORT
    ,CLICKING_ON_DONE_WOULD_MEAN_THE_LIST_LIST_ENTRIES_AND_GERMPLASM_RECORDS_WILL_BE_SAVED_IN_THE_DATABASE
    
    //List Manager Screens
    ,LIST_MANAGER_TAB_LABEL
    ,LIST_MANAGER_SCREEN_LABEL
    ,BROWSE_LISTS
    ,PROJECT_LISTS
    ,REVIEW_LIST_DETAILS
    ,REVIEW_DETAILS
    ,SEARCH_LISTS_AND_GERMPLASM
    ,LIST_DETAILS
    ,LIST_DATA
    ,LIST_SEED_INVENTORY
    ,CLOSE_ALL_TABS
    ,SAVE_IN
    ,CHANGE_LOCATION
    ,SELECT_LOCATION
    ,SELECT_LOCATION_FOLDER
    ,LIST_LOCATION
    ,NEW_LIST_DETAILS
    
    //List Manager Screen: List Details
    ,NAME_LABEL
    ,CREATION_DATE_LABEL
    ,TYPE_LABEL
    ,STATUS_LABEL
    ,LIST_OWNER_LABEL
    ,ERROR_MONTH_OUT_OF_RANGE
    ,ERROR_DAY_OUT_OF_RANGE
    ,SEARCH_FOR
    ,SEARCH
    ,MATCHING_LISTS
    ,SELECT_A_LIST_TO_VIEW_THE_DETAILS
    ,MATCHING_GERMPLASM
    ,SELECT_A_GERMPLASM_TO_VIEW_THE_DETAILS
    ,BUILD_A_NEW_LIST
    ,START_A_NEW_LIST
    ,CLICK_AND_DRAG_ON_PANEL_EDGES_TO_RESIZE
    ,BUILD_YOUR_LIST_BY_DRAGGING_LISTS_OR_GERMPLASM_RECORDS_INTO_THIS_NEW_LIST_WINDOW
    ,DATE_LABEL
    ,NOTES
    ,ADD_NOTES
    ,EDIT_NOTES
    ,VIEW_NOTES
    ,SAVE_LIST
    ,DETAILS
    ,VIEW_HEADER
    ,EDIT_LIST
    ,SELECT_A_MATCHING_LIST_OR_GERMPLASM_TO_VIEW_THE_DETAILS
    
    //List Manager Screen: Data
    ,NO_LISTDATA_RETRIEVED_LABEL
    ,SAVE_GERMPLASMLIST_DATA_SAVING_SUCCESS
    ,ERROR_LIST_ENTRIES_MUST_BE_SELECTED
    ,INVALID_DELETING_LIST_ENTRIES
    ,INVALID_USER_DELETING_LIST_ENTRIES
    ,COPY_TO_NEW_LIST_WINDOW_LABEL
    ,ERROR_IN_SAVING_GERMPLASMLIST_DATA_CHANGES
    ,UNSUCCESSFUL
    ,SAVE_GERMPLASMLIST_DATA_COPY_TO_NEW_LIST_FAILED
    ,SAVE_GERMPLASMLIST_DATA_COPY_TO_NEW_LIST_SUCCESS
    ,SAVE_LABEL
    ,SAVE_GERMPLASMLIST_DATA_COPY_TO_EXISTING_LIST_FAILED
    ,ERROR_IN_GETTING_GERMPLASM_LIST_BY_ID
    
    //Germplasm Details
    ,BASIC_DETAILS
    ,ATTRIBUTES
    ,PEDIGREE_TREE
    ,CREATION_METHOD
    ,LOCATION
    ,PREFERRED_NAME
    ,REFERENCE
    ,ERROR_IN_GETTING_GERMPLASM_DETAILS
    ,ERROR_IN_GENERATING_PEDIGREE_TREE
    ,SAVE_TO_LIST
    ,MORE_DETAILS
    ,CLICK_TO_VIEW_GERMPLASM_DETAILS
    ,VIEW_PEDIGREE_GRAPH
    ,INCLUDE_DERIVATIVE_LINES
    ,APPLY
    ,PEDIGREE_LEVEL_LABEL
    
    ,SELECT_ALL
    ,FILL_WITH_EMPTY
    ,FILL_WITH_PREF_NAME
    ,FILL_WITH_PREF_ID
    ,FILL_WITH_ATTRIBUTE
    ,FILL_WITH_ATTRIBUTE_WINDOW
    ,FILL_WITH_LOCATION_NAME
    ,FILL_WITH_BREEDING_METHOD_INFO
    ,FILL_WITH_BREEDING_METHOD_NAME
    ,FILL_WITH_BREEDING_METHOD_ID
    ,FILL_WITH_BREEDING_METHOD_GROUP
    ,FILL_WITH_BREEDING_METHOD_NUMBER
    ,FILL_WITH_BREEDING_METHOD_ABBREVIATION
    ,FILL_WITH_GERMPLASM_DATE
    ,FILL_WITH_CROSS_FEMALE_INFORMATION
	,FILL_WITH_CROSS_FEMALE_GID
	,FILL_WITH_CROSS_FEMALE_PREFERRED_NAME
	,FILL_WITH_CROSS_MALE_INFORMATION
	,FILL_WITH_CROSS_MALE_GID
	,FILL_WITH_CROSS_MALE_PREFERRED_NAME
	,FILL_WITH_CROSS_EXPANSION
    
    ,INVALID_INPUT
    ,INVALID_LIST_FOLDER_NAME
    ,NO_ENTRIES_ERROR_MESSAGE
    ,ERROR_SAVING_GERMPLASM_LIST
    ,ERROR_SAVING_GERMPLASM_LIST_ENTRIES
    ,LIST_AND_ENTRIES_SAVED_SUCCESS
    ,NAME_CAN_NOT_BE_BLANK
    ,DESCRIPTION_CAN_NOT_BE_BLANK
    ,NAME_CAN_NOT_BE_LONG
    ,DESCRIPTION_CAN_NOT_BE_LONG
    ,EXISTING_LIST_ERROR_MESSAGE
    ,EXISTING_LIST_IN_CENTRAL_ERROR_MESSAGE
    ,ERROR_VALIDATING_LIST
    ,ERROR_GETTING_LOCAL_IBDB_USER_ID
    ,ERROR_GETTING_SAVED_ENTRIES
    ,DELETE_SELECTED_ENTRIES

    ,SEARCH_FOR_LABEL
    ,ERROR_INVALID_INPUT_MUST_BE_NUMERIC
    ,SEARCH_LABEL
    ,NAMES_LABEL
    ,GID_LABEL
    ,METHOD_LABEL
    ,LOCATION_LABEL

    
    ,EXPORT_LIST
    ,EXPORT_LIST_FOR_GENOTYPING
    ,ERROR_IN_GETTING_GERMPLASM_LIST_RESULT_BY_PREFERRED_NAME
    ,ERROR_IN_SEARCH
    ,ERROR_IN_GETTING_NAMES_BY_GERMPLASM_ID

    ,TYPEDESC_LABEL
    ,ERROR_IN_GETTING_ATTRIBUTES_BY_GERMPLASM_ID
    ,TOOLS
    ,ADD_COLUMN

    ,INVALID_LIST_ID
    ,INVALID_DESIGNATION_NAME
    ,RESET
    ,RESET_LIST
    ,CONFIRM_RESET_LIST_BUILDER_FIELDS
    ,SHOW_FAVORITE_LOCATIONS
    ,SHOW_ONLY_FAVORITE_LOCATIONS
    ,SHOW_ONLY_FAVORITE_METHODS
    ,LOCK_AND_EXPORT_CONFIRM
    ,SEARCH_RESULTS
    ,NO_SEARCH_RESULTS
    ,TOTAL_LIST_ENTRIES
    ,EXACT_MATCHES_ONLY
    ,INCLUDE_PARENTS
    
    //General error msg
    ,ERROR_REPORT_TO
    ,ERROR_WITH_MODIFYING_LIST_TREE
    ,UNABLE_TO_MOVE_ROOT_FOLDERS
    ,UNABLE_TO_MOVE_YOUR_LISTS_TO_PUBLIC_FOLDERS
    ,UNABLE_TO_MOVE_PUBLIC_LISTS, INVALID_OPERATION
    
    //Delete folder msg
    ,INVALID_CANNOT_DELETE_ITEM
    ,DELETE_LIST_FOLDER
    ,DELETE_LIST_FOLDER_CONFIRM
    ,YES
    ,NO
    ,ERROR
    ,TAG
    ,TAG_ALL
    ,SUCCESSFULLY_DELETED_ITEM
   
    //Unsaved changes msg
    ,WARNING
    ,UNSAVED_CHANGES_LISTDATA
    ,ERROR_UNABLE_TO_DELETE_LOCKED_LIST
    ,ADD_SPACE_BETWEEN_SUFFIX_AND_CODE
    ,CANCEL
    ,SPECIFY_START_NUMBER
    ,PLEASE_SPECIFY_A_PREFIX
    ,PLEASE_SPECIFY_A_STARTING_NUMBER
    ,STARTING_NUMBER_HAS_TOO_MANY_DIGITS
    ,PLEASE_ENTER_VALID_STARTING_NUMBER
    ,SEQUENCE_TOO_LONG_FOR_SEED_SOURCE
    ,SEQUENCE_TOO_LONG_FOR_ENTRY_CODE
    ,FILL_WITH_SEQUENCE_NUMBER
    ,MANAGE_LOCATIONS
    ,MANAGE_METHODS
    ,SAVE_IN_WITH_COLON
    
    
    //Crossing Manager - ChooseSettings
    ,CHOOSE_SETTING
    ,CREATE_CROSSES
    ,SAVE_CROSS_LIST
    
    ,SPECIFYING_CROSSES
    ,HOW_WOULD_YOU_LIKE_TO_SPECIFY_CROSSES
    ,SPECIFY_CROSSES_MANUALLY
    ,UPLOAD_LIST_OF_CROSSES
    ,NO_FILE_SELECTED
    ,BROWSE
    
    ,DEFINE_CROSSING_SETTINGS
    ,CROSSING_SETTINGS_TAB_LABEL
    ,MANAGE_CROSSES
    ,CROSSING_MANAGER_TOOL_DESCRIPTION
    ,MANAGE_SAVED_CROSSING_SETTINGS
    ,CHOOSE_SAVED_SETTINGS
    ,MAKE_A_COPY
    ,ADD_NEW_SETTINGS
    ,DELETE
    
    ,INDICATES_A_MANDATORY_FIELD
    ,USE_PREVIOUSLY_SAVED_SETTING
    
    ,BREEDING_METHOD
    ,CHOOSE_BREEDING_METHOD_TO_APPLY_TO_NEW_CROSSES
    ,SELECT_A_METHOD_TO_USE_FOR_ALL_CROSSES
    ,SET_METHOD_BASED_ON_STATUS_OF_PARENTAL_LINES
    ,NAMING
    ,SPECIFY_NAMING_CONVENTION_FOR_CROSSES
    ,CROSS_NAME_PREFIX
    ,SUFFIX_OPTIONAL
    ,SPECIFY_DIFFERENT_STARTING_SEQUENCE_NUMBER
    ,SEPARATOR_FOR_PARENTAGE_DESIGNATION
    
    ,ADDITIONAL_DETAILS
    ,SAVE_SETTINGS
    ,SAVE_THIS_SETTING_TO_USE_AGAIN
    ,SAVE_AS
    ,SET_AS_DEFAULT_FOR_THIS_PROGRAM_OVERRIDES_PREVIOUS_DEFAULTS
    
    //Manage Crosses Save List Labels
    ,SAVE_CROSS_LIST_AND_PARENT_LISTS
    ,SPECIFY_CROSS_LIST_DETAILS
    ,SPECIFY_FEMALE_PARENT_LIST_DETAILS
    ,SPECIFY_MALE_PARENT_LIST_DETAILS
    ,SAVE_CROSS_LIST_AS
    ,SAVE_FEMALE_PARENT_AS
    ,SAVE_MALE_PARENT_AS
    ,LIST_NAME
    ,LIST_TYPE
    ,LIST_DATE
    ,CHOOSE_LOCATION
    ,CHANGE
    ,SAVE_LIST_AS
    ,SELECT
    ,FINISH
    ,SUCCESS_SAVE_FOR_FEMALE_LIST
    ,SUCCESS_SAVE_FOR_MALE_LIST
    ,PARENTS_LISTS
    
    //Crossing Manager-Summary page
    ,SUMMARY
    ,CROSS_LIST_ENTRIES
    ,EXPORT_CROSS_LIST
    ,MGID
    ,FGID
    ,FEMALE_PARENT_LIST_DETAILS
    ,MALE_PARENT_LIST_DETAILS
    ,SAVED_AS
    ,BREEDING_METHOD_APPLIED_TO_NEW_CROSSES
    //Side by Side - List Manager
    ,LISTS
    ,AVAILABLE_LISTS
    ,CHECK_ICON 
    ,HASHTAG
    
    ,EDIT_LIST_HEADER
    ,EDIT_HEADER
    ,BUILD_LIST_DRAG_INSTRUCTIONS
}
