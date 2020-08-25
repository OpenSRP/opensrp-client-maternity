package org.smartregister.maternity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.maternity.utils.MaternityDbConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.robolectric.util.ReflectionHelpers.setField;

@RunWith(MockitoJUnitRunner.class)
public class ClientLookUpListAdapterTest {

    private ClientLookUpListAdapter adapter;

    @Mock
    private List<CommonPersonObject> data;

    @Mock
    private Context context;

    @Mock
    private LayoutInflater mInflater;

    @Mock
    private static ClientLookUpListAdapter.ClickListener clickListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        adapter = new ClientLookUpListAdapter(data, context);
        setField(adapter, "clickListener", clickListener);
        setField(adapter, "mInflater", mInflater);
    }

    @Test
    public void onCreateViewHolderShouldReturnMyViewHolder() throws Exception {


        View itemView = mock(View.class);
        ViewGroup parent = mock(ViewGroup.class);
        ClientLookUpListAdapter.MyViewHolder viewHolder = mock(ClientLookUpListAdapter.MyViewHolder.class);

        doReturn(itemView).when(mInflater).inflate(anyInt(), any(ViewGroup.class), anyBoolean());

        int viewType = -1;

        assertThat(viewHolder, instanceOf(adapter.onCreateViewHolder(parent, viewType).getClass()));
    }

    @Test
    public void onBindViewHolderShouldVerifyImplementation() {

        TextView txtName = mock(TextView.class);
        View itemView = mock(View.class);
        TextView txtDetails = mock(TextView.class);

        ClientLookUpListAdapter.MyViewHolder viewHolder = mock(ClientLookUpListAdapter.MyViewHolder.class);
        setField(viewHolder, "txtName", txtName);
        setField(viewHolder, "itemView", itemView);
        setField(viewHolder, "txtDetails", txtDetails);

        String firstName = "First Name";
        String lastName = "Last Name";
        String openSrpId = "AFFFFFFF000000000000000000034";

        String caseId = "3sf-3-323ef3-fdsf3";
        String relationalId = "3223-s-df-3";
        String type = "unkonwn";
        Map<String, String> columnsMap = new HashMap<>();
        columnsMap.put(MaternityDbConstants.Column.Client.FIRST_NAME, firstName);
        columnsMap.put(MaternityDbConstants.Column.Client.LAST_NAME, lastName);
        columnsMap.put(MaternityDbConstants.KEY.OPENSRP_ID, openSrpId);

        CommonPersonObject commonPersonObject = new CommonPersonObject(caseId, relationalId, columnsMap, type);
        commonPersonObject.setColumnmaps(columnsMap);

        when(data.get(anyInt())).thenReturn(commonPersonObject);
        doReturn("Opensrp Id").when(context).getString(anyInt());

        int position = 0;
        adapter.onBindViewHolder(viewHolder, position);

        String fullName = firstName + " " + lastName;
        String details = "Opensrp Id - " + openSrpId;
        verify(txtName, Mockito.times(1)).setText(fullName);
        verify(txtDetails, Mockito.times(1)).setText(details);
    }

    @Test
    public void getItemCountShouldReturnValidSize() {

        int size = 1;
        when(data.size()).thenReturn(size);
        assertEquals(size, adapter.getItemCount());
    }

    @Test
    public void onClickShouldVerifyListener() {

        View view = mock(View.class);
        ClientLookUpListAdapter.MyViewHolder viewHolder = new ClientLookUpListAdapter.MyViewHolder(view);
        viewHolder.onClick(view);

        verify(clickListener, Mockito.times(1)).onItemClick(view);
    }

}
