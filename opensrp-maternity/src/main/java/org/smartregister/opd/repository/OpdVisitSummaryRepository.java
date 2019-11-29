package org.smartregister.opd.repository;

import android.support.annotation.NonNull;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.opd.pojos.OpdVisitSummary;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;
import org.smartregister.opd.utils.OpdUtils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-10-31
 */

public class OpdVisitSummaryRepository extends BaseRepository {

    public OpdVisitSummaryRepository(Repository repository) {
        super(repository);
    }

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
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.VISIT_DATE
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Column.OpdTestConducted.TEST
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Column.OpdTestConducted.RESULT
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.TYPE
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.CODE
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.DISEASE
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.MEDICINE
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.DOSAGE
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.DURATION
                    , OpdDbConstants.Table.OPD_VISIT
                    , OpdDbConstants.Table.OPD_DIAGNOSIS
                    , OpdDbConstants. Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_DIAGNOSIS, OpdDbConstants.Column.OpdDiagnosis.VISIT_ID
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_TEST_CONDUCTED, OpdDbConstants.Column.OpdTestConducted.VISIT_ID
                    , OpdDbConstants.Table.OPD_TREATMENT
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_TREATMENT, OpdDbConstants.Column.OpdTreatment.VISIT_ID
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.ID
                    , "'" + StringUtils.join(visitIds, "','") + "'"
                    , OpdDbConstants.Table.OPD_VISIT, OpdDbConstants.Column.OpdVisit.VISIT_DATE
            );

            if (StringUtils.isNotBlank(baseEntityId)) {
                mCursor = db.rawQuery(query, null);

                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        OpdVisitSummary visitSummaryResult = getVisitSummaryResult(mCursor);
                        String dateString = (new SimpleDateFormat(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, Locale.ENGLISH)).format(visitSummaryResult.getVisitDate());

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
                    , OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_VISIT
                    , OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID
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
                    , OpdDbConstants.Column.OpdVisit.ID
                    , OpdDbConstants.Table.OPD_VISIT
                    , OpdDbConstants.Column.OpdVisit.BASE_ENTITY_ID
                    , baseEntityId
                    , OpdDbConstants.Column.OpdVisit.VISIT_DATE
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

        opdVisitModel.setTestName(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.TEST)));
        opdVisitModel.setTestResult(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTestConducted.RESULT)));
        opdVisitModel.setDiagnosis(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DIAGNOSIS)));
        opdVisitModel.setDiagnosisType(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.TYPE)));
        opdVisitModel.setDiseaseCode(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.CODE)));
        opdVisitModel.setDisease(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdDiagnosis.DISEASE)));

        String medicine = cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.MEDICINE));

        if (medicine != null) {
            OpdVisitSummary.Treatment treatment = new OpdVisitSummary.Treatment();
            treatment.setMedicine(medicine);
            treatment.setDosage(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.DOSAGE)));
            treatment.setDuration(cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdTreatment.DURATION)));
            opdVisitModel.setTreatment(treatment);
        }

        opdVisitModel.setVisitDate(OpdUtils.convertStringToDate(OpdConstants.DateFormat.YYYY_MM_DD_HH_MM_SS, cursor.getString(cursor.getColumnIndex(OpdDbConstants.Column.OpdVisit.VISIT_DATE))));

        return opdVisitModel;
    }


}
