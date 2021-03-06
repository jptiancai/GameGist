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
			return val + "->" + next;
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
# Palindrome Number, 整数是否是回文

```java

package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.isPalindrome(123321));
	}

	/**
	 * 收尾分别比较，比起反转要好很多
	 * 编程精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/simulation/palindrome-number.html
	 * @param x
	 * @return
	 */
	public boolean isPalindrome(int x) {
        if (x < 0) return false;
        int d = 1; // 除数
        while (x / d >= 10) d *= 10;

        while (x > 0) {
            int q = x / d;  // 商,即首位
            int r = x % 10;   // 余数，即末位
            if (q != r) return false;
            x = x % d / 10; //准备下一次
            d /= 100;
        }
        return true;
    }
}
```
#  Regular Expression Matching,正则表达式匹配

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.isMatch("a", "."));
		
		Pattern p = Pattern.compile(".");
		Matcher matcher = p.matcher("a");
		System.out.println(matcher.find());
	}

	/**
	 * 正则表达式匹配的问题，可以借鉴下jdk中是如何实现的
	 * 九章算法 ： http://www.jiuzhang.com/solutions/regular-expression-matching/
	 * @param s
	 * @param p
	 * @return
	 */
	public boolean isMatch(String s, String p) {
        //Java note: s.substring(n) will be "" if n == s.length(), but if n > s.length(), index oob error
        
        int i = 0, j = 0;
        //you don't have to construct a state machine for this problem
 
        if (s.length() == 0) {
            return checkEmpty(p);
        }
 
        if (p.length() == 0) {
            return false;
        }
 
        char c1 = s.charAt(0);
        char d1 = p.charAt(0), d2 = '0'; //any init value except '*'for d2 will do
 
        if (p.length()>1){
            d2 = p.charAt(1);
        }
 
        if (d2 == '*') {
            if (compare(c1, d1)) {
                //fork here: 1. consume the character, and use the same pattern again.
                //2. keep the character, and skip 'd1*' pattern
                 
                //Here is also an opportunity to use DP, but the idea is the same
                return isMatch(s.substring(1), p) || isMatch(s, p.substring(2));
            }
            else {
                return isMatch(s, p.substring(2));
            }
        }
        else {
            if (compare(c1, d1)) {
                return isMatch(s.substring(1), p.substring(1));
            }
            else {
                return false;
            }
        }
    }
    
    public boolean compare(char c1, char d1){
        return d1 == '.' || c1 == d1;
    }
 
    public boolean checkEmpty(String p) {
        if (p.length()%2 != 0) {
            return false;  
        }
 
        for (int i = 1; i < p.length(); i+=2) {
            if (p.charAt(i) != '*') {
                return false;
            }
        }
        return true;
    }
}

```

# Container With Most Water, 装最多水的容器

```java
package com.imop.lj.test.battle;

public class UrlTest {

	public static void main(String[] args) throws Exception{
		UrlTest urlTest = new UrlTest();
		int height[] = {1, 3, 2};
		System.out.println(urlTest.maxArea(height));
	}
	
	/**
	 * 题解不是很明白:http://www.lintcode.com/zh-cn/problem/container-with-most-water/
	 * 算法精粹：　https://soulmachine.gitbooks.io/algorithm-essentials/content/java/greedy/container-with-most-water.html
	 * @param height
	 * @return
	 */
	public int maxArea(int[] height) {
		int start = 0;
		int end = height.length - 1;
		int result = Integer.MIN_VALUE;
		while (start < end) {
			int area = Math.min(height[end], height[start]) * (end - start);
			result = Math.max(result, area);
			if (height[start] <= height[end]) {
				start++;
			} else {
				end--;
			}
		}
		return result;
	}
}

```

# Integer to Roman, 整数转罗马数字

```java
package com.imop.lj.test.battle;

public class UrlTest {

	public static void main(String[] args) throws Exception{
		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.intToRoman(4));
		System.out.println(urlTest.intToRoman(10));
	}
	

	/**
	 *找到数字转换罗马数字的规律即可,然后对应的找即可
	 *
	 *4 -> IV
	 *<br>
	 *12 -> XII
	 *<br>
	 *21 -> XXI
	 *<br>
	 *99 -> XCIX
	 *<br>
	 * @param num
	 * @return
	 */
	public String intToRoman(int num) {
		if(num <= 0) {
			return "";
		}
	    int[] nums = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	    String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
	    StringBuilder res = new StringBuilder();
	    int digit=0;
	    while (num > 0) {
	        int times = num / nums[digit];
	        num -= nums[digit] * times;
	        for ( ; times > 0; times--) {
	            res.append(symbols[digit]);
	        }
	        digit++;
	    }
	    return res.toString();
	}
	
}

```

# Roman to Integer, 罗马转成数字

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.romanToInt("II"));
		
	}

	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/roman-to-integer/
	 * @param s
	 * @return
	 */
	public int romanToInt(String s) {
	    if (s == null || s.length()==0) {
                return 0;
	    }
	    Map<Character, Integer> m = new HashMap<Character, Integer>();
	    m.put('I', 1);
	    m.put('V', 5);
	    m.put('X', 10);
	    m.put('L', 50);
	    m.put('C', 100);
	    m.put('D', 500);
	    m.put('M', 1000);

	    int length = s.length();
	    int result = m.get(s.charAt(length - 1));
	    for (int i = length - 2; i >= 0; i--) {
	        if (m.get(s.charAt(i + 1)) <= m.get(s.charAt(i))) {
	            result += m.get(s.charAt(i));
	        } else {
	            result -= m.get(s.charAt(i));
	        }
	    }
	    return result;
	}
}

```

# Longest Common Prefix, 最长公共前缀

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		String[] strs={"ABCD", "ABEF"};
		System.out.println(urlTest.longestCommonPrefix(strs));

	}

	/**
	 * 可以分为两个方法： 纵向扫描（指针纵向比较）和横向扫描（每个字符串和第一个字符串比较）
	 * 
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/string/longest-common-prefix.html
	 * 在 "ABCD" "ABEF" 和 "ACEF" 中,  LCP 为 "A"
	 * <br>
	 * 在 "ABCDEFG", "ABCEFG", "ABCEFA" 中, LCP 为 "ABC"
	 * <br>
	 * @param strs
	 * @return
	 */
	public String longestCommonPrefix(String[] strs) {

		if (strs.length == 0)
			return "";

		for (int j = 0; j < strs[0].length(); ++j) { // 纵向扫描

			for (int i = 1; i < strs.length; ++i) {

				if (j == strs[i].length() ||

				strs[i].charAt(j) != strs[0].charAt(j))

					return strs[0].substring(0, j);

			}

		}

		return strs[0];

	}
}

