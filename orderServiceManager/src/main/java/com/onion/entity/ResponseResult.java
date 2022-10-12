package com.onion.entity;

/**
 * @author onion_Knight
 */
public class ResponseResult {

    private Integer code;

    private String msg;

    private static final Integer CODE = 200;

    private static final String MSG = "success!";

    public ResponseResult(){
        this(CODE,MSG);
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static ResponseResult success() {
        return new ResponseResult();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
