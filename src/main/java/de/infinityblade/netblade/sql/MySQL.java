package de.infinityblade.netblade.sql;

import java.sql.DriverManager;
import java.util.logging.Logger;
import de.infinityblade.netblade.LogManager;

public class MySQL extends SQL
{
	private String m_user;
	private String m_password;
	private String m_host;
	private String m_database;

	public MySQL(String inHost, String inDatabase, String inUser, String inPassword)
	{
		this(LogManager.getLogger(), inHost, inDatabase, inUser, inPassword);
	}

	public MySQL(Logger inLogger, String inHost, String inDatabase, String inUser, String inPassword)
	{
		super(inLogger);
		this.m_host = inHost;
		this.m_database = inDatabase;
		this.m_user = inUser;
		this.m_password = inPassword;
	}

	@Override
	public boolean connect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			this.m_mysqlConnection = DriverManager.getConnection("jdbc:mysql://" + this.m_host + "/" + this.m_database, this.m_user, this.m_password);
			this.m_mysqlConnection.setAutoCommit(true);
			LogManager.getLogger().config("Connected to database.");
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
}