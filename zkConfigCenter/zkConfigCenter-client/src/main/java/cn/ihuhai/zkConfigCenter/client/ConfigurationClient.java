package cn.ihuhai.zkConfigCenter.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ihuhai.zkConfigCenter.client.cache.CacheProvider;
import cn.ihuhai.zkConfigCenter.client.model.ConfigurationItem;
import cn.ihuhai.zkConfigCenter.client.util.JsonUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.zkclient.IZkChildListener;
import com.github.zkclient.IZkClient;
import com.github.zkclient.IZkDataListener;
import com.github.zkclient.ZkClient;

/**
 * 配置管理客户端
 * 
 * @author huhai
 * 
 */
public class ConfigurationClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationClient.class);

	private String configName;
	private ResourceBundle rb;
	private CacheProvider cacheProvider;

	private String root;
	private String defaultConfigNodeName;
	private String configNodeName;
	private String productLine;
	private String configBasePath;
	private String configPath;
	private String zkServerList;
	private IZkClient zkClient;
	private int sessionTimeout;
	private int connectionTimeout;

	public ConfigurationClient(String resourceFileName) {
		this(resourceFileName, "default");
	}

	/**
	 * 构造器实例
	 * 
	 * @param resourceFileName
	 *            配置文件名称（properties文件，不带后缀）
	 * @param configName
	 *            配置名称
	 */
	public ConfigurationClient(String resourceFileName, String configName) {
		if (StringUtils.isNotBlank(configName)) {
			this.configName = configName;
		} else {
			throw new IllegalArgumentException("configName should not be null and empty");
		}

		rb = ResourceBundle.getBundle(resourceFileName);
		root = StringUtils.defaultIfBlank(rb.getString("node.root"), "/configCenter");
		configNodeName = StringUtils.defaultIfBlank(rb.getString("node.config"), "config");
		defaultConfigNodeName = StringUtils.defaultIfBlank(rb.getString("node.default"), "default");
		productLine = StringUtils.defaultIfBlank(rb.getString("node.productLine"), "productLine1");
		configBasePath = root + Constant.PATH_DELIMITER + configNodeName + productLine + Constant.PATH_DELIMITER;
		configPath = configBasePath + configName;
		zkServerList = rb.getString("servers");
		String sessionTimeoutStr = rb.getString("sessionTimeout");
		String connectionTimeoutStr = rb.getString("connectionTimeout");
		configName = defaultConfigNodeName;
		
		if(NumberUtils.isNumber(sessionTimeoutStr)){
			sessionTimeout = Integer.valueOf(sessionTimeoutStr);
		}else{
			sessionTimeout = IZkClient.DEFAULT_SESSION_TIMEOUT;
		}
		if(NumberUtils.isNumber(connectionTimeoutStr)){
			connectionTimeout = Integer.valueOf(connectionTimeoutStr);
		}else{
			connectionTimeout = IZkClient.DEFAULT_CONNECTION_TIMEOUT;
		}
	}
	
	public void init(){
		if(StringUtils.isNotBlank(zkServerList)){
			zkClient = new ZkClient(zkServerList, sessionTimeout, connectionTimeout);
		}
	}
	
	public void watch() {
		if (isReady()) {
			zkClient.subscribeDataChanges("", new IZkDataListener() {
				public void handleDataDeleted(String path) throws Exception {

				}

				public void handleDataChange(String path, byte[] data) throws Exception {

				}
			});
			zkClient.subscribeChildChanges(configBasePath, new IZkChildListener(){

				public void handleChildChange(String parentPath, List<String> currentChildren) throws Exception {
					// TODO Auto-generated method stub
				}
				
			});
		}
	}
	
	private boolean isReady(){
		return (null != zkClient && StringUtils.isNotBlank(configBasePath));
	}
	
	private List<ConfigurationItem> getConfigurationFromZk(){
		List<ConfigurationItem> result = Collections.emptyList();
		if(isReady()){
			if(zkClient.exists(configPath)){
				List<String> list = zkClient.getChildren(configPath);
				if(null != list && !list.isEmpty()){
					result = new ArrayList<ConfigurationItem>();
					for(String item : list){
						result.add((ConfigurationItem) JsonUtil.deserialize(item, ConfigurationItem.class));
					}
				}
			}
		}
		
		return result;
	}
	
	private List<ConfigurationItem> getConfigurationFromFile(){
		//TODO 
		return null;
	}

	private String getPath(String key) {
		return configPath + Constant.PATH_DELIMITER + key;
	}

	public String get(String key) {
		return getPath(key);
	}

	public Integer getInteger(String key) {
		String value = getPath(key);
		if (NumberUtils.isDigits(value)) {
			return Integer.valueOf(value);
		} else {
			LOGGER.error("value is not an integer num:" + key + "=" + value);
		}
		return null;
	}

	public Long getLong(String key) {
		String value = getPath(key);
		if (NumberUtils.isDigits(value)) {
			return Long.valueOf(value);
		} else {
			LOGGER.error("value is not an long num:" + key + "=" + value);
		}
		return null;
	}

	public Double getDouble(String key) {
		String value = getPath(key);
		if (NumberUtils.isNumber(value)) {
			return Double.valueOf(value);
		} else {
			LOGGER.error("value is not an num:" + key + "=" + value);
		}
		return null;
	}

	/**
	 * 获取字符串数组配置项值
	 * 
	 * @param key
	 *            配置项名称
	 * @param delimiter
	 *            数组元素分隔符
	 * @param trim
	 *            是否去掉首尾空格
	 * @return
	 */
	public String[] getStringArray(String key, String delimiter, boolean trim) {
		String value = getPath(key);
		if (StringUtils.isNotBlank(value)) {
			String[] arr = value.split(delimiter);
			if (arr.length > 0 && trim) {
				for (int i = 0; i < arr.length; i++) {
					arr[i] = arr[i].trim();
				}
			}
			return arr;
		}
		return null;
	}

	/**
	 * 获取字符串数组配置项值(使用英文分号作为分隔符，去掉数组元素首位空格)
	 * 
	 * @param key
	 *            配置项名称
	 * @return
	 */
	public String[] getStringArray(String key) {
		return getStringArray(key, Constant.ARRAY_ELEMENT_DELIMITER, true);
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
		configPath = configBasePath + configName;
	}
	
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getDefaultConfigNodeName() {
		return defaultConfigNodeName;
	}

	public void setDefaultConfigNodeName(String defaultConfigNodeName) {
		this.defaultConfigNodeName = defaultConfigNodeName;
	}

	public String getConfigNodeName() {
		return configNodeName;
	}

	public void setConfigNodeName(String configNodeName) {
		this.configNodeName = configNodeName;
	}

	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}

	public String getConfigBasePath() {
		return configBasePath;
	}

	public void setConfigBasePath(String configBasePath) {
		this.configBasePath = configBasePath;
	}

	public String getZkServerList() {
		return zkServerList;
	}

	public void setZkServerList(String zkServerList) {
		this.zkServerList = zkServerList;
	}

	public IZkClient getZkClient() {
		return zkClient;
	}

	public void setZkClient(IZkClient zkClient) {
		this.zkClient = zkClient;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public CacheProvider getCacheProvider() {
		return cacheProvider;
	}

	public void setCacheProvider(CacheProvider cacheProvider) {
		this.cacheProvider = cacheProvider;
	}

}
