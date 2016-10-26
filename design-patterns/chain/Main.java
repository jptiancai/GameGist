package chain;

public class Main {
	public static void main(String[] args) {  
        IWorker worker1 = new Worker1();  
        IWorker worker2 = new Worker2();  
        IWorker worker3 = new Worker3();  
  
        worker1.setNext(worker2);  
        worker2.setNext(worker3);  
        Iphone iphone = new Iphone();  
        worker1.handleIphone(iphone);  
  
        System.out.println(iphone.getState());  
    }  
}