```

# 3Sum， 三数之和等于0

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] num ={-1, 0 , 1, 2, -1, -4};
		System.out.println(urlTest.threeSum(num));

	}

	/**
	 * 结果不可以重复，而且是a << b << c
	 * 九章算法 ： http://www.jiuzhang.com/solutions/3sum/
	 * @param num
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> threeSum(int[] num) {
		
		ArrayList<ArrayList<Integer>> rst = new ArrayList<ArrayList<Integer>>();
		if(num == null || num.length < 3) {
			return rst;
		}
		
		// 先排序
		
		Arrays.sort(num);
		//-4,-1,-1,0,1,2
		for (int i = 0; i < num.length - 2; i++) {
			if (i != 0 && num[i] == num[i - 1]) {
				continue; // to skip duplicate numbers; e.g [0,0,0,0]
			}
			//使用了左右两个指针
			int left = i + 1;
			int right = num.length - 1;
			while (left < right) {
				int sum = num[left] + num[right] + num[i];
				if (sum == 0) {
					ArrayList<Integer> tmp = new ArrayList<Integer>();
					tmp.add(num[i]);
					tmp.add(num[left]);
					tmp.add(num[right]);
					rst.add(tmp);
					left++;
					right--;
					while (left < right && num[left] == num[left - 1]) { // to skip duplicates
						left++;
					}
					while (left < right && num[right] == num[right + 1]) { // to skip duplicates
						right--;
					}
				} else if (sum < 0) {
					left++;
				} else {
					right--;
				}
			}
		}
		return rst;
	}
}

```

# 3Sum Closest, 最接近的三数之和

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.Arrays;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] num ={-1, 2, 1, -4};
		System.out.println(urlTest.threeSumClosest(num, 1));

	}

	/**
	 * 九章算法： http://www.jiuzhang.com/solutions/3sum-closest/
	 * <br>
	 * 还是排序
	 * @param numbers
	 * @param target
	 * @return
	 */
	public int threeSumClosest(int[] numbers, int target) {
        if (numbers == null || numbers.length < 3) {
            return -1;
        }
        
        Arrays.sort(numbers);
        int bestSum = numbers[0] + numbers[1] + numbers[2];
        for (int i = 0; i < numbers.length; i++) {
            int start = i + 1, end = numbers.length - 1;
            while (start < end) {
                int sum = numbers[i] + numbers[start] + numbers[end];
                if (Math.abs(target - sum) < Math.abs(target - bestSum)) {
                    bestSum = sum;
                }
                if (sum < target) {
                    start++;
                } else {
                    end--;
                }
            }
        }
        
        return bestSum;
    }
}

```

# Letter Combinations of a Phone Number， 电话号码的字母组合

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		String digits = "23";
		// 递归
		System.out.println(urlTest.letterCombinations(digits));
		// 迭代
		System.out.println(urlTest.letterCombinations2(digits));

	}

	/**
	 * 九章算法 ：
	 * http://www.jiuzhang.com/solutions/letter-combinations-of-a-phone-number/
	 * 
	 * 手机上数字下对应的字母的组合有多少种
	 * 
	 * @param digits
	 * @return
	 */
	public ArrayList<String> letterCombinations(String digits) {
		ArrayList<String> result = new ArrayList<String>();

		if (digits == null || digits.equals("")) {
			return result;
		}

		Map<Character, char[]> map = new HashMap<Character, char[]>();
		map.put('0', new char[] {});
		map.put('1', new char[] {});
		map.put('2', new char[] { 'a', 'b', 'c' });
		map.put('3', new char[] { 'd', 'e', 'f' });
		map.put('4', new char[] { 'g', 'h', 'i' });
		map.put('5', new char[] { 'j', 'k', 'l' });
		map.put('6', new char[] { 'm', 'n', 'o' });
		map.put('7', new char[] { 'p', 'q', 'r', 's' });
		map.put('8', new char[] { 't', 'u', 'v' });
		map.put('9', new char[] { 'w', 'x', 'y', 'z' });

		StringBuilder sb = new StringBuilder();
		helper(map, digits, sb, result);

		return result;
	}

	private void helper(Map<Character, char[]> map, String digits, StringBuilder sb, ArrayList<String> result) {
		if (sb.length() == digits.length()) {
			result.add(sb.toString());
			return;
		}

		for (char c : map.get(digits.charAt(sb.length()))) {
			sb.append(c);
			helper(map, digits, sb, result);
			sb.deleteCharAt(sb.length() - 1);
		}
	}

	private static final String[] keyboard =

	new String[] { " ", "", "abc", "def",

			"ghi", "jkl", "mno", "pqrs", "tuv", "wxyz" };

	public List<String> letterCombinations2(String digits) {

		if (digits.isEmpty())
			return new ArrayList<>();

		List<String> result = new ArrayList<>();

		result.add("");

		for (char d : digits.toCharArray()) {

			final int n = result.size();

			final int m = keyboard[d - '0'].length();

			// resize to n * m

			for (int i = 1; i < m; ++i) {

				for (int j = 0; j < n; ++j) {

					result.add(result.get(j));

				}

			}

			for (int i = 0; i < result.size(); ++i) {

				result.set(i, result.get(i) + keyboard[d - '0'].charAt(i / n));

			}

		}

		return result;

	}
}

```

# 4Sum, 四个数之和，满足目标值的所有组合情况

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] num={1, 0, -1, 0, -2, 2};
		
		// 递归
		System.out.println(urlTest.fourSum(num, 0));

	}

	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/4sum/
	 * left和right两个指针
	 * @param num
	 * @param target
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> fourSum(int[] num, int target) {
		ArrayList<ArrayList<Integer>> rst = new ArrayList<ArrayList<Integer>>();
		Arrays.sort(num);

		for (int i = 0; i < num.length - 3; i++) {
			if (i != 0 && num[i] == num[i - 1]) {
				continue;
			}

			for (int j = i + 1; j < num.length - 2; j++) {
				if (j != i + 1 && num[j] == num[j - 1])
					continue;

				int left = j + 1;
				int right = num.length - 1;
				while (left < right) {
					int sum = num[i] + num[j] + num[left] + num[right];
					if (sum < target) {
						left++;
					} else if (sum > target) {
						right--;
					} else {
						ArrayList<Integer> tmp = new ArrayList<Integer>();
						tmp.add(num[i]);
						tmp.add(num[j]);
						tmp.add(num[left]);
						tmp.add(num[right]);
						rst.add(tmp);
						left++;
						right--;
						while (left < right && num[left] == num[left - 1]) {
							left++;
						}
						while (left < right && num[right] == num[right + 1]) {
							right--;
						}
					}
				}
			}
		}

		return rst;
	}
}

```

# Remove Nth Node From End of List, 删除链表中倒数第n个节点

```java
package com.imop.lj.test.battle;

import java.io.IOException;


