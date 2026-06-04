package org.swp.my_learning_path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class MyLearningPathApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLearningPathApplication.class, args);
    }

}
