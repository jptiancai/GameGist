package procon;

public class Consumer implements Runnable {  
	  
    private Container container;  
  
    public Consumer(Container container) {  
        super();  
        this.container = container;  
    }  
  
    public void run() {  
        for (;;) {  
            Food food = container.offer();  
            try {  
                Thread.sleep((long) (Math.random() * 3000));  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
            if (food != null) {  
                System.out.println(food.getName() + "被消费！");  
            }  
        }  
    }  
}  
