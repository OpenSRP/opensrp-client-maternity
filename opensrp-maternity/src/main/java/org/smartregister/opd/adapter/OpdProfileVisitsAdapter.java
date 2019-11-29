package org.smartregister.opd.adapter;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
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
import org.smartregister.util.StringUtil;

import java.util.ArrayList;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-30
 */
public class OpdProfileVisitsAdapter extends RecyclerView.Adapter<OpdProfileVisitsAdapter.YamlViewHolder> {

    private Context context;
    private LayoutInflater mInflater;
    private ArrayList<Pair<YamlConfigWrapper, Facts>> items;

    // data is passed into the constructor
    public OpdProfileVisitsAdapter(@NonNull Context context, ArrayList<Pair<YamlConfigWrapper, Facts>> items) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.items = items;
    }

    // inflates the row layout from xml when needed
    @Override
    @NonNull
    public YamlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.opd_profile_overview_row, parent, false);
        return new YamlViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(@NonNull YamlViewHolder holder, int position) {
        Pair<YamlConfigWrapper, Facts> pair = items.get(position);

        YamlConfigWrapper yamlConfigWrapper = pair.first;
        Facts facts = pair.second;

        if (yamlConfigWrapper != null && facts != null) {
            String group = yamlConfigWrapper.getGroup();

            if (!TextUtils.isEmpty(group)) {
                holder.sectionHeader.setText(StringUtil.humanize(group));
                holder.sectionHeader.setVisibility(View.VISIBLE);
            } else {
                holder.sectionHeader.setVisibility(View.GONE);
            }

            String subGroup = yamlConfigWrapper.getSubGroup();
            if (!TextUtils.isEmpty(subGroup)) {
                if (OpdUtils.isTemplate(subGroup)) {
                    subGroup = OpdUtils.fillTemplate(subGroup, facts);
                }

                holder.subSectionHeader.setText(StringUtil.humanize(subGroup));
                holder.subSectionHeader.setVisibility(View.VISIBLE);
            } else {
                holder.subSectionHeader.setVisibility(View.GONE);
            }

            if (yamlConfigWrapper.getYamlConfigItem() != null) {
                YamlConfigItem yamlConfigItem = yamlConfigWrapper.getYamlConfigItem();

                fillSectionDetailAndTemplate(holder, facts, yamlConfigItem);
                setRowRedFontText(holder, facts, yamlConfigItem);

                holder.sectionDetailTitle.setVisibility(View.VISIBLE);
                holder.sectionDetails.setVisibility(View.VISIBLE);

            } else {
                holder.sectionDetailTitle.setVisibility(View.GONE);
                holder.sectionDetails.setVisibility(View.GONE);
            }
        }
    }

    private void fillSectionDetailAndTemplate(@NonNull YamlViewHolder holder, @NonNull Facts facts, @Nullable YamlConfigItem yamlConfigItem) {
        if (yamlConfigItem != null && yamlConfigItem.getTemplate() != null) {
            Template template = getTemplate(yamlConfigItem.getTemplate());

            boolean isHtml = yamlConfigItem.getHtml() != null && yamlConfigItem.getHtml();

            if (OpdUtils.isTemplate(template.detail)) {
                String output = OpdUtils.fillTemplate(isHtml, template.detail, facts);

                if (isHtml) {
                    OpdUtils.setTextAsHtml(holder.sectionDetails, output);
                } else {
                    holder.sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation
                }
            } else {
                holder.sectionDetails.setText(template.detail);
            }

            if (OpdUtils.isTemplate(template.title)) {
                String output = OpdUtils.fillTemplate(template.title, facts);
                holder.sectionDetailTitle.setText(output);
            } else {
                holder.sectionDetailTitle.setText(template.title);
            }
        }
    }

    private void setRowRedFontText(@NonNull YamlViewHolder holder, @NonNull Facts facts, @Nullable YamlConfigItem yamlConfigItem) {
        if (yamlConfigItem != null && yamlConfigItem.getIsRedFont() != null && OpdLibrary.getInstance().getOpdRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
            holder.sectionDetailTitle.setTextColor(getColor(R.color.overview_font_red));
            holder.sectionDetails.setTextColor(getColor(R.color.overview_font_red));
        } else {
            holder.sectionDetailTitle.setTextColor(getColor(R.color.overview_font_left));
            holder.sectionDetails.setTextColor(getColor(R.color.overview_font_right));
        }
    }

    private int getColor(@ColorRes int colorId) {
        return context.getResources().getColor(colorId);
    }

    private OpdProfileVisitsAdapter.Template getTemplate(String rawTemplate) {
        OpdProfileVisitsAdapter.Template template = new OpdProfileVisitsAdapter.Template();

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

    // total number of rows
    @Override
    public int getItemCount() {
        //return size;
        return items.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class YamlViewHolder extends RecyclerView.ViewHolder {

        public View parent;
        private TextView sectionHeader;
        private TextView subSectionHeader;
        private TextView sectionDetails;
        private TextView sectionDetailTitle;

        YamlViewHolder(View itemView) {
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