package de.infinityblade.netblade;

import java.util.logging.Logger;
import de.infinityblade.netblade.mysql.MySQL;
import de.infinityblade.netblade.network.Client;
import de.infinityblade.netblade.network.ConnectionManager;
import de.infinityblade.netblade.network.ServerStatus;
import de.infinityblade.netblade.scheduler.Scheduler;

public interface NetBladeServer
{
	public Client[] getClients();
	public void start();
	public void shutdown();
	public int getPort();
	public void setPort(int inPort);
	public Logger getLogger();
	public MySQL getSQLServerConnection();
	public void dispatchCommand(String inCommand, String[] inArgs);
	public ServerConfiguration getConfig();
	public void reload();
	public Client getClient(int inID);
	public Client[] getOnlineClients();
	public ConnectionManager getConnectionManager();
	public boolean isFull();
	public int getFreeSpots();
	public int getMaxClients();
	public ServerStatus getStatus();
	public void setStatus(ServerStatus inStatus);
	public boolean isEncrypted();
	public void setEncrypted(boolean inEncrypted);
	public Scheduler getScheduler();
}