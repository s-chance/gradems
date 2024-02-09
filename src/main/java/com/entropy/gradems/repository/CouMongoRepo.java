package com.entropy.gradems.repository;

import com.entropy.gradems.po.Course;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CouMongoRepo extends ReactiveMongoRepository<Course, String> {
}
