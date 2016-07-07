package zookeeperTest1.watchAndLock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;


public class Linktest {
	protected static String hosts = "172.28.20.98:2181,172.28.20.100:2181,172.28.20.101:2181";
//	protected static String hosts = "172.28.20.100:2181";



	private static int SESSION_TIMEOUT = 40000;
	private static CountDownLatch connectedSignal = new CountDownLatch(1);
	
	protected static ZooKeeper zk;
	private static String nodePath = "/linktest2";
    private static CyclicBarrier cb;
    
	/** 连接数 */
	static int sessions  = 800;
	static int totalNode = 1000;
	static int size = 0;
	static byte[] testdata;
	static int watchNode = 5;
	
	
	static long startTime = 0;
	static long endTime = 0;
	static int watchcount = 0;
	static long startWatchTime = 0;
	static long endWatchTime = 0;
	
	static Boolean setTime = false;

//	private static CountDownLatch acreateLatch = new CountDownLatch(totalNode);
//	private static CountDownLatch asetLatch = new CountDownLatch(totalNode);
	private static CountDownLatch allLatch = new CountDownLatch(watchNode*sessions*sessions);
	private static CountDownLatch allsetwatchLatch = new CountDownLatch(watchNode*sessions);
	private static CountDownLatch watchNotifyLatch = new CountDownLatch(watchNode*sessions*sessions);

	public static void main(String[] args) throws Exception {
		//TODO
		PropertyConfigurator.configure("resources/log4j.properties");
		
		/** 创建根节点 */
		Linktest test = new Linktest();
		test.connect();
		//test.create(nodePath, "zookeeper test root".getBytes());
		
		/** 初始化数据 */
		testdata = new byte[size];
	    for(int i=0;i < size;i++){
	        testdata[i] = 't';
	    }
	    

	    cb = new CyclicBarrier(sessions, new Runnable () {
            public void run() {
                System.out.println("All sessions established: "+ cb.getParties());
                startTime = System.currentTimeMillis();
            }
        });
	    
	    CreateThread[] threadArray = new CreateThread[sessions];
		for (int i = 0; i < sessions; i++) {
			threadArray[i] = new CreateThread(i);
			threadArray[i].start();
		}
		
		for (int i = 0; i < sessions; i++) {
			threadArray[i].join();
		}
		
		endTime  = System.currentTimeMillis();

		test.close();
		long duration = endTime-startTime;
		int ops = (int)((totalNode*sessions)/duration);
		System.out.println("watch test finish, duration = " + (endTime-startWatchTime));
		System.out.println("test finish, duration = " + duration + " ops:" + ops);
		
	}

