package chain;

public class Worker3 implements IWorker{
	
	private IWorker next;  

	@Override
	public void handleIphone(Iphone iphone) {
		 iphone.setState(iphone.getState() + "我现在是一台完整的Iphone了。");  
	        if (next != null)  
	            next.handleIphone(iphone);  
	}

	@Override
	public void setNext(IWorker worker) {
		this.next = worker;
	}

}
