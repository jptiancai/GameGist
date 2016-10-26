package chain;

public interface IWorker {
	/** 
     * 处理方法 
     * @param iphone 
     * @author lifh 
     */  
    void handleIphone(Iphone iphone);  
    /** 
     * 设置下一个处理者 
     * @param worker 
     * @author lifh 
     */  
    void setNext(IWorker worker);  
}
