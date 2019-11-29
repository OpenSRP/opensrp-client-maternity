package org.smartregister.opd.domain;


import android.support.annotation.Nullable;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */

public class YamlConfigItem {

    public static final String GENERIC_YAML_ITEMS = "generic_yaml_items";

    private String template;
    private String relevance;
    private String isRedFont;
    private Boolean isMultiWidget;
    private Boolean isHtml;

    public YamlConfigItem() {
    }

    public YamlConfigItem(@Nullable String template, @Nullable String relevance, @Nullable String isRedFont) {
        this.template = template;
        this.relevance = relevance;
        this.isRedFont = isRedFont;
        this.isMultiWidget = Boolean.FALSE;
    }

    @Nullable
    public String getIsRedFont() {
        return isRedFont;
    }

    public void setIsRedFont(@Nullable String isRedFont) {
        this.isRedFont = isRedFont;
    }

    @Nullable
    public String getTemplate() {
        return template;
    }

    public void setTemplate(@Nullable String template) {
        this.template = template;
    }

    @Nullable
    public String getRelevance() {
        return relevance;
    }

    public void setRelevance(@Nullable String relevance) {
        this.relevance = relevance;
    }

    @Nullable
    public Boolean isMultiWidget() {
        return isMultiWidget;
    }

    public void setIsMultiWidget(@Nullable Boolean multiWidget) {
        this.isMultiWidget = multiWidget;
    }

    @Nullable
    public Boolean getHtml() {
        return isHtml;
    }

    public void setHtml(Boolean html) {
        isHtml = html;
    }
}
