
import java.net.DatagramPacket;

public interface PacketIdProcess extends PacketProcess{
	
	String getPacketId(DatagramPacket datagramPacket);
	
}
