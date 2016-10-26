package procon;

public class Main {
	 public static void main(String[] args) {  
		    Container container = new Container(5);  
		    Thread producer1 = new Thread(new Producer(container));  
		    // Thread producer2 = new Thread(new Producer(container));  
		    // producer2.start();  
		    Thread consumer1 = new Thread(new Consumer(container));  
		    producer1.start();  
		    consumer1.start();  
	    }  
}
