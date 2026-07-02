package org.swp.my_learning_path.service;

public interface SystemSettingService {
    String getSettingValue(String key, String defaultValue);
    Integer getSettingValueAsInteger(String key, Integer defaultValue);
    void saveSetting(String key, String value, String description);
}
