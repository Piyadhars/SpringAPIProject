package com.api.springapiproject.Repository;

import com.api.springapiproject.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    @Override
    Optional<User> findById(String s);

    @Override
    boolean existsById(String s);

    @Query(value = "{ '_id': ?0}")
    User findByMail(String c);

    @Query(value = "{ 'confirmation_token': ?0}")
    User findByConfirmation_token(String c);

//    @Query(fields = "{'_id' :1}")
//    User findId();
    @Query("SELECT email FROM USER")
    List<User> findId();


}
