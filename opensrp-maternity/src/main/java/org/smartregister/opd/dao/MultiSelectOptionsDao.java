package org.smartregister.opd.dao;

import org.smartregister.opd.pojos.OpdMultiSelectOption;

public interface MultiSelectOptionsDao extends OpdGenericDao<OpdMultiSelectOption> {
    OpdMultiSelectOption getLatest(String key);
}
