package org.generationcp.breeding.manager.service;

import org.generationcp.breeding.manager.application.Message;
import org.generationcp.commons.util.UserUtil;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 * Date: 9/26/2014
 * Time: 1:50 PM
 */
@Configurable
public class BreedingManagerServiceImpl implements BreedingManagerService {

	private static final Logger LOG = LoggerFactory.getLogger(BreedingManagerServiceImpl.class);

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

    @Autowired
    private GermplasmDataManager germplasmDataManager;

    @Autowired
    private GermplasmListManager germplasmListManager;

	@Autowired
	private UserDataManager userDataManager;

	@Override
	public String getOwnerListName(Integer userId) throws MiddlewareQueryException {
		String username = "";

		try {
			if (userId != null) {
				return computeListName(userDataManager.getUserById(userId));
			}

		} catch (MiddlewareQueryException ex) {
			LOG.error("Error with getting list owner name of user with id: " + userId, ex);
			throw ex;
		}

		return username;
	}

	protected String computeListName(User user) throws MiddlewareQueryException {
		String userName = "";
		if (user != null) {
			int personId = user.getPersonid();
			Person p = userDataManager.getPersonById(personId);

			if (p != null) {
				userName = p.getFirstName() + " " + p.getMiddleName() + " " + p.getLastName();
			} else {
				userName = user.getName();
			}
		}

		return userName;
	}

	@Override
	public String getDefaultOwnerListName() throws MiddlewareQueryException{
		try {
			int currentUser = UserUtil.getCurrentUserLocalId(workbenchDataManager);

			return computeListName(userDataManager.getUserById(currentUser));
		} catch (MiddlewareQueryException e) {
			LOG.error("Error with getting list owner name of default user ", e);
						throw e;
		}
	}

    @Override
    public List<Germplasm> doGermplasmSearch(String q, Operation o, boolean includeParents, boolean searchPublicData) throws BreedingManagerSearchException {
        validateEmptySearchString(q);
        try {
            List<Germplasm> results = germplasmDataManager.searchForGermplasm(q, o, includeParents, searchPublicData);

            if (null == results || results.isEmpty()) {
                if (!searchPublicData) {
                    throw new BreedingManagerSearchException(Message.NO_SEARCH_RESULTS_UNCHECKED_PUBLIC_DATA);
                }else {
                    throw new BreedingManagerSearchException(Message.NO_SEARCH_RESULTS);
                }
            }

            return results;

        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
            throw new BreedingManagerSearchException(Message.ERROR_DATABASE,e);
        }
    }

    @Override
    public List<GermplasmList> doGermplasmListSearch(String q, Operation o, boolean searchPublicData) throws BreedingManagerSearchException {
        validateEmptySearchString(q);

        try {
            List<GermplasmList> results = germplasmListManager.searchForGermplasmList(q, o, searchPublicData);

            if (null == results || results.isEmpty()) {
                if (!searchPublicData) {
                    throw new BreedingManagerSearchException(Message.NO_SEARCH_RESULTS_UNCHECKED_PUBLIC_DATA);
                }else {
                    throw new BreedingManagerSearchException(Message.NO_SEARCH_RESULTS);
                }
            }

            return results;

        } catch (MiddlewareQueryException e) {
            LOG.error(e.getMessage(),e);
            throw new BreedingManagerSearchException(Message.ERROR_DATABASE,e);
        }
    }

    protected void validateEmptySearchString(String q) throws BreedingManagerSearchException {
        if("".equals(q.replaceAll(" ", "").trim())) {
            throw new BreedingManagerSearchException(Message.SEARCH_QUERY_CANNOT_BE_EMPTY);
        }
    }
}