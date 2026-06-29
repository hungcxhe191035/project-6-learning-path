package org.swp.my_learning_path.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swp.my_learning_path.entity.SystemSetting;
import org.swp.my_learning_path.repository.SystemSettingRepository;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;

    @Override
    @Transactional(readOnly = true)
    public String getSettingValue(String key, String defaultValue) {
        return systemSettingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(defaultValue);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getSettingValueAsInteger(String key, Integer defaultValue) {
        String val = getSettingValue(key, null);
        if (val == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    @Transactional
    public void saveSetting(String key, String value, String description) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElse(SystemSetting.builder()
                        .settingKey(key)
                        .build());
        setting.setSettingValue(value);
        if (description != null) {
            setting.setDescription(description);
        }
        setting.setDeleteFlag(false);
        systemSettingRepository.save(setting);
    }
}
