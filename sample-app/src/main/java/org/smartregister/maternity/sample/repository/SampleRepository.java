package org.smartregister.maternity.sample.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.configurableviews.repository.ConfigurableViewsRepository;
import org.smartregister.maternity.repository.OpdCheckInRepository;
import org.smartregister.maternity.repository.MaternityDetailsRepository;
import org.smartregister.maternity.repository.OpdVisitRepository;
import org.smartregister.maternity.sample.BuildConfig;
import org.smartregister.maternity.sample.application.MaternitySampleApplication;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.Repository;
import org.smartregister.repository.SettingsRepository;
import org.smartregister.repository.UniqueIdRepository;

import timber.log.Timber;

/**
 *
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-11-29
 *
 */

public class SampleRepository extends Repository {


    public static String PASSWORD = "Sample_PASS";
    protected SQLiteDatabase readableDatabase;
    protected SQLiteDatabase writableDatabase;
    private Context context;

    public SampleRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(),
                MaternitySampleApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        super.onCreate(database);
        EventClientRepository
                .createTable(database, EventClientRepository.Table.client, EventClientRepository.client_column.values());
        EventClientRepository
                .createTable(database, EventClientRepository.Table.event, EventClientRepository.event_column.values());

        ConfigurableViewsRepository.createTable(database);
        UniqueIdRepository.createTable(database);

        SettingsRepository.onUpgrade(database);

        OpdVisitRepository.createTable(database);
        OpdCheckInRepository.createTable(database);
        MaternityDetailsRepository.createTable(database);
    }


    @Override
    public SQLiteDatabase getReadableDatabase() {
        return getReadableDatabase(PASSWORD);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(PASSWORD);
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase(String password) {
        if (writableDatabase == null || !writableDatabase.isOpen()) {
            if (writableDatabase != null) {
                writableDatabase.close();
            }
            writableDatabase = super.getWritableDatabase(password);
        }
        return writableDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase(String password) {
        try {
            if (readableDatabase == null || !readableDatabase.isOpen()) {
                if (readableDatabase != null) {
                    readableDatabase.close();
                }
                readableDatabase = super.getReadableDatabase(password);
            }
            return readableDatabase;
        } catch (Exception e) {
            Timber.e(e);
            return null;
        }

    }

    @Override
    public synchronized void close() {
        if (readableDatabase != null) {
            readableDatabase.close();
        }

        if (writableDatabase != null) {
            writableDatabase.close();
        }
        super.close();
    }

}
