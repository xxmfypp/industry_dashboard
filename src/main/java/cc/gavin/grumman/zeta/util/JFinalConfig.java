package cc.gavin.grumman.zeta.util;

import cc.gavin.grumman.zeta.controller.IndexController;
import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.render.ViewType;
import org.apache.log4j.Logger;

public class JFinalConfig extends com.jfinal.config.JFinalConfig {
	
	protected final static Logger logger = Logger.getLogger(JFinalConfig.class);
	
	@Override
	public void afterJFinalStart() {
		// TODO Auto-generated method stub
		super.afterJFinalStart();

	}

	@Override
	public void configConstant(Constants arg0) {
		// TODO Auto-generated method stub
		arg0.setDevMode(ConfigFileUtil.getJfinalDebug());
		arg0.setEncoding("UTF-8");
		arg0.setViewType(ViewType.JSP);
		arg0.setMaxPostSize(arg0.getMaxPostSize()*20);
	}

	@Override
	public void configHandler(Handlers arg0) {
		// TODO Auto-generated method stub
		arg0.add(new DruidStatViewHandler("/druid"));
	}

	@Override
	public void configInterceptor(Interceptors arg0) {
		arg0.add(new TxByRegex(".*delete.*"));
		arg0.add(new TxByRegex(".*add.*"));
		arg0.add(new TxByRegex(".*update.*"));
	}

	@Override
	public void configPlugin(Plugins arg0) {
		DruidPlugin druidPlugin = new DruidPlugin(
				ConfigFileUtil.getDbConnectionUrl(),
				ConfigFileUtil.getDbConnectionUername(),
				ConfigFileUtil.getDbConnectionPassword());
		druidPlugin.setFilters("wall,stat,mergeStat,config");
		arg0.add(druidPlugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);

		arg0.add(arp);

		arp.setDialect(new MysqlDialect());
		arp.setShowSql(ConfigFileUtil.getJfinalDebug());

	}

	@Override
	public void configRoute(Routes arg0) {
		// TODO Auto-generated method stub
		arg0.add("/",IndexController.class);
	}


}
