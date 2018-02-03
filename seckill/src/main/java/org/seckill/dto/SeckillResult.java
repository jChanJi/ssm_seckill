package org.seckill.dto;

/**
 * Create By ChanJi on 2018/1/30
 */
//封装json结果
public class SeckillResult<T> {
    private boolean success;
    private  T data;
    private  String errror;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String errror) {
        this.success = success;
        this.errror = errror;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrror() {
        return errror;
    }

    public void setErrror(String errror) {
        this.errror = errror;
    }
}
