package de.infinityblade.netblade.network.packets;

import java.util.List;

public abstract class BaseFormattedPacket extends BasePacket implements FormattedPacket
{
	protected List<Object> m_parsedData;
	protected boolean m_isParsed = false;
	protected PacketParser m_parser;

	public BaseFormattedPacket(byte[] inData)
	{
		super(inData);
	}

	public BaseFormattedPacket(List<Object> inData)
	{
		super();
		this.setParsedData(inData);
	}

	@Override
	public List<Object> getParsedData()
	{
		return this.m_parsedData;
	}

	@Override
	public void setParsedData(List<Object> inData)
	{
		this.m_parsedData = inData;
	}

	@Override
	public void parse()
	{
		if(this.m_parser != null)
		{
			if(this.m_rawData != null)
				this.setParsedData(this.m_parser.parse(this.getRawData()));
			else
				this.setRawData(this.m_parser.parse(this.getParsedData()));
		}
	}

	@Override
	public boolean isParsed()
	{
		return this.m_isParsed;
	}

	@Override
	public boolean hasParser()
	{
		return this.m_parser != null;
	}

	@Override
	public PacketParser getParser()
	{
		return this.m_parser;
	}

	@Override
	public void setPacketParser(PacketParser inParser)
	{
		this.m_parser = inParser;
	}
}