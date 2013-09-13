package de.infinityblade.netblade.network.packets;

public class EmptyPacket extends BasePacket
{
	public EmptyPacket()
	{
		super();
	}

	public EmptyPacket(byte[] inRawData)
	{
		super(inRawData);
	}

	@Override
	public Packet clone()
	{
		return new EmptyPacket(this.m_rawData);
	}
}
