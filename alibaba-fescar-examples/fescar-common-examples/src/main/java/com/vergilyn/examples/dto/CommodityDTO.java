package com.vergilyn.examples.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Data
public class CommodityDTO implements Serializable {

    private Integer id;

    private String commodityCode;

    private String name;

    private Integer count;
}
