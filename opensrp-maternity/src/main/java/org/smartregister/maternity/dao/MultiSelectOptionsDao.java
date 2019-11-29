package org.smartregister.maternity.dao;

import org.smartregister.maternity.pojos.OpdMultiSelectOption;

public interface MultiSelectOptionsDao extends OpdGenericDao<OpdMultiSelectOption> {
    OpdMultiSelectOption getLatest(String key);
}
