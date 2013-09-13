package de.infinityblade.netblade.network;

public class ServerInfo
{
	private final String m_address;
	private ServerStatus m_status;
	private int m_users;
	private int m_maxUsers;

	public ServerInfo(String inAddress)
	{
		this.m_address = inAddress;
		this.m_status = ServerStatus.UNKNOWN;
		this.m_users = 0;
		this.m_maxUsers = 0;
	}

	public String getServerAddress()
	{
		return this.m_address;
	}

	public ServerStatus getStatus()
	{
		return this.m_status;
	}

	public void setStatus(ServerStatus inStatus)
	{
		this.m_status = inStatus;
	}

	public void setUsers(int inUsers)
	{
		this.m_users = inUsers;
	}

	public int getUsers()
	{
		return this.m_users;
	}

	public void setMaxUsers(int inMax)
	{
		this.m_maxUsers = inMax;
	}

	public int getMaxUsers()
	{
		return this.m_maxUsers;
	}
}