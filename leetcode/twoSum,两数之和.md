[TOC]



先按照频率来学习[LeetCode 问题汇总（算法，难度，频率）](http://blog.csdn.net/sbitswc/article/details/21163721)

# twoSum,两数之和

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UrlTest {

	public static void main(String[] args) throws IOException {
		
		int[] nums = {2, 7 ,11 ,15};
		int target = 15;
		UrlTest test = new UrlTest();
		int[] twoSum = test.twoSum(nums, target);
		if(twoSum != null){
			System.out.print("index1=" + twoSum[0] +"," + "index2=" + twoSum[1]);
		}else{
			System.out.println("not found.");
		}
		
	}
	
	/**
	 * 本来是求和的，不过来一个逆向思维，这个问题就迎刃而解，时间复杂度度由O(n^2)变成O(n)
	 * <br>
	 * 官网的讨论：https://discuss.leetcode.com/category/9/two-sum
	 * <br>
	 * 九章算法 ：http://www.jiuzhang.com/solutions/two-sum/
	 * <br>
	 * 算法精粹 ：https://soulmachine.gitbooks.io/algorithm-essentials/content/java/linear-list/array/two-sum.html
	 * @param nums
	 * @param target
	 * @return
	 */
	private int[] twoSum(int[] nums, int target){

		Map<Integer, Integer> m = new HashMap<Integer, Integer>();
		for (int i = 0; i < nums.length; i++) {
			//2 -> 0
			//7 -> 1
			//11 -> 2
			//15 -> 3
			m.put(nums[i], i);
		}
		
		for (int i = 0; i < nums.length; i++) {
			//9 - 2 = 7
			Integer v = m.get(target - nums[i]);
			if(v != null && v > i){
				return new int[]{i + 1, v + 1};
			}
		}
		
		return null;
	}
	

}

```

# add two numbers,链表求和

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		// 2->4->3 + 5->6->4 = 7->0->8
		ListNode l1 = urlTest.new ListNode(2);
		ListNode node = urlTest.new ListNode(4);
		ListNode node2 = urlTest.new ListNode(3);
		l1.next = node;
		node.next = node2;

		System.out.println(l1.toString());

		ListNode l2 = urlTest.new ListNode(5);
		ListNode node3 = urlTest.new ListNode(6);
		ListNode node4 = urlTest.new ListNode(4);
		l2.next = node3;
		node3.next = node4;

		System.out.println(l2.toString());

		System.out.println(urlTest.addTwoNumbers(l1, l2).toString());

	}

	public class ListNode {
		int val;
		ListNode next;

		ListNode(int x) {
			val = x;
			next = null;
		}

		@Override
		public String toString() {
			return "ListNode [val=" + val + ", next=" + next + "]";
		}

	}

	/**
	 * 时间复杂度O(m + n) 九章算法： http://www.jiuzhang.com/solutions/add-two-numbers/
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

		ListNode dummy = new ListNode(-1); // 头节点

		int carry = 0;

		ListNode prev = dummy;

		for (ListNode pa = l1, pb = l2;

		pa != null || pb != null;

		pa = pa == null ? null : pa.next,

		pb = pb == null ? null : pb.next,

		prev = prev.next) {

			final int ai = pa == null ? 0 : pa.val;

			final int bi = pb == null ? 0 : pb.val;

			final int value = (ai + bi + carry) % 10;

			carry = (ai + bi + carry) / 10;

			prev.next = new ListNode(value); // 尾插法

		}

		if (carry > 0)

			prev.next = new ListNode(carry);

		return dummy.next;
	}

}

```

