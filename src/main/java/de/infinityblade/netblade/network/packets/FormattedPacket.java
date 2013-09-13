package de.infinityblade.netblade.network.packets;

import java.util.List;

public interface FormattedPacket extends Packet
{
	public List<Object> getParsedData();
	public void setParsedData(List<Object> inData);
	public void parse();
	public boolean isParsed();
	public boolean hasParser();
	public PacketParser getParser();
	public void setPacketParser(PacketParser inParser);
}