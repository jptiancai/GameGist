
- `��������`:spring�������������һ������,��������ڼ�����ʲô����,���������.
- `���÷�Χ`:spring������ʼ�����м��ض�̬����Դ
- `ʵ������`:�ο�[spring�������������һ�����飨����ContextRefreshedEvent�¼���](http://zhaoshijie.iteye.com/blog/1974682)
 - ע���spring mvc��Ŀ���������ֿ�
 - 
- `�������`:
 - [spring Standard and Custom Events](http://stackoverflow.com/questions/5728376/spring-applicationlistener-is-not-receiving-events)
- `Ӧ��ʵ��`:
 - [addressbook-sample-mongodb](https://github.com/yholkamp/addressbook-sample-mongodb/blob/5dc3668aed5512d56b2c6fd1e2bd53dc92192d81/web-ui/src/main/java/nl/enovation/addressbook/cqrs/webui/init/RunDBInitializerWhenNeeded.java)
 - [Axon-trader](https://github.com/AxonFramework/Axon-trader/blob/bf21804968d90008b02f5503a10869c49a180898/web-ui/src/main/java/org/axonframework/samples/trader/webui/init/RunDBInitializerWhenNeeded.java)
> ͨ����[java-api-examples](http://www.programcreek.com/java-api-examples/)��վ����`ContextRefreshedEvent`�ؼ���,



```
import java.io.File;

import org.apache.commons.logging.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import cons.GameData;
import service.server.LoadConfig;
//��������:����mysql����Դ,����mongodb����Դ,���س�ʼ������.
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
				//�����ڴ�����
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