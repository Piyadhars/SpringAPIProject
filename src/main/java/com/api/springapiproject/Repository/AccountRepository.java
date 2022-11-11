package com.api.springapiproject.Repository;

import com.api.springapiproject.Model.Account;
import com.api.springapiproject.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends MongoRepository<Account,String> {
    @Override
    Optional<Account> findById(String s);

    @Query(value = "{ '_id': ?0}")
    Account findByMailId(String c);
}
