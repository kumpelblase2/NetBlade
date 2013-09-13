package de.infinityblade.netblade.network.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.charset.Charset;
import java.util.*;
import de.infinityblade.netblade.LogManager;
import de.infinityblade.netblade.network.TransferDirection;

public class Packet
{
	private byte[] m_rawData;
	private List<String> m_data;
	private final TransferDirection m_direction;
	private final Charset m_charset = Charset.forName("UTF-8");
	private int m_opcode = -1;
	private boolean m_isParsed = false;

	public Packet(int inOpCode, List<Object> inData)
	{
		inData.add(0, inOpCode + "");
		this.m_opcode = inOpCode;
		this.m_direction = TransferDirection.SERVER_TO_CLIENT;
		this.m_data = this.parseObjects(inData);
		this.parse();
	}

	public Packet(int inOpCode, Object... inData)
	{
		this(inOpCode, new ArrayList<Object>(Arrays.asList(inData)));
	}

	public Packet(byte[] inData)
	{
		this.m_direction = TransferDirection.CLIENT_TO_SERVER;
		this.m_rawData = inData;
	}

	public Packet(List<Object> inData)
	{
		this.m_direction = TransferDirection.SERVER_TO_CLIENT;
		this.m_data = this.parseObjects(inData);
	}

	private List<String> parseObjects(Collection<?> inData)
	{
		List<String> list = new ArrayList<String>(inData.size());

		for(Object o : inData)
		{
		    if(o == null)
		        continue;
			if(o instanceof String)
				list.add((String)o);
			else if(o instanceof Integer)
				list.add((Integer)o + "");
			else if(o instanceof Collection)
				list.addAll(this.parseObjects((Collection<?>)o));
			else if(o instanceof Enum)
				list.add(((Enum<?>)o).ordinal() + "");
			else if(o.getClass().isArray())
				list.addAll(this.parseObjects(Arrays.asList(o)));
			else
				list.add(o.toString());
		}

		return list;
	}

	public byte[] getRawData()
	{
		return this.m_rawData;
	}

	public List<String> getParsedData()
	{
		return this.m_data;
	}

	public TransferDirection getDirection()
	{
		return this.m_direction;
	}

	public Packet parse()
	{
		return this.parse(this.getDirection());
	}

	public Packet parse(TransferDirection inDirection)
	{
		if(inDirection == TransferDirection.CLIENT_TO_SERVER)
		{
		    if(this.m_rawData[this.m_rawData.length - 1] != 0x1C)
		    {
		        this.m_opcode = 0;
		        return this;
		    }

			int lastPos = 0;
			this.m_data = new ArrayList<String>();
			for(int i = 0; i < this.m_rawData.length; i++)
			{
				if(this.m_rawData[i] == 0x1C)
				{
					byte[] data = Arrays.copyOfRange(this.m_rawData, lastPos, i);
					this.m_data.add(new String(data, this.m_charset));
					lastPos = i + 1;
				}
			}

			if(this.m_data.size() > 0)
				this.m_opcode = Integer.parseInt(this.m_data.get(0));
		}
		else
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream buffer = new DataOutputStream(out);
			try
			{
				for(String data : this.m_data)
				{
					byte[] string = data.getBytes(this.m_charset);
					buffer.write(string);
					buffer.write(new byte[]{ 0x1C });
				}

				buffer.write(new byte[] { 0x1D });
			}
			catch(Exception e)
			{
			}

			this.m_rawData = out.toByteArray();
		}
		this.m_isParsed = true;
		return this;
	}

	public int getOpCode()
	{
		if(this.m_opcode != -1)
			return this.m_opcode;

		if(this.m_data == null)
		{
			for(int i = 0; i < this.m_rawData.length; i++)
			{
				if(this.m_rawData[i] == 0x1C)
				{
					int opcode = Integer.parseInt(new String(this.m_rawData, 0, i, this.m_charset));
					this.m_opcode = opcode;
					return opcode;
				}
			}

			return -1;
		}
		else
		{
			if(this.m_data.size() == 0)
				return -1;

			try
			{
				int opcode = Integer.parseInt(this.m_data.get(0));
				this.m_opcode = opcode;
				return opcode;
			}
			catch(Exception e)
			{
				LogManager.getLogger().warning("Unable to parse opcode: " + e.getMessage());
				LogManager.getLogger().finer("Packetdata: " + Arrays.toString(this.m_rawData));
				return -1;
			}
		}
	}

	public boolean isParsed()
	{
		return this.m_isParsed;
	}

	public static List<Packet> spreadPackets(byte[] inRawData)
	{
		int lastPos = 0;
		List<Packet> packetData = new ArrayList<Packet>();
		for(int i = 0; i < inRawData.length; i++)
		{
			if(inRawData[i] == 0x1D)
			{
				byte[] data = Arrays.copyOfRange(inRawData, lastPos, i);
				packetData.add(new Packet(data).parse());
				lastPos = i + 1;
			}
		}

		return packetData;
	}
}