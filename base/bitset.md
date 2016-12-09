- 游戏项目中用到bitset 的地方
  - 经验模板中校验级别有没有重复的和是不是连续的（get和set方法）
  - 进入临时背包提示和临时背包已满提示，通过两个整型变量（get和set方法）
  - 角色的一、二级或抗性的变化（!isEmpty()不为空的情况，说明就是有变化）
- 不错的分析文章
  - [java BitSet](http://blog.csdn.net/haojun186/article/details/8482343)
  - [java.util.BitSet的用法详解](http://www.52ij.com/jishu/java/99004.html)
- 必须要看源码： 
- 以下是自己简单的测试





```java
package com.imop.lj.test.battle;

import java.io.IOException;
import java.util.BitSet;


public class UrlTest {

	public static void main(String[] args) throws IOException {

		BitSet bs = new BitSet(3);
		bs.set(1);
		bs.set(2);
		bs.set(3);
		

		if(bs.get(1)){
			System.out.println("1");
		}
		
		if(bs.get(4)){
			System.out.println("4");
		}

	}
}


//结果只输出1，而没有输出4
```
