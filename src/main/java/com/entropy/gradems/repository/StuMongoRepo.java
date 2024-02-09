package com.entropy.gradems.repository;

import com.entropy.gradems.po.Student;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface StuMongoRepo extends ReactiveMongoRepository<Student, String> {
}
