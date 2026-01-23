package top.tangtian.balofhope.dlt.colletcion.model.resp;

/**
 * @author tangtian
 * @date 2026-01-21 13:07
 */
/**
 * 大乐透API响应最外层实体
 */
public class DltApiResponse {

	/** 数据来源 */
	private String dataFrom;

	/** 是否为空标志 */
	private Boolean emptyFlag;

	/** 错误代码，"0"表示成功 */
	private String errorCode;

	/** 错误消息 */
	private String errorMessage;

	/** 是否成功 */
	private Boolean success;

	/** 响应数据主体 */
	private DltResponseValue value;

	// Getters and Setters
	public String getDataFrom() {
		return dataFrom;
	}

	public void setDataFrom(String dataFrom) {
		this.dataFrom = dataFrom;
	}

	public Boolean getEmptyFlag() {
		return emptyFlag;
	}

	public void setEmptyFlag(Boolean emptyFlag) {
		this.emptyFlag = emptyFlag;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public DltResponseValue getValue() {
		return value;
	}

	public void setValue(DltResponseValue value) {
		this.value = value;
	}
}
