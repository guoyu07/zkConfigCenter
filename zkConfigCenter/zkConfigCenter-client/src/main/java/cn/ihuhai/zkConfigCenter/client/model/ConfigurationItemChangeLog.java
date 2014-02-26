package cn.ihuhai.zkConfigCenter.client.model;

/**
 * 配置项变化日志
 * @author huhai
 *
 */
public class ConfigurationItemChangeLog {

	private String value;
	private String operator;
	private long operationTime;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public long getOperationTime() {
		return operationTime;
	}

	public void setOperationTime(long operationTime) {
		this.operationTime = operationTime;
	}
}
