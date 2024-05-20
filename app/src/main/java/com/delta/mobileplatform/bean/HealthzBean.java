package com.delta.mobileplatform.bean;

import java.util.List;

//public class HealthzBean {
//}
public class HealthzBean {
    private String status;
    private String machineName;
    private String systemId;
    private String totalDuration;
    private List<HealthCheck> healthChecks;

    public HealthzBean() {
    }

    public HealthzBean(String status, String machineName, String systemId, String totalDuration, List<HealthCheck> healthChecks) {
        this.status = status;
        this.machineName = machineName;
        this.systemId = systemId;
        this.totalDuration = totalDuration;
        this.healthChecks = healthChecks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<HealthCheck> getHealthChecks() {
        return healthChecks;
    }

    public void setHealthChecks(List<HealthCheck> healthChecks) {
        this.healthChecks = healthChecks;
    }
}

 class HealthCheck {
    private String name;
    private String preferredName;
    private List<String> categories;
    private String description;
    private String checkStart;
    private String duration;
    private String status;
    private List<String> traceMessages;
    private String errorMessage;

    public HealthCheck() {
    }

    public HealthCheck(String name, String preferredName, List<String> categories, String description, String checkStart, String duration, String status, List<String> traceMessages, String errorMessage) {
        this.name = name;
        this.preferredName = preferredName;
        this.categories = categories;
        this.description = description;
        this.checkStart = checkStart;
        this.duration = duration;
        this.status = status;
        this.traceMessages = traceMessages;
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCheckStart() {
        return checkStart;
    }

    public void setCheckStart(String checkStart) {
        this.checkStart = checkStart;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTraceMessages() {
        return traceMessages;
    }

    public void setTraceMessages(List<String> traceMessages) {
        this.traceMessages = traceMessages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}