package org.smartregister.opd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.smartregister.opd.OpdLibrary;
import org.smartregister.opd.R;
import org.smartregister.opd.domain.YamlConfigItem;
import org.smartregister.opd.domain.YamlConfigWrapper;
import org.smartregister.opd.utils.OpdUtils;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class OpdProfileOverviewAdapter extends RecyclerView.Adapter<OpdProfileOverviewAdapter.ViewHolder> {

    private List<YamlConfigWrapper> mData;
    private LayoutInflater mInflater;
    private Facts facts;
    private Context context;

    // data is passed into the constructor
    public OpdProfileOverviewAdapter(@NonNull Context context, @NonNull List<YamlConfigWrapper> data, @NonNull Facts facts) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.facts = facts;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.opd_profile_overview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String group = mData.get(position).getGroup();
        if (!TextUtils.isEmpty(group)) {
            holder.sectionHeader.setText(processUnderscores(group));
            holder.sectionHeader.setVisibility(View.VISIBLE);
        } else {
            holder.sectionHeader.setVisibility(View.GONE);
        }

        String subGroup = mData.get(position).getSubGroup();
        if (!TextUtils.isEmpty(subGroup)) {
            holder.subSectionHeader.setText(processUnderscores(subGroup));
            holder.subSectionHeader.setVisibility(View.VISIBLE);
        } else {
            holder.subSectionHeader.setVisibility(View.GONE);
        }

        if (mData.get(position).getYamlConfigItem() != null) {

            YamlConfigItem yamlConfigItem = mData.get(position).getYamlConfigItem();

            if (yamlConfigItem != null && yamlConfigItem.getTemplate() != null) {
                Template template = getTemplate(yamlConfigItem.getTemplate());
                String output = OpdUtils.fillTemplate(template.detail, this.facts);

                holder.sectionDetailTitle.setText(template.title);
                holder.sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation
            }

            if (yamlConfigItem != null && yamlConfigItem.getIsRedFont() != null && OpdLibrary.getInstance().getOpdRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
            } else {
                holder.sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
                holder.sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));
            }

            holder.sectionDetailTitle.setVisibility(View.VISIBLE);
            holder.sectionDetails.setVisibility(View.VISIBLE);

        } else {
            holder.sectionDetailTitle.setVisibility(View.GONE);
            holder.sectionDetails.setVisibility(View.GONE);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    private Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
        }

        return template;

    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View parent;
        private TextView sectionHeader;
        private TextView subSectionHeader;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;

        ViewHolder(View itemView) {
            super(itemView);
            sectionHeader = itemView.findViewById(R.id.overview_section_header);
            subSectionHeader = itemView.findViewById(R.id.overview_subsection_header);
            sectionDetailTitle = itemView.findViewById(R.id.overview_section_details_left);
            sectionDetails = itemView.findViewById(R.id.overview_section_details_right);

            parent = itemView;
        }
    }

    private class Template {
        public String title = "";
        public String detail = "";
    }

}