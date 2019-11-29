package org.smartregister.opd.pojos;

public class OpdMultiSelectOption {

    private int id;
    private String json;
    private String version;
    private String type;
    private String createdAt;
    private String appVersion;

    public OpdMultiSelectOption() {
    }

    public OpdMultiSelectOption(String version, String type) {
        this.version = version;
        this.type = type;
    }

    public OpdMultiSelectOption(int id, String json, String version, String type, String createdAt) {
        this.id = id;
        this.json = json;
        this.version = version;
        this.type = type;
        this.createdAt = createdAt;
    }

    public OpdMultiSelectOption(int id, String json, String version, String type, String createdAt, String appVersion) {
        this.id = id;
        this.json = json;
        this.version = version;
        this.type = type;
        this.createdAt = createdAt;
        this.appVersion = appVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
