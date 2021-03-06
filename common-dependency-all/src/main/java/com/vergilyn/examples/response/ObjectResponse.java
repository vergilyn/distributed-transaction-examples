package com.vergilyn.examples.response;

import java.io.Serializable;

import com.vergilyn.examples.enums.RspStatusEnum;

/**
 * @author VergiLyn
 * @date 2019-03-28
 */
public class ObjectResponse<T> extends BaseResponse implements Serializable {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ObjectResponse<T> result(RspStatusEnum status){
        this.setStatus(status.getCode());
        this.setMessage(status.getMessage());

        return this;
    }

    public static ObjectResponse<Void> success(){
        return success(null);
    }

    public static <T> ObjectResponse<T> success(T data){
        ObjectResponse<T> response = new ObjectResponse<>();
        response.setData(data);
        response.result(RspStatusEnum.SUCCESS);

        return response;
    }

    public static ObjectResponse<Void> failure(){
        return failure(null);
    }

    public static <T> ObjectResponse<T> failure(T data){
        ObjectResponse<T> response = new ObjectResponse<>();
        response.setData(data);
        response.result(RspStatusEnum.FAIL);

        return response;
    }

    public static <T> ObjectResponse<T> failureOther(String errorMsg){
        ObjectResponse<T> response = new ObjectResponse<>();
        response.setMessage(errorMsg);
        response.setStatus(RspStatusEnum.FAIL_OTHER.getCode());

        return response;
    }
}
