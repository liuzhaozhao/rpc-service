package service;

import java.io.Serializable;

public class DataBean<T> implements Serializable {
	private static final long serialVersionUID = 7933714516649991146L;

	public static final int CODE_SUCCESS = 0;
	public static final int CODE_FAIL = -1;

	private int successCode = CODE_SUCCESS;
	private int code;
	private T data;
	private String msg = "";

	public DataBean() {}

	/**
	 * 初始化一个失败的DataBean
	 * 
	 * @param msg
	 */
	public DataBean(String msg) {
		this.code = CODE_FAIL;
		this.msg = msg;
	}

	/**
	 * 初始化一个给定code的DataBean
	 * 
	 * @param msg
	 */
	public DataBean(int code) {
		this.code = code;
	}

	public DataBean(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static <T> DataBean<T> getSuccessData(T data) {
		DataBean<T> obj = new DataBean<T>(CODE_SUCCESS);
		obj.setSuccessData(data);
		return obj;
	}

//	public boolean isSuccessCode() {
//		return this.code == successCode;
//	}

	public DataBean<T> setData(T data) {
		this.data = data;
		return this;
	}

	public DataBean<T> setData(int code, T data) {
		this.code = code;
		this.data = data;
		return this;
	}

	public DataBean<T> setSuccessData(T data) {
		this.code = CODE_SUCCESS;
		this.data = data;
		return this;
	}

	public DataBean<T> setSuccessData(int code, T data) {
		this.code = code;
		this.successCode = code;
		this.data = data;
		return this;
	}

	public DataBean<T> setErrorMsg(String msg) {
		this.code = CODE_FAIL;
		this.msg = msg;
		return this;
	}

	public DataBean<T> setErrorMsg(int code, String msg) {
		this.code = code;
		this.msg = msg;
		return this;
	}

	public void setSuccessCode(int successCode) {
		this.successCode = successCode;
//		this.code = successCode;
	}
	
	public int getSuccessCode() {
		return successCode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

}
