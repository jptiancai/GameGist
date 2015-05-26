### IBM

- [全面分析 Spring 的编程式事务管理及声明式事务管理](https://www.ibm.com/developerworks/cn/education/opensource/os-cn-spring-trans/)

### 实战:包包淘电商网站

- [Spring 3.x 企业应用开发实战 ](http://book.51cto.com/art/201203/320993.htm)
 - [17.6.3 服务类Bean的装配](http://book.51cto.com/art/201203/321313.htm)

- [ liuchenjunnan / study-spring](http://code.taobao.org/p/study-spring/src/trunk/chapter17/src/main/resources/baobaotao-service.xml):淘宝Code上面有人把该书的所有章节都共享了出来,还不错.
    - [study-spring/ trunk / chapter17 / src / test / java / com / baobaotao /](http://code.taobao.org/p/study-spring/src/trunk/chapter17/src/test/java/com/baobaotao/):针对淘包包的各种单元测试,强烈推荐!!!!!
	
	
```


import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;

import utils.LogFactory;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import cons.AttributeConstants;
import cons.ErrorConstants;
import cons.GameData;
import cons.SystemConstants;
import cons.WebConstants;
import exception.ErrorException;
import mongodb.domain.UserData;
import output.BaseJson;
import output.ResultToSet;
import service.GuiderService;
import service.UserDataService;
import utils.PlayLogUtil;
import utils.StringUtil;
import utils.WebUtil;

@Aspect
@Component
public class UpdateVersionAspectj {

	private static final Log logger = LogFactory
			.getLog(UpdateVersionAspectj.class);

	private static final long LOG_HANDLE_TIME = 2500;

	@Autowired
	private GuiderService guiderService;

	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private String dragonDebug;

	private Gson gson = new Gson();
	
	ThreadLocal<Integer> layerNum = new ThreadLocal<Integer>();

	@Pointcut("execution(public void service.*.*"
			+ "(output.BaseJson,java.util.Map<String, String>,..))")
	protected void UpdateVersionRetry() {
	}
	
	/**
	 * 性能记录
	 * @param pjp
	 * @return
	 * @throws Throwable
	 */
	@Around("execution(public * service.*.*(..))")
	public Object showHandlingTime(ProceedingJoinPoint pjp) throws Throwable{
		long timeBegin = System.currentTimeMillis();		
		Integer layerNum = this.layerNum.get();
		if(null == layerNum || layerNum < 0){
			layerNum = 0;
		}
		layerNum ++;
		this.layerNum.set(layerNum);
		Object result = null;		
		try {
			result = pjp.proceed();
		} catch (Throwable e) {
			throw e;
		}finally{
			long handleTime =  (System.currentTimeMillis() - timeBegin) ;
			if( dragonDebug.equals("on") || handleTime > LOG_HANDLE_TIME){
				logger.info(StringUtils.repeat("　　　　", layerNum) + "HandlingTime:" + handleTime + " \t handle:" + pjp.toString());				
			}
			this.layerNum.set(layerNum - 1);
		}
		return result;
	}

	@Around("UpdateVersionRetry() && args(json,paramMap,..)")
	public void doRetryBaseJson(ProceedingJoinPoint pjp, BaseJson json,
			Map<String, String> paramMap) throws Throwable {
		if (!GameData.CONFIG_READY)
			throw new ErrorException(
					ErrorConstants.SERVER_CONFIG_DATA_NOT_READY);
		int tryTimes = 0;
		while (tryTimes < SystemConstants.UPDATE_VERSION_MAX) {
			try {
				int guiderStoryId = WebUtil.getIntValueFromRequestMap(paramMap,
						WebConstants.PARAM_GUIDER_STORY_ID, -1);
				String guiderState = paramMap
						.get(WebConstants.PARAM_GUIDER_STORY_STATE);
				if (guiderStoryId >= 0) {
					if (guiderState == null)
						throw new ErrorException(ErrorConstants.PARAMETERERROR,
								WebConstants.PARAM_GUIDER_STORY_STATE);
					if (guiderService.guiderProcess(guiderStoryId, guiderState,
							json, paramMap) == false)
						pjp.proceed();
				} else
					pjp.proceed();
				// 同步消息
				try {
					int userId = WebUtil.getUserIdFromMap(paramMap);
					if (userId > 0) {
						String[] nameArray = { AttributeConstants.player,
								AttributeConstants.updateTime };
						UserData userData = userDataService
								.getUserDataInNameList(userId,
										Arrays.asList(nameArray));
						userDataService.flushUserInfo(userData);
						json.getSetList()
								.add(new ResultToSet("player", userData
										.getPlayer()));
					}
				} catch (Exception e) {
				}
			} catch (ErrorException e) {
				if (e.getErrorCode() == ErrorConstants.DB_DATA_VERSION_NOT_MATCH) {
					tryTimes++;
					if (tryTimes < SystemConstants.UPDATE_VERSION_MAX) {
						BaseJson jsonAgain = new BaseJson(json.getMessageCode());
						json = new BaseJson(jsonAgain.getMessageCode());
						pjp.getArgs()[0] = jsonAgain;
						logger.debug("Retry for version times:" + tryTimes);
						continue;
					}
				}
				throw e;
			}
			break;
		}
	}

	@Around("execution(public * web.*.*(..))"
			+ " and args (request)")
	public Object logPrint(ProceedingJoinPoint pjp, HttpServletRequest request)
			throws Throwable {
		long timeBegin = System.currentTimeMillis();
		PlayLogUtil.clientIpTl.set(WebUtil.getIpAddr(request));
		String serverId = request.getParameter("serverId");
		if (serverId != null && !serverId.equals("0")) {
			UserDataService.serverIdLocal.set(serverId);
		} else {
			UserDataService.serverIdLocal.set(null);
		}
		Object response = null;
		Exception exception = null;
		try {
			response = pjp.proceed();
		} catch (Exception e) {
			exception = e;
		}
		StringBuffer logBuffer = new StringBuffer();
		logBuffer.append("(");
		logBuffer.append(System.currentTimeMillis() - timeBegin);
		logBuffer.append("ms)\t");
		logBuffer.append(WebConstants.WEB_LOG_REQUEST);
		logBuffer.append("=");
		logBuffer.append(WebUtil.getRequestStr(request));
		logBuffer.append("\t");
		logBuffer.append(WebConstants.WEB_LOG_RESPONSE);
		logBuffer.append("=");
		boolean isSuccess = true;
		if (response != null) {
			logBuffer.append(gson.toJson(response));
			if(response instanceof BaseJson){
				if(((BaseJson) response).getErrorCode() == ErrorConstants.SERVER_ERROR){
					isSuccess = false;
				}
			}
		} else {
			isSuccess = false;
			logger.error("Uncatched Request Error:",exception);
			logBuffer.append("Response error!");
		}
		if(isSuccess){
			logger.info(logBuffer);
		}else{
			logger.error(logBuffer);
		}

		return response;
	}
}


```