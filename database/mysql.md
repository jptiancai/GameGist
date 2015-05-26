- `概念解释`:
 - [Configuring jdbc-pool for high-concurrency](http://www.tomcatexpert.com/blog/2010/04/01/configuring-jdbc-pool-high-concurrency)
 - [Tomcat JDBC Connection Pool configuration for production and development](http://www.codingpedia.org/ama/tomcat-jdbc-connection-pool-configuration-for-production-and-development/)
 - [Configuring the High Concurrency JDBC Connection Pool](http://static.springsource.com/projects/tc-server/6.0/admin/radmjdbc.html#radmjdbc__description)
 - [DBCP连接池原理分析](http://elf8848.iteye.com/blog/1931778)
 - [正确配置DBCP防止数据库重启引起的访问错误](http://www.netingcn.com/dbcp-config.html)



```

	<!-- for mysql and hibernate -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
		destroy-method="close" p:driverClassName="${ds.jdbc.driverClassName}"
		p:url="${ds.jdbc.url}" p:username="${ds.jdbc.username}" p:password="${ds.jdbc.password}"
		p:timeBetweenEvictionRunsMillis="${ds.jdbc.timeBetweenEvictionRunsMillis}"
		p:minEvictableIdleTimeMillis="${ds.jdbc.minEvictableIdleTimeMillis}" 
		p:initialSize="${ds.jdbc.initialSize}" p:maxActive="${ds.jdbc.maxActive}" />
				
	<bean id="dynamicDataSource" class="mysql.DynamicDataSource">
		<property name="targetDataSources">
			<map key-type="java.lang.String">
			</map>
		</property>
		<property name="defaultTargetDataSource" ref="dataSource"></property>
	</bean>

	<bean id="sessionFactory"
		class="org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean">
		<property name="dataSource" ref="dynamicDataSource" />
		<property name="packagesToScan">
			<list>
				<value>mysql.domain</value>
			</list>
		</property>
		<property name="hibernateProperties">
			<props>
				<prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
				<prop key="hibernate.show_sql">false</prop>
				<prop key="hibernate.cache.use_query_cache">false</prop>
				<prop key="hibernate.cache.use_second_level_cache">false</prop>
				<prop key="hibernate.autoReconnect">true</prop>
				<prop key="hibernate.connection.release_mode">after_statement</prop>
			</props>
		</property>
	</bean>
	<bean id="hibernateTemplate" class="org.springframework.orm.hibernate3.HibernateTemplate"
		p:sessionFactory-ref="sessionFactory" />

###Mysql - linkx
ds.jdbc.driverClassName=com.mysql.jdbc.Driver

##Need to Check
ds.jdbc.url=...
ds.jdbc.username=root
ds.jdbc.password=
##End Check
##Auguments
ds.jdbc.timeBetweenEvictionRunsMillis=3600000
ds.jdbc.minEvictableIdleTimeMillis=7200000
##Need to Check
ds.jdbc.initialSize=10
ds.jdbc.maxActive=15
##End Check
```


```
###MySQL Dynamic DataSource Config
mysqlConnectionPoolInitialSize=10
mysqlConnectionPoolMaxActive=30
```

### mysql动态Bean

```

import java.util.ArrayList;
import java.util.List;

/**
 * 动态bean描述
 * 如果想让某些bean可以动态加载，定义动态加载对象并继承此抽象类，
 * 之后调用getXml()接口既可返回spring配置字符串
 */
public abstract class DynamicBean {
	
	protected String beanName;
	protected List<String> dataSources;
	
	public DynamicBean(String beanName) {
		this.beanName = beanName;
	}
	
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public List<String> getDataSources() {
		if(dataSources == null) {
			dataSources = new ArrayList<String>();
		}
		return dataSources;
	}
	public void setDataSources(List<String> dataSources) {
		this.dataSources = dataSources;
	}
	public void addDataSource(String dataSourceBeanName) {
		this.getDataSources().add(dataSourceBeanName);
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
			.append("</beans>");
		return buf.toString();
	}
}
```


```

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DataSourceDynamicBean extends DynamicBean {
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
	private static int timeBetweenEvictionRunsMillis = 200000;
	private static int minEvictableIdleTimeMillis = 600000;
	private static int initialSize = 10;
	private static int maxActive = 15;
	
	@Autowired
	@Qualifier("mysqlConnectionPoolInitialSize")
	private static String mysqlConnectionPoolInitialSize;
	
	@Autowired
	@Qualifier("mysqlConnectionPoolMaxActive")
	private static String mysqlConnectionPoolMaxActive;
	
	public DataSourceDynamicBean(String beanName) {
		super(beanName);
		
		if (mysqlConnectionPoolInitialSize != null && Integer.parseInt(mysqlConnectionPoolInitialSize) > initialSize) {
			initialSize = Integer.parseInt(mysqlConnectionPoolInitialSize);
		}
		if (mysqlConnectionPoolMaxActive != null && Integer.parseInt(mysqlConnectionPoolMaxActive) > maxActive) {
			maxActive = Integer.parseInt(mysqlConnectionPoolMaxActive);
		}
	}

	@Override
	public String getBeanXml() {
		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<bean id=\""+super.beanName+"\" class=\"org.apache.commons.dbcp.BasicDataSource\"");
		xmlBuf.append("		destroy-method=\"close\" p:driverClassName=\""+this.driverClassName+"\"");
		xmlBuf.append("		p:url=\""+this.url+"\" p:username=\""+this.username+"\" p:password=\""+this.password+"\"");
		xmlBuf.append("		p:timeBetweenEvictionRunsMillis=\"" + timeBetweenEvictionRunsMillis + "\"");
		xmlBuf.append("		p:minEvictableIdleTimeMillis=\"" + minEvictableIdleTimeMillis + "\"");
		xmlBuf.append("		p:initialSize=\"" + initialSize + "\" p:maxActive=\"" + maxActive + "\" >");

		xmlBuf.append("<property name=\"validationQuery\" value=\"SELECT COUNT(*) FROM DUAL\" />");
		xmlBuf.append("<property name=\"testOnBorrow\" value=\"true\" />");
		xmlBuf.append("<property name=\"testOnReturn\" value=\"true\" />");
		xmlBuf.append("<property name=\"testWhileIdle\" value=\"true\" />");
		xmlBuf.append("</bean>");
		
		return xmlBuf.toString();
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

public class DynamicResource implements Resource {
	private DynamicBean dynamicBean;
	
	public DynamicResource(DynamicBean dynamicBean){
		this.dynamicBean = dynamicBean;
	}
	/* (non-Javadoc)
	 * @see org.springframework.core.io.InputStreamSource#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(dynamicBean.getXml().getBytes("UTF-8"));
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



### mysql的DAO基类

```
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.util.Assert;

/**
 * DAO基类，其它DAO可以直接继承这个DAO，不但可以复用共用的方法，还可以获得泛型的好处。
 */
public class BaseDao<T> {
	private Class<T> entityClass;

	@Autowired
	private HibernateTemplate hibernateTemplate;

	/**
	 * 通过反射获取子类确定的泛型类
	 */
	@SuppressWarnings("unchecked")
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class<T>) params[0];
	}

	/**
	 * 根据ID加载PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T load(Serializable id) {
		return (T) getHibernateTemplate().load(entityClass, id);
	}

	/**
	 * 根据ID获取PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T get(Serializable id) {
		return (T) getHibernateTemplate().get(entityClass, id);
	}

	/**
	 * 获取PO的所有对象
	 * 
	 * @return
	 */
	public List<T> loadAll() {
		return getHibernateTemplate().loadAll(entityClass);
	}

	/**
	 * 保存PO
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		getHibernateTemplate().save(entity);
	}

	/**
	 * 保存或更新PO
	 * 
	 * @param entity
	 */
	public void saveOrUpdate(T entity){
		getHibernateTemplate().saveOrUpdate(entity);
	}
	
	/**
	 * 保存PO
	 * 
	 * @param entity
	 */
	public void saveAll(List<T> entities) {
		getHibernateTemplate().saveOrUpdateAll(entities);
	}

	/**
	 * 删除PO
	 * 
	 * @param entity
	 */
	public void remove(T entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * 更改PO
	 * 
	 * @param entity
	 */
	public void update(T entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * 执行HQL查询
	 * 
	 * @param sql
	 * @return 查询结果
	 */
	public List<?> find(String hql) {
		return this.getHibernateTemplate().find(hql);
	}

	/**
	 * 执行带参的HQL查询
	 * 
	 * @param sql
	 * @param params
	 * @return 查询结果
	 */
	public List<?> find(String hql, Object... params) {
		return this.getHibernateTemplate().find(hql, params);
	}

	/**
	 * 对延迟加载的实体PO执行初始化
	 * 
	 * @param entity
	 */
	public void initialize(Object entity) {
		this.getHibernateTemplate().initialize(entity);
	}

	/**
	 * 分页查询函数，使用hql.
	 * 
	 * @param pageNo
	 *            页号,从1开始.
	 */
	public Page pagedQuery(String hql, int pageNo, int pageSize,
			Object... values) {
		Assert.hasText(hql);
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");
		// Count查询
		String countQueryString = " select count (*) "
				+ removeSelect(removeOrders(hql));
		List<?> countlist = getHibernateTemplate().find(countQueryString,
				values);
		long totalCount = (Long) countlist.get(0);

		if (totalCount < 1)
			return new Page();
		// 实际查询返回分页对象
		int startIndex = Page.getStartOfPage(pageNo, pageSize);

		Query query = createQuery(hql, values);
		List<?> list = query.setFirstResult(startIndex).setMaxResults(pageSize)
				.list();

		return new Page(startIndex, totalCount, pageSize, list);
	}

	/**
	 * 分页查询函数，使用hql.
	 * 
	 * @param pageNo
	 *            页号,从1开始.
	 */
	public Page pagedQueryNotNull(String hql, int pageNo, int pageSize,
			Object... values) {
		// Count查询
		String countQueryString = " select count (*) "
				+ removeSelect(removeOrders(hql));
		List<?> countlist = getHibernateTemplate().find(countQueryString,
				values);
		long totalCount = (Long) countlist.get(0);
		if (totalCount < 1)
			return new Page();
		if (pageNo < 1)
			pageNo = 1;
		if ((pageNo - 1) * pageSize > totalCount) {
			pageNo = (int) (totalCount / pageSize);
			if (totalCount % pageSize != 0)
				pageNo++;
		}

		// 实际查询返回分页对象
		int startIndex = Page.getStartOfPage(pageNo, pageSize);

		Query query = createQuery(hql, values);
		List<?> list = query.setFirstResult(startIndex).setMaxResults(pageSize)
				.list();

		return new Page(startIndex, totalCount, pageSize, list);
	}

	/**
	 * 创建Query对象.
	 * 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
	 * 留意可以连续设置,如下：
	 * 
	 * <pre>
	 * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
	 * </pre>
	 * 
	 * 调用方式如下：
	 * 
	 * <pre>
	 *        dao.createQuery(hql)
	 *        dao.createQuery(hql,arg0);
	 *        dao.createQuery(hql,arg0,arg1);
	 *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
	 * </pre>
	 * 
	 * @param values
	 *            可变参数.
	 */
	public Query createQuery(String hql, Object... values) {
		Assert.hasText(hql);
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	/**
	 * 创建sql query
	 * @param sql
	 * @param values
	 * @return
	 */
	public Query createSqlQuery(String sql, Object... values) {
		Assert.hasText(sql);
		Query query = getSession().createSQLQuery(sql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}

	/**
	 * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql
				+ " must has a keyword 'from'");
		return hql.substring(beginPos);
	}

	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 * 
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public Session getSession() {
		return SessionFactoryUtils.getSession(
				hibernateTemplate.getSessionFactory(), true);
	}

}
```