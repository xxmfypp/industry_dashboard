/******************************************************************
 * Copyright 2014 Payeigs Inc., All rights reserved.  
 *
 *	FILE:	ConfigFileUtil.java
 * CREATOR:	xuman.xu
 * *****************************************************************/
package cc.gavin.grumman.zeta.util;

import java.io.File;

import org.apache.log4j.Logger;

import com.jfinal.kit.PropKit;

public class ConfigFileUtil {

	protected final static Logger logger = Logger
			.getLogger(ConfigFileUtil.class);

	private static long lastModifyTime = 0l;

	private static String configHome = System.getenv("ZETA_HOME");

	private static String configFilePath = configHome + File.separator + "conf"
			+ File.separator + "industry_dashboard.properties";

	public static void getConfig() {
		if (lastModifyTime == 0l) {
			lastModifyTime = new File(configFilePath).lastModified();
			PropKit.use(new File(configFilePath));
		} else {
			if (getReload()) {
				long nowLastModifyTime = new File(configFilePath)
						.lastModified();
				if (nowLastModifyTime != lastModifyTime) {
					logger.info("检测到配置文件有变动,重新加载配置文件");
					PropKit.clear();
					PropKit.use(new File(configFilePath));
					lastModifyTime = nowLastModifyTime;
				}
			}
		}
	}

	/**
	 * 获取是否动态家在配置文件
	 *
	 * @return
	 */
	private static Boolean getReload() {
		return PropKit.getBoolean("config.reload", true);
	}

	/**
	 * 获取数据库连接地址
	 *
	 * @return
	 */
	public static String getDbConnectionUrl() {
		getConfig();
		return PropKit.get("db.connection.url");
	}

	/**
	 * 获取数据库用户名
	 *
	 * @return
	 */
	public static String getDbConnectionUername() {
		getConfig();
		return PropKit.get("db.connection.username");
	}

	/**
	 * 获取数据库密码
	 *
	 * @return
	 */
	public static String getDbConnectionPassword() {
		getConfig();
		return PropKit.get("db.connection.password");
	}

	/**
	 * 系统是否为测试状态
	 *
	 * @return
	 */
	public static Boolean getJfinalDebug() {
		getConfig();
		return PropKit.getBoolean("jfinal.debug", true);
	}

	public static Integer getThreadCount(){
		getConfig();
		return PropKit.getInt("thread.count", 500);
	}

}