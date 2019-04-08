package com.vergilyn.examples.repository;

import com.vergilyn.examples.entity.Account;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface AccountRepository extends CrudRepository<Account, Integer> {

    @Modifying
    @Query("update Account set amount = amount - ?2 where userId = ?1")
    int decrease(String userId, Double amount);
}
