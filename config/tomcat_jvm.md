- [linux tomcat jvm�ڴ��Ż�]](http://blog.163.com/agw_slsyn/blog/static/30915112201152911558409)

> �����������������Ƚϴ��ʱ��,��Ҫ�����������ڴ�,������linux�������Ĳο�:

����ͨ������`top`-->`1`,�鿴���������ڴ��CPU

```
top - 16:42:38 up 408 days, 23:50,  1 user,  load average: 0.00, 0.00, 0.01
Tasks: 146 total,   1 running, 145 sleeping,   0 stopped,   0 zombie
Cpu0  :  0.0%us,  0.3%sy,  0.0%ni, 99.7%id,  0.0%wa,  0.0%hi,  0.0%si,  0.0%st
Cpu1  :  0.0%us,  0.3%sy,  0.0%ni, 99.7%id,  0.0%wa,  0.0%hi,  0.0%si,  0.0%st
Cpu2  :  0.7%us,  0.3%sy,  0.0%ni, 99.0%id,  0.0%wa,  0.0%hi,  0.0%si,  0.0%st
Cpu3  :  0.3%us,  0.3%sy,  0.0%ni, 99.3%id,  0.0%wa,  0.0%hi,  0.0%si,  0.0%st
Mem:  12195544k total, 11271504k used,   924040k free,   105816k buffers
Swap:  2097144k total,   190756k used,  1906388k free,  9299828k cached

```

֮���趨tomcat/bin/catalina.sh �ļ�
```
JAVA_OPTS="-Xms256m -Xmx512m -Xss1024K -XX:PermSize=128m -XX:MaxPermSize=256m" 
```