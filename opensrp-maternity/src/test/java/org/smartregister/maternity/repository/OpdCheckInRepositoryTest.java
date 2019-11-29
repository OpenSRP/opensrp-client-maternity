package org.smartregister.maternity.repository;

import android.content.ContentValues;

import net.sqlcipher.MatrixCursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.repository.Repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

@RunWith(RobolectricTestRunner.class)
public class OpdCheckInRepositoryTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void createTable() {
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);

        OpdCheckInRepository.createTable(database);

        Mockito.verify(database, Mockito.times(1))
                .execSQL(Mockito.contains("CREATE TABLE opd_check_in"));
        Mockito.verify(database, Mockito.times(1))
                .execSQL(Mockito.contains("CREATE INDEX opd_check_in_base_entity_id"));
        Mockito.verify(database, Mockito.times(1))
                .execSQL(Mockito.contains("CREATE INDEX opd_check_in_visit_id"));
        Mockito.verify(database, Mockito.times(1))
                .execSQL(Mockito.contains("CREATE INDEX opd_check_in_form_submission_id"));
    }

    @Test
    public void createValuesForShouldPopulateContentValueWithCorrectKeys() {
        OpdCheckIn opdCheckIn = new OpdCheckIn();
        opdCheckIn.setId(1);
        opdCheckIn.setFormSubmissionId("event-id");

        OpdCheckInRepository opdCheckInRepository = new OpdCheckInRepository(Mockito.mock(Repository.class));

        ContentValues contentValues = opdCheckInRepository.createValuesFor(opdCheckIn);

        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.ID));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.FORM_SUBMISSION_ID));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.VISIT_ID));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.BASE_ENTITY_ID));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.PREGNANCY_STATUS));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.HIV_RESULTS_PREVIOUSLY));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.IS_TAKING_ART));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.CURRENT_HIV_RESULT));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.VISIT_TYPE));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY));
        assertTrue(contentValues.containsKey(MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_DUE_DATE));
    }

    @Test
    public void getLatestCheckInShouldQueryDbAndReturnNonNullResult() {
        Repository repository = Mockito.mock(Repository.class);
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(database).when(repository).getWritableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{MaternityDbConstants.Column.OpdCheckIn.ID
                , MaternityDbConstants.Column.OpdCheckIn.FORM_SUBMISSION_ID
                , MaternityDbConstants.Column.OpdCheckIn.VISIT_ID
                , MaternityDbConstants.Column.OpdCheckIn.BASE_ENTITY_ID
                , MaternityDbConstants.Column.OpdCheckIn.PREGNANCY_STATUS
                , MaternityDbConstants.Column.OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.HIV_RESULTS_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.IS_TAKING_ART
                , MaternityDbConstants.Column.OpdCheckIn.CURRENT_HIV_RESULT
                , MaternityDbConstants.Column.OpdCheckIn.VISIT_TYPE
                , MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_DUE_DATE
                , MaternityDbConstants.Column.OpdCheckIn.CREATED_AT
                , MaternityDbConstants.Column.OpdCheckIn.UPDATED_AT});

        matrixCursor.addRow(new Object[]{
                1
                , "event-id"
                , 1
                , "base-entity-id"
                , "positive"
                , "no"
                , null
                , null
                , "unknown"
                , "referral"
                , "no"
                , null
                , 797
                , 797
        });
        Mockito.doReturn(matrixCursor).when(database)
                .query(Mockito.eq("opd_check_in")
                        , Mockito.any(String[].class)
                        , Mockito.eq("base_entity_id = ?")
                        , Mockito.any(String[].class)
                        , Mockito.nullable(String.class)
                        , Mockito.nullable(String.class)
                        , Mockito.eq("created_at DESC")
                        , Mockito.eq("1"));

        OpdCheckInRepository opdCheckInRepository = new OpdCheckInRepository(repository);
        OpdCheckIn checkIn = opdCheckInRepository.getLatestCheckIn("my-id");

        Mockito.verify(database, Mockito.times(1))
                .query(Mockito.eq("opd_check_in")
                        , Mockito.any(String[].class)
                        , Mockito.eq("base_entity_id = ?")
                        , Mockito.any(String[].class)
                        , Mockito.nullable(String.class)
                        , Mockito.nullable(String.class)
                        , Mockito.eq("created_at DESC")
                        , Mockito.eq("1"));

        Assert.assertNotNull(checkIn);
    }

    @Test
    public void getCheckInByVisitShouldQueryDbAndReturnNonNullResult() {
        Repository repository = Mockito.mock(Repository.class);
        SQLiteDatabase database = Mockito.mock(SQLiteDatabase.class);
        Mockito.doReturn(database).when(repository).getWritableDatabase();

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{MaternityDbConstants.Column.OpdCheckIn.ID
                , MaternityDbConstants.Column.OpdCheckIn.FORM_SUBMISSION_ID
                , MaternityDbConstants.Column.OpdCheckIn.VISIT_ID
                , MaternityDbConstants.Column.OpdCheckIn.BASE_ENTITY_ID
                , MaternityDbConstants.Column.OpdCheckIn.PREGNANCY_STATUS
                , MaternityDbConstants.Column.OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.HIV_RESULTS_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.IS_TAKING_ART
                , MaternityDbConstants.Column.OpdCheckIn.CURRENT_HIV_RESULT
                , MaternityDbConstants.Column.OpdCheckIn.VISIT_TYPE
                , MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_DUE_DATE
                , MaternityDbConstants.Column.OpdCheckIn.CREATED_AT
                , MaternityDbConstants.Column.OpdCheckIn.UPDATED_AT});

        matrixCursor.addRow(new Object[]{
                1
                , "event-id"
                , 1
                , "base-entity-id"
                , "positive"
                , "no"
                , null
                , null
                , "unknown"
                , "referral"
                , "no"
                , null
                , 797
                , 797
        });
        Mockito.doReturn(matrixCursor).when(database)
                .query(Mockito.eq("opd_check_in")
                        , Mockito.any(String[].class)
                        , Mockito.eq("visit_id = ?")
                        , Mockito.any(String[].class)
                        , Mockito.nullable(String.class)
                        , Mockito.nullable(String.class)
                        , Mockito.eq("created_at DESC")
                        , Mockito.eq("1"));

        OpdCheckInRepository opdCheckInRepository = new OpdCheckInRepository(repository);
        OpdCheckIn checkIn = opdCheckInRepository.getCheckInByVisit("dsldk");

        Mockito.verify(database, Mockito.times(1))
                .query(Mockito.eq("opd_check_in")
                        , Mockito.any(String[].class)
                        , Mockito.eq("visit_id = ?")
                        , Mockito.any(String[].class)
                        , Mockito.nullable(String.class)
                        , Mockito.nullable(String.class)
                        , Mockito.eq("created_at DESC")
                        , Mockito.eq("1"));

        Assert.assertNotNull(checkIn);
    }

    @Test
    public void getCheckInResultShoudlGenerateValidCheckInObjectFromCursor() {

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{MaternityDbConstants.Column.OpdCheckIn.ID
                , MaternityDbConstants.Column.OpdCheckIn.FORM_SUBMISSION_ID
                , MaternityDbConstants.Column.OpdCheckIn.VISIT_ID
                , MaternityDbConstants.Column.OpdCheckIn.BASE_ENTITY_ID
                , MaternityDbConstants.Column.OpdCheckIn.PREGNANCY_STATUS
                , MaternityDbConstants.Column.OpdCheckIn.HAS_HIV_TEST_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.HIV_RESULTS_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.IS_TAKING_ART
                , MaternityDbConstants.Column.OpdCheckIn.CURRENT_HIV_RESULT
                , MaternityDbConstants.Column.OpdCheckIn.VISIT_TYPE
                , MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_SCHEDULED_PREVIOUSLY
                , MaternityDbConstants.Column.OpdCheckIn.APPOINTMENT_DUE_DATE
                , MaternityDbConstants.Column.OpdCheckIn.CREATED_AT
                , MaternityDbConstants.Column.OpdCheckIn.UPDATED_AT});

        String eventId = "event-id";
        String baseEntityId = "base-entity-id";
        int checkInDate = 797;
        matrixCursor.addRow(new Object[]{
                1
                , eventId
                , "visit-id"
                , baseEntityId
                , "positive"
                , "no"
                , null
                , null
                , "unknown"
                , "referral"
                , "no"
                , null
                , checkInDate
                , checkInDate
        });

        matrixCursor.moveToNext();

        OpdCheckInRepository opdCheckInRepository = new OpdCheckInRepository(Mockito.mock(Repository.class));
        OpdCheckIn checkIn = opdCheckInRepository.getCheckInResult(matrixCursor);

        assertEquals(1, checkIn.getId());
        assertEquals("visit-id", checkIn.getVisitId());
        assertEquals(eventId, checkIn.getFormSubmissionId());
        assertEquals(checkInDate, checkIn.getUpdatedAt());
        assertNull(checkIn.getAppointmentDueDate());
    }
}