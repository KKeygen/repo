package com.dismai.exception;

import com.dismai.common.ApiResponse;
import com.dismai.enums.BaseCode;
import lombok.Data;

@Data
public class DismaiFrameException extends BaseException {

	private Integer code;
	
	private String message;

	public DismaiFrameException() {
		super();
	}

	public DismaiFrameException(String message) {
		super(message);
	}
	
	
	public DismaiFrameException(String code, String message) {
		super(message);
		this.code = Integer.parseInt(code);
		this.message = message;
	}
	
	public DismaiFrameException(Integer code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
	
	public DismaiFrameException(BaseCode baseCode) {
		super(baseCode.getMsg());
		this.code = baseCode.getCode();
		this.message = baseCode.getMsg();
	}
	
	public DismaiFrameException(ApiResponse apiResponse) {
		super(apiResponse.getMessage());
		this.code = apiResponse.getCode();
		this.message = apiResponse.getMessage();
	}

	public DismaiFrameException(Throwable cause) {
		super(cause);
	}

	public DismaiFrameException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	public DismaiFrameException(Integer code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
		this.message = message;
	}
}
