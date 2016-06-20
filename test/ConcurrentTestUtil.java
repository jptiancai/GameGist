package atomic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
 
public class ConcurrentTestUtil {
     
    /**
     * ���̲߳���ִ��ĳ������
     * @param concurrentThreads �����߳�������������ģ�Ⲣ�������û���
     * @param times �ܹ�ִ�ж��ٴ�
     * @param task  ����
     * @param resultHandler ���������
     * @param executeTimeoutMillis ִ�������ܳ�ʱ
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public static <T> void concurrentTest(long concurrentThreads, int times, final Callable<T> task,
            ResultHandler<T> resultHandler, long executeTimeoutMillis) 
            throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool((int) concurrentThreads);
        List<Future<T>> results = new ArrayList<Future<T>> (times);
        long startTimeMillis = System.currentTimeMillis();
        for(int i = 0; i < times; i++) {
            results.add(executor.submit(task));  
        }
        executor.shutdown();
//      while(!executor.awaitTermination(1, TimeUnit.SECONDS));   // ÿ��1s�Ӽ���̳߳��Ƿ��ѹر�
        boolean executeCompleteWithinTimeout = executor.awaitTermination(executeTimeoutMillis, 
                TimeUnit.MILLISECONDS);
        if(!executeCompleteWithinTimeout) {
            System.out.println("Execute tasks out of timeout [" + executeTimeoutMillis + "ms]");
             
            /*
             * ȡ����������
             */
            for (Future<T> r : results) {
                r.cancel(true);
            }
        }
        else {
            long totalCostTimeMillis = System.currentTimeMillis() - startTimeMillis;
            // �̳߳ش�ʱ�϶��ѹرգ�����������
            for(Future<T> r: results) {
                /*
                 * r.get()�����ǵȴ�����ִ�н���������ﲻ��Ҫ�ȴ�����Ϊ�����Ѿ����̳߳عر���
                 */
            	if(resultHandler != null) {
            		resultHandler.handle(r.get());
            	}
            }
             
            System.out.println("concurrent threads: " + concurrentThreads + ", times: "
                    + times);  
            System.out.println("total cost time(ms): " + totalCostTimeMillis 
                    + "ms, avg time(ms): " + ((double) totalCostTimeMillis / times));
            System.out.println("tps: " + (double) (times * 1000) / totalCostTimeMillis);
        }
    }
     
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ConcurrentTestUtil.concurrentTest(100, 3000, 
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return "ok";
                    }
                }, 
                new ResultHandler<String>() {
                    @Override
                    public void handle(String result) {
                        System.out.println("result: " + result);
                    }
                }, 6000);
    }
 
}