package event;

public class XiaoWang implements Observer{

	@Override
	public void update(Subject subject) {
		  XiaoMing xm = (XiaoMing) subject;  
	      System.out.println("小王得到通知：" + xm.getState() + ";小王说:活该！");  
	}

}
