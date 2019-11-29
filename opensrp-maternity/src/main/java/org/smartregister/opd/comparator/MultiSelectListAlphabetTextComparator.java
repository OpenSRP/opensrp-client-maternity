package org.smartregister.opd.comparator;

import com.vijay.jsonwizard.domain.MultiSelectItem;

import java.util.Comparator;

public class MultiSelectListAlphabetTextComparator implements Comparator<MultiSelectItem> {
    @Override
    public int compare(MultiSelectItem o1, MultiSelectItem o2) {
        return o1.getText().toLowerCase().compareTo(o2.getText().toLowerCase());
    }
}
