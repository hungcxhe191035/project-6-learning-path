package org.swp.my_learning_path.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopInstructorDTO {
    private Long instructorId;
    private String fullName;
    private String email;
    private Long courseCount;
    private BigDecimal revenue;
}
