package gnutellaP2P;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PerformanceEvaluation {
	int peerid;
	int port;
	String sharedDir;
	String searchFileName;
	boolean bPerformSearch=false;
	public PerformanceEvaluation(int peerid,int port,String sharedDir,String searchFileName,boolean bPerformSearch) {
		// TODO Auto-generated constructor stub
		this.peerid=peerid;
		this.port=port;
		this.sharedDir=sharedDir;
		this.searchFileName=searchFileName;
		this.bPerformSearch=bPerformSearch;
	}
public long startProcess(){

		// Function to handle the peer Operations.
		//
		//String sharedDir;
		ArrayList<String> localFiles = new ArrayList<String>();
		List<Thread> threadInstancesList = new ArrayList<Thread>();
		long starttime=0;
		long endtime = 0;
		int iSearchCount=0;
	Peer p=new Peer();
		int searchCounter = 0;
		Boolean bExit = false;
		ArrayList<NeighborPeers> neighborPeers = new ArrayList<NeighborPeers>();
		ArrayList<PeerDetails> searchResult_Peers = new ArrayList<PeerDetails>();
		ArrayList<NeighborConnectionThread> neighborConnThreadList = new ArrayList<NeighborConnectionThread>();
		//
		try {
			//
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			// Collect peerid and port from the user.
		//	System.out.println("Enter the peerid");
			//peerid = Integer.parseInt(br.readLine());
		//	System.out.println("Enter the port");
		//	port = Integer.parseInt(br.readLine());
		//	System.out.println("Session for peer id: " + peerid + " started...");
			//
			// Get the directory to share with other peers.
		//	System.out.println("Enter the shared directory");
		//	sharedDir = br.readLine();
			//
			// Get the filenames that existed locally
			p.getLocalFiles(sharedDir, localFiles);
			//
			// Run peer as a server on a specific port
			p.runPeerAsServer(peerid, port, sharedDir, localFiles);
 if(bPerformSearch){		
					// Search file name option
					// 
	 starttime = System.currentTimeMillis();
	 while(++iSearchCount<=1000){
					// Clear the previous search contents
					neighborPeers.clear();
					threadInstancesList.clear();
					neighborConnThreadList.clear();
					searchResult_Peers.clear();
				
					//
					// Get Neighbor peers
					p.getNeighborPeers(neighborPeers, peerid);
					//
					// Generate unique message id
					++searchCounter;
					String msgId = "Peer"+peerid+".Search" + searchCounter;
					System.out.println("Message id for search: " + msgId);
					//
					// Loop through all the neighbor peers
					for (int i = 0; i < neighborPeers.size(); i++) {
						System.out.println("Sending request to " + neighborPeers.get(i).peerId + " "
								+ neighborPeers.get(i).portno);
						NeighborConnectionThread connectionThread = new NeighborConnectionThread(
								neighborPeers.get(i).ip, neighborPeers.get(i).portno, searchFileName, msgId, peerid,
								neighborPeers.get(i).peerId);
						Thread threadInstance = new Thread(connectionThread);
						threadInstance.start();
						//
						// Save connection thread instances
						threadInstancesList.add(threadInstance);
						neighborConnThreadList.add(connectionThread);

					}
					//
					// Wait until child threads finished execution
					for (int i = 0; i < threadInstancesList.size(); i++)
						((Thread) threadInstancesList.get(i)).join();
					//
					// Get hit query result of all the neighbor peers
					System.out.println("   *** Search Paths ***");
					for (int i = 0; i < neighborConnThreadList.size(); i++) {
						HitQuery hitQueryResult = (HitQuery) neighborConnThreadList.get(i).getValue();
						if (hitQueryResult.foundPeers.size() > 0) {
							//
							// Save the neighbor peer result
							searchResult_Peers.addAll(hitQueryResult.foundPeers);
						}
						//
						// Display the paths in which search performed
						for (int count = 0; count < hitQueryResult.paths.size(); count++) {
							String path = peerid + hitQueryResult.paths.get(count);
							System.out.println("Search Path: " + path);
						}

					}
					System.out.println("   ******");
					
					if (searchResult_Peers.size() == 0) {
						System.out.println(searchFileName+" File not found in the nework");
					} else {
						System.out.println(searchFileName+" File found in the nework at below peers");
					}
					// Display the peers list where the searchFilename file
					// existed.
					for (int i = 0; i < searchResult_Peers.size(); i++) {
						System.out.println("--Found at Peer" + searchResult_Peers.get(i).peerId
								+ " , running on localhost:" + searchResult_Peers.get(i).port);

					}
					// call method for download functionality
					if (searchResult_Peers.size()>0){
					selectPeerToDownload(p, searchResult_Peers, searchFileName, sharedDir);
					}
	 }
					
	 endtime = System.currentTimeMillis();			
 }
		} catch (Exception e) {
			System.out.println(e);
		}
	return (endtime-starttime);

}
public void selectPeerToDownload(Peer p, ArrayList<PeerDetails> searchResult_Peers, String fileName,
		String Path) {
	// Functionality to download the file from the peers
	int peerId;
	try {
			peerId = 5;
			p.download(searchResult_Peers, peerId, fileName, Path);
		}
	 catch (Exception e) {
		System.out.println(e.getMessage());
	
}
}
}
