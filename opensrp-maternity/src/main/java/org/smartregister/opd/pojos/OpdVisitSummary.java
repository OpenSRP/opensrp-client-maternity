package org.smartregister.opd.pojos;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-31
 */

public class OpdVisitSummary extends OpdVisitSummaryResultModel {

    private HashSet<String> diseases = new HashSet<>();
    private HashMap<String, Treatment> treatments = new HashMap<>();

    public HashSet<String> getDiseases() {
        return diseases;
    }

    @Override
    public void setDisease(String disease) {
        super.setDisease(disease);

        addDisease(disease);
    }

    @Override
    public void setTreatment(Treatment treatment) {
        super.setTreatment(treatment);
        addTreatment(treatment);
    }

    public void addDisease(@NonNull String diseaseCode) {
        diseases.add(diseaseCode);
    }

    public HashMap<String, Treatment> getTreatments() {
        return treatments;
    }

    public void addTreatment(@NonNull Treatment treatment) {
        treatments.put(treatment.getMedicine(), treatment);
    }

}
