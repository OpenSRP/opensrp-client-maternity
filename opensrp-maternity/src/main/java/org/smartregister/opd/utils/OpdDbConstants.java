package org.smartregister.opd.utils;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public interface OpdDbConstants {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    interface KEY {
        String ID = "_id";
        String MOTHER_FIRST_NAME = "mother_first_name";
        String MOTHER_MIDDLE_NAME = "mother_middle_name";
        String MOTHER_LAST_NAME = "mother_last_name";
        String HOME_ADDRESS = "home_address";

        String REGISTER_TYPE = "register_type";

        String FIRST_NAME = "first_name";
        String MIDDLE_NAME = "middle_name";
        String LAST_NAME = "last_name";
        String DOB = "dob";

        String GENDER = "gender";

        String REGISTER_ID = "register_id";
        String BASE_ENTITY_ID = "base_entity_id";

        String TABLE = "ec_client";
        String OPENSRP_ID = "opensrp_id";
        String LAST_INTERACTED_WITH = "last_interacted_with";
        String DATE_REMOVED = "date_removed";
    }

    interface Column {

        interface OpdCheckIn {
            String ID = "_id";
            String FORM_SUBMISSION_ID = "form_submission_id";
            String VISIT_ID = "visit_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String PREGNANCY_STATUS = "pregnancy_status";
            String HAS_HIV_TEST_PREVIOUSLY = "has_hiv_test_previously";
            String HIV_RESULTS_PREVIOUSLY = "hiv_results_previously";
            String IS_TAKING_ART = "is_taking_art";
            String CURRENT_HIV_RESULT = "current_hiv_result";
            String VISIT_TYPE = "visit_type";
            String APPOINTMENT_SCHEDULED_PREVIOUSLY = "appointment_scheduled_previously";
            String APPOINTMENT_DUE_DATE = "appointment_due_date";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
        }

        interface OpdMultiSelectOptions {
            String ID = "_id";
            String JSON = "json";
            String TYPE = "type";
            String VERSION = "version";
            String CREATED_AT = "created_at";
            String APP_VERSION = "app_version";
        }

        interface OpdVisit {
            String ID = "_id";
            String VISIT_DATE = "visit_date";
            String PROVIDER_ID = "provider_id";
            String LOCATION_ID = "location_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String CREATED_AT = "created_at";
        }

        interface Client {
            String ID = "_id";
            String PHOTO = "photo";
            String FIRST_NAME = "first_name";
            String LAST_NAME = "last_name";
            String BASE_ENTITY_ID = "base_entity_id";
            String DOB = "dob";
            String OPENSRP_ID = "opensrp_id";
            String RELATIONALID = "relationalid";
            String NATIONAL_ID = "national_id";
            String GENDER = "gender";
        }

        interface OpdDetails {
            String ID = "_id";
            String BASE_ENTITY_ID = "base_entity_id";
            String PENDING_DIAGNOSE_AND_TREAT = "pending_diagnose_and_treat";
            String CURRENT_VISIT_START_DATE = "current_visit_start_date";
            String CURRENT_VISIT_END_DATE = "current_visit_end_date";
            String CURRENT_VISIT_ID = "visit_id";
            String CREATED_AT = "created_at";
        }

        interface OpdDiagnosisAndTreatmentForm {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FORM = "form";
            String CREATED_AT = "created_at";
        }

        interface OpdDiagnosis {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String DIAGNOSIS = "diagnosis";
            String TYPE = "type";
            String DISEASE = "disease";
            String ICD10_CODE = "icd10_code";
            String CODE = "code";
            String DETAILS = "details";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
            String VISIT_ID = "visit_id";
        }

        interface OpdTreatment {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String MEDICINE = "medicine";
            String TYPE = "type";
            String DOSAGE = "dosage";
            String DURATION = "duration";
            String NOTE = "note";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
            String VISIT_ID = "visit_id";
            String PROPERTY = "property";
        }

        interface OpdServiceDetail {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String FEE = "fee";
            String VISIT_ID = "visit_id";
            String DETAILS = "details";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
        }

        interface OpdTestConducted {
            String ID = "id";
            String BASE_ENTITY_ID = "base_entity_id";
            String TEST = "test";
            String RESULT = "result";
            String CREATED_AT = "created_at";
            String UPDATED_AT = "updated_at";
            String VISIT_ID = "visit_id";
        }
    }

    interface Table {

        String EC_CLIENT = "ec_client";
        String OPD_CHECK_IN = "opd_check_in";
        String OPD_VISIT = "opd_visit";
        String OPD_DETAILS = "opd_details";
        String OPD_DIAGNOSIS_AND_TREATMENT_FORM = "opd_diagnosis_and_treatment_form";
        String OPD_DIAGNOSIS = "opd_diagnosis";
        String OPD_TREATMENT = "opd_treatment";
        String OPD_SERVICE_DETAIL = "opd_service_detail";
        String OPD_TEST_CONDUCTED = "opd_test_conducted";
        String OPD_MULTI_SELECT_LIST_OPTION = "opd_multi_select_list_option";

    }
}
