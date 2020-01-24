package org.smartregister.maternity.processor;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.sqlcipher.database.SQLiteException;

import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.sync.MiniClientProcessorForJava;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.db.Event;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.Obs;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.exception.CheckInEventProcessException;
import org.smartregister.maternity.pojos.MaternityDetails;
import org.smartregister.maternity.pojos.OpdCheckIn;
import org.smartregister.maternity.pojos.OpdVisit;
import org.smartregister.maternity.utils.MaternityConstants;
import org.smartregister.maternity.utils.MaternityDbConstants;
import org.smartregister.sync.ClientProcessorForJava;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 */

public class MaternityMiniClientProcessorForJava extends ClientProcessorForJava implements MiniClientProcessorForJava {

    private HashSet<String> eventTypes = null;
    private SimpleDateFormat dateFormat = new SimpleDateFormat(MaternityDbConstants.DATE_FORMAT, Locale.US);

    public MaternityMiniClientProcessorForJava(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HashSet<String> getEventTypes() {
        if (eventTypes == null) {
            eventTypes = new HashSet<>();
            eventTypes.add(MaternityConstants.EventType.MATERNITY_REGISTRATION);
            eventTypes.add(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_OUTCOME);
            eventTypes.add(MaternityConstants.EventType.MATERNITY_CLOSE);
        }

        return eventTypes;
    }

    @Override
    public boolean canProcess(@NonNull String eventType) {
        return getEventTypes().contains(eventType);
    }

    @Override
    public void processEventClient(@NonNull EventClient eventClient, @NonNull List<Event> unsyncEvents, @Nullable ClientClassification clientClassification) throws Exception {
        Event event = eventClient.getEvent();

        String eventType = event.getEventType();

        if (eventType.equals(MaternityConstants.EventType.MATERNITY_REGISTRATION)
                || eventType.equals(MaternityConstants.EventType.UPDATE_MATERNITY_REGISTRATION)) {
            ArrayList<EventClient> eventClients = new ArrayList<>();
            eventClients.add(eventClient);
            processClient(eventClients);

            updateRegisterTypeColumn(event, "maternity");

            HashMap<String, String> keyValues = new HashMap<>();
            generateKeyValuesFromEvent(event, keyValues);


            String conceptionDate = keyValues.get(MaternityConstants.Event.MaternityRegistration.CONCEPTION_DATE);
            String gravida = keyValues.get(MaternityConstants.Event.MaternityRegistration.GRAVIDA);
            String para = keyValues.get(MaternityConstants.Event.MaternityRegistration.PARA);
            String currentHivStatus = keyValues.get(MaternityConstants.Event.MaternityRegistration.CURRENT_HIV_STATUS);
            String previousHivStatus = keyValues.get(MaternityConstants.Event.MaternityRegistration.PREVIOUS_HIV_STATUS);

            if (gravida != null && conceptionDate != null) {

                MaternityDetails maternityDetails = new MaternityDetails(eventClient.getClient().getBaseEntityId(), gravida, conceptionDate);
                maternityDetails.setPara(para);
                maternityDetails.setHivStatus(currentHivStatus != null ? currentHivStatus : previousHivStatus);
                maternityDetails.setCreatedAt(new Date());

                //TODO: Figure out how to reuse the already created repository
                MaternityLibrary.getInstance().getMaternityDetailsRepository().saveOrUpdate(maternityDetails);
            } else {
                Timber.e(new Exception("Maternity Registration Event skipped for missing gravida or conceptionDate"));
            }
        } else if (eventType.equals(MaternityConstants.EventType.MATERNITY_CLOSE)) {
            if (eventClient.getClient() == null) {
                throw new CheckInEventProcessException(String.format("Client %s referenced by %s event does not exist", event.getBaseEntityId(), MaternityConstants.EventType.CHECK_IN));
            }

            unsyncEvents.add(event);
        }
    }

    private void generateKeyValuesFromEvent(@NonNull Event event, HashMap<String, String> keyValues) {
        List<Obs> obs = event.getObs();

        for (Obs observation : obs) {
            String key = observation.getFormSubmissionField();
            List<Object> values = observation.getValues();

            if (values.size() > 0) {
                String value = (String) values.get(0);

                if (!TextUtils.isEmpty(value)) {
                    keyValues.put(key, value);
                    continue;
                }
            }

            List<Object> humanReadableValues = observation.getHumanReadableValues();
            if (humanReadableValues.size() > 0) {
                String value = (String) humanReadableValues.get(0);

                if (!TextUtils.isEmpty(value)) {
                    keyValues.put(key, value);
                    continue;
                }
            }
        }
    }

    private void updateLastInteractedWith(@NonNull Event event, @NonNull String visitId) throws CheckInEventProcessException {
        String tableName = event.getEntityType();
        String lastInteractedWithDate = String.valueOf(new Date().getTime());

        ContentValues contentValues = new ContentValues();
        contentValues.put("last_interacted_with", lastInteractedWithDate);

        int recordsUpdated = MaternityLibrary.getInstance().getRepository().getWritableDatabase()
                .update(tableName, contentValues, "base_entity_id = ?", new String[]{event.getBaseEntityId()});

        if (recordsUpdated < 1) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }

        // Update FTS
        CommonRepository commonrepository = CoreLibrary.getInstance().context().commonrepository(tableName);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("last_interacted_with", lastInteractedWithDate);

        boolean isUpdated = false;
        String fieldName = "base_entity_id";
        if ("ec_child".equals(tableName)) {
            fieldName = "object_id";
        }

        if (commonrepository.isFts()) {
            recordsUpdated = MaternityLibrary.getInstance().getRepository().getWritableDatabase()
                    .update(CommonFtsObject.searchTableName(tableName), contentValues, fieldName + " = ?", new String[]{event.getBaseEntityId()});
            isUpdated = recordsUpdated > 0;
        }

        if (!isUpdated) {
            abortTransaction();
            throw new CheckInEventProcessException(String.format("Updating last interacted with for visit %s for base_entity_id %s in table %s failed"
                    , visitId
                    , event.getBaseEntityId()
                    , tableName));
        }
    }

