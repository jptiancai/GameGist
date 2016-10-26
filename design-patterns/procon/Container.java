package procon;

import java.util.ArrayList;
import java.util.List;

public class Container {  
	  
    //缓冲区大小   
    private int size;  
    private List<Food> foods;  
  
    public Container(int size) {  
        this.size = size;  
        foods = new ArrayList<Food>(size);  
    }  
  
    public synchronized void poll(Food food) {  
        while (foods.size() >= size) {  
            try {  
                wait();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
        foods.add(food);  
        notifyAll();  
    }  
    public synchronized Food offer() {  
        Food food = null;  
        while (foods.size() == 0) {  
            try {  
                wait();  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
        }  
        food = foods.remove(foods.size() - 1);  
        notifyAll();  
        return food;  
    }  
}  
