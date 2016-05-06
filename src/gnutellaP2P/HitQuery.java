package gnutellaP2P;

import java.io.Serializable;
import java.util.ArrayList;

public class HitQuery implements Serializable {
	/** Class to maintain the details of hit query
		 * 
		 */
	private static final long serialVersionUID = 1L;
	public ArrayList<PeerDetails> foundPeers = new ArrayList<PeerDetails>();
	public ArrayList<String> paths = new ArrayList<String>();
}
