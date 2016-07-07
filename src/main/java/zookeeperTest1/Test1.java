package zookeeperTest1;

public class Test1 {
	private static String regex = "class com.spring.aop.ManagerStatAspect";
	public static void main(String[] args) {
		String s = "10-28 16:09:26 [qtp1633362160-91] INFO  statistic - class com.spring.aop.ManagerStatAspect${\"method\":\"com.systoon.scloud.master.dal.AppFileDalService.putAppFile\",\"ip\":\"172.28.6.131\",\"port\":\"30001\",\"father\":\"com.systoon.scloud.master.service.CommitService.commit/CommitService.java/247\",\"requestIp\":\"\",\"argsMap\":{\"java.lang.String:1908367734\":\"f62815a25338c2ba8c349bdfd7511a48\",\"com.systoon.scloud.common.model.AppFile:683301857\":{\"stoId\":\"b11eeadcf9c10e085781c143dafb3ad01001\",\"fileId\":100126633417,\"appId\":1001,\"ctime\":1445651761870,\"utime\":1446019766046,\"indexAppFileId\":\"b27d015ea9d44a638eb650b470a475d551212aa3f62815a25338c2ba8c349bdfd7511a481001\",\"pub\":0,\"suffix\":\"\",\"trace_reserve_mark\":\"b81a06f1-c618-4b5e-8ddd-2c87493db5d4\"},\"java.lang.String:-555565755\":\"b27d015ea9d44a638eb650b470a475d551212aa3\"},\"processTime\":5,\"time\":1446019766051}";
//		String [] arrStr = s.split("class com.spring.aop.ManagerStatAspect21412");
//		for (int i = 0; i < arrStr.length; i++) {
//			System.out.println(i+"="+arrStr[i]);
//		}
		String []json = s.split(regex);
		System.out.println(s.contains("class com.spring.aop.ManagerStatAspec"));
		System.out.println(s.contains("clas125125125125.ManagerStatAspec"));
		
		
		
//		JSON
	}
}
