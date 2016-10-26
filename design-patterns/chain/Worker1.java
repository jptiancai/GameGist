package chain;

public class Worker1 implements IWorker{
	
	private IWorker next;  

	@Override
	public void handleIphone(Iphone iphone) {
		 iphone.setState(iphone.getState() + "我被装了一个黑色的后盖；");  
	        if (next != null)  
	            next.handleIphone(iphone);  
	}

	@Override
	public void setNext(IWorker worker) {
		this.next = worker;
	}

}
