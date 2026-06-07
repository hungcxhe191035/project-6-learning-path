package org.swp.my_learning_path.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterRequest {
    String email;
    String password;
    String confirmPassword;
    String fullName;
    String phone;
}
