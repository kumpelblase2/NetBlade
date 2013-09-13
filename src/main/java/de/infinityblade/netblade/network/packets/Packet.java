package de.infinityblade.netblade.network.packets;

public interface Packet extends Cloneable
{
	public byte[] getRawData();
	public void setRawData(byte[] inData);
	public byte getPacketEndIdentifier();
	public void setPacketEndIdentifier(byte inIdentifier);
	public int getOpCode();
	public Packet clone();
}