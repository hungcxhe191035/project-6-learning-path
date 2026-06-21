package org.swp.my_learning_path.service;

import org.swp.my_learning_path.dto.response.AdminDashboardDTO;

public interface AdminDashboardService {
    AdminDashboardDTO getDashboardData(String period);
}