    private void abortTransaction() {
        if (MaternityLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }

    private void commitSuccessfulTransaction() {
        if (MaternityLibrary.getInstance().getRepository().getWritableDatabase().inTransaction()) {
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().setTransactionSuccessful();
            MaternityLibrary.getInstance().getRepository().getWritableDatabase().endTransaction();
        }
    }


    @NonNull
    private OpdCheckIn generateCheckInRecordFromCheckInEvent(@NonNull Event event, @NonNull Client client, HashMap<String, String> keyValues, String visitId, Date visitDate) {
        OpdCheckIn checkIn = new OpdCheckIn();
        checkIn.setVisitId(visitId);
        checkIn.setPregnancyStatus(keyValues.get(MaternityConstants.JsonFormField.PREGNANCY_STATUS));
        checkIn.setHasHivTestPreviously(keyValues.get(MaternityConstants.JsonFormField.HIV_TESTED));
        checkIn.setHivResultsPreviously(keyValues.get(MaternityConstants.JsonFormField.HIV_PREVIOUS_STATUS));
        checkIn.setIsTakingArt(keyValues.get(MaternityConstants.JsonFormField.IS_PATIENT_TAKING_ART));
        checkIn.setCurrentHivResult(keyValues.get(MaternityConstants.JsonFormField.CURRENT_HIV_STATUS));
        checkIn.setVisitType(keyValues.get(MaternityConstants.JsonFormField.VISIT_TYPE));
        checkIn.setAppointmentScheduledPreviously(keyValues.get(MaternityConstants.JsonFormField.APPOINTMENT_DUE));
        checkIn.setAppointmentDueDate(keyValues.get(MaternityConstants.JsonFormField.APPOINTMENT_DUE_DATE));
        checkIn.setFormSubmissionId(event.getFormSubmissionId());
        checkIn.setBaseEntityId(client.getBaseEntityId());
        checkIn.setUpdatedAt(new Date().getTime());

        if (visitDate != null) {
            checkIn.setCreatedAt(visitDate.getTime());
        }

        return checkIn;
    }

    @Override
    public boolean unSync(@Nullable List<Event> events) {
        if (events != null) {
            for (Event event : events) {
                if (MaternityConstants.EventType.MATERNITY_CLOSE.equals(event.getEventType())) {
                    // Delete the maternity details
                    MaternityLibrary.getInstance().getMaternityDetailsRepository().delete(event.getBaseEntityId());

                    // Delete the actual client in the maternity table OR REMOVE THE Maternity register type
                    updateRegisterTypeColumn(event, null);
                }
            }
        }
        return true;
    }

    private void updateRegisterTypeColumn(@NonNull Event event, @Nullable String registerType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MaternityDbConstants.Column.Client.REGISTER_TYPE, registerType);

        MaternityLibrary.getInstance().context().commonrepository(MaternityDbConstants.Table.EC_CLIENT)
                .updateColumn(MaternityDbConstants.Table.EC_CLIENT, contentValues, event.getBaseEntityId());

        MaternityLibrary.getInstance().getRepository().getWritableDatabase()
                .update(CommonFtsObject.searchTableName(MaternityDbConstants.Table.EC_CLIENT)
                        , contentValues, MaternityDbConstants.Column.Client.BASE_ENTITY_ID + " = ?", new String[]{event.getBaseEntityId()});
    }
}