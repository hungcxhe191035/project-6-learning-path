package org.swp.my_learning_path.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherApplyResponse {
    boolean success;
    String message;
    String code;
    BigDecimal originalPrice;
    BigDecimal discountAmount;
    BigDecimal actualPaid;
    BigDecimal instructorShare;
    BigDecimal adminShare;
}
