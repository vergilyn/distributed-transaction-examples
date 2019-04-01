package com.vergilyn.examples.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author VergiLyn
 * @date 2019-04-01
 */
@Data
@ToString
public class StorageDTO {
    private Integer id;
    private String commodityCode;
    private String name;
    private Integer total;
}
