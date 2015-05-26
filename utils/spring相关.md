
- `功能描述`:spring容器加载完毕做一件事情,如果加载期间遇到什么问题,会输出错误.
- `适用范围`:spring容器初始化后中加载动态数据源
- `实际运行`:参考[spring容器加载完毕做一件事情（利用ContextRefreshedEvent事件）](http://zhaoshijie.iteye.com/blog/1974682)
 - 注意和spring mvc项目的容器区分开
 - 
- `概念解释`:
 - [spring Standard and Custom Events](http://stackoverflow.com/questions/5728376/spring-applicationlistener-is-not-receiving-events)
- `应用实例`:
 - [addressbook-sample-mongodb](https://github.com/yholkamp/addressbook-sample-mongodb/blob/5dc3668aed5512d56b2c6fd1e2bd53dc92192d81/web-ui/src/main/java/nl/enovation/addressbook/cqrs/webui/init/RunDBInitializerWhenNeeded.java)
 - [Axon-trader](https://github.com/AxonFramework/Axon-trader/blob/bf21804968d90008b02f5503a10869c49a180898/web-ui/src/main/java/org/axonframework/samples/trader/webui/init/RunDBInitializerWhenNeeded.java)
> 通过在[java-api-examples](http://www.programcreek.com/java-api-examples/)网站搜索`ContextRefreshedEvent`关键字,



```
import java.io.File;

import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import cons.GameData;
import service.server.LoadConfig;
//干三件事:加载mysql数据源,加载mongodb数据源,加载初始化数据.
public class RunDBInitializerWhenNeeded  implements ApplicationListener<ContextRefreshedEvent>{
	private static final Log logger = LogFactory.getLog(RunDBInitializerWhenNeeded .class);
	
	private boolean isDynamicDataSource = true;
	
	private boolean isDynamicMongoSource = true;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("onApplicationEvent:" + event);
		try {
			ApplicationContext applicationContext = event.getApplicationContext();
			if(!"Root WebApplicationContext".equals(applicationContext.getDisplayName())
					&& !GameData.CONFIG_READY){
				if(isDynamicDataSource){
					LoadConfig.initDataSource(applicationContext);
				}
				if (isDynamicMongoSource) {
					LoadConfig.initMongoSources(applicationContext);
				}
				//加载内存数据
				applicationContext.getBean(LoadConfig.class).loadDicData();
				GameData.CONFIG_READY = true;
				File file = new File("loadConfigFinishMarkFile");
				file.createNewFile();
				logger.info("load config finish!");
			}
		} catch (Exception e) {
			GameData.CONFIG_READY = false;
			logger.error(e.getMessage(), e);
		}
	}

	public boolean getIsDynamicDataSource() {
		return isDynamicDataSource;
	}

	public void setIsDynamicDataSource(boolean isDynamicDataSource) {
		this.isDynamicDataSource = isDynamicDataSource;
	}
}
```