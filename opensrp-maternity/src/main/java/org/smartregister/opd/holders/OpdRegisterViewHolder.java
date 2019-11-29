package org.smartregister.opd.holders;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.smartregister.opd.R;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-13
 */

public class OpdRegisterViewHolder extends RecyclerView.ViewHolder {
    public TextView textViewParentName;
    public TextView textViewChildName;
    public TextView textViewGender;
    public Button dueButton;
    public View dueButtonLayout;
    public View childColumn;
    public TextView tvRegisterType;
    public TextView tvLocation;

    public TextView secondDotDivider;
    public TextView firstDotDivider;

    public OpdRegisterViewHolder(View itemView) {
        super(itemView);

        textViewParentName = itemView.findViewById(R.id.tv_opdRegisterListRow_parentName);
        textViewChildName = itemView.findViewById(R.id.tv_opdRegisterListRow_childName);
        textViewGender = itemView.findViewById(R.id.tv_opdRegisterListRow_gender);
        dueButton = itemView.findViewById(R.id.btn_opdRegisterListRow_clientAction);
        dueButtonLayout = itemView.findViewById(R.id.ll_opdRegisterListRow_clientActionWrapper);
        tvRegisterType = itemView.findViewById(R.id.tv_opdRegisterListRow_registerType);
        tvLocation = itemView.findViewById(R.id.tv_opdRegisterListRow_location);

        childColumn = itemView.findViewById(R.id.child_column);
        secondDotDivider = itemView.findViewById(R.id.tv_opdRegisterListRow_secondDotDivider);
        firstDotDivider = itemView.findViewById(R.id.tv_opdRegisterListRow_firstDotDivider);
    }

    public void showCareGiverName() {
        textViewParentName.setVisibility(View.VISIBLE);
    }

    public void removeCareGiverName() {
        textViewParentName.setVisibility(View.GONE);
    }

    public void showPersonLocation() {
        tvLocation.setVisibility(View.VISIBLE);
        secondDotDivider.setVisibility(View.VISIBLE);
    }

    public void removePersonLocation() {
        tvLocation.setVisibility(View.GONE);
        secondDotDivider.setVisibility(View.GONE);
    }

    public void showRegisterType() {
        tvRegisterType.setVisibility(View.VISIBLE);
        firstDotDivider.setVisibility(View.VISIBLE);
    }

    public void hideRegisterType() {
        tvRegisterType.setVisibility(View.GONE);
        firstDotDivider.setVisibility(View.GONE);
    }
}