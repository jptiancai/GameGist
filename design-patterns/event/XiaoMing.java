package event;

public class XiaoMing extends Subject{
	 private String state;  
	  
	    //实际要通进行通知的方法  
	    public void change() {  
	        this.notifyObservers();  
	    }  
	    public String getState() {  
	        return state;  
	    }  
	    public void setState(String state) {  
	        this.state = state;  
	    }  
}
