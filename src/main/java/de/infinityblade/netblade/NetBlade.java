package de.infinityblade.netblade;

import de.infinityblade.netblade.sql.SQL;
import de.infinityblade.netblade.network.Client;
import de.infinityblade.netblade.network.ConnectionManager;
import de.infinityblade.netblade.network.ServerStatus;
import de.infinityblade.netblade.scheduler.Scheduler;

public final class NetBlade
{
	public static NetBladeServer s_server;

	private NetBlade()
	{
	}

	public static void setServer(NetBladeServer inServer)
	{
		s_server = inServer;
	}

	public static NetBladeServer getServer()
	{
		return s_server;
	}

	public static int getPort()
	{
		return s_server.getPort();
	}

	public static Client[] getClients()
	{
		return s_server.getClients();
	}

	public static SQL getSQLServerConnection()
	{
		return s_server.getSQLServerConnection();
	}

	public static void reload()
	{
		s_server.reload();
	}

	public static ServerConfiguration getConfig()
	{
		return s_server.getConfig();
	}

	public static Client getClient(int inID)
	{
		return s_server.getClient(inID);
	}

	public static Client[] getOnlineClients()
	{
		return s_server.getOnlineClients();
	}

	public static ConnectionManager getConnectionManager()
	{
		return s_server.getConnectionManager();
	}

	public static boolean isFull()
	{
		return s_server.isFull();
	}

	public static int getFreeSpots()
	{
		return s_server.getFreeSpots();
	}

	public static int getMaxClients()
	{
		return s_server.getMaxClients();
	}

	public static ServerStatus getStatus()
	{
		return s_server.getStatus();
	}

	public static Scheduler getScheduler()
	{
	    return s_server.getScheduler();
	}
}