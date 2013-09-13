package de.infinityblade.netblade.network;

import java.net.Socket;
import de.infinityblade.netblade.NetBlade;
import de.infinityblade.netblade.network.packets.Packet;

public abstract class Client
{
	private final int m_connectionID;
	protected ClientConnection m_connection;
	protected ConnectionState m_connectionState = ConnectionState.DISCONNECTED;
	protected final ConnectionManager m_manager;

	public Client(int inConnectionID, Socket inConnection, ConnectionManager inManager)
	{
		this.m_connectionID = inConnectionID;
		this.m_manager = inManager;
		this.setConnectionState(ConnectionState.CONNECTED);
		this.m_connection = new ClientConnection(inConnection, this);
		this.m_connection.start();
	}

    public int getConnectionID()
	{
		return this.m_connectionID;
	}

	public ConnectionState getConnectionState()
	{
		return this.m_connectionState;
	}

	public ConnectionManager getConnectionManager()
	{
		return this.m_manager;
	}

	public void setConnectionState(ConnectionState inState)
	{
		NetBlade.getServer().getLogger().fine("[" + this.getConnectionManager().getName() + "] Setting level of client " + this.m_connectionID + " to " + inState.toString());
		this.m_connectionState = inState;
		if(inState == ConnectionState.DISCONNECTED)
			this.m_connection = null;
	}

	public ClientConnection getConnection()
	{
		return this.m_connection;
	}

	public void handle(Packet inPacket)
	{
		NetBlade.getServer().getLogger().finest("[" + this.getConnectionManager().getOperationHandler().getName() + "] Handling operation " + inPacket.getOpCode() + " for client " + this.getConnectionID() + ".");
		this.getConnectionManager().getOperationHandler().handle(this, inPacket);
	}

	public void disconnect()
	{
		this.m_connection.stopWorking();
	}

	@Override
	public int hashCode()
	{
		return this.m_connectionID;
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof Client && ((Client)object).getConnectionID() == this.getConnectionID();
	}

	public boolean isOnline()
	{
		return this.getConnectionState() != ConnectionState.DISCONNECTED;
	}

	public void onQuit(QuitReason inReason)
	{
	}
}