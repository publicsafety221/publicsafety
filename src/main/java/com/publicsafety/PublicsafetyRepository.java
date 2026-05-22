package com.publicsafety;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PublicsafetyRepository extends MongoRepository<Publicsafety, String> {
}