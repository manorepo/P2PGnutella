package gnutellaP2P;

public class PerformanceLauncher {
	public static void main(String args[]) {
	//	int noofPeersToSearch = Integer.parseInt(args[0]);
		int noofPeersToSearch=0;
		int count = 0;
		boolean bSearch = false;
		if (++count <= noofPeersToSearch) {
			bSearch = true;
		} else {
			bSearch = false;
		}
	
		if (++count <= noofPeersToSearch) {
			bSearch = true;
		} else {
			bSearch = false;
		}

		
		if (++count <= noofPeersToSearch) {
			bSearch = true;
		} else {
			bSearch = false;
		}

		if (++count <= noofPeersToSearch) {
			bSearch = true;
		} else {
			bSearch = false;
		}
			PerformancePeerClass peer2 = new PerformancePeerClass(2, 8002, "//home//TTU//mkotapat//peer2",
				"CommonFile1.txt", false);
		peer2.start();
		PerformancePeerClass peer3 = new PerformancePeerClass(3, 8003, "//home//TTU//mkotapat//peer3",
				"CommonFile1.txt", false);
		peer3.start();
		PerformancePeerClass peer4 = new PerformancePeerClass(4, 8004, "//home//TTU//mkotapat//peer4",
				"CommonFile1.txt", bSearch);
		peer4.start();
		if (++count <= noofPeersToSearch) {
			bSearch = true;
		} else {
			bSearch = false;
		}

		
		PerformancePeerClass peer5 = new PerformancePeerClass(5, 8005, "//home//TTU//mkotapat//peer5",
				"CommonFile1.txt", bSearch);
		peer5.start();
		PerformancePeerClass peer6 = new PerformancePeerClass(6, 8006, "//home//TTU//mkotapat//peer6",
				"CommonFile1.txt", bSearch);
		peer6.start();
		PerformancePeerClass peer7 = new PerformancePeerClass(7, 8007, "//home//TTU//mkotapat//peer7",
				"CommonFile1.txt", bSearch);
		peer7.start();
		PerformancePeerClass peer8 = new PerformancePeerClass(8, 8008, "//home//TTU//mkotapat//peer8",
				"CommonFile1.txt", bSearch);
		peer8.start();
		PerformancePeerClass peer9 = new PerformancePeerClass(9, 8009, "//home//TTU//mkotapat//peer9",
				"CommonFile1.txt", bSearch);
		peer9.start();
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PerformancePeerClass peer1 = new PerformancePeerClass(1, 8001, "//home//TTU//mkotapat//peer1",
				"CommonFile1.txt", true);
		peer1.start();

	
		
		
	}
}

class PerformancePeerClass extends Thread {
	int peerid;
	int port;
	String sharedDir;
	String searchFileName;
	boolean bPerformSearch = false;

	public PerformancePeerClass(int peerid, int port, String sharedDir, String searchFileName, boolean bPerformSearch) {
		this.peerid = peerid;
		this.port = port;
		this.sharedDir = sharedDir;
		this.searchFileName = searchFileName;
		this.bPerformSearch = bPerformSearch;
	}

	public void run() {

		PerformanceEvaluation p = new PerformanceEvaluation(peerid, port, sharedDir, searchFileName, bPerformSearch);
		long time = p.startProcess();
		System.out.println("peerid "+peerid+" search running time is "+time);

	}
}
