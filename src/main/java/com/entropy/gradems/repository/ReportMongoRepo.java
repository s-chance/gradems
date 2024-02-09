package com.entropy.gradems.repository;

import com.entropy.gradems.po.Report;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReportMongoRepo extends ReactiveMongoRepository<Report,String> {
}
