package de.infinityblade.netblade.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import de.infinityblade.netblade.NetBlade;
import de.infinityblade.netblade.network.packets.*;

public class ClientConnection extends Thread
{
	private final Socket m_socket;
	private final Queue<Packet> m_inQueue;
	private final Queue<Packet> m_outQueue;
	private final Client m_client;
	private byte[] m_buffer;
	private List<Byte> m_temp = new ArrayList<Byte>();
	public static final int BUFFER_SIZE = 1024;
	private ClientConnectionWorkerThread m_worker;
	private InputStream m_inputStream;
	private OutputStream m_outputStream;

	public ClientConnection(Socket inSocket, Client inClient)
	{
		this.m_socket = inSocket;
		this.m_client = inClient;
		this.m_inQueue = new LinkedBlockingQueue<Packet>();
		this.m_outQueue = new LinkedBlockingQueue<Packet>();
		this.m_buffer = new byte[BUFFER_SIZE];
		this.m_worker = new ClientConnectionWorkerThread(this);
		this.m_worker.start();
		try
		{
			this.m_inputStream = inSocket.getInputStream();
			this.m_outputStream = inSocket.getOutputStream();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		while(this.m_client.getConnectionState() != ConnectionState.DISCONNECTED)
		{
			int received = -1;
			try
			{
				received = this.m_inputStream.read(this.m_buffer);
				NetBlade.getServer().getLogger().finer("[" + this.getClient().getConnectionManager().getName() + "] Received " + received + " bytes from client " + this.m_client.getConnectionID());
			}
			catch(Exception e)
			{
				if(this.m_client.getConnectionState() != ConnectionState.DISCONNECTED)
					e.printStackTrace();
			}

			if(received == -1)
			{
				this.m_client.onQuit(QuitReason.DISCONNECT);
				this.m_client.setConnectionState(ConnectionState.DISCONNECTED);
				break;
			}

			if(this.m_buffer[received - 1] != 0x1D)
			{
			    for(int i = 0; i < received; i++)
			    {
			        this.m_temp.add(this.m_buffer[i]);
			    }
			    continue;
			}

			byte[] data;
			if(this.m_temp.size() == 0)
			{
			    data = new byte[received];
			    System.arraycopy(this.m_buffer, 0, data, 0, received);
			}
			else
			{
			    data = new byte[this.m_temp.size() + received];
			    for(int i = 0; i < this.m_temp.size(); i++)
			    {
			        data[i] = this.m_temp.get(i);
			    }

			    System.arraycopy(this.m_buffer, 0, data, this.m_temp.size(), received);
			    this.m_temp.clear();
			}

			for(Packet p : this.readPackets(data))
			{
				this.m_inQueue.offer(p);
			}

			this.m_buffer = new byte[BUFFER_SIZE];
		}

		this.m_buffer = null;
		this.m_worker.cancel();
		this.m_worker = null;
	}

	public Packet getNextOutgoingPacket()
	{
		return this.m_outQueue.poll();
	}

	public void enqueueOutgoingPacket(Packet inPacket)
	{
		this.m_outQueue.offer(inPacket);
	}

	void sendPackets(List<Packet> inPackets)
	{
		try
		{
			int size = 0;
			for(Packet packet : inPackets)
			{
				if(packet instanceof FormattedPacket && !((FormattedPacket)packet).isParsed())
					((FormattedPacket)packet).parse();

				try
				{
					size += packet.getRawData().length;
					this.m_outputStream.write(packet.getRawData());
				}
				catch(Exception e)
				{
					NetBlade.getServer().getLogger().warning("[" + this.getClient().getConnectionManager().getName() + "] Error sending packet to client: " + e.getMessage());
					NetBlade.getServer().getLogger().finer("[" + this.getClient().getConnectionManager().getName() + "] Packet was: " + Arrays.toString(packet.getRawData()));
				}
			}

			this.m_outputStream.flush();
			NetBlade.getServer().getLogger().finest("[" + this.getClient().getConnectionManager().getName() + "] Sent "+ size + " bytes to client " + this.m_client.getConnectionID());
		}
		catch(Exception ex)
		{
			NetBlade.getServer().getLogger().warning("[" + this.getClient().getConnectionManager().getName() + "] Error flushing packet to client: " + ex.getMessage());
		}
	}

	public Client getClient()
	{
		return this.m_client;
	}

	public void stopWorking()
	{
		try
		{
			this.m_worker.cancel();
			this.m_inQueue.clear();
			this.m_outQueue.clear();
			this.m_client.setConnectionState(ConnectionState.DISCONNECTED);
			this.m_socket.close();
		}
		catch(Exception e)
		{
			NetBlade.getServer().getLogger().warning("[" + this.getClient().getConnectionManager().getName() + "] Unable to disconnect client with id " + this.m_client.getConnectionID() + ": " + e.getMessage());
		}
	}

	public Packet getNextIncomingPacket()
	{
		return this.m_inQueue.poll();
	}

	public String getIP()
	{
		return this.m_socket.getInetAddress().getHostAddress();
	}

	protected List<Packet> readPackets(byte[] inData)
	{
		return BasePacket.spreadPackets(NetBlade.getServer().getEmptyPacket(), inData);
	}
}