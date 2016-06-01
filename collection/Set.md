import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.TreeSet;

public class UrlTest {

	public static void main(String[] args) throws IOException {
		Random r = new Random();
		 
		HashSet<Integer> hashSet = new HashSet<Integer>();
		TreeSet<Integer> treeSet = new TreeSet<Integer>();
		LinkedHashSet<Integer> linkedSet = new LinkedHashSet<Integer>();
	 
		// start time
		long startTime = System.nanoTime();
	 
		for (int i = 0; i < 1000; i++) {
			int x = r.nextInt(1000 - 10) + 10;
			hashSet.add(new Integer(x));
		}
		// end time
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		System.out.println("HashSet add: " + duration);
	 
		// start time
		startTime = System.nanoTime();
		for (int i = 0; i < 1000; i++) {
			int x = r.nextInt(1000 - 10) + 10;
			treeSet.add(new Integer(x));
		}
		// end time
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.println("TreeSet add: " + duration);
	 
		// start time
		startTime = System.nanoTime();
		for (int i = 0; i < 1000; i++) {
			int x = r.nextInt(1000 - 10) + 10;
			linkedSet.add(new Integer(x));
		}
		// end time
		endTime = System.nanoTime();
		duration = endTime - startTime;
		System.out.println("LinkedHashSet add: " + duration);
		
		System.out.println("--------------------------------");
		
		// start time
		long startTime1 = System.nanoTime();
		for (Integer i : hashSet) {
			System.out.print(i);
		}
		// end time
		long endTime1 = System.nanoTime();
		long duration1 = endTime1 - startTime1;
		System.out.println("\nHashSet iterator: " + duration1);
	 
		// start time
		startTime1 = System.nanoTime();
		for (Integer i : treeSet) {
			System.out.print(i);
		}
		// end time
		endTime1 = System.nanoTime();
		duration1 = endTime1 - startTime1;
		System.out.println("\nTreeSet iterator: " + duration1);
	 
		// start time
		startTime1 = System.nanoTime();
		for (Integer i : linkedSet) {
			System.out.print(i);
		}
		// end time
		endTime1 = System.nanoTime();
		duration1 = endTime1 - startTime1;
		System.out.println("\nLinkedHashSet iterator: " + duration1);
		
	}

}
