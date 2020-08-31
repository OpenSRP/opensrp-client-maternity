package org.smartregister.maternity.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.maternity.BaseRobolectricUnitTest;
import org.smartregister.maternity.pojo.MaternityChild;
import org.smartregister.maternity.utils.MaternityDbConstants;

public class MaternityChildRepositoryTest extends BaseRobolectricUnitTest {

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    private MaternityChildRepository maternityChildRepository;

    @Before
    public void setUp() {
        maternityChildRepository = new MaternityChildRepository();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTableShouldCreateTableAndIndexes() {
        MaternityChildRepository.createTable(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(3))
                .execSQL(Mockito.anyString());
    }

    @Test
    public void testSaveOrUpdateShouldCallInsert() {
        MaternityChild maternityChild = new MaternityChild();
        maternityChild.setBaseEntityId("324324");
        maternityChild.setMotherBaseEntityId("324234-wr");

        MaternityChildRepository maternityChildRepositorySpy = Mockito.spy(maternityChildRepository);

        Mockito.doReturn(sqLiteDatabase).when(maternityChildRepositorySpy)
                .getWritableDatabase();

        maternityChildRepositorySpy.saveOrUpdate(maternityChild);

        Mockito.verify(sqLiteDatabase, Mockito.times(1))
                .insert(Mockito.eq(MaternityDbConstants.Table.MATERNITY_CHILD),
                        Mockito.eq(null),
                        Mockito.any(ContentValues.class));
    }
}