public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		ListNode l1 = urlTest.new ListNode(1);
		ListNode node = urlTest.new ListNode(2);
		ListNode node2 = urlTest.new ListNode(3);
		ListNode node3 = urlTest.new ListNode(4);
		ListNode node4 = urlTest.new ListNode(5);
		l1.next = node;
		node.next = node2;
		node2.next = node3;
		node3.next = node4;
		
		System.out.println(l1.toString());
		urlTest.removeNthFromEnd(l1, 2);
		System.out.println(l1.toString());
		

	}
	
	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/remove-nth-node-from-end-of-list/
	 * 给出链表1->2->3->4->5->null和 n = 2.
	 * <br>
	 * 删除倒数第二个节点之后，这个链表将变成1->2->3->5->null
	 * @param head
	 * @param n
	 * @return
	 */
	public ListNode removeNthFromEnd(ListNode head, int n) {
        if (n <= 0) {
            return null;
        }
        
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        ListNode preDelete = dummy;
        for (int i = 0; i < n; i++) {
            if (head == null) {
                return null;
            }
            head = head.next;
        }
        while (head != null) {
            head = head.next;
            preDelete = preDelete.next;
        }
        preDelete.next = preDelete.next.next;
        return dummy.next;
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
			return val + "->" + next;
		}

	}
}

```

# Valid Parentheses， 有效的括号序列,编译器的基础

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.Stack;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		System.out.println(urlTest.isValid("{}"));
		System.out.println(urlTest.isValid("{}]"));

	}

	/**
	 * 用栈的思想解决此类问题
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/stack-and-queue/stack/valid-parentheses.html
	 * @param s
	 * @return
	 */
	public boolean isValid(String s) {

		final String left = "([{";

		final String right = ")]}";

		Stack<Character> stk = new Stack<>();

		for (int i = 0; i < s.length(); ++i) {

			final char c = s.charAt(i);

			if (left.indexOf(c) != -1) {

				stk.push(c);

			} else {

				if (!stk.isEmpty() &&

				stk.peek() == left.charAt(right.indexOf(c)))

					stk.pop();

				else

					return false;

			}

		}

		return stk.empty();

	}
}

```

# Merge Two Sorted Lists, 合并两个排序链表

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.Stack;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		ListNode l1 = urlTest.new ListNode(1);
		ListNode node = urlTest.new ListNode(3);
		ListNode node2 = urlTest.new ListNode(8);
		ListNode node3 = urlTest.new ListNode(11);
		ListNode node4 = urlTest.new ListNode(15);
		l1.next = node;
		node.next = node2;
		node2.next = node3;
		node3.next = node4;
		
		ListNode l2 = urlTest.new ListNode(2);
		
		System.out.println(l1.toString());
		System.out.println(l2.toString());
		System.out.println(urlTest.mergeTwoLists(l1, l2));

	}

	/**
	 * 直接挪动指针，算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/sorting/merge-sort/merge-two-sorted-lists.html
	 * <br>
	 * 给出 1->3->8->11->15->null，2->null， 返回 1->2->3->8->11->15->null
	 * @param l1
	 * @param l2
	 * @return
	 */
	public ListNode mergeTwoLists(ListNode l1, ListNode l2) {

		if (l1 == null)
			return l2;

		if (l2 == null)
			return l1;

		ListNode dummy = new ListNode(-1);

		ListNode p = dummy;

		for (; l1 != null && l2 != null; p = p.next) {

			if (l1.val > l2.val) {
				p.next = l2;
				l2 = l2.next;
			}

			else {
				p.next = l1;
				l1 = l1.next;
			}

		}

		p.next = l1 != null ? l1 : l2;

		return dummy.next;

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
			return val + "->" + next;
		}

	}
}
```

# Generate Parentheses, 生成括号

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		System.out.println(urlTest.generateParenthesis(3));
		

	}

	/**
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/dfs/generate-parentheses.html
	 * 递归算法求出
	 * <br>
	 * 给定 n = 3, 可生成的组合如下:"((()))", "(()())", "(())()", "()(())", "()()()"
	 * @param n
	 * @return
	 */
	public List<String> generateParenthesis(int n) {

		if (n == 0)
			return new ArrayList<>(Arrays.asList(""));

		if (n == 1)
			return new ArrayList<>(Arrays.asList("()"));

		List<String> result = new ArrayList<>();

		for (int i = 0; i < n; ++i)

			for (String inner : generateParenthesis(i))

				for (String outer : generateParenthesis(n - 1 - i))

					result.add("(" + inner + ")" + outer);

		return result;

	}
}

```

# Merge k Sorted Lists， 合并k个排序链表(栈溢出，待解决)

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		ListNode l1 = urlTest.new ListNode(2);
		ListNode node1 = urlTest.new ListNode(4);
		l1.next = node1;
		
		ListNode l3 = urlTest.new ListNode(-1);
		
		List<ListNode> nodeLst = new ArrayList<ListNode>();
		nodeLst.add(l1);
		nodeLst.add(node1);
		
		nodeLst.add(null);
		
		nodeLst.add(l3);
		System.out.println(l1.toString());
		System.out.println(l3.toString());
		System.out.println(urlTest.mergeKLists(nodeLst).toString());
		

	}
	
	
	/**
	 * 九章算法 ：http://www.jiuzhang.com/solutions/merge-k-sorted-lists/
	 * <br>
	 * 给出3个排序链表[2->4->null,null,-1->null]，返回 -1->2->4->null
	 * @param lists
	 * @return
	 */
    public ListNode mergeKLists(List<ListNode> lists) {
        if (lists.size() == 0) {
            return null;
        }
        return mergeHelper(lists, 0, lists.size() - 1);
    }
    
    private ListNode mergeHelper(List<ListNode> lists, int start, int end) {
        if (start == end) {
            return lists.get(start);
        }
        
        int mid = start + (end - start) / 2;
        ListNode left = mergeHelper(lists, start, mid);
        ListNode right = mergeHelper(lists, mid + 1, end);
        return mergeTwoLists(left, right);
    }
    
    private ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                tail.next = list1;
                tail = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                tail = list2;
                list2 = list2.next;
            }
        }
        if (list1 != null) {
            tail.next = list1;
        } else {
            tail.next = list2;
        }
        
        return dummy.next;
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
			return val + "->" + next;
		}

	}
}

```

# Swap Nodes in Pairs， 两两交换链表中的节点

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		ListNode l1 = urlTest.new ListNode(1);
		ListNode node1 = urlTest.new ListNode(2);
		ListNode node2 = urlTest.new ListNode(3);
		ListNode node3 = urlTest.new ListNode(4);
		l1.next = node1;
		node1.next = node2;
		node2.next = node3;
		
		System.out.println(l1.toString());
		System.out.println(urlTest.swapPairs(l1).toString());
		

	}
	
	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/swap-nodes-in-pairs/
	 * @param head
	 * @return
	 */
	public ListNode swapPairs(ListNode head) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        head = dummy;
        //0-》1-》2-》3-》4
        while (head.next != null && head.next.next != null) {
            ListNode n1 = head.next, n2 = head.next.next;
            // head->n1->n2->...
            // => head->n2->n1->...
            //n1和n2互换
            head.next = n2;
            n1.next = n2.next;
            n2.next = n1;
            
            // move to next pair
            //移动head指针
            head = n1;
        }
        
        return dummy.next;
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
			return val + "->" + next;
		}

	}
}

```

