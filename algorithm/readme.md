用java实现的一些常见算法，工作和面试中会遇到。

- 设有一组N个数，而要确定其中第k个最大者
- 求多个点之间的最短路径, Floyd算法
```java
package com.imop.lj.test.battle;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;

import com.google.common.collect.Lists;

public class UrlTest2 {

	
	
	static MultiKeyMap pathMap = new MultiKeyMap();
	
	static void Floyd(int[][] cost, int v)
    {
        int n = cost[0].length;  // 获取顶点个数
        int[][] A = new int[n][n];   // 存放最短路径长度
        int[][] path = new int[n][n];// 存放最短路径信息

        for (int i = 0; i < n; i++)  
        {
            for (int j = 0; j < n; j++)
            {
                // 辅助数组A和path的初始化
                A[i][j] = cost[i][j];
                path[i][j] = -1;
            }
        }

        // Flyod算法核心代码部分
        for (int k = 0; k < n; k++)
        {
            for (int i = 0; i < n; i++)
            {
                for (int j = 0; j < n; j++)
                {
                    // 如果存在中间顶点K的路径
                    if (i != j && A[i][k] != 0 && A[k][j] != 0)
                    {
                        // 如果加入中间顶点k后的路径更短
                        if (A[i][j] == 0 || A[i][j] > A[i][k] + A[k][j])
                        {
                            // 使用新路径代替原路径
                            A[i][j] = A[i][k] + A[k][j];
                            path[i][j] = k;
                        }
                    }
                }
            }
        }

        // 打印最短路径及路径长度
        for (int i = 0; i < n; i++)
        {
            for (int j = 0; j < n; j++)
            {
                if (A[i][j] == 0)
                {
                    if (i != j)
                    {
                    	System.out.println(MessageFormat.format("从{0}到{1}没有路径!", i + 100, j + 100));
                    	 pathMap.put(i + 100, j + 100, Lists.newArrayListWithCapacity(0));
                    }
                }
                else
                {
                	 List<Integer> pathLst = Lists.newArrayList();
                	
                	System.out.println(MessageFormat.format("从{0}到{1}的路径为：", i + 100, j + 100));
                    System.out.print(i + 100 + "→");
                    pathLst.add(i + 100);
                    pathMap.put(i + 100, j + 100, pathLst);
                    // 使用递归获取指定顶点的路径
                    GetPath(path, i, j, pathLst);
                    System.out.print(j + 100 + "     ");
                    pathLst.add(j + 100);
                    pathMap.put(i + 100, j + 100, pathLst);
                    System.out.println(MessageFormat.format("路径长度为：{0}", A[i][j]));
                }
            }
            System.out.println();
        }
    }

    static void GetPath(int[][] path, int i, int j, List<Integer> pathLst)
    {
        int k = path[i][j];
        if (k == -1)
        {
            return;
        }

        GetPath(path, i, k, pathLst);
        System.out.print(k + 100 + "→");
        pathLst.add(k + 100);
        GetPath(path, k, j, pathLst);
    }
    
    static void FloydTest()
    {
        int[][] cost = new int[12][12];
        // 初始化邻接矩阵
        cost[1][3] = 1;
        cost[1][6] = 1;
        
        cost[2][3] = 1;
        cost[2][6] = 1;
        cost[2][7] = 1;
        cost[2][11] = 1;
        
        cost[3][1] = 1;
        cost[3][2] = 1;
        
        cost[4][10] = 1;
        cost[4][11] = 1;
        
        cost[5][7] = 1;
        cost[5][8] = 1;
        cost[5][9] = 1;
        
        cost[6][1] = 1;
        cost[6][2] = 1;
        
        cost[7][2] = 1;
        cost[7][5] = 1;
        
        cost[8][5] = 1;
        cost[8][9] = 1;
        
        cost[9][5] = 1;
        cost[9][8] = 1;
        
        cost[10][4] = 1;
        cost[10][11] = 1;
        
        cost[11][2] = 1;
        cost[11][4] = 1;
        cost[11][10] = 1;
        // 使用Flyod算法计算最短路径
        Floyd(cost, 0);
    }
    
    public static void main(String[] args) {
    	long start = System.currentTimeMillis();
		FloydTest();
		long end = System.currentTimeMillis();
		System.out.println(end - start + "ms");
		System.out.println(pathMap);
	}
}

```

