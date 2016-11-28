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
	 * 九章在线练习 : http://www.lintcode.com/zh-cn/problem/add-two-numbers/
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

# Longest Substring Without Repeating Characters, 求最大无重复字符子串

```java

package com.imop.lj.test.battle;

import java.util.Arrays;

public class UrlTest {

	public static void main(String[] args) throws Exception{
		UrlTest urlTest = new UrlTest();
		System.out.println("最大无重复字符子串长度是: "+urlTest.lengthOfLongestSubstring("abcabcbb"));
	}
	
	/**
	 * 最大无重复字符子串
	 * <br>
	 * a 的ASCII 码是97
	 * <br>
	 * 九章算法 :http://www.jiuzhang.com/solutions/longest-substring-without-repeating-characters/
	 * <br>
	 * Code Ganker　：http://blog.csdn.net/linhuanmars/article/details/19949159
	 * <br>
	 * 算法精粹
	 * <br>
	 * abcabcbb -> abc -> 3
	 * <br>
	 * abbc -> 2 
	 * <br>
	 * bbbbb -> b -> 1
	 * <br>
	 * @param s
	 * @return
	 */
	 public int lengthOfLongestSubstring(String s) {
		 final int ASCII_MAX = 255;
	        int[] last = new int[ASCII_MAX]; // 记录字符上次出现过的位置
	        int start = 0; // 记录当前子串的起始位置

	        Arrays.fill(last, -1); // 0也是有效位置，因此初始化为-1
	        int max_len = 0;
	        for (int i = 0; i < s.length(); i++) {
	            if (last[s.charAt(i)] >= start) {
	                max_len = Math.max(i - start, max_len);
	                start = last[s.charAt(i)] + 1;
	            }
	            last[s.charAt(i)] = i;
	        }
	        
	        System.out.println("最大无重复字符子串:" +s.substring(0,Math.max((int)s.length() - start, max_len)));
	        return Math.max((int)s.length() - start, max_len);  // 别忘了最后一次，例如"abcd"
	 }
}
```
# Median of Two Sorted Arrays, 找到两个排序数组的中位数

```java

package com.imop.lj.test.battle;

public class UrlTest {

	public static void main(String[] args) throws Exception{
	
		UrlTest urlTest = new UrlTest();
		int[] A = {1,2,3};
		int[] B = {4,5};
		System.out.println(urlTest.findMedianSortedArrays(A, B));
		
	}
	
	/**找到两个排序数组的中位数,曾经创新工场的面试题
	 * <br>
	 * 九章算法:http://www.jiuzhang.com/solutions/median-of-two-sorted-arrays/,
	 * 其中有很好的例子输出: http://www.lintcode.com/zh-cn/problem/median-of-two-sorted-arrays/
	 * <br>
	 * Code Ganker :http://blog.csdn.net/linhuanmars/article/details/19905515
	 * <br>
	 * ACM之家 :　http://www.acmerblog.com/leetcode-median-of-two-sorted-arrays-5330.html
	 * <br>
	 * <br>
	 * 给出数组A = [1,2,3,4,5,6] B = [2,3,4,5]，中位数3.5
	 * <br>
	 * 给出数组A = [1,2,3] B = [4,5]，中位数 3
	 * @param A
	 * @param B
	 * @return
	 */
	 public double findMedianSortedArrays(int A[], int B[]) {
	        int len = A.length + B.length;
	        if (len % 2 == 1) {
	            return findKth(A, 0, B, 0, len / 2 + 1);
	        }
	        return (
	            findKth(A, 0, B, 0, len / 2) + findKth(A, 0, B, 0, len / 2 + 1)
	        ) / 2.0;
	    }

	    // find kth number of two sorted array
	    public static int findKth(int[] A, int A_start,
	                              int[] B, int B_start,
	                              int k){		
			if (A_start >= A.length) {
				return B[B_start + k - 1];
			}
			if (B_start >= B.length) {
				return A[A_start + k - 1];
			}

			if (k == 1) {
				return Math.min(A[A_start], B[B_start]);
			}
			
			int A_key = A_start + k / 2 - 1 < A.length
			            ? A[A_start + k / 2 - 1]
			            : Integer.MAX_VALUE;
			int B_key = B_start + k / 2 - 1 < B.length
			            ? B[B_start + k / 2 - 1]
			            : Integer.MAX_VALUE; 
			
			if (A_key < B_key) {
				return findKth(A, A_start + k / 2, B, B_start, k - k / 2);
			} else {
				return findKth(A, A_start, B, B_start + k / 2, k - k / 2);
			}
		}
}
```
# Longest Palindromic Substring, 最长回文子串

```java

package com.imop.lj.test.battle;

public class UrlTest {

	public static void main(String[] args) throws Exception{
	
		
		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.longestPalindrome("abcdzdcab"));
	}
	
	
	/**
	 * 海上在上海
	 * 样例,给出字符串 "abcdzdcab"，它的最长回文子串为 "cdzdc"。
	 * <br>
	 * 其实题目做到现在,就是为了理解其中好的算法,比如解决这道题的算法有很多种,值得学习
	 * 最长回文子串——Manacher 算法 : https://segmentfault.com/a/1190000003914228
	 * <br>
	 * 算法精粹 : https://soulmachine.gitbooks.io/algorithm-essentials/content/java/string/longest-palindromic-substring.html
	 * <br>
	 * 
	 * @param s
	 * @return
	 */
	// Transform S into T.
    // For example, S = "abba", T = "^#a#b#b#a#$".
    // ^ and $ signs are sentinels appended to each end to avoid bounds checking
    public String preProcess(final String s) {
        int n = s.length();
        if (n == 0) return "^$";

        StringBuilder ret = new StringBuilder("^");
        for (int i = 0; i < n; i++) ret.append("#" + s.charAt(i));

        ret.append("#$");
        return ret.toString();
    }

    String longestPalindrome(String s) {
        String T = preProcess(s);
        final int n = T.length();
        // 以T[i]为中心，向左/右扩张的长度，不包含T[i]自己，
        // 因此 P[i]是源字符串中回文串的长度
        int[] P = new int[n];
        int C = 0, R = 0;

        for (int i = 1; i < n - 1; i++) {
            int iMirror = 2 * C - i; // equals to i' = C - (i-C)

            P[i] = (R > i) ? Math.min(R - i, P[iMirror]) : 0;

            // Attempt to expand palindrome centered at i
            while (T.charAt(i + 1 + P[i]) == T.charAt(i - 1 - P[i]))
                P[i]++;

            // If palindrome centered at i expand past R,
            // adjust center based on expanded palindrome.
            if (i + P[i] > R) {
                C = i;
                R = i + P[i];
            }
        }

        // Find the maximum element in P.
        int maxLen = 0;
        int centerIndex = 0;
        for (int i = 1; i < n - 1; i++) {
            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }

        final int start =(centerIndex - 1 - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }
}
```
# ZigZag Conversion, 之字回型串，待添加

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.convert("PAYPALISHIRING", 3));

	}

	 /**
	  * PAYPALISHIRING
	  * <pre>
	  *P   A   H   N
	  *A P L S I I G
	  *Y   I   R		
	  *</pre>
	  *算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/simulation/zigzag-conversion.html
	  * @param s
	  * @param nRows
	  * @return
	  */
	 public String convert(String s, int numRows) {
	        if (numRows <= 1 || s.length() <= 1) return s;
	        StringBuilder result = new StringBuilder();
	        for (int i = 0; i < numRows; i++) {
	            for (int j = 0, index = i; index < s.length();
	                 j++, index = (2 * numRows - 2) * j + i) {
	                result.append(s.charAt(index));  // 垂直元素
	                if (i == 0 || i == numRows - 1) continue;   // 斜对角元素
	                if (index + (numRows - i - 1) * 2 < s.length())
	                    result.append(s.charAt(index + (numRows - i - 1) * 2));
	            }
	        }
	        return result.toString();
	    }

}

```



# Reverse Integer， 反转整数

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.reverse(Integer.MAX_VALUE));
	}

	/**
	 * 两个特殊情况：负数和溢出的情况
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/simulation/reverse-integer.html
	 * @param x
	 * @return
	 */
	public int reverse(int x) {
		//存储反转后的数
        long r = 0;
        long t = x;
        t = t > 0 ? t : -t;
        for (; t > 0; t /= 10)
            r = r * 10 + t % 10;

        //最后统一判断下
        boolean sign = x > 0 ? false: true;
        if (r > 2147483647 || (sign && r > Integer.MAX_VALUE)) {
            return 0;
        } else {
            if (sign) {
                return (int)-r;
            } else {
                return (int)r;
            }
        }
    }
}

```

# String to Integer, 字符串转换成整数

```java

package com.imop.lj.test.battle;

public class UrlTest {

	public static void main(String[] args) throws Exception{
	
		
		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.myAtoi("++c"));
		System.out.println(Integer.parseInt("3924x8fc"));
	}
	
	/**
	 * 先看java的Integer.parseInt是如何实现的
	 * 编程精粹: https://soulmachine.gitbooks.io/algorithm-essentials/content/java/string/atoi.html
	 * @param str
	 * @return
	 */
	 public int myAtoi(String str) {
	        // Start typing your Java solution below
	        // DO NOT write main() function
	        if(str == null) {
	            return 0;
	        }
	        str = str.trim();
	        if (str.length() == 0) {
	            return 0;
	        }
	            
	        int sign = 1;
	        int index = 0;
	    
	        if (str.charAt(index) == '+') {
	            index++;
	        } else if (str.charAt(index) == '-') {
	            sign = -1;
	            index++;
	        }
	        long num = 0;
	        for (; index < str.length(); index++) {
	            if (str.charAt(index) < '0' || str.charAt(index) > '9')
	                break;
	            num = num * 10 + (str.charAt(index) - '0');
	            if (num > Integer.MAX_VALUE ) {
	                break;
	            }
	        }   
	        if (num * sign >= Integer.MAX_VALUE) {
	            return Integer.MAX_VALUE;
	        }
	        if (num * sign <= Integer.MIN_VALUE) {
	            return Integer.MIN_VALUE;
	        }
	        return (int)num * sign;
	    }
}
```
