
import java.net.DatagramPacket;

public interface PacketVarlidate {

	boolean varlidatePacket(DatagramPacket datagramPacket,String key,byte[] reqAuthenticator);
	
}
