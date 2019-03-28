package com.vergilyn.examples.repository;

import com.vergilyn.examples.entity.Storage;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public interface StorageRepository extends CrudRepository<Storage, Integer> {

    /**
     * 扣减商品库存
     * @Param: commodityCode 商品code  count扣减数量
     * @Return:
     */
    @Modifying
    @Query("update Storage set total = ?2 where commodityCode = ?1")
    int decreaseStorage(String commodityCode, Integer total);
}
