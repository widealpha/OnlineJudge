package cn.sdu.oj.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class ResultEntity<T> {
    @JSONField(ordinal = 1)
    private int code;
    @JSONField(ordinal = 2)
    private String message;
    @JSONField(ordinal = 3)
    private T data;

    public ResultEntity() {
    }

    public ResultEntity(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResultEntity<Void> empty() {
        return new ResultEntity<>(0, "TODO", null);
    }

    public static ResultEntity<Boolean> success() {
        return new ResultEntity<>(0, "SUCCESS", true);
    }

    public static ResultEntity<Boolean> success(String message) {
        return new ResultEntity<>(0, message, true);
    }

    public static <T> ResultEntity<T> success(String message, T data) {
        return new ResultEntity<>(0, message, data);
    }

    public static <T> ResultEntity<T> data(T data) {
        return success("success", data);
    }

    public static ResultEntity<Void> data(StatusCode statusCode) {
        return new ResultEntity<>(statusCode.getCode(), statusCode.getMessage(), null);
    }

    public static <T> ResultEntity<T> data(StatusCode statusCode, T data) {
        return new ResultEntity<>(statusCode.getCode(), statusCode.getMessage(), data);
    }

    public static ResultEntity<Boolean> data() {
        return success("success");
    }

    public static ResultEntity<Boolean> error(String message) {
        return new ResultEntity<>(-1, message, false);
    }

    public static <T> ResultEntity<T> error(String message, T data) {
        return new ResultEntity<>(-1, message, data);
    }

    public static ResultEntity<Boolean> error(int code, String message) {
        return new ResultEntity<>(code, message, false);
    }

    public static <T> ResultEntity<T> error(StatusCode entity) {
        return new ResultEntity<>(entity.getCode(), entity.getMessage(), null);
    }
    public static <T> ResultEntity<T> error(StatusCode entity, String message) {
        return new ResultEntity<>(entity.getCode(), message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