	public void connect() throws Exception {
		zk = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnWatcher());
		// 等待连接完成
		connectedSignal.await();
	}
	
	private static void create(String Path, byte[] data) throws Exception {
		Stat stat = null; 
		stat = zk.exists(Path, false);
		if (null != stat) {
			List<String> pathList = zk.getChildren(Path, false);
	        for (String subNode : pathList) {
	        	zk.delete(Path + "/" + subNode, -1);
	        }
			zk.delete(Path, -1);
		}
		
		zk.create(Path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}


	public void close() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static class CreateThread extends Thread {
		private CountDownLatch connectedLatch = new CountDownLatch(1);
		private CountDownLatch acreateLatch = new CountDownLatch(totalNode);
		private CountDownLatch asetLatch = new CountDownLatch(totalNode);
		private CountDownLatch asetwatchLatch = new CountDownLatch(watchNode);
		private CountDownLatch agetLatch = new CountDownLatch(totalNode);
		private CountDownLatch adeleteLatch = new CountDownLatch(totalNode);
		private CountDownLatch awatchLatch = new CountDownLatch(watchNode*sessions);
		private CountDownLatch waitLatch = new CountDownLatch(watchNode*sessions);
		
		private long time;
		private int watchCount = 0;
		
		private ZooKeeper zkConnect;
		int index;
	
		CreateThread(int num) {
			this.index = num;
		}

		public void run() {
			try {
				zkConnect = new ZooKeeper(hosts, SESSION_TIMEOUT, new ConnectWatcher());
				connectedLatch.await();
				long sessionID = zkConnect.getSessionId();
				System.out.printf("thread" + this.index + "connected session id %#x\r\n", sessionID);
				try {
					cb.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}

				String testPath = nodePath + "/" + "session" + index;
				
				Thread current = Thread.currentThread();
				
				
				
				
				/** Sync create*/
//				long begin = System.currentTimeMillis();
//				CreateNode(zkConnect, totalNode, testPath);
//				long end = System.currentTimeMillis();
//				stat.createTime = end - begin;
//				System.out.println("thread" + this.index + " sync create use time : " + stat.createTime);
				
				/** Async create*/
				long beginAsync = System.currentTimeMillis();
				String createCtx = String.valueOf(index);
				CreateNodeAsync(zkConnect, totalNode, testPath, createCtx);
				acreateLatch.await();
				long endAsync = System.currentTimeMillis();
				time = endAsync - beginAsync;
				System.out.println("thread" + this.index + " async create use time : " + time);

				/** Async set data*/
//				long beginAsyncCreate = System.currentTimeMillis();
//				SetNodeAsync(zkConnect, totalNode, testPath);
//				asetLatch.await();
//				long endAsyncCreate = System.currentTimeMillis();
//				time = endAsyncCreate - beginAsyncCreate;
//				System.out.println("thread" + this.index + " async set use time : " + time);
//				
				/** Async get data*/
//				long beginAsyncGet = System.currentTimeMillis();
//				String ctx = Long.toString(sessionID, 16);
//				
//				Map<String, Object> ctxMap = new HashMap<String, Object>();
//				ctxMap.put("sessionID", ctx);
//				ctxMap.put("threadID", current.getId());
//				ctxMap.put("start", Long.toString(beginAsyncGet));
//				
//				GetNodeAsync(zkConnect, totalNode, testPath, ctxMap);
//				agetLatch.await();
//				long endAsyncGet = System.currentTimeMillis();
//				time = endAsyncGet - beginAsyncGet;
//				System.out.println("thread" + this.index + " async get use time : " + time);
//								
				/** Async delete data*/
//				long beginAsyncDelete = System.currentTimeMillis();
//				String delCtx = Long.toHexString(sessionID);
//				DeleteNodeAsync(zkConnect, totalNode, testPath, delCtx);
//				adeleteLatch.await();
//				long endAsyncDelete = System.currentTimeMillis();
//				time = endAsyncDelete - beginAsyncDelete;
//				System.out.println("thread" + this.index + " async delete use time : " + time);
				
				
				/** watch test 
				 * 每个会话watch所有节点*/
//				long beginGetwatch = System.currentTimeMillis();
//				//GetNodeSetWatchAsync(zkConnect, 10, 100, nodePath + "/" + "session");
//				GetNodeSetWatchAsync(zkConnect, watchNode, sessions, nodePath + "/" + "session");
//				//GetNodeAsync(zkConnect, totalNode, testPath);
//				//agetLatch.await();
//				awatchLatch.await();
//				long endGetwatch = System.currentTimeMillis();
//				time = endGetwatch - beginGetwatch;
////				System.out.println("thread" + this.index + " get and set watch use time : " + time);
//				
//				//System.out.println("thread" + this.index + " allLatch : " + allLatch.getCount());
//				allLatch.await();
//				//System.out.println("thread" + this.index + "all watched");
////				
//				
//				
//
//				/** set data to watched path */
////				SetWatchedNodeAsync(zkConnect, watchNode, index, nodePath + "/" + "session");
////				asetwatchLatch.await();
//				SetWatchedNode(zkConnect, watchNode, index, nodePath + "/" + "session");
//				
////				System.out.println("thread" + this.index + " set data to all watched path" + "waitLatch " + waitLatch.getCount());
//				//waitLatch.await();
//				watchNotifyLatch.await();

				
				
				

				zkConnect.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public class ConnectWatcher implements Watcher {
			public void process(WatchedEvent event) {
				if (event.getState() == KeeperState.SyncConnected) {
					connectedLatch.countDown();
				}
			}
		}
		
		public void CreateNodeAsync(ZooKeeper zkConnect, Integer nodeCount, String rootPath, String ctx) {
			for (int i = 0; i < nodeCount; i++) {
				zkConnect.create(rootPath + "_" + i, testdata, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new AsyncCreateCallback(), ctx);
			}
		}
		
		public void SetNodeAsync(ZooKeeper zkConnect, Integer nodeCount, String rootPath) {
			for (int i = 0; i < nodeCount; i++) {
				zkConnect.setData(rootPath + "_" + i, testdata, -1, new AsyncSetCallback(), "Async Set Data");
			}
		}
		
		public void SetWatchedNodeAsync(ZooKeeper zkConnect, Integer nodeCount, Integer index, String rootPath) {
			for (int j = 0; j < nodeCount; j++) {
				zkConnect.setData(rootPath + index + "_" + j, testdata, -1, new AsyncSetCallback(), "Async Set Data");
			}
		}
		
		public void SetWatchedNode(ZooKeeper zkConnect, Integer nodeCount, Integer index, String rootPath) {
			for (int j = 0; j < nodeCount; j++) {
				try {
					zkConnect.setData(rootPath + index + "_" + j, testdata, -1);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}
				//waitLatch.countDown();
			}
		}
		
		public void GetNodeAsync(ZooKeeper zkConnect, Integer nodeCount, String rootPath, Object ctx) {
			for (int i = 0; i < nodeCount; i++) {
				zkConnect.getData(rootPath + "_" + i, new GetWatcher(), new AsyncGetCallback(), ctx);
//				zkConnect.getData(rootPath + "_" + i, null, new AsyncGetCallback(), "Async Get Data");
			}
		}
		
		public void GetNodeSetWatchAsync(ZooKeeper zkConnect, Integer nodeCount, Integer session, String rootPath) {
			for (int i = 0; i < session; i++) {
				for (int j = 0; j < nodeCount; j++) {
					zkConnect.getData(rootPath + i + "_" + j, new WatchTestWatcher(), new AsyncWatchCallback(), "Async Get Data");
				}
			}
		}
		
		public void DeleteNodeAsync(ZooKeeper zkConnect, Integer nodeCount, String rootPath, String ctx) {
			for (int i = 0; i < nodeCount; i++) {
				//zkConnect.getData(rootPath + "_" + i, new GetWatcher(), new AsyncGetCallback(), "Async Get Data");
				zkConnect.delete(rootPath + "_" + i, -1, new AsyncDeleteCallback(), ctx);
			}
		}
		
		public class AsyncCreateCallback implements AsyncCallback.StringCallback {
			@Override
	        public void processResult(int rc, String path, Object ctx, String name) {
				if (0 != rc) {
					System.out.println("create error rc: " + rc);
				}
	            acreateLatch.countDown();
//	            System.out.println(ctx + "acreateLatch = " + acreateLatch.getCount());
	        }
		}
		
		public class AsyncSetCallback implements AsyncCallback.StatCallback {
			@Override
	        public void processResult(int rc, String path, Object ctx, Stat stat) {
				if (0 != rc) {
					System.out.println("set error rc: " + rc);
				}
				asetLatch.countDown();
				//asetwatchLatch.countDown();
	        }
		}
		
		public class AsyncGetCallback implements AsyncCallback.DataCallback {
			@Override
			public void processResult(int rc, String path, Object ctx, byte data[], Stat stat) {
				if (0 != rc) {
					long current = System.currentTimeMillis();
					Map ctxMap = (HashMap<String, String>)ctx;
					long start = Long.parseLong((String) ctxMap.get("start"));
					String sessionID = (String) ctxMap.get("sessionID");
					long threadID = (long) ctxMap.get("threadID");
					long time = current - start;

					System.out.println("get error rc: " + rc + " sessionID : " + sessionID + "/" + threadID + "/time:" + time);
					
				}
				agetLatch.countDown();
			}
		}
		
		public class AsyncWatchCallback implements AsyncCallback.DataCallback {
			@Override
			public void processResult(int rc, String path, Object ctx, byte data[], Stat stat) {
				awatchLatch.countDown();
				allLatch.countDown();
			}
		}
		
		public class GetWatcher implements Watcher {
			public void process(WatchedEvent event) {

			}
		}
		
		public class WatchTestWatcher implements Watcher {
			public void process(WatchedEvent event) {
				//watchCount++;
				waitLatch.countDown();
				watchNotifyLatch.countDown();
				if (false == setTime) {
					startWatchTime = System.currentTimeMillis();
					System.out.println("startWatchTime = " + startWatchTime);
					setTime = true;
				}
				System.out.println("watchNotifyLatch = " + watchNotifyLatch.getCount());
			}
		}
		
		public class AsyncDeleteCallback implements AsyncCallback.VoidCallback {
			@Override
			public void processResult(int rc, String path, Object ctx) {
				if (0 != rc) {
					System.out.println(ctx + " delete error rc: " + rc);
				}

				adeleteLatch.countDown();
//				if (0 == adeleteLatch.getCount()) {
//					System.out.println(ctx + "adeleteLatch " + adeleteLatch.getCount());	
//				}
				
			}
		}
	}
	
	public static void CreateNode(ZooKeeper zkConnect, Integer nodeCount, String rootPath) {
		for (int i = 0; i < nodeCount; i++) {
			try {
				zkConnect.create(rootPath + "_" + i, testdata, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	
	public static class ConnWatcher implements Watcher {
		public void process(WatchedEvent event) {
			if (event.getState() == KeeperState.SyncConnected) {
				connectedSignal.countDown();
			}
		}
	}
	
	
}
