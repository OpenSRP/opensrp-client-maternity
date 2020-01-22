package org.smartregister.maternity.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.maternity.pojos.OpdVisitSummary;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class OpdVisitSummaryRepository extends BaseRepository {

    @NonNull
    public List<OpdVisitSummary> getOpdVisitSummaries(@NonNull String baseEntityId, int pageNo) {
        LinkedHashMap<String, OpdVisitSummary> opdVisitSummaries = new LinkedHashMap<>();

        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String[] visitIds = getVisitIds(baseEntityId, pageNo);

            String query = String.format("SELECT %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s, %s.%s FROM %s " +
                            "INNER JOIN %s ON %s.%s = %s.%s " +
                            "LEFT JOIN %s ON %s.%s = %s.%s " +
                            "LEFT JOIN %s ON %s.%s = %s.%s WHERE %s.%s = '%s' AND %s.%s IN (%s) ORDER BY %s.%s DESC"
                    , MaternityDbConstants.Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.VISIT_DATE
                    , MaternityDbConstants.Table.OPD_TEST_CONDUCTED, MaternityDbConstants.Column.OpdTestConducted.TEST
                    , MaternityDbConstants.Table.OPD_TEST_CONDUCTED, MaternityDbConstants.Column.OpdTestConducted.RESULT
                    , MaternityDbConstants.Table.OPD_DIAGNOSIS, MaternityDbConstants.Column.OpdDiagnosis.DIAGNOSIS
                    , MaternityDbConstants.Table.OPD_DIAGNOSIS, MaternityDbConstants.Column.OpdDiagnosis.TYPE
                    , MaternityDbConstants.Table.OPD_DIAGNOSIS, MaternityDbConstants.Column.OpdDiagnosis.CODE
                    , MaternityDbConstants.Table.OPD_DIAGNOSIS, MaternityDbConstants.Column.OpdDiagnosis.DISEASE
                    , MaternityDbConstants.Table.OPD_TREATMENT, MaternityDbConstants.Column.OpdTreatment.MEDICINE
                    , MaternityDbConstants.Table.OPD_TREATMENT, MaternityDbConstants.Column.OpdTreatment.DOSAGE
                    , MaternityDbConstants.Table.OPD_TREATMENT, MaternityDbConstants.Column.OpdTreatment.DURATION
                    , MaternityDbConstants.Table.OPD_VISIT
                    , MaternityDbConstants.Table.OPD_DIAGNOSIS
                    , MaternityDbConstants. Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.ID
                    , MaternityDbConstants.Table.OPD_DIAGNOSIS, MaternityDbConstants.Column.OpdDiagnosis.VISIT_ID
                    , MaternityDbConstants.Table.OPD_TEST_CONDUCTED
                    , MaternityDbConstants.Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.ID
                    , MaternityDbConstants.Table.OPD_TEST_CONDUCTED, MaternityDbConstants.Column.OpdTestConducted.VISIT_ID
                    , MaternityDbConstants.Table.OPD_TREATMENT
                    , MaternityDbConstants.Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.ID
                    , MaternityDbConstants.Table.OPD_TREATMENT, MaternityDbConstants.Column.OpdTreatment.VISIT_ID
                    , MaternityDbConstants.Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
                    , MaternityDbConstants.Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.ID
                    , "'" + StringUtils.join(visitIds, "','") + "'"
                    , MaternityDbConstants.Table.OPD_VISIT, MaternityDbConstants.Column.OpdVisit.VISIT_DATE
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        OpdVisitSummary visitSummaryResult = getVisitSummaryResult(mCursor);
                        String dateString = (new SimpleDateFormat(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH)).format(visitSummaryResult.getVisitDate());

                        OpdVisitSummary existingOpdVisitSummary = opdVisitSummaries.get(dateString);
                        if (existingOpdVisitSummary != null) {
                            // Add any extra disease codes
                            String disease = visitSummaryResult.getDisease();
                            if (disease != null && !existingOpdVisitSummary.getDisease().contains(disease)) {
                                existingOpdVisitSummary.addDisease(disease);
                            }

                            // Add any extra treatments/medicines
                            OpdVisitSummary.Treatment treatment = visitSummaryResult.getTreatment();
                            if (treatment != null && treatment.getMedicine() != null && !existingOpdVisitSummary.getTreatments().containsKey(treatment.getMedicine())) {
                                existingOpdVisitSummary.addTreatment(treatment);
                            }
                        } else {
                            opdVisitSummaries.put(dateString, visitSummaryResult);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return new ArrayList<>(opdVisitSummaries.values());
    }

    public int getVisitPageCount(@NonNull String baseEntityId) {
        Cursor mCursor = null;
        int pageCount = 0;
        try {
            SQLiteDatabase db = getReadableDatabase();

            String query = String.format("SELECT count(%s) FROM %s WHERE %s = '%s'"
                    , MaternityDbConstants.Column.OpdVisit.ID
                    , MaternityDbConstants.Table.OPD_VISIT
                    , MaternityDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        int recordCount = mCursor.getInt(0);
                        pageCount = (int) Math.ceil(recordCount/10d);
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return pageCount;
    }


    public String[] getVisitIds(@NonNull String baseEntityId, int pageNo) {
        ArrayList<String> visitIds = new ArrayList<>();
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            int offset = pageNo * 10;

            String query = String.format("SELECT %s FROM %s WHERE %s = '%s' ORDER BY %s DESC LIMIT 10 OFFSET %d "
                    , MaternityDbConstants.Column.OpdVisit.ID
                    , MaternityDbConstants.Table.OPD_VISIT
                    , MaternityDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
                    , MaternityDbConstants.Column.OpdVisit.VISIT_DATE
                    , offset
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        visitIds.add(mCursor.getString(0));
                    }
                }
            }

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return visitIds.toArray(new String[0]);
    }

    @NonNull
    public OpdVisitSummary getVisitSummaryResult(@NonNull Cursor cursor) {
        OpdVisitSummary opdVisitModel = new OpdVisitSummary();

        opdVisitModel.setTestName(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdTestConducted.TEST)));
        opdVisitModel.setTestResult(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdTestConducted.RESULT)));
        opdVisitModel.setDiagnosis(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdDiagnosis.DIAGNOSIS)));
        opdVisitModel.setDiagnosisType(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdDiagnosis.TYPE)));
        opdVisitModel.setDiseaseCode(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdDiagnosis.CODE)));
        opdVisitModel.setDisease(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdDiagnosis.DISEASE)));

        String medicine = cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdTreatment.MEDICINE));

        if (medicine != null) {
            OpdVisitSummary.Treatment treatment = new OpdVisitSummary.Treatment();
            treatment.setMedicine(medicine);
            treatment.setDosage(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdTreatment.DOSAGE)));
            treatment.setDuration(cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdTreatment.DURATION)));
            opdVisitModel.setTreatment(treatment);
        }

        opdVisitModel.setVisitDate(MaternityUtils.convertStringToDate(MaternityConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(MaternityDbConstants.Column.OpdVisit.VISIT_DATE))));

        return opdVisitModel;
    }


}
