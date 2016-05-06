package gnutellaP2P;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Peer {
	public static void main(String args[]) {

		Peer peerInstance = new Peer();
		peerInstance.peerOperations(args);
	}

	public void peerOperations(String args[]) {
		// Function to handle the peer Operations.
		//
		String sharedDir;
		ArrayList<String> localFiles = new ArrayList<String>();
		List<Thread> threadInstancesList = new ArrayList<Thread>();
		int port;
		int peerid;
		int searchCounter = 0;
		int choice;
		Boolean bExit = false;
		ArrayList<NeighborPeers> neighborPeers = new ArrayList<NeighborPeers>();
		String searchFileName;
		ArrayList<PeerDetails> searchResult_Peers = new ArrayList<PeerDetails>();
		ArrayList<NeighborConnectionThread> neighborConnThreadList = new ArrayList<NeighborConnectionThread>();
		//
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			//
			// Collect peerid and port from the user.
			System.out.println("Enter the peerid");
			peerid = Integer.parseInt(br.readLine());
			System.out.println("Enter the port");
			port = Integer.parseInt(br.readLine());
			System.out.println("Session for peer id: " + peerid + " started...");
			//
			// Get the directory to share with other peers.
			System.out.println("Enter the shared directory");
			sharedDir = br.readLine();
			//
			// Get the filenames that existed locally
			getLocalFiles(sharedDir, localFiles);
			//
			// Run peer as a server on a specific port
			runPeerAsServer(peerid, port, sharedDir, localFiles);

			// User Menu
			while (true) {
				System.out.println("************ Main Menu ***************");
				System.out.println("1. Search File");
				System.out.println("2. Exit");
				System.out.println("**************************************");
				System.out.println("Select your choice");
				choice = Integer.parseInt(br.readLine());
				switch (choice) {
				case 1:
					// Search file name option
					//
					// Clear the previous search contents
					neighborPeers.clear();
					threadInstancesList.clear();
					neighborConnThreadList.clear();
					searchResult_Peers.clear();
					//
					System.out.println("Enter file name to search:");
					searchFileName = br.readLine();
					//
					// Get Neighbor peers
					getNeighborPeers(neighborPeers, peerid);
					//
					// Generate unique message id
					++searchCounter;
					String msgId = "Peer1.Search" + searchCounter;
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
					System.out.println("*** Search Paths ***");
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
					System.out.println("*******************");
					
					if (searchResult_Peers.size() == 0) {
						System.out.println(searchFileName+" File not found in the nework");
					} else {
						System.out.println(searchFileName+" File found in the nework at below peers");
					}
					// Display the peers list where the searchFilename file
					// existed.
					for (int i = 0; i < searchResult_Peers.size(); i++) {
						System.out.println("--Found at Peer" + searchResult_Peers.get(i).peerId
								+ " , running on 127.0.0.1:" + searchResult_Peers.get(i).port);

					}
					// call method for download functionality
					if (searchResult_Peers.size()>0){
					selectPeerToDownload(br, searchResult_Peers, searchFileName, sharedDir);
					}
					break;
				default:
					bExit = true;
				}
				if (bExit) {
					// End of the client session
					System.exit(1);
					break;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void getLocalFiles(String sharedDir, ArrayList<String> localFiles) {
		// Get the local files that existed in the local directory
		File directoryObj = new File(sharedDir);
		File newfind;
		String filename;
		String[] filesList = directoryObj.list();
		for (int i = 0; i < filesList.length; i++) {
			newfind = new File(filesList[i]);
			filename = newfind.getName();
			// Store the file name in arrayList
			localFiles.add(filename);

		}

	}

	public void runPeerAsServer(int peerId, int port, String sharedDir, ArrayList<String> localFiles) {
		// Run peer remote methods on provided port
		try {
			LocateRegistry.createRegistry(port);
			PeerInterface stub = new PeerInterfaceRemote(sharedDir, peerId, port, localFiles);
			Naming.rebind("rmi://localhost:" + port + "/peerServer", stub);
			System.out.println("Peer " + peerId + " acting as server on 127.0.0.1:" + port);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void getNeighborPeers(ArrayList<NeighborPeers> neighborPeers, int peerId) {
		// Get the Neighbor peers for the provided peer id
		String property = null;
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");

			// load a properties file
			prop.load(input);
			property = "peerid." + peerId + ".neighbors";
			// get the property value and print it out
			String[] strNeighbors = prop.getProperty(property).split(",");
			for (int i = 0; i < strNeighbors.length; i++) {
				NeighborPeers tempPeer = new NeighborPeers();
				tempPeer.peerId = strNeighbors[i];
				tempPeer.ip = prop.getProperty(strNeighbors[i] + ".ip");
				tempPeer.portno = Integer.parseInt(prop.getProperty(strNeighbors[i] + ".port"));
				neighborPeers.add(tempPeer);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void selectPeerToDownload(BufferedReader br, ArrayList<PeerDetails> searchResult_Peers, String fileName,
			String Path) {
		// Functionality to download the file from the peers
		int choice;
		int peerId;
		try {
			System.out.println("***Download Menu***");
			System.out.println("1.Download file");
			System.out.println("2.Exit");
			System.out.println("*******************");
			
			System.out.println("Select operaion");
			choice = Integer.parseInt(br.readLine());
			switch (choice) {
			case 1:
				System.out.println("Enter peer id to connect and download the file");
				// Select the peer id from where the file to be downloaded.
				peerId = Integer.parseInt(br.readLine());
				download(searchResult_Peers, peerId, fileName, Path);
				break;

			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public void download(ArrayList<PeerDetails> searchResult_Peers, int peerId, String fileName, String Path)
			throws IOException {
		// Download functionality
		int totalPeers, iCount = 0;
		int port = 0;
		String Host = null;
		totalPeers = searchResult_Peers.size();
		while (iCount < totalPeers) {
			if (peerId == searchResult_Peers.get(iCount).peerId) {
				port = searchResult_Peers.get(iCount).port;
				Host = searchResult_Peers.get(iCount).hostIp;
				break;
			}
			iCount++;
		}

		System.out.println("Downloading from " + Host + ":" + port);
		//
		// Get an object for peer server to download the file.
		PeerInterface PeerServer = null;
		try {
			PeerServer = (PeerInterface) Naming.lookup("rmi://localhost:" + port + "/peerServer");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Call remote method, obtain, to download the file
		byte[] fileData = null;
		try {
			fileData = PeerServer.obtain(fileName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		// Create new file for current peer with the downloaded bytes of
		// data
		BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(Path + "//" + fileName));
		try {
			output.write(fileData, 0, fileData.length);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		output.flush();
		output.close();
		System.out.println("\"" + fileName + "\" downloaded to path: " + Path);

	}

}
