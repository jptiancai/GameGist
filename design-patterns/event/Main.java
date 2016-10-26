package event;

public class Main {

	public static void main(String[] args) {
		
		
		//被监听者
		XiaoMing xm = new XiaoMing();
		xm.setState("小明病了");
		
		//创建监听者并注册
		XiaoWang xw = new XiaoWang();
		ZhangSan zs = new ZhangSan();
		xm.addObserver(xw);
		xm.addObserver(zs);
		
		//被监听者发生变化
		xm.change();
		
	}
}
