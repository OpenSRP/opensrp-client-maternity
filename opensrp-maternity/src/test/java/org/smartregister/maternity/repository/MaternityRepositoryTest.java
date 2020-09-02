package org.smartregister.maternity.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.maternity.BaseTest;
import org.smartregister.maternity.MaternityLibrary;
import org.smartregister.maternity.configuration.MaternityConfiguration;
import org.smartregister.maternity.pojo.MaternityMetadata;
import org.smartregister.maternity.utils.MaternityUtils;
import org.smartregister.repository.Repository;

import static org.junit.Assert.*;

public class MaternityRepositoryTest extends BaseTest {

    @Mock
    private MaternityLibrary maternityLibrary;

    @Mock
    private MaternityConfiguration maternityConfiguration;

    @Mock
    private MaternityMetadata maternityMetadata;

    private MaternityRepository maternityRepository;

    @Before
    public void setUp() {
        maternityRepository = new MaternityRepository();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testUpdateLastInteractedWithShouldCallExpectedMethods() {
        MaternityRepository maternityRepositorySpy = Mockito.spy(maternityRepository);

        String baseEntityId = "2wds-dw3rwer";
        String tableName = "ec_client";
        SQLiteDatabase sqLiteDatabase = Mockito.mock(SQLiteDatabase.class);
        CommonRepository commonRepository = Mockito.mock(CommonRepository.class);
        org.smartregister.Context context = Mockito.mock(org.smartregister.Context.class);

        Mockito.doReturn(true).when(commonRepository).isFts();
        Mockito.doReturn(commonRepository).when(context).commonrepository(tableName);
        Mockito.doReturn(context).when(maternityLibrary).context();
        Mockito.doReturn(sqLiteDatabase).when(maternityRepositorySpy).getWritableDatabase();
        Mockito.doReturn(tableName).when(maternityMetadata).getTableName();
        Mockito.doReturn(maternityMetadata).when(maternityConfiguration).getMaternityMetadata();
        Mockito.doReturn(maternityConfiguration).when(maternityLibrary).getMaternityConfiguration();

        ReflectionHelpers.setStaticField(MaternityLibrary.class, "instance", maternityLibrary);

        maternityRepositorySpy.updateLastInteractedWith(baseEntityId);

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .update(Mockito.eq(tableName),
                        Mockito.any(ContentValues.class),
                        Mockito.eq("base_entity_id = ?"),
                        Mockito.eq(new String[]{baseEntityId}));

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .update(Mockito.eq(CommonFtsObject.searchTableName(tableName)),
                        Mockito.any(ContentValues.class),
                        Mockito.eq(CommonFtsObject.idColumn + " = ?"),
                        Mockito.eq(new String[]{baseEntityId}));
    }
}