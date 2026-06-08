package org.swp.my_learning_path.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {

    private Long cartItemId;

    private Long courseId;

    private String courseTitle;

    private String thumbnailUrl;

    private BigDecimal price;

    private String instructorName;
}
