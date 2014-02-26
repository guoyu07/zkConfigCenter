package cn.ihuhai.zkConfigCenter.client.model;

import java.util.List;

/**
 * 配置项
 * @author huhai
 *
 */
public class ConfigurationItem {

	private String key;
	private String value;
	private List<ConfigurationItemChangeLog> changelogs;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<ConfigurationItemChangeLog> getChangelogs() {
		return changelogs;
	}

	public void setChangelogs(List<ConfigurationItemChangeLog> changelogs) {
		this.changelogs = changelogs;
	}

}
