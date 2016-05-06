package gnutellaP2P;
import java.rmi.*;
//
// Interface for Remote method invocations
public interface PeerInterface extends Remote{
	public byte[] obtain(String filename)throws RemoteException;
	public HitQuery query(int fromPeerId,String msgId,String fileName)throws RemoteException;
}
