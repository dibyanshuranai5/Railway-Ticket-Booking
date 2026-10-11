package com.trainbooking.pnr.repository;

import com.trainbooking.pnr.model.PnrRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PnrRepository extends MongoRepository<PnrRecord, String> {
}
