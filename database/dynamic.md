
- [Hierarchy For Package org.springframework.context](http://docs.spring.io/spring-framework/docs/2.5.x/api/org/springframework/context/package-tree.html)

- [Springʵ��Bean��̬ע��](http://blog.sina.com.cn/s/blog_74af50ec01018yy9.html)

```
ApplicationContext application = null;
		
		if (applicationContext == null) {
			application = ContextLoader.getCurrentWebApplicationContext();
		}
		else {
			application = applicationContext;
		}

				//���ö�̬����Դ,��mysql������
				MongoDynamicBean mongoDynamicBean = new MongoDynamicBean(mongoBeanName, mongoTemplateName);
				mongoDynamicBean.setWithServerZoneInfo(serverZone);
				
				//��spring����ת�ɶ�̬��������
				ConfigurableApplicationContext configApplicationContext = (ConfigurableApplicationContext) application;
				//��ȡIoc�����е�bean����ע���
				XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry)configApplicationContext.getBeanFactory());
				reader.setResourceLoader(configApplicationContext);
				reader.setEntityResolver(new ResourceEntityResolver(configApplicationContext));
				//���µ�bean���ص�spring ������
				reader.loadBeanDefinitions(new MongoResource(mongoDynamicBean));
				
				//db��Ӧ��Ϣע�������������
				Object mongoTemplate = configApplicationContext.containsBean(mongoTemplateName)?
						configApplicationContext.getBean(mongoTemplateName):null;

```
- [Spring(AbstractRoutingDataSource)ʵ�ֶ�̬����Դ�л�](http://linhongyu.blog.51cto.com/6373370/1615895)

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