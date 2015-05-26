
- [Hierarchy For Package org.springframework.context](http://docs.spring.io/spring-framework/docs/2.5.x/api/org/springframework/context/package-tree.html)

- [Spring实现Bean动态注册](http://blog.sina.com.cn/s/blog_74af50ec01018yy9.html)

```
ApplicationContext application = null;
		
		if (applicationContext == null) {
			application = ContextLoader.getCurrentWebApplicationContext();
		}
		else {
			application = applicationContext;
		}

				//设置动态数据源,和mysql的类似
				MongoDynamicBean mongoDynamicBean = new MongoDynamicBean(mongoBeanName, mongoTemplateName);
				mongoDynamicBean.setWithServerZoneInfo(serverZone);
				
				//将spring容器转成动态可配置型
				ConfigurableApplicationContext configApplicationContext = (ConfigurableApplicationContext) application;
				//获取Ioc容器中的bean定义注册表
				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)configApplicationContext.getBeanFactory());
				reader.setResourceLoader(configApplicationContext);
				reader.setEntityResolver(new ResourceEntityResolver(configApplicationContext));
				//将新的bean加载到spring 容器中
				reader.loadBeanDefinitions(new MongoResource(mongoDynamicBean));
				
				//db相应信息注册进此容器管理
				Object mongoTemplate = configApplicationContext.containsBean(mongoTemplateName)?
						configApplicationContext.getBean(mongoTemplateName):null;

```
- [Spring(AbstractRoutingDataSource)实现动态数据源切换](http://linhongyu.blog.51cto.com/6373370/1615895)

```

import java.util.Map;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import service.UserDataService;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	public static String getDataSourceKeyByServerId(String serverId){
		if (null != serverId) {
			return "dataSource" + serverId;
		}
		return "dataSource";
	}
	
	@Override
	protected Object determineCurrentLookupKey() {
		String serverId = UserDataService.serverIdLocal.get();
		return getDataSourceKeyByServerId(serverId);
	}
	
	public boolean hasDataSourceByServerId(String serverId){
		String bakServerId = UserDataService.serverIdLocal.get();
		UserDataService.serverIdLocal.set(serverId);
		boolean hasDataSource = true;
		try{
			this.determineTargetDataSource();
		}catch(IllegalStateException e){
			hasDataSource = false;
		}finally{
			UserDataService.serverIdLocal.set(bakServerId);
		}
		return hasDataSource;
	}
	
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
	}
}

```