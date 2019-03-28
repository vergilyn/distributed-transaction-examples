package com.vergilyn.examples.response;

import java.io.Serializable;

import lombok.Data;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
@Data
public class BaseResponse implements Serializable {

    private int status = 200;

    private String message;
}
