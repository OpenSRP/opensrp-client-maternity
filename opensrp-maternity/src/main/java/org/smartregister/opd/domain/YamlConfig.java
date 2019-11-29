package org.smartregister.opd.domain;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class YamlConfig {

    private String group;
    private String sub_group;
    private List<YamlConfigItem> fields;
    private String test_results;

    public YamlConfig() {
    }

    public YamlConfig(@Nullable String group, @Nullable String sub_group, @Nullable List<YamlConfigItem> fields, @Nullable String test_results) {
        this.group = group;
        this.sub_group = sub_group;
        this.fields = fields;
        this.test_results = test_results;
    }

    @Nullable
    public String getSubGroup() {
        return sub_group;
    }

    public void setSubGroup(@Nullable String sub_group) {
        this.sub_group = sub_group;
    }

    @Nullable
    public String getGroup() {
        return group;
    }

    public void setGroup(@Nullable String group) {
        this.group = group;
    }

    @Nullable
    public List<YamlConfigItem> getFields() {
        return fields;
    }

    public void setFields(@Nullable List<YamlConfigItem> fields) {
        this.fields = fields;
    }

    @Nullable
    public String getTestResults() {
        return test_results;
    }

    public void setTestResults(@Nullable String test_results) {
        this.test_results = test_results;
    }

}