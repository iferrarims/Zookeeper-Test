package zookeeperTest1.watchAndLock;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

import org.apache.zookeeper.ZooKeeper;

import zookeeperTest1.watchAndLock.PresureUtil.ConnWatcher;

public class PresureTest {
	
	private static String hosts = "172.28.20.98:2181,172.28.20.100:2181,172.28.20.101:2181";
	private static ZooKeeper zk;
	
	public static void main(String[] args) throws Exception {
		zk = new ZooKeeper(hosts, 5000, new ConnWatcher());
	}
}
