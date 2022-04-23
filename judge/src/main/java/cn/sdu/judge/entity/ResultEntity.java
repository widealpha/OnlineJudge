package cn.sdu.judge.entity;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

public class ResultEntity {
    @JSONField(ordinal = 1)
    private int code;
    @JSONField(ordinal = 2)
    private String message;
    @JSONField(ordinal = 3)
    private Object data;

    public ResultEntity(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResultEntity empty() {
        return new ResultEntity(0, "", null);
    }

    public static ResultEntity success() {
        return new ResultEntity(0, "success", true);
    }

    public static ResultEntity success(String message) {
        return new ResultEntity(0, message, true);
    }

    public static ResultEntity success(String message, Object data) {
        return new ResultEntity(0, message, data);
    }

    public static ResultEntity data(Object data) {
        return success("success", data);
    }

    public static ResultEntity data(StatusCode statusCode) {
        if (statusCode == StatusCode.SUCCESS) {
            return new ResultEntity(statusCode.getCode(), statusCode.getMessage(), true);
        } else {
            return new ResultEntity(statusCode.getCode(), statusCode.getMessage(), false);
        }
    }

    public static ResultEntity data(StatusCode statusCode, Object data) {
        if (data != null) {
            return new ResultEntity(statusCode.getCode(), statusCode.getMessage(), data);
        } else if (statusCode == StatusCode.SUCCESS) {
            return new ResultEntity(statusCode.getCode(), statusCode.getMessage(), true);
        } else {
            return new ResultEntity(statusCode.getCode(), statusCode.getMessage(), false);
        }
    }

    public static ResultEntity data() {
        return success("success");
    }

    public static ResultEntity error(String message) {
        return new ResultEntity(-1, message, false);
    }

    public static ResultEntity error(String message, Object data) {
        return new ResultEntity(-1, message, data);
    }

    public static ResultEntity error(int code, String message) {
        return new ResultEntity(code, message, false);
    }

    public static ResultEntity error(StatusCode entity) {
        return new ResultEntity(entity.getCode(), entity.getMessage(), false);
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
