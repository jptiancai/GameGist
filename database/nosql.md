

### mongoDB

- [官方文档5.3.5. Registering a MongoDbFactory instance using XML based metadata](http://docs.spring.io/spring-data/data-document/docs/current/reference/html/#d0e1210)

```
<mongo:db-factory id="mongoDbFactory" host="${mongo.host}"
		port="${mongo.port}" dbname="${mongo.database}" username="${mongo.username}"
		password="${mongo.password}" />

	<bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
		<constructor-arg name="mongoDbFactory" ref="mongoDbFactory" />
	</bean>
	
	<!-- Document mapping -->
	<mongo:mapping-converter base-package="mongodb.domain" />
	<mongo:repositories base-package="mongodb.dao" />
	
	<!-- Dynamic Mongo Template -->
	<bean id="dynamicMongoTemplate" class="mongodb.DynamicMongoTemplate">
		<constructor-arg name="defaultTemplate" ref="mongoTemplate" />
	</bean>

####for mongodb cliet####

##Need to Ckeck
mongo.replicaSet=192.168.1.50:30000
mongo.host=192.168.1.50
mongo.port=30000
mongo.database=dragon2
mongo.username=root
mongo.password=
##End Check
##mongo options
mongo.autoConnectRetry=true
mongo.connectionsPerHost=200
mongo.threadsAllowedToBlockForConnectionMultiplier=5
mongo.connectTimeout=0
mongo.maxWaitTime=2000
mongo.socketKeepAlive=true
mongo.socketTimeout=0
##Need to Check - !!!Must be false
mongo.slaveOk=false
##End Check
mongo.writeNumber=1
mongo.writeTimeout=0
mongo.fsync=false
```




- `功能描述`:
- `适用范围`:
- `实际运行`:
- `概念解释`:
- `应用实例`:





```

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import service.UserDataService;
import com.mongodb.WriteResult;

public class DynamicMongoTemplate {
	
	private static final Log logger = LogFactory.getLog(DynamicMongoTemplate.class);
	
	protected Map<Object, Object> targetTemplates = new HashMap<Object, Object>();
	
	@Autowired
	protected MongoTemplate defaultTemplate;

	public DynamicMongoTemplate() {
	}
	
	public DynamicMongoTemplate(MongoTemplate defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}
	
	protected MongoTemplate getTargetTemplate() {
		String serverId = UserDataService.serverIdLocal.get();
		
		return this.getTargetTemplate(serverId);
	}
	
	protected MongoTemplate getTargetTemplate(String serverId) {
		String templateName = getMongoTemplateName(serverId);
		MongoTemplate mongoTemplate = (MongoTemplate) this.targetTemplates.get(templateName);
		
		if (mongoTemplate == null) {
			logger.error("[MongoTemplate Error] ServerId = " + serverId + ", MongoTemplate can not be null!");
			mongoTemplate = this.defaultTemplate;
		}
		
		return mongoTemplate;
	}
	
	public static String getMongoSourceName(String serverId) {
		return "mongo" + serverId;
	}
	
	public static String getTemplateName(String beanName) {
		return beanName + "Template";
	}
	
	public static String getMongoTemplateName(String serverId) {
		String sourceName = getMongoSourceName(serverId);
		return getTemplateName(sourceName);
	}
	
	public void setTargetTemplates(Map<Object, Object> mongoTemplates) {
		this.targetTemplates = mongoTemplates;
	}
	
	public void afterPropertiesSet() {
		if (targetTemplates == null || targetTemplates.isEmpty()) {
			throw new IllegalArgumentException("Property 'targetTemplates' is required!");
		}
	}
	
	public <T> T findOne(Query query, Class<T> entityClass) {
		return this.getTargetTemplate().findOne(query, entityClass);
	}
	
	public <T> T findOne(String serverId, Query query, Class<T> entityClass) {
		return this.getTargetTemplate(serverId).findOne(query, entityClass);
	}
	
	public <T> List<T> find(Query query, Class<T> entityClass) {
		return this.getTargetTemplate().find(query, entityClass);
	}
	
	public <T> List<T> find(String serverId, Query query, Class<T> entityClass) {
		return this.getTargetTemplate(serverId).find(query, entityClass);
	}
	
	public <T> T findById(Object id, Class<T> entityClass) {
		return this.getTargetTemplate().findById(id, entityClass);
	}

	public <T> T findById(String serverId, Object id, Class<T> entityClass) {
		return this.getTargetTemplate(serverId).findById(id, entityClass);
	}
	
	public <T> T findById(Object id, Class<T> entityClass, String collectionName) {
		return this.getTargetTemplate().findById(id, entityClass, collectionName);
	}
	
	public <T> T findById(String serverId, Object id, Class<T> entityClass, String collectionName) {
		return this.getTargetTemplate(serverId).findById(id, entityClass, collectionName);
	}
	
	public <T> T findAndModify(Query query, Update update, Class<T> entityClass) {
		return this.getTargetTemplate().findAndModify(query, update, entityClass);
	}

	public <T> T findAndModify(String serverId, Query query, Update update, Class<T> entityClass) {
		return this.getTargetTemplate(serverId).findAndModify(query, update, entityClass);
	}
	
	public long count(Query query, Class<?> entityClass) {
		return this.getTargetTemplate().count(query, entityClass);
	}

	public long count(String serverId, Query query, Class<?> entityClass) {
		return this.getTargetTemplate(serverId).count(query, entityClass);
	}
	
	public void insert(Object objectToSave) {
		this.getTargetTemplate().insert(objectToSave);
	}
	
	public void insert(String serverId, Object objectToSave) {
		this.getTargetTemplate(serverId).insert(objectToSave);
	}
	
	public void save(Object objectToSave) {
		this.getTargetTemplate().save(objectToSave);
	}
	
	public void save(String serverId, Object objectToSave) {
		this.getTargetTemplate(serverId).save(objectToSave);
	}
	
	public WriteResult updateFirst(Query query, Update update,
			Class<?> entityClass) {
		return this.getTargetTemplate().updateFirst(query, update, entityClass);
	}

	public WriteResult updateFirst(String serverId, Query query, Update update,
			Class<?> entityClass) {
		return this.getTargetTemplate(serverId).updateFirst(query, update, entityClass);
	}
	
	public WriteResult updateMulti(Query query, Update update,
			Class<?> entityClass) {
		return this.getTargetTemplate().updateMulti(query, update, entityClass);
	}
	
	public WriteResult updateMulti(String serverId, Query query, Update update,
			Class<?> entityClass) {
		return this.getTargetTemplate(serverId).updateMulti(query, update, entityClass);
	}
	
	public void remove(Object object) {
		this.getTargetTemplate().remove(object);
	}
	
	public void remove(String serverId, Object object) {
		this.getTargetTemplate(serverId).remove(object);
	}
	
	public <T> void remove(Query query, Class<T> entityClass) {
		this.getTargetTemplate().remove(query, entityClass);
	}
	
	public <T> void remove(String serverId, Query query, Class<T> entityClass) {
		this.getTargetTemplate(serverId).remove(query, entityClass);
	}
	
}

```

### mongoDB动态Bean

```

/**
 * 动态bean描述
 * 如果想让某些bean可以动态加载，定义动态加载对象并继承此抽象类，
 * 之后调用getXml()接口既可返回spring配置字符串
 */
public abstract class MongoBean {
	
	protected String beanName;
	
	protected String templateName;
	
	public MongoBean(String beanName, String templateName) {
		this.beanName = beanName;
		this.templateName = templateName;
	}
	
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	/**
	 * 获取bean 的xml描述
	 * @return
	 */
	public abstract String getBeanXml();
	
	/**
	 * 生成完整的xml字符串
	 * @return
	 */
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
			.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"")
			.append("		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tx=\"http://www.springframework.org/schema/tx\"")
			.append("		xmlns:context=\"http://www.springframework.org/schema/context\" xmlns:p=\"http://www.springframework.org/schema/p\"")
			.append("		xmlns:mongo=\"http://www.springframework.org/schema/data/mongo\"")
			.append("		xsi:schemaLocation=\"")
			.append("	    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd")
			.append("	    http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-3.0.xsd")
			.append("	    http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd")
			.append("	    http://www.springframework.org/schema/data/mongo")
			.append("	    http://www.springframework.org/schema/data/mongo/spring-mongo-1.0.xsd\" >\n")
			.append(getBeanXml())
//			.append("<mongo:mapping-converter base-package=\"mongodb.domain\" />")
//			.append("<mongo:repositories base-package=\"mongodb.dao\" />")
			.append("</beans>");
		return buf.toString();
	}
}
```


```


import mysql.domain.ServerZoneInfo;

public class MongoDynamicBean extends MongoBean {
	
	//mongo数据源
	private String mongoReplicaSet;
	private String mongoHost;
	private int mongoPort;
	private String mongoDatabase;
	private String mongoUsername;
	private String mongoPassword;
	
	//mongo属性
//	private static int mongoConnectionsPerHost = 200;
//	private static int mongoThreadsAllowedToBlockForConnectionMultiplier = 5;
//	private static int mongoConnectTimeout = 0;
//	private static int mongoMaxWaitTime = 2000;
//	private static boolean mongoAutoConnectRetry = true;
//	private static boolean mongoSocketKeepAlive = true;
//	private static int mongoSocketTimeout = 0;
//	private static boolean mongoSlaveOk = false;
//	private static int mongoWriteNumber = 1;
//	private static int mongoWriteTimeOut = 0;
//	private static boolean mongoFsync = false;
	
	public MongoDynamicBean(String beanName, String mongoTemplateName) {
		super(beanName, mongoTemplateName);
	}

	@Override
	public String getBeanXml() {
		StringBuffer xmlBuf = new StringBuffer();
//		xmlBuf.append("<mongo:mongo>");
//		xmlBuf.append("<mongo:options connections-per-host=\"" + mongoConnectionsPerHost + "\"");
//		xmlBuf.append("		threads-allowed-to-block-for-connection-multiplier=\"" + mongoThreadsAllowedToBlockForConnectionMultiplier + "\"");
//		xmlBuf.append("		connect-timeout=\"" + mongoConnectTimeout + "\" max-wait-time=\"" + mongoMaxWaitTime + "\"");
//		xmlBuf.append("		auto-connect-retry=\"" + mongoAutoConnectRetry + "\" socket-keep-alive=\"" + mongoSocketKeepAlive + "\"");
//		xmlBuf.append("		socket-timeout=\"" + mongoSocketTimeout + "\" slave-ok=\"" + mongoSlaveOk + "\"");
//		xmlBuf.append("		write-number=\"" + mongoWriteNumber + "\" write-timeout=\"" + mongoWriteTimeOut + "\"");
//		xmlBuf.append("		write-fsync=\"" + mongoFsync + "\" />");
//		xmlBuf.append("</mongo:mongo>");
		xmlBuf.append("<mongo:db-factory id=\"" + super.beanName + "\" host=\"" + mongoHost + "\"");
		xmlBuf.append("		port=\"" + mongoPort + "\" dbname=\"" + mongoDatabase + "\" username=\"" + mongoUsername + "\"");
		xmlBuf.append("		password=\"" + mongoPassword + "\" />");
		xmlBuf.append("<bean id=\"" + super.templateName + "\" class=\"org.springframework.data.mongodb.core.MongoTemplate\">");
		xmlBuf.append("		<constructor-arg name=\"mongoDbFactory\" ref=\"" + super.beanName + "\" />");
		xmlBuf.append("</bean>");

		return xmlBuf.toString();
	}
	
	public void setWithServerZoneInfo(ServerZoneInfo serverZone) {
		this.mongoReplicaSet = serverZone.getMongoReplicaSet();
		this.mongoHost = serverZone.getMongoHost();
		this.mongoPort = serverZone.getMongoPort();
		this.mongoDatabase = serverZone.getMongoDatabase();
		this.mongoUsername = serverZone.getMongoUsername();
		this.mongoPassword = serverZone.getMongoPassword();
	}
	
	public void setMongoDynamicBean(String mongoReplicaSet, String mongoHost, int mongoPort, String mongoDatabase, String mongoUsername, String mongoPassword) {
		this.mongoReplicaSet = mongoReplicaSet;
		this.mongoHost = mongoHost;
		this.mongoPort = mongoPort;
		this.mongoDatabase = mongoDatabase;
		this.mongoUsername = mongoUsername;
		this.mongoPassword = mongoPassword;
	}

	public String getMongoReplicaSet() {
		return mongoReplicaSet;
	}

	public void setMongoReplicaSet(String mongoReplicaSet) {
		this.mongoReplicaSet = mongoReplicaSet;
	}

	public String getMongoHost() {
		return mongoHost;
	}

	public void setMongoHost(String mongoHost) {
		this.mongoHost = mongoHost;
	}

	public int getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
	}

	public String getMongoDatabase() {
		return mongoDatabase;
	}

	public void setMongoDatabase(String mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}

	public String getMongoUsername() {
		return mongoUsername;
	}

	public void setMongoUsername(String mongoUsername) {
		this.mongoUsername = mongoUsername;
	}

	public String getMongoPassword() {
		return mongoPassword;
	}

	public void setMongoPassword(String mongoPassword) {
		this.mongoPassword = mongoPassword;
	}
	
}

```

```


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;

public class MongoResource implements Resource {
	private MongoBean mongoBean;
	
	public MongoResource(MongoBean dynamicBean){
		this.mongoBean = dynamicBean;
	}
	/* (non-Javadoc)
	 * @see org.springframework.core.io.InputStreamSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(mongoBean.getXml().getBytes("UTF-8"));
	}
	//其他实现方法省略
	public long contentLength() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public Resource createRelative(String arg0) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public File getFile() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getFilename() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public URI getURI() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public URL getURL() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isReadable() {
		// TODO Auto-generated method stub
		return false;
	}
	public long lastModified() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}
}
```


### mongoDB的Dao基类

```


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import cons.AttributeConstants;
import mongodb.DynamicMongoTemplate;

public abstract class BaseDao<T> {
	private Class<T> entityClass;

	@Autowired
	protected DynamicMongoTemplate dynamicMongoTemplate;

	/**
	 * 通过反射获取子类确定的泛型类
	 */
	@SuppressWarnings("unchecked")
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class<T>) params[0];
	}

	public void setMongoTemplate(DynamicMongoTemplate dynamicTemplate) {
		this.dynamicMongoTemplate = dynamicTemplate;
	}
	
	public DynamicMongoTemplate getMongoTemplate() {
		return this.dynamicMongoTemplate;
	}

	public long count(Query query) {
		return dynamicMongoTemplate.count(query, entityClass);
	}
	
	public long count(String serverId, Query query) {
		return dynamicMongoTemplate.count(serverId, query, entityClass);
	}

	public List<T> find(Query query) {
		return dynamicMongoTemplate.find(query, entityClass);
	}
	
	public List<T> find(String serverId, Query query) {
		return dynamicMongoTemplate.find(serverId, query, entityClass);
	}

	public T findOne(Query query) {
		return dynamicMongoTemplate.findOne(query, entityClass);
	}
	
	public T findOne(String serverId, Query query) {
		return dynamicMongoTemplate.findOne(serverId, query, entityClass);
	}

	public T findByID(Object id) {
		return dynamicMongoTemplate.findById(id, entityClass);
	}
	
	public T findByID(String serverId, Object id) {
		return dynamicMongoTemplate.findById(serverId, id, entityClass);
	}

	public T findByID(int id, String dbname) {
		return dynamicMongoTemplate.findById(id, entityClass, dbname);
	}
	
	public T findByID(String serverId, int id, String dbname) {
		return dynamicMongoTemplate.findById(serverId, id, entityClass, dbname);
	}

	public void insert(T obj) {
		dynamicMongoTemplate.insert(obj);
	}
	
	public void insert(String serverId, T obj) {
		dynamicMongoTemplate.insert(serverId, obj);
	}

	public void delete(T obj) {
		dynamicMongoTemplate.remove(obj);
	}
	
	public void delete(String serverId, T obj) {
		dynamicMongoTemplate.remove(serverId, obj);
	}

	public void deleteById(String id) {
		Query query = new Query(Criteria.where(AttributeConstants._id).is(id));
		dynamicMongoTemplate.remove(query, entityClass);
	}
	
	public void deleteById(String serverId, String id) {
		Query query = new Query(Criteria.where(AttributeConstants._id).is(id));
		dynamicMongoTemplate.remove(serverId, query, entityClass);
	}
	
	public void deleteByQuery(Query query) {
		dynamicMongoTemplate.remove(query, entityClass);
	}
	
	public void deleteByQuery(String serverId, Query query) {
		dynamicMongoTemplate.remove(serverId, query, entityClass);
	}

	/**
	 * 
	 * @param query
	 * @param update
	 * @return the old object
	 */
	public T findAndModify(Query query, Update update) {
		return dynamicMongoTemplate.findAndModify(query, update, entityClass);
	}
	
	public T findAndModify(String serverId, Query query, Update update) {
		return dynamicMongoTemplate.findAndModify(serverId, query, update, entityClass);
	}

	public void updateFirst(Query query, Update update) {
		dynamicMongoTemplate.updateFirst(query, update, entityClass);
	}
	
	public void updateFirst(String serverId, Query query, Update update) {
		dynamicMongoTemplate.updateFirst(serverId, query, update, entityClass);
	}

	public void updateMulti(Query query, Update update) {
		dynamicMongoTemplate.updateMulti(query, update, entityClass);
	}
	
	public void updateMulti(String serverId, Query query, Update update) {
		dynamicMongoTemplate.updateMulti(serverId, query, update, entityClass);
	}

	/**
	 * 存儲時不會判斷類型,請謹慎調用
	 * 
	 * @param obj
	 */
	public void save(T obj) {
		dynamicMongoTemplate.save(obj);
	}
	
	public void save(String serverId, T obj) {
		dynamicMongoTemplate.save(serverId, obj);
	}

}

```