# Reverse Nodes in k-Group， K组翻转链表

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		ListNode l1 = urlTest.new ListNode(1);
		ListNode node1 = urlTest.new ListNode(2);
		ListNode node2 = urlTest.new ListNode(3);
		ListNode node3 = urlTest.new ListNode(4);
		l1.next = node1;
		node1.next = node2;
		node2.next = node3;

		System.out.println(l1.toString());
		System.out.println(urlTest.reverseKGroup(l1, 3).toString());

	}

	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/reverse-nodes-in-k-group/
	 * @param head
	 * @param k
	 * @return
	 */
	public ListNode reverseKGroup(ListNode head, int k) {
		if (head == null || k <= 1) {
			return head;
		}

		//0-》1-》2-》3-》4
		ListNode dummy = new ListNode(0);
		dummy.next = head;

		head = dummy;
		while (head.next != null) {
			head = reverseNextK(head, k);
		}

		return dummy.next;
	}

	// reverse head->n1->..->nk->next..
	// to head->nk->..->n1->next..
	// return n1
	private ListNode reverseNextK(ListNode head, int k) {
		// check there is enough nodes to reverse
		ListNode next = head; // next is not null
		for (int i = 0; i < k; i++) {
			if (next.next == null) {
				return next;
			}
			next = next.next;
		}

		// reverse
		ListNode n1 = head.next;
		ListNode prev = head, curt = n1;
		for (int i = 0; i < k; i++) {
			ListNode temp = curt.next;
			curt.next = prev;
			prev = curt;
			curt = temp;
		}

		n1.next = curt;
		head.next = prev;
		return n1;
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
			return val + "->" + next;
		}

	}
}

```

# Remove Duplicates from Sorted Array， 删除排序数组中的重复数字

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		int[] nums={1,1,2};
		System.out.println(urlTest.removeDuplicates(nums));

	}

	/**
	 * 算法精粹：https://soulmachine.gitbooks.io/algorithm-essentials/content/java/linear-list/array/remove-duplicates-from-sorted-array.html
	 * <br>
	 * 给出数组A =[1,1,2]，你的函数应该返回长度2，此时A=[1,2]。
	 * @param nums
	 * @return
	 */
	public int removeDuplicates(int[] nums) {

		if (nums.length == 0)
			return 0;

		int index = 1;

		for (int i = 1; i < nums.length; i++) {
			//元素不重复
			if (nums[i] != nums[index - 1])

				nums[index++] = nums[i];

		}

		return index;

	}
}

```

# Remove Duplicates from Sorted Array II

```
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		int[] nums = { 1, 1, 1, 2, 2, 3 };
		System.out.println(urlTest.removeDuplicates(nums));

	}

	/**
	 * https://soulmachine.gitbooks.io/algorithm-essentials/content/java/linear-list/array/remove-duplicates-from-sorted-array-ii.html
	 * <br>
	 * 最大重复次数为2的数组
	 * @param nums
	 * @return
	 */
	public int removeDuplicates(int[] nums) {

		if (nums.length <= 2)
			return nums.length;

		int index = 2;

		for (int i = 2; i < nums.length; i++) {

			if (nums[i] != nums[index - 2])

				nums[index++] = nums[i];

		}

		return index;

	}
}

```

# Remove Element， 删除元素

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();

		int[] A = { 1, 1, 1, 2, 2, 3 };
		System.out.println(urlTest.removeElement(A, 1));

	}

	/**
	 * 算法精粹 ： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/linear-list/array/remove-element.html
	 * @param nums
	 * @param target
	 * @return
	 */
	public int removeElement(int[] nums, int target) {

		int index = 0;

		for (int i = 0; i < nums.length; ++i) {

			if (nums[i] != target) {

				nums[index++] = nums[i];

			}

		}

		return index;

	}
}

```

# Implement strStr， 字符串查找



更高效的的算法有KMP算法

- [字符串匹配的KMP算法](http://www.ruanyifeng.com/blog/2013/05/Knuth–Morris–Pratt_algorithm.html)


- [为什么java String.contains 没有使用类似KMP字符串匹配算法进行优化？](https://www.zhihu.com/question/27852656)
- [KMP字符串匹配算法和JDK](http://mikecoder.cn/?post=61)

Boyer- Mooer算法

- [字符串匹配的Boyer-Moore算法](http://www.ruanyifeng.com/blog/2013/05/boyer-moore_string_search_algorithm.html)
  - 阮一峰推荐的参考文献[Computer Algorithms: Boyer-Moore String Searching](http://www.stoimen.com/blog/2012/04/17/computer-algorithms-boyer-moore-string-search-and-matching/),看完后想说英语真好

Rabin-Karp算法 

- [图说Rabin-Karp字符串查找算法](http://www.ituring.com.cn/article/1759),原文竟然和上面推荐的是一样的



```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.strStr("abcdabcdefg", "bcd"));

	}

	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/implement-strstr/
	 * <br>
	 * 如果 source = "source" 和 target = "target"，返回 -1。
	 * <br>
	 * 如果 source = "abcdabcdefg" 和 target = "bcd"，返回 1
	 * @param source
	 * @param target
	 * @return
	 */
	public int strStr(String source, String target) {
		if (source == null || target == null) {
			return -1;
		}

		for (int i = 0; i < source.length() - target.length() + 1; i++) {
			int j = 0;
			for (j = 0; j < target.length(); j++) {
				if (source.charAt(i + j) != target.charAt(j)) {
					break;
				}
			}
			// finished loop, target found
			if (j == target.length()) {
				return i;
			}
		}
		return -1;
	}
}

```

# Divide Two Integers, 两个整数相除

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.divide(100, 9));

	}

	/**
	 * 九章算法： http://www.jiuzhang.com/solutions/divide-two-integers/
	 * <br>
	 * 将两个整数相除，要求不使用乘法、除法和 mod 运算符。如果溢出，返回 2147483647 。
	 * <br>
	 * 还有加、减和位运算。 最简单的方法，是不断减去被除数。在这个基础上，可以做一点优化，每次把被除 数翻倍，从而加速。
	 * @param dividend
	 * @param divisor
	 * @return
	 */
	public int divide(int dividend, int divisor) {
		if (divisor == 0) {
			return dividend >= 0 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		}

		if (dividend == 0) {
			return 0;
		}

		if (dividend == Integer.MIN_VALUE && divisor == -1) {
			return Integer.MAX_VALUE;
		}

		boolean isNegative = (dividend < 0 && divisor > 0) || (dividend > 0 && divisor < 0);

		long a = Math.abs((long) dividend);
		long b = Math.abs((long) divisor);
		int result = 0;
		while (a >= b) {
			int shift = 0;
			while (a >= (b << shift)) {
				shift++;
			}
			a -= b << (shift - 1);
			result += 1 << (shift - 1);
		}
		return isNegative ? -result : result;
	}
}

```

# Substring with Concatenation of All Words,找到子串的下标

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		String[] str1 = {"foo", "bar"};
		String[] str2 = {"bar", "lov"};
		System.out.println(urlTest.findSubstring("barfoothefoobarman", str1));
		System.out.println(urlTest.findSubstring("barlovbarloveman", str2));

	}

	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/substring-with-concatenation-of-all-words/
	 * <br>
	 * 给一个字符串 S 和一个单词列表，单词长度都一样，找出所有 S 的子串，子串由所有单词组成，返回子串的起始位置,顺序无所谓
	 * @param S
	 * @param L
	 * @return
	 */
	public ArrayList<Integer> findSubstring(String S, String[] L) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		HashMap<String, Integer> toFind = new HashMap<String, Integer>();
		HashMap<String, Integer> found = new HashMap<String, Integer>();
		int m = L.length, n = L[0].length();
		for (int i = 0; i < m; i++) {
			if (!toFind.containsKey(L[i])) {
				toFind.put(L[i], 1);
			} else {
				toFind.put(L[i], toFind.get(L[i]) + 1);
			}
		}
		for (int i = 0; i <= S.length() - n * m; i++) {
			found.clear();
			int j;
			for (j = 0; j < m; j++) {
				int k = i + j * n;
				String stub = S.substring(k, k + n);
				if (!toFind.containsKey(stub))
					break;
				if (!found.containsKey(stub)) {
					found.put(stub, 1);
				} else {
					found.put(stub, found.get(stub) + 1);
				}
				if (found.get(stub) > toFind.get(stub))
					break;
			}
			if (j == m)
				result.add(i);
		}
		return result;
	}
}

```

# Next Permutation， 下一个排列



- [next_permutation原理剖析](http://blog.csdn.net/qq575787460/article/details/41215475) ： 这篇文章终于把这个问题讲明白了

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] num = {1, 3, 2, 3};
		urlTest.nextPermutation(num);
		for (int i = 0; i < num.length; i++) {
			System.out.print(num[i] + ", ");
		}

	}

	/**
	 * 九章算法 ： http://www.jiuzhang.com/solutions/next-permutation/
	 * @param num
	 */
	public void nextPermutation(int[] num) {
        if (num == null) {
            return;
        }
        
        int len = num.length;
        for (int i = len - 2; i >= 0; i--) {
            if (num[i + 1] > num[i]) {
                int j;
                for (j = len - 1; j > i - 1; j--) {
                    if (num[j] > num[i]) {
                        break;
                    }
                }

                swap(num, i, j);
                reverse(num, i + 1, len-1);
                return;
            }
        }

        reverse(num, 0, len-1);
    }

    void swap(int[] num, int i, int j) {
        int tmp = num[i];
        num[i] = num[j];
        num[j] = tmp;
    }

    void reverse(int[] num, int beg, int end) {
        for (int i = beg, j = end; i < j; i ++, j --) {
            swap(num, i, j);
        }
    }
}

```

# Longest Valid Parentheses，最长有效括号对，和生成括号的算法可以对比着看

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.Stack;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.longestValidParentheses("()()"));

	}

	/**
	 * 使用栈
	 * <br>
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/stack-and-queue/stack/longest-valid-parentheses.html
	 * @param s
	 * @return
	 */
	public int longestValidParentheses(String s) {

		// the position of the last ')'

		int maxLen = 0, last = -1;

		// keep track of the positions of non-matching '('s

		Stack<Integer> lefts = new Stack<>();

		for (int i = 0; i < s.length(); ++i) {

			if (s.charAt(i) == '(') {

				lefts.push(i);

			} else {

				if (lefts.empty()) {

					// no matching left

					last = i;

				} else {

					// find a matching pair

					lefts.pop();

					if (lefts.empty()) {

						maxLen = Math.max(maxLen, i - last);

					} else {

						maxLen = Math.max(maxLen, i - lefts.peek());

					}

				}

			}

		}

		return maxLen;

	}
}

```

# Search in Rotated Sorted Array， 搜索旋转排序数组

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = {0,1,2,3,4,5,6,7};
		System.out.println(urlTest.search(nums, 4));

	}

	/**
	 * 算法精粹 : https://soulmachine.gitbooks.io/algorithm-essentials/content/java/search/search-in-rotated-sorted-array.html
	 * <br>
	 * 二分查找
	 * @param nums
	 * @param target
	 * @return
	 */
	public int search(int[] nums, int target) {

		int first = 0, last = nums.length;

		while (first != last) {

			final int mid = first

					+ (last - first) / 2;

			if (nums[mid] == target)

				return mid;

			if (nums[first] <= nums[mid]) {

				if (nums[first] <= target && target < nums[mid])

					last = mid;

				else

					first = mid + 1;

			} else {

				if (nums[mid] < target && target <= nums[last - 1])

					first = mid + 1;

				else

					last = mid;

			}

		}

		return -1;

	}
}

```

# Search for a Range， 搜索区间

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 5, 7, 7, 8, 8, 10 };
		int[] searchRange = urlTest.searchRange(nums, 8);
		for (int i = 0; i < searchRange.length; i++) {
			System.out.println(searchRange[i]);
		}

	}

	/**
	 * 给定一个包含 n 个整数的排序数组，找出给定目标值 target 的起始和结束位置。
	 * <br>
	 * 给出[5, 7, 7, 8, 8, 10]和目标值target=8,返回[3, 4]
	 * <br>
	 * 算法精粹 ： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/search/search-for-a-range.html
	 * <br>
	 * @param nums
	 * @param target
	 * @return
	 */
	public int[] searchRange(int[] nums, int target) {

		int lower = lower_bound(nums, 0, nums.length, target);

		int upper = upper_bound(nums, 0, nums.length, target);

		if (lower == nums.length || nums[lower] != target)

			return new int[] { -1, -1 };

		else

			return new int[] { lower, upper - 1 };

	}

	int lower_bound(int[] A, int first, int last, int target) {

		while (first != last) {

			int mid = first + (last - first) / 2;

			if (target > A[mid])
				first = ++mid;

			else

				last = mid;

		}

		return first;

	}

	int upper_bound(int[] A, int first, int last, int target) {

		while (first != last) {

			int mid = first + (last - first) / 2;

			if (target >= A[mid])
				first = ++mid;

			// 与 lower_bound 仅此不同

			else

				last = mid;

		}

		return first;

	}
}

```

# Search Insert Position， 搜索插入位置

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 1, 3, 5, 6 };
		int searchRange = urlTest.searchInsert(nums, 5);
		System.out.println(searchRange);
	}

	/**
	 * [1,3,5,6]，5 → 2
	 * <br>
	 * [1,3,5,6]，2 → 1
	 * <br>
	 * [1,3,5,6]， 7 → 4
	 * <br>
	 * [1,3,5,6]，0 → 0
	 * <br>
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/search/search-insert-position.html
	 * @param nums
	 * @param target
	 * @return
	 */
	public int searchInsert(int[] nums, int target) {

		return lower_bound(nums, 0, nums.length, target);

	}

	int lower_bound(int[] A, int first, int last, int target) {

		while (first != last) {

			int mid = first + (last - first) / 2;

			if (target > A[mid])
				first = ++mid;

			else

				last = mid;

		}

		return first;

	}
}

```

# Valid Sudoku， 判断数独是否合法

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.Arrays;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		char[][] nums = { {'5', '3', '.', '.', '7', '.', '.', '.', '.'},
						  {'6', '.', '.', '1', '9', '5', '.', '.', '.'}, 
						  {'.', '9', '8', '.', '.', '.', '.', '6', '.'},
						  {'8', '.', '.', '.', '6', '.', '.', '.', '3'},
						  {'4', '.', '.', '8', '.', '3', '.', '.', '1'},
						  {'7', '.', '.', '.', '2', '.', '.', '.', '6'},
						  {'.', '6', '.', '.', '.', '.', '2', '8', '.'},
						  {'.', '.', '.', '4', '1', '9', '.', '.', '5'},
						  {'.', '.', '.', '.', '8', '.', '.', '7', '9'},
						};
		System.out.println(urlTest.isValidSudoku(nums));
	}

	
	/**
	 * 请判定一个数独是否有效。9*9的数独表格
	 * <br>
	 * 该数独可能只填充了部分数字，其中缺少的数字用 . 表示。
	 * <br>
	 * 算法精髓： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/linear-list/array/valid-sudoku.html
	 * @param board
	 * @return
	 */
	public boolean isValidSudoku(char[][] board) {

		boolean[] used = new boolean[9];

		for (int i = 0; i < 9; ++i) {

			Arrays.fill(used, false);

			for (int j = 0; j < 9; ++j) // 检查行

				if (!check(board[i][j], used))

					return false;

			Arrays.fill(used, false);

			for (int j = 0; j < 9; ++j) // 检查列

				if (!check(board[j][i], used))

					return false;

		}

		for (int r = 0; r < 3; ++r) // 检查 9 个子格子

			for (int c = 0; c < 3; ++c) {

				Arrays.fill(used, false);

				for (int i = r * 3; i < r * 3 + 3; ++i)

					for (int j = c * 3; j < c * 3 + 3; ++j)

						if (!check(board[i][j], used))

							return false;

			}

		return true;

	}

	private static boolean check(char ch, boolean[] used) {

		if (ch == '.')
			return true;

		if (used[ch - '1'])
			return false;

		return used[ch - '1'] = true;

	}
}

```

# Sudoku Solver， 数独解题

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.Arrays;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		char[][] nums = { { '5', '3', '.', '.', '7', '.', '.', '.', '.' },
						  { '6', '.', '.', '1', '9', '5', '.', '.', '.' }, 
						  { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
						  { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
						  { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
						  { '7', '.', '.', '.', '2', '.', '.', '.', '6' }, 
						  { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
						  { '.', '.', '.', '4', '1', '9', '.', '.', '5' }, 
						  { '.', '.', '.', '.', '8', '.', '.', '7', '9' }, };
		urlTest.solveSudoku(nums);
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if(j == 8){
					System.out.println();
				}else{
					System.out.print(nums[i][j]);
				} 
			}
		}
	}

	/**
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/dfs/sudoku-solver.html
	 * @param board
	 */
	public void solveSudoku(char[][] board) {

		_solveSudoku(board);

	}

	private static boolean _solveSudoku(char[][] board) {

		for (int i = 0; i < 9; ++i)

			for (int j = 0; j < 9; ++j) {

				if (board[i][j] == '.') {

					for (int k = 0; k < 9; ++k) {

						board[i][j] = Character.forDigit(k + 1, 10);

						if (isValid(board, i, j) && _solveSudoku(board))

							return true;

						board[i][j] = '.';

					}

					return false;

				}

			}

		return true;

	}

	// 检查 (x, y) 是否合法

	private static boolean isValid(char[][] board, int x, int y) {

		int i, j;

		for (i = 0; i < 9; i++) // 检查 y 列

			if (i != x && board[i][y] == board[x][y])

				return false;

		for (j = 0; j < 9; j++) // 检查 x 行

			if (j != y && board[x][j] == board[x][y])

				return false;

		for (i = 3 * (x / 3); i < 3 * (x / 3 + 1); i++)

			for (j = 3 * (y / 3); j < 3 * (y / 3 + 1); j++)

				if ((i != x || j != y) && board[i][j] == board[x][y])

					return false;

		return true;

	}
}

```

# Count and Say，  报数

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.countAndSay(5));
	}

	/**
	 * 给定 n = 5, 返回 "111221"
	 * <br>
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/string/count-and-say.html
	 * @param n
	 * @return
	 */
	public String countAndSay(int n) {

		String s = "1";

		while (--n > 0)

			s = getNext(s);

		return s;

	}

	String getNext(final String s) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length();) {

			int j = notEqual(s, i);

			sb.append(j - i);

			sb.append(s.charAt(i));

			i = j;

		}

		return sb.toString();

	}

	// find the first char that not equal to fromIndex

	private static int notEqual(final String s, int fromIndex) {

		final char target = s.charAt(fromIndex);

		int i = fromIndex;

		for (; i < s.length(); ++i) {

			if (s.charAt(i) != target)
				break;

		}

		return i;

	}
}

```

# Combination Sum， 数字组合

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums={2,3,6,7};
		int target = 7;
		System.out.println(urlTest.combinationSum(nums, target));
	}

	
	/**
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/dfs/combination-sum.html
	 * <br>
	 * 给出候选数组[2,3,6,7]和目标数字7, 返回 [[7],[2,2,3]]
	 * <br>
	 * @param nums
	 * @param target
	 * @return
	 */
	public List<List<Integer>> combinationSum(int[] nums, int target) {

		Arrays.sort(nums);

		List<List<Integer>> result = new ArrayList<>(); // 最终结果

		List<Integer> path = new ArrayList<>(); // 中间结果

		dfs(nums, path, result, target, 0);

		return result;

	}

	private static void dfs(int[] nums, List<Integer> path,

		List<List<Integer>> result, int gap, int start){

		if (gap == 0) {

		// 找到一个合法解

		result.add(new ArrayList<Integer>(path));

		return;

		}

		for (int i = start; i < nums.length; i++) { // 扩展状态

		if (gap < nums[i]) return; // 剪枝

		path.add(nums[i]); // 执行扩展动作

		dfs(nums, path, result, gap - nums[i], i);

		path.remove(path.size() - 1);// 撤销动作

		}

		}
}
```

# Combination Sum II ，数字组合II

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 10,1,2,7,6,1,5 };
		int target = 8;
		System.out.println(urlTest.combinationSum2(nums, target));
	}

	/**
	 * 算法精粹：
	 * https://soulmachine.gitbooks.io/algorithm-essentials/content/java/dfs/combination-sum-ii.html <br>
	 * 不允许重复 <br>
	 * 
	 * @param nums
	 * @param target
	 * @return
	 */
	public List<List<Integer>> combinationSum2(int[] nums, int target) {

		Arrays.sort(nums); // 跟第 50 行配合，

		// 确保每个元素最多只用一次

		List<List<Integer>> result = new ArrayList<>();

		List<Integer> path = new ArrayList<>();

		dfs(nums, path, result, target, 0);

		return result;

	}

	// 使用nums[start, nums.size())之间的元素，能找到的所有可行解

	private static void dfs(int[] nums, List<Integer> path,

			List<List<Integer>> result, int gap, int start) {

		if (gap == 0) {

			// 找到一个合法解

			result.add(new ArrayList<>(path));

			return;

		}

		int previous = -1;

		for (int i = start; i < nums.length; i++) {

			// 如果上一轮循环已经使用了nums[i]，则本次循环就不能再选nums[i]，

			// 确保nums[i]最多只用一次

			if (previous == nums[i])
				continue;

			if (gap < nums[i])
				return;

			// 剪枝

			previous = nums[i];

			path.add(nums[i]);

			dfs(nums, path, result, gap - nums[i], i + 1);

			path.remove(path.size() - 1);

			// 恢复环境

		}

	}
}

```

# First Missing Positive， 丢失第一个整数

- [常见排序算法【归档】](http://bubkoo.com/2014/01/17/sort-algorithm/archives/)



```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 1, 2, 0 };
		System.out.println(urlTest.firstMissingPositive(nums));
	}

	/**算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/sorting/bucket-sort/first-missing-positive.html
	 * <br>
	 * 桶排序
	 * <br>
	 * 如果给出 [1,2,0], return 3, 如果给出 [3,4,-1,1], return 2
	 * @param nums
	 * @return
	 */
	public int firstMissingPositive(int[] nums) {

		bucket_sort(nums);

		for (int i = 0; i < nums.length; ++i)

			if (nums[i] != (i + 1))

				return i + 1;

		return nums.length + 1;

	}

	private static void bucket_sort(int[] A) {

		final int n = A.length;

		for (int i = 0; i < n; i++) {

			while (A[i] != i + 1) {

				if (A[i] < 1 || A[i] > n || A[i] == A[A[i] - 1])

					break;

				// swap

				int tmp = A[i];

				A[i] = A[tmp - 1];

				A[tmp - 1] = tmp;

			}

		}

	}
}

```

# Trapping Rain Water， 接雨水

![](rainwatertrap.png)

给出 n 个非负整数，代表一张X轴上每个区域宽度为 1 的海拔图, 计算这个海拔图最多能接住多少（面积）雨水。

如上图所示，海拔分别为 [0,1,0,2,1,0,1,3,2,1,2,1], 返回 6.

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 0,1,0,2,1,0,1,3,2,1,2,1};
		System.out.println(urlTest.trap(nums));
	}

	/**
	 * 算法精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/linear-list/array/trapping-rain-water.html
	 * @param A
	 * @return
	 */
	public int trap(int[] A) {

		final int n = A.length;

		int peak_index = 0; // 最高的柱子，将数组分为两半

		for (int i = 0; i < n; i++)

			if (A[i] > A[peak_index])
				peak_index = i;

		int water = 0;

		for (int i = 0, left_peak = 0; i < peak_index; i++) {

			if (A[i] > left_peak)
				left_peak = A[i];

			else
				water += left_peak - A[i];

		}

		for (int i = n - 1, right_peak = 0; i > peak_index; i--) {

			if (A[i] > right_peak)
				right_peak = A[i];

			else
				water += right_peak - A[i];

		}

		return water;

	}
}

```

# Multiply Strings，大数相乘 

另一种解法： http://www.cnblogs.com/TenosDoIt/p/3735309.html

另外更高效的计算大整数乘法一般有：（1）karatsuba算法，复杂度为3nlog3≈3n1.585，可以参考[百度百科](https://wapbaike.baidu.com/item/karatsuba乘法?uid=E06CC90AB91510ED1E00FAB0EC7472D8&bd_page_type=1&st=1&step=2&net=0&bk_fr=bk_srch)、[面试题——大整数乘法](http://bbs.byr.cn/#!article/ACM_ICPC/68305)、[乘法算法-Karatsuba算法](http://blog.csdn.net/jiyanfeng1/article/details/8543846)。（2）基于FFT(快速傅里叶变换)的算法，复杂度为o(nlogn), 可以参考[FFT, 卷积, 多项式乘法, 大整数乘法](http://klogk.com/posts/FFT-convolution-polynomial-biginteger/)

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.multiply("289", "785"));
		System.out.println(289 * 785);
	}

	/**
	 * 算法精粹：https://soulmachine.gitbooks.io/algorithm-essentials/content/java/simulation/multiply-strings.html
	 * <br>
	 * 高精度乘法。
	 * <br>常见的做法是将字符转化为一个int，一一对应，形成一个int数组。
	 * <br>但是这样很浪费 空间，一个int32的最大值是 2^{31}-1=2147483647 ，可以与9个字符对应，由于 有乘法，减半，则至少可以与4个字符一一对应。一个int64可以与9个字符对应。
	 * @param num1
	 * @param num2
	 * @return
	 */
	public String multiply(String num1, String num2) {

		BigInt bigInt1 = new BigInt(num1);

		BigInt bigInt2 = new BigInt(num2);

		BigInt result = BigInt.multiply(bigInt1, bigInt2);

		return result.toString();

	}

	// 一个字符对应一个int

	static class BigInt {

		private final int[] d;

		public BigInt(String s) {

			this.d = fromString(s);

		}

		public BigInt(int[] d) {

			this.d = d;

		}

		private static int[] fromString(String s) {

			int[] d = new int[s.length()];

			for (int i = s.length() - 1, j = 0; i >= 0; --i)

				d[j++] = Character.getNumericValue(s.charAt(i));

			return d;

		}

		@Override

		public String toString() {

			final StringBuilder sb = new StringBuilder();

			for (int i = d.length - 1; i >= 0; --i) {

				sb.append(Character.forDigit(d[i], 10));

			}

			return sb.toString();

		}

		public static BigInt multiply(BigInt x, BigInt y) {

			int[] z = new int[x.d.length + y.d.length];

			for (int i = 0; i < x.d.length; ++i) {

				for (int j = 0; j < y.d.length; ++j) {

					z[i + j] += x.d[i] * y.d[j];

					z[i + j + 1] += z[i + j] / 10;

					z[i + j] %= 10;

				}

			}

			// find the first 0 from right to left

			int i = z.length - 1;

			for (; i > 0 && z[i] == 0; --i)
				/* empty */;

			if (i == z.length - 1) {

				return new BigInt(z);

			} else { // make a copy

				int[] tmp = new int[i + 1];

				System.arraycopy(z, 0, tmp, 0, i + 1);

				return new BigInt(tmp);

			}

		}

	}
}

```



# Wildcard Matching， 通配符匹配

```java
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		System.out.println(urlTest.isMatch("aa", "*"));
	}

	/**
	 * isMatch("aa","a") → false <br>
	 * isMatch("aa","aa") → true <br>
	 * isMatch("aaa","aa") → false <br>
	 * isMatch("aa", "*") → true <br>
	 * isMatch("aa", "a*") → true <br>
	 * isMatch("ab", "?*") → true <br>
	 * isMatch("aab", "c*a*b") → false <br>
	 * 编程精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/string/wildcard-matching.html
	 * @param s
	 * @param p
	 * @return
	 */
    public boolean isMatch(String s, String p) {
        int i = 0, j = 0;
        int ii = -1, jj = -1;
        while (i < s.length()) {
            if (j < p.length() && p.charAt(j) == '*') {
                // skip continuous '*'
                while (j < p.length() && p.charAt(j) == '*') ++j;
                if (j == p.length()) return true;
                ii = i;
                jj = j;
            }
            if (j < p.length() && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                ++i; ++j;
            } else {
                if (ii == -1) return false;
                ++ii;
                i = ii;
                j = jj;
            }
        }
        // skip continuous '*'
        while (j < p.length() && p.charAt(j) == '*') ++j;
        return i == s.length() && j == p.length();
    }

}

```

# Jump Game II， 跳跃游戏 II

```
package com.imop.lj.test.battle;

import java.io.IOException;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = {2,3,1,1,4};
		System.out.println(urlTest.jump(nums));
	}

	/**
	 * 给出数组A = [2,3,1,1,4]，最少到达数组最后一个位置的跳跃次数是2(从数组下标0跳一步到数组下标1，然后跳3步到数组的最后一个位置，一共跳跃2次)
	 * <br>
	 * 编程精粹： https://soulmachine.gitbooks.io/algorithm-essentials/content/java/greedy/jump-game-ii.html
	 * <br>
	 * @param nums
	 * @return
	 */
	public int jump(int[] nums) {

		int result = 0;

		// the maximum distance that has been reached

		int last = 0;

		// the maximum distance that can be reached by using "ret+1" steps

		int cur = 0;

		for (int i = 0; i < nums.length; ++i) {

			if (i > last) {

				last = cur;

				++result;

			}

			cur = Math.max(cur, i + nums[i]);

		}

		return result;

	}

}

```

# Permutations，全排列

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 1, 2, 3};
		System.out.println(urlTest.permute(nums));
	}

	/**
	 * 九章算法：http://www.jiuzhang.com/solutions/permutations/
	 * <br>
	 * 给出一个列表[1,2,3]，其全排列为：
	 * <pre>
		[
		  [1,2,3],
		  [1,3,2],
		  [2,1,3],
		  [2,3,1],
		  [3,1,2],
		  [3,2,1]
		]
		</pre>
	 * @param nums
	 * @return
	 */
	public List<List<Integer>> permute(int[] nums) {
		ArrayList<List<Integer>> permutations = new ArrayList<List<Integer>>();
		if (nums == null) {

			return permutations;
		}

		if (nums.length == 0) {
			permutations.add(new ArrayList<Integer>());
			return permutations;
		}

		int n = nums.length;
		ArrayList<Integer> stack = new ArrayList<Integer>();

		stack.add(-1);
		while (stack.size() != 0) {
			Integer last = stack.get(stack.size() - 1);
			stack.remove(stack.size() - 1);

			// increase the last number
			int next = -1;
			for (int i = last + 1; i < n; i++) {
				if (!stack.contains(i)) {
					next = i;
					break;
				}
			}
			if (next == -1) {
				continue;
			}

			// generate the next permutation
			stack.add(next);
			for (int i = 0; i < n; i++) {
				if (!stack.contains(i)) {
					stack.add(i);
				}
			}

			// copy to permutations set
			ArrayList<Integer> permutation = new ArrayList<Integer>();
			for (int i = 0; i < n; i++) {
				permutation.add(nums[stack.get(i)]);
			}
			permutations.add(permutation);
		}

		return permutations;
	}

}

```

# Permutations II, 全排列II

```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlTest {

	public static void main(String[] args) throws IOException {

		UrlTest urlTest = new UrlTest();
		int[] nums = { 1, 2, 2 };
		System.out.println(urlTest.permuteUnique(nums));
	}

	/**
	 * 给出一个具有重复数字的列表，找出列表所有不同的排列
	 * 
	 * @param nums:
	 *            A list of integers.
	 * @return: A list of unique permutations.
	 */
	public List<List<Integer>> permuteUnique(int[] nums) {

		ArrayList<List<Integer>> results = new ArrayList<List<Integer>>();

		if (nums == null) {
			return results;
		}

		if (nums.length == 0) {
			results.add(new ArrayList<Integer>());
			return results;
		}

		Arrays.sort(nums);
		ArrayList<Integer> list = new ArrayList<Integer>();
		int[] visited = new int[nums.length];
		for (int i = 0; i < visited.length; i++) {
			visited[i] = 0;
		}

		helper(results, list, visited, nums);
		return results;
	}

	public void helper(ArrayList<List<Integer>> results, ArrayList<Integer> list, int[] visited, int[] nums) {

		if (list.size() == nums.length) {
			results.add(new ArrayList<Integer>(list));
			return;
		}

		for (int i = 0; i < nums.length; i++) {
			if (visited[i] == 1 || (i != 0 && nums[i] == nums[i - 1] && visited[i - 1] == 0)) {
				continue;
			}
			/*
			 * 上面的判断主要是为了去除重复元素影响。 比如，给出一个排好序的数组，[1,2,2]，那么第一个2和第二2如果在结果中互换位置，
			 * 我们也认为是同一种方案，所以我们强制要求相同的数字，原来排在前面的，在结果
			 * 当中也应该排在前面，这样就保证了唯一性。所以当前面的2还没有使用的时候，就 不应该让后面的2使用。
			 */
			visited[i] = 1;
			list.add(nums[i]);
			helper(results, list, visited, nums);
			list.remove(list.size() - 1);
			visited[i] = 0;
		}
	}

}

```

# Rotate Image， 旋转图像

```java

```

