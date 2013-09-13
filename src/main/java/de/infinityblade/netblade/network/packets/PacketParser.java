package de.infinityblade.netblade.network.packets;

import java.util.List;

public interface PacketParser
{
	public List<Object> parse(byte[] inData);
	public byte[] parse(List<Object> inData);
}