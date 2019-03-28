package com.vergilyn.examples.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Entity
@Table(name = "storage")
@Data
@NoArgsConstructor
@ToString
public class Storage implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String commodityCode;
    private String name;
    private Integer total;

}
