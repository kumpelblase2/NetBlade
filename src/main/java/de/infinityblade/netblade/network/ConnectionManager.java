package de.infinityblade.netblade.network;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import de.infinityblade.netblade.network.packets.Packet;
import de.infinityblade.netblade.operations.OperationHandler;

public class ConnectionManager
{
	protected Logger m_log;
	protected Client[] m_clients;
	protected int m_port;
	protected int m_maxClients = 1;
	protected boolean m_isRunning = false;
	protected ServerSocket m_socket;
	protected Class<? extends Client> m_clientclass;
	private Constructor<? extends Client> m_clientConstructor;
	protected OperationHandler m_handler;
	protected final String m_name;
	protected boolean m_isEncrypted = false;

	public ConnectionManager(Class<? extends Client> inClientClass, String inName)
	{
		this.m_name = inName;
		try
		{
			this.m_port = 1337;
			this.m_clientclass = inClientClass;
			this.m_clientConstructor = inClientClass.getConstructor(int.class, Socket.class, ConnectionManager.class);
			this.m_handler = new OperationHandler("Server");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public ConnectionManager(Class<? extends Client> inClientClass, String inName, Logger inLogger)
	{
		this(inClientClass, inName);
		this.m_log = inLogger;
	}

	public Client[] getClients()
	{
		return this.m_clients;
	}

	public void setLogger(Logger inLogger)
	{
		this.m_log = inLogger;
	}

	public void start()
	{
		try
		{
			this.m_log.config("[" + this.m_name + "] Binding port " + this.m_port + (this.isEncrypted() ? " with ssl encryption" : ""));
			if(this.isEncrypted())
			{
			    this.m_socket = SSLServerSocketFactory.getDefault().createServerSocket(this.m_port);
			    ((SSLServerSocket)this.m_socket).setNeedClientAuth(false);
			}
			else
			    this.m_socket = new ServerSocket(this.m_port);
		}
		catch(IOException e)
		{
			this.m_log.severe("[" + this.m_name + "] Port already in use. Shutdown.");
			return;
		}

		this.m_clients = new Client[this.m_maxClients];
		this.m_isRunning = true;
	}

	public boolean isRunning()
	{
		return this.m_isRunning;
	}

	public int getPort()
	{
		return this.m_port;
	}

	public void setPort(int inPort)
	{
		this.m_port = inPort;
	}

	public Logger getLogger()
	{
		return this.m_log;
	}

	public void setMaxClients(int inMax)
	{
		this.m_maxClients = inMax;
	}

	public int getMaxClients()
	{
		return this.m_maxClients;
	}

	public void removeClient(Client inClient)
	{
		if(inClient.getConnection() != null)
			inClient.getConnection().stopWorking();

		inClient.setConnectionState(ConnectionState.DISCONNECTED);
		this.removeClient(inClient.getConnectionID());
	}

	public void removeClient(int inID)
	{
		this.m_clients[inID] = null;
		this.onClientRemove(inID);
	}

	protected int getNextFreeID()
	{
		for(int i = 0; i < this.m_clients.length; i++)
		{
			if(this.m_clients[i] != null)
			{
				if(this.m_clients[i].getConnectionState() == ConnectionState.DISCONNECTED)
					return i;
			}
			else
				return i;
		}

		return -1;
	}

	public void disconnectClients()
	{
		if(this.m_clients == null)
			return;

		for(Client client : this.m_clients)
		{
			if(client != null && client.getConnectionState() != ConnectionState.DISCONNECTED)
				client.disconnect();
		}
	}

	public void startListening()
	{
		while(this.m_isRunning)
		{
			try
			{
				Socket client = this.m_socket.accept();
				if(!this.onClientConnect(client))
				{
					client.shutdownInput();
					client.shutdownOutput();
					client.close();
					continue;
				}

				int freeID = this.getNextFreeID();
				if(freeID == -1)
				{
					//: send no free slot
					client.shutdownInput();
					client.shutdownOutput();
					client.close();
					continue;
				}

				Client c = this.m_clientConstructor.newInstance(freeID, client, this);
				this.onClientAccept(c);
				this.m_clients[freeID] = c;
			}
			catch(Exception e)
			{
				if(this.m_isRunning)
					e.printStackTrace();
			}
		}

		this.onStop();
		this.m_log.finer("[" + this.m_name + "] Stopped listening.");
	}

	protected void onClientAccept(Client inClient)
	{
		this.m_log.fine("[" + this.m_name + "] Client id " + inClient.getConnectionID());
	}

	protected boolean onClientConnect(Socket inSocket)
	{
		this.m_log.info("[" + this.m_name + "] Accepted new client from " + inSocket.getRemoteSocketAddress().toString());
		return true;
	}

	protected void onStop()
	{
		try
		{
			if(this.m_socket != null && !this.m_socket.isClosed())
				this.m_socket.close();
		}
		catch(Exception e)
		{
			this.m_log.warning("[" + this.m_name + "] Error shutting down: " + e.getMessage());
		}
	}

	protected void onClientRemove(int inClient)
	{
		this.m_log.finest("[" + this.m_name + "] Removed client " + inClient);
	}

	public Client getClient(int inID)
	{
		return this.m_clients[inID];
	}

	public Client[] getOnlineClients()
	{
		ArrayList<Client> online = new ArrayList<Client>();
		for(Client client : this.getClients())
		{
			if(client != null && client.isOnline())
				online.add(client);
		}

		return online.toArray(new Client[online.size()]);
	}

	public void broadcastPacket(Packet inPacket)
	{
		for(Client client : this.m_clients)
		{
			if(client != null && client.isOnline())
				client.getConnection().enqueueOutgoingPacket(inPacket);
		}
	}

	public void stop()
	{
		try
		{
			this.m_isRunning = false;
			this.m_socket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public OperationHandler getOperationHandler()
	{
		return this.m_handler;
	}

	public String getName()
	{
		return this.m_name;
	}

	public boolean isEncrypted()
	{
		return this.m_isEncrypted;
	}

	public void setEncrypted(boolean inEncrypted)
	{
		this.m_isEncrypted = inEncrypted;
	}
}