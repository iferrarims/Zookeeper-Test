package zookeeperTest1.zookeeperTest1;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
/** 


/**
 * 
 * 使用curator监听zookeeper节点
 * 
 * @author qindongliang
 * **/
public class CuratorWatchTest {
	
	public static void main(String[] args) throws Exception {  
		 RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		 CuratorFramework client = CuratorFrameworkFactory.newClient("172.28.6.131:2181,172.28.6.132:2181,172.28.6.133:2181", 5000,3000,retryPolicy);
	     // 启动 上面的namespace会作为一个最根的节点在使用时自动创建  
	     client.start();  
	  
	     // 创建一个节点  
	     client.create().forPath("/head", new byte[0]);  
	  
	     // 异步地删除一个节点  
	     client.delete().inBackground().forPath("/head");  
	  
	     // 创建一个临时节点  
	     client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/head/child", new byte[0]);  
	  
	     // 取数据  
	     client.getData().watched().inBackground().forPath("/test");  
	  
	     String path = "/test_path";  
	     // 检查路径是否存在  
	     client.checkExists().forPath(path);  
	  
	     // 异步删除  
	     client.delete().inBackground().forPath("/head");  
	  
	     // 注册观察者，当节点变动时触发  
//	     client.getData().usingWatcher(new Watcher() {  
//	         public void process(WatchedEvent event) {  
//	             System.out.println("node is changed");  
//	         }  
//	     }).inBackground().forPath("/test");  
	  
	     // 结束使用  
	     client.close();  
	 }

}