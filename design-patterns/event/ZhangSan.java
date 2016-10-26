package event;

public class ZhangSan implements Observer{

	@Override
	public void update(Subject subject) {
		  XiaoMing xm = (XiaoMing) subject;  
		  System.out.println("张三得到通知：" + xm.getState() + ";张三说：快吃药吧！");  
	}

}
