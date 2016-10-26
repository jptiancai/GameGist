package event;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
	 private List<Observer> observers = new ArrayList<Observer>();  
	  
	    /** 
	     * 增加一个观察者 
	     * @param observer 
	     * @author lifh 
	     */  
	    public void addObserver(Observer observer) {  
	        observers.add(observer);  
	    }  
	    /** 
	     * 删除一个观察者 
	     * @param observer 
	     * @author lifh 
	     */  
	    public void removeObserver(Observer observer) {  
	        observers.remove(observer);  
	    }  
	    /** 
	     * 通知所有观察者 
	     * @author lifh 
	     */  
	    public void notifyObservers() {  
	        for (Observer observer : observers) {  
	            observer.update(this);  
	        }  
	    }  
}
