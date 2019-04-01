package com.vergilyn.examples.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Data
public class BusinessDTO implements Serializable {

    private String userId;

    private String commodityCode;

    private String name;

    private Integer total;

    private BigDecimal amount;

    private Boolean rollback;
}
