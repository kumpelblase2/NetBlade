package de.infinityblade.netblade.network;

import java.util.ArrayList;
import java.util.List;
import de.infinityblade.netblade.LogManager;
import de.infinityblade.netblade.network.packets.Packet;

public class ClientConnectionWorkerThread extends Thread
{
	private ClientConnection m_client;
	private boolean m_running = true;

	public ClientConnectionWorkerThread(ClientConnection inClient)
	{
		this.m_client = inClient;
	}

	public void cancel()
	{
		this.m_running = false;
	}

	public void run()
	{
		while(this.m_running && this.m_client.getClient().getConnectionState() != ConnectionState.DISCONNECTED)
		{
			Packet out = this.m_client.getNextOutgoingPacket();
			List<Packet> toSend = new ArrayList<Packet>();
			for(int i = 0; i < 3 && out != null; i++, out = this.m_client.getNextOutgoingPacket())
			{
				toSend.add(out);
			}

			if(toSend.size() > 0)
				this.m_client.sendPackets(toSend);

			Packet in = this.m_client.getNextIncomingPacket();
			if(in != null)
				this.m_client.getClient().handle(in);

			try
			{
				Thread.sleep(10);
			}
			catch(Exception e)
			{
				LogManager.getLogger().warning("[" + this.m_client.getClient().getConnectionManager().getName() + "] Issue waiting for next step: " + e.getMessage());
			}
		}

		LogManager.getLogger().finest("[" + this.m_client.getClient().getConnectionManager().getName() + "] Stopping worker for client " + this.m_client.getClient().getConnectionID());
		this.m_client = null;
	}
}