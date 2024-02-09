package com.entropy.gradems.repository;

import com.entropy.gradems.po.Department;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DepartMongoRepo extends ReactiveMongoRepository<Department, String> {
}
