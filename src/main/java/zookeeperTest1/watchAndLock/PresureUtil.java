package zookeeperTest1.watchAndLock;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import zookeeperTest1.watchAndLock.Linktest.ConnWatcher;



public class PresureUtil {
	
	private static CountDownLatch connectedLatch = new CountDownLatch(1);
	
	
	public static class ConnWatcher implements Watcher {
		public void process(WatchedEvent event) {
			if (event.getState() == KeeperState.SyncConnected) {
				connectedLatch.countDown();
			}
		}
	}

}
