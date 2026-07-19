package org.swp.my_learning_path.configuration;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Custom failure handler: phân biệt lý do lỗi đăng nhập
        SimpleUrlAuthenticationFailureHandler failureHandler =
                new SimpleUrlAuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(
                            jakarta.servlet.http.HttpServletRequest request,
                            jakarta.servlet.http.HttpServletResponse response,
                            org.springframework.security.core.AuthenticationException exception)
                            throws java.io.IOException, jakarta.servlet.ServletException {
                        String redirectUrl;
                        if (exception instanceof LockedException) {
                            redirectUrl = "/login?locked=true";
                        } else if (exception instanceof DisabledException) {
                            redirectUrl = "/login?disabled=true";
                        } else {
                            redirectUrl = "/login?error=true";
                        }
                        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                    }
                };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Public pages
                        .requestMatchers(
                                "/",
                                "/home",
                                "/courses",
                                "/course/**",        // Trang chi tiết khoá học — xem được khi chưa login
                                "/search",           // Tìm kiếm — xem được khi chưa login
                                "/api/courses/*/blogs", // Xem danh sách blog công khai
                                "/api/blogs/*/view",    // Xem chi tiết blog công khai
                                "/login",
                                "/forgot-password",
                                "/register",
                                "/css/**",
                                "/js/**",
                                "/image/**"
                        ).permitAll()
                        // ADMIN only
                        .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        // INSTRUCTOR routes: chỉ STUDENT mới cần nộp đơn, INSTRUCTOR xem trạng thái
                        .requestMatchers("/instructor/courses/**", "/instructor/course/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/instructor/vouchers/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/instructor/qna/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/instructor/apply").hasAnyRole("STUDENT", "INSTRUCTOR")
                        .requestMatchers("/instructor/apply/status").hasAnyRole("STUDENT", "INSTRUCTOR")
                        // API Security: Khóa bảo mật API soạn khóa học và S3
                        .requestMatchers("/api/instructor/**").hasRole("INSTRUCTOR")
                        .requestMatchers("/api/s3/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                        // USER routes: các trang học tập, profile
                        .requestMatchers("/user/**")
                        .hasAnyRole("STUDENT", "INSTRUCTOR")
                        // Trang học — học viên đã đăng ký mới được vào
                        .requestMatchers("/learn/**")
                        .hasAnyRole("STUDENT", "INSTRUCTOR")
                        // Đăng ký khoá học miễn phí — phải đăng nhập
                        .requestMatchers("/api/enroll/**")
                        .authenticated()
                        // Tất cả request khác cần đăng nhập
                        .anyRequest()
                        .authenticated()
                )
                // Login bằng form
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/do-login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/home", true)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // Access denied
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}