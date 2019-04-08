package com.vergilyn.examples.repository;

import com.vergilyn.examples.entity.Order;

import org.springframework.data.repository.CrudRepository;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface OrderRepository extends CrudRepository<Order, Integer> {
}
