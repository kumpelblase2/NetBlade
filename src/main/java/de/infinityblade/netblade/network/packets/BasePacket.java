package de.infinityblade.netblade.network.packets;

import java.util.*;

public abstract class BasePacket implements Packet
{
	protected byte[] m_rawData;
	protected static byte DEFAULT_END_IDENTIFIER = 0x1D;
	protected byte m_endIdentifier;

	public BasePacket()
	{
	}

	public BasePacket(byte[] inData)
	{
		this();
		this.setRawData(inData);
		this.setPacketEndIdentifier(DEFAULT_END_IDENTIFIER);
	}

	public byte[] getRawData()
	{
		return this.m_rawData;
	}

	@Override
	public void setRawData(byte[] inData)
	{
		this.m_rawData = inData;
	}

	@Override
	public byte getPacketEndIdentifier()
	{
		return this.m_endIdentifier;
	}

	@Override
	public void setPacketEndIdentifier(byte inIdentifier)
	{
		this.m_endIdentifier = inIdentifier;
	}

	public int getOpCode()
	{
		if(this.m_rawData == null || this.m_rawData.length <= 0)
			return -1;

		return (int)this.m_rawData[0];
	}

	public static List<Packet> spreadPackets(Packet inBase, byte[] inRawData)
	{
		int lastPos = 0;
		List<Packet> packetData = new ArrayList<Packet>();
		for(int i = 0; i < inRawData.length; i++)
		{
			if(inRawData[i] == inBase.getPacketEndIdentifier())
			{
				byte[] data = Arrays.copyOfRange(inRawData, lastPos, i);
				Packet newPacket = inBase.clone();
				newPacket.setRawData(data);
				packetData.add(newPacket);
				lastPos = i + 1;
			}
		}

		return packetData;
	}

	@Override
	public Packet clone()
	{
		return this;
	}
}