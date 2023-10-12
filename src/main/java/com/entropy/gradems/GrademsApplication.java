package com.entropy.gradems;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.entropy.gradems.mapper")
public class GrademsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrademsApplication.class, args);
    }

}
