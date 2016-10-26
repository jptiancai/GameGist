package procon;

public class Producer implements Runnable {  
	  
    private Container container;  
  
    public Producer(Container container) {  
        super();  
        this.container = container;  
    }  
  
    public void run() {  
        for (int i = 0; i < 10; i++) {  
            Food food = new Food();  
            food.setName("馒头" + i);  
            System.out.println("生产者生产出" + food.getName());  
            container.poll(food);  
            try {  
                Thread.sleep((long) (Math.random() * 3000));  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
}  