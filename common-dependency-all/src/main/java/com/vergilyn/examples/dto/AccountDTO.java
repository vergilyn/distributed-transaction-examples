package com.vergilyn.examples.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Data
public class AccountDTO implements Serializable {

    private Integer id;

    private String userId;

    private BigDecimal amount;
}
