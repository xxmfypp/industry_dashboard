package cc.gavin.grumman.zeta.util;

import cc.gavin.grumman.zeta.controller.IndexController;
import cc.gavin.grumman.zeta.interceptor.SessionInterceptor;
import cc.gavin.grumman.zeta.service.CollectionConstatService;
import com.jfinal.config.*;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.ext.plugin.quartz.QuartzPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.tx.TxByRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.render.ViewType;
import org.apache.log4j.Logger;

import java.util.concurrent.ForkJoinPool;

public class JFinalConfig extends com.jfinal.config.JFinalConfig {
	
	protected final static Logger logger = Logger.getLogger(JFinalConfig.class);

	public static ForkJoinPool fjp = new ForkJoinPool(50);
	
	@Override
	public void afterJFinalStart() {
		// TODO Auto-generated method stub
		super.afterJFinalStart();

		fjp = new ForkJoinPool(ConfigFileUtil.getThreadCount());

		CollectionConstatService.collection();

	}

	@Override
	public void configConstant(com.jfinal.config.Constants arg0) {
		// TODO Auto-generated method stub
		arg0.setDevMode(ConfigFileUtil.getJfinalDebug());
		arg0.setEncoding("UTF-8");
		arg0.setViewType(ViewType.JSP);
	}

	@Override
	public void configHandler(Handlers arg0) {
		// TODO Auto-generated method stub
		arg0.add(new DruidStatViewHandler("/druid"));
		arg0.add(new ContextPathHandler("contextPath"));
	}

	@Override
	public void configInterceptor(Interceptors arg0) {
		arg0.add(new TxByRegex(".*delete.*"));
		arg0.add(new TxByRegex(".*add.*"));
		arg0.add(new TxByRegex(".*update.*"));
		arg0.add(new SessionInterceptor());

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
		arp.setShowSql(true);

		// 配置quartz
		QuartzPlugin quartzPlugin = new QuartzPlugin();
		arg0.add(quartzPlugin);


	}

	@Override
	public void configRoute(Routes arg0) {
		// TODO Auto-generated method stub
		arg0.add("/",IndexController.class,"/WEB-INF/view/");
	}


}
