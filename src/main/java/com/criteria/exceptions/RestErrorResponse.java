package com.criteria.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Karthik Suresh
 *
 */
@JsonInclude(Include.NON_NULL)
public class RestErrorResponse {

	private int errCode;

	private String errParam;

	private String errMessage;

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrParam() {
		return errParam;
	}

	public void setErrParam(String errParam) {
		this.errParam = errParam;
	}

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

}