- 求多个点之间的最短路径,Dijkstra算法

```java
package com.imop.lj.test.battle;

import java.text.MessageFormat;

public class UrlTest
{
    
    
    
    static void Dijkstra(int[][] cost, int v)
        {
            int n = cost[0].length; // 计算顶点个数
            int[] s = new int[n];      // 集合S
            int[] dist = new int[n];   // 结果集
            int[] path = new int[n];   // 路径集

            for (int i = 0; i < n; i++)
            {
                // 初始化结果集
                dist[i] = cost[v][i];
                // 初始化路径集
                if (cost[v][i] > 0)
                {
                    // 如果源点与顶点存在边
                    path[i] = v;
                }
                else
                {
                    // 如果源点与顶点不存在边
                    path[i] = -1;
                }
            }

            s[v] = 1;   // 将源点加入集合S
            path[v] = 0;

            for (int i = 0; i < n; i++)
            {
                int u = 0;  // 指示剩余顶点在dist集合中的最小值的索引号
                int minDis = Integer.MAX_VALUE; // 指示剩余顶点在dist集合中的最小值大小

                // 01.计算dist集合中的最小值
                for (int j = 0; j < n; j++)
                {
                    if (s[j] == 0 && dist[j] > 0 && dist[j] < minDis)
                    {
                        u = j;
                        minDis = dist[j];
                    }
                }

                s[u] = 1; // 将抽出的顶点放入集合S中

                // 02.计算源点经过顶点u到其余顶点的距离
                for (int j = 0; j < n; j++)
                {
                    // 如果顶点不在集合S中
                    if (s[j] == 0)
                    {
                        // 加入的顶点如与其余顶点存在边，并且重新计算的值小于原值
                        if (cost[u][j] > 0 && (dist[j] == 0 || dist[u] + cost[u][j] < dist[j]))
                        {
                            // 计算更小的值代替原值
                            dist[j] = dist[u] + cost[u][j];
                            path[j] = u;
                        }
                    }
                }
            }


            // 打印源点到各顶点的路径及距离
            for (int i = 0; i < n; i++)
            {
                if (s[i] == 1)
                {
                	System.out.println(MessageFormat.format("从{0}到{1}的最短路径为：", v, i));
                    System.out.print(v + "→");
                    // 使用递归获取指定顶点在路径上的前一顶点
                    GetPath(path, i, v);
                    System.out.print(i + "SUM:");
                    System.out.println(MessageFormat.format("路径长度为：{0}", dist[i]));
                }
            }
        }

        static void GetPath(int[] path, int i, int v)
        {
            int k = path[i];
            if (k == v)
            {
                return;
            }

            GetPath(path, k, v);
            System.out.print(k + "→");
        }
        
        
        static void DijkstraTest()
        {
            int[][] cost = new int[5][5];
            // 初始化邻接矩阵
            cost[0][1] = 1;
            cost[0][3] = 1;
            cost[0][4] = 1;
            cost[1][0] = 1;
            cost[1][2] = 1;
            cost[2][1] = 1;
            cost[2][3] = 1;
            cost[2][4] = 1;
            cost[3][0] = 1;
            cost[3][2] = 1;
            cost[3][4] = 1;
            cost[4][0] = 1;
            cost[4][2] = 1;
            cost[4][3] = 1;
            // 使用Dijkstra算法计算最短路径
            Dijkstra(cost, 0);
        }
        
        
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		DijkstraTest();
		long end = System.currentTimeMillis();
		System.out.println(end - start + "ms");
	}
}

```
