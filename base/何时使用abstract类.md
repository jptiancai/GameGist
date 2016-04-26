- 适用场景:
 - 当发现同时要开发两个相同的类,但类中各个方法要求有各自的具体实现
 - 当要增加一个需求,和之前的类有很多的耦合,但是某个方法还是要具体实现


- 举例说明:

**抽象类**

```java
import net.sf.json.JSONObject;

public abstract class AbstractOnlineGiftManager<T> implements JsonPropDataHolder {
	public static final String CURR_RECEIVE_ID = "currReceiveId";
	public static final String START_TIME = "startTime";
	
	protected Human owner;
	protected int currReceiveId;
	protected long startTime;
	
	public AbstractOnlineGiftManager(Human human){
		owner = human;
	}
	
	public void load(){
		if(currReceiveId < 1){
			this.initGift();
		}
	}
	
	@Override
	public String toJsonProp() {
		JSONObject obj = new JSONObject();
		obj.put(CURR_RECEIVE_ID, this.currReceiveId);
		obj.put(START_TIME, this.startTime);
		return obj.toString();
	}
	
	public void initGift(){
		this.currReceiveId = 1;
		this.startTime = Globals.getTimeService().now();
		this.owner.setModified();
	}
	
	public abstract long getCd();
	
	public boolean isOpening(){
		return this.getCurrReceiveOnlineGiftTemplate() != null;
	}
	
	
	public abstract T getCurrReceiveOnlineGiftTemplate();
	
	@Override
	public void loadJsonProp(String value) {
		if(value == null || value.isEmpty()){
			this.initGift();
			return;
		}
		
		JSONObject obj = JSONObject.fromObject(value);
		if(obj == null || obj.isEmpty()){
			this.initGift();
			return;
		}
		
		currReceiveId = JsonUtils.getInt(obj, CURR_RECEIVE_ID);
		startTime = JsonUtils.getLong(obj, START_TIME);
		
		if(currReceiveId < 1){
			this.initGift();
		}
	}

	public void next(){
		this.currReceiveId ++;
		this.startTime = Globals.getTimeService().now();
		this.owner.setModified();
	}
	
	public int getCurrReceiveId() {
		return currReceiveId;
	}

	public void setCurrReceiveId(int currReceiveId) {
		this.currReceiveId = currReceiveId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public Human getOwner() {
		return owner;
	}

}

```

**抽象子类1**

```java
package com.imop.lj.gameserver.onlinegift;

public class OnlineGiftManager extends AbstractOnlineGiftManager<OnlineGiftTemplate> {
	public OnlineGiftManager(Human human) {
		super(human);
	}

	@Override
	public long getCd() {
		OnlineGiftTemplate tmpl = this.getCurrReceiveOnlineGiftTemplate();
		if (tmpl == null) {
			Loggers.humanLogger.error("OnlineGiftManager.getCd onlineGiftTemplateId = "	+ this.currReceiveId + " does not exist!!!");
			return Long.MAX_VALUE;
		}

		long cd = this.startTime + tmpl.getCd()	- Globals.getTimeService().now();
		return cd <= 0 ? 0 : cd;
	}

	@Override
	public OnlineGiftTemplate getCurrReceiveOnlineGiftTemplate() {
		return Globals.getTemplateCacheService().get(this.currReceiveId, OnlineGiftTemplate.class);
	}
}

```

**抽象子类2**

```java
package com.imop.lj.gameserver.onlinegift;

public class SpecOnlineGiftManager extends AbstractOnlineGiftManager<SpecOnlineGiftTemplate> {

	public SpecOnlineGiftManager(Human human) {
		super(human);
	}

	@Override
	public long getCd() {
		SpecOnlineGiftTemplate tmpl = this.getCurrReceiveOnlineGiftTemplate();
		if(tmpl == null){
			Loggers.humanLogger.error("SpecOnlineGiftManager.getCd onlineGiftTemplateId = " + this.currReceiveId + " does not exist!!!");
			return Long.MAX_VALUE;
		}
		
		long cd = this.startTime + tmpl.getCd() - Globals.getTimeService().now();
		return cd <= 0 ? 0 : cd;
	}

	@Override
	public SpecOnlineGiftTemplate getCurrReceiveOnlineGiftTemplate() {
		return Globals.getTemplateCacheService().get(this.currReceiveId, SpecOnlineGiftTemplate.class);
	}

}


```