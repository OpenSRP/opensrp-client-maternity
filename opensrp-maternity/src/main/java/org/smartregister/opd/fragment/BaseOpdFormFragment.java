package org.smartregister.opd.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.presenters.JsonWizardFormFragmentPresenter;

import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.event.Listener;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.adapter.ClientLookUpListAdapter;
import org.smartregister.opd.interactor.OpdFormInteractor;
import org.smartregister.opd.pojos.OpdMetadata;
import org.smartregister.opd.presenter.OpdFormFragmentPresenter;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.HashMap;
import java.util.List;

public class BaseOpdFormFragment extends JsonWizardFormFragment implements ClientLookUpListAdapter.ClickListener {

    private Snackbar snackbar = null;
    private AlertDialog alertDialog = null;
    private final Listener<List<CommonPersonObject>> lookUpListener =
            new Listener<List<CommonPersonObject>>() {
                @Override
                public void onEvent(List<CommonPersonObject> data) {
                    showClientLookUp(data);
                }
            };
    private OpdFormFragmentPresenter presenter;

    public static JsonWizardFormFragment getFormFragment(String stepName) {
        BaseOpdFormFragment jsonFormFragment = new BaseOpdFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(JsonFormConstants.JSON_FORM_KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Nullable
    private Form getForm() {
        return this.getActivity() != null && this.getActivity() instanceof JsonFormActivity ?
                ((JsonFormActivity) this.getActivity()).getForm() : null;
    }

    public OpdFormFragmentPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        super.updateVisibilityOfNextAndSave(next, save);
        Form form = getForm();
        OpdMetadata opdMetadata = OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata();

        if (form != null && form.isWizard() && opdMetadata != null
                && !opdMetadata.isFormWizardValidateRequiredFieldsBefore()) {
            this.getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(save);
        }
    }

    private void showClientLookUp(List<CommonPersonObject> data) {
        if (!data.isEmpty()) {
            showInfoSnackBar(data);
        } else {
            if (snackbar != null) {
                snackbar.dismiss();
            }
        }
    }

    private void showInfoSnackBar(final List<CommonPersonObject> data) {
        snackbar = Snackbar.make(getMainView(), getActivity().getString(R.string.client_matches, data.size()),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.tap_to_view, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateResultDialog(data);
            }
        });
        show(snackbar, 30000);
    }

    private void updateResultDialog(final List<CommonPersonObject> data) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.opd_lookup_results, null);
        RecyclerView recyclerView = view.findViewById(R.id.opd_lookup_recyclerview);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.OpdDialog);
        builder.setView(view).setNegativeButton(R.string.dismiss, null);
        builder.setCancelable(true);
        alertDialog = builder.create();
        //
        setUpDialog(recyclerView, data);
        //
        alertDialog.show();
    }

    protected void setUpDialog(RecyclerView recyclerView, List<CommonPersonObject> data) {
        ClientLookUpListAdapter clientLookUpListAdapter = new ClientLookUpListAdapter(data, getActivity());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(clientLookUpListAdapter);
        clientLookUpListAdapter.notifyDataSetChanged();
        clientLookUpListAdapter.setOnClickListener(this);
    }

    protected void show(final Snackbar snackbar, int duration) {
        if (snackbar == null) {
            return;
        }

        float drawablePadding = getResources().getDimension(R.dimen.register_drawable_padding);
        int paddingInt = Float.valueOf(drawablePadding).intValue();

        float textSize = getActivity().getResources().getDimension(R.dimen.snack_bar_text_size);

        View snackbarView = snackbar.getView();
        snackbarView.setMinimumHeight(Float.valueOf(textSize).intValue());
        snackbarView.setBackgroundResource(R.color.accent);

        final Button actionView = snackbarView.findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        actionView.setGravity(Gravity.CENTER);
        actionView.setTextColor(getResources().getColor(R.color.white));

        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setGravity(Gravity.CENTER);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(paddingInt);
        textView.setPadding(paddingInt, 0, 0, 0);
        textView.setTextColor(getResources().getColor(R.color.white));

        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionView.performClick();
            }
        });

        snackbar.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackbar.dismiss();
            }
        }, duration);

    }

    public Listener<List<CommonPersonObject>> lookUpListener() {
        return lookUpListener;
    }

    @Override
    public void onItemClick(View view) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
            CommonPersonObjectClient client = null;
            if (view.getTag() != null && view.getTag() instanceof CommonPersonObjectClient) {
                client = (CommonPersonObjectClient) view.getTag();
            }

            if (client != null) {
                startActivityOnLookUp(client);
            }
        }
    }

    protected void startActivityOnLookUp(@NonNull CommonPersonObjectClient client) {
        Intent intent = new Intent(getActivity(), OpdLibrary.getInstance().getOpdConfiguration().getOpdMetadata().getProfileActivity());

        // Add register_id FROM opensrp_id
        String opensrpId = client.getColumnmaps().get(OpdDbConstants.Column.Client.OPENSRP_ID);
        client.getColumnmaps().put(OpdConstants.ColumnMapKey.REGISTER_ID, opensrpId);
        client.getDetails().put(OpdConstants.ColumnMapKey.REGISTER_ID, opensrpId);

        intent.putExtra(OpdConstants.IntentKey.CLIENT_OBJECT, client);
        startActivity(intent);
    }

    @Override
    public void finishWithResult(Intent returnIntent) {
        Activity activity = getActivity();

        if (activity instanceof BaseOpdFormActivity) {
            BaseOpdFormActivity opdFormActivity = (BaseOpdFormActivity) activity;

            HashMap<String, String> parcelableData = opdFormActivity.getParcelableData();

            for (String key : parcelableData.keySet()) {
                String value = parcelableData.get(key);

                if (value != null) {
                    returnIntent.putExtra(key, value);
                }
            }
        }

        if (activity != null) {
            activity.setResult(Activity.RESULT_OK, returnIntent);
            activity.finish();
        }
    }

    @Override
    protected JsonWizardFormFragmentPresenter createPresenter() {
        presenter = new OpdFormFragmentPresenter(this, OpdFormInteractor.getInstance());
        return presenter;
    }
}
