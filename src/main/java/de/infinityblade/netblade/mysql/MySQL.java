package de.infinityblade.netblade.mysql;

import java.sql.*;
import java.util.concurrent.LinkedBlockingQueue;
import de.infinityblade.netblade.*;

public class MySQL implements Runnable
{
	private Connection m_mysqlConnection;
	private final NetBladeServer m_server;
	private final ServerConfiguration m_info;
	private LinkedBlockingQueue<MySQLQuery> m_queries;
	private boolean m_shouldRun = true;

	public MySQL(NetBladeServer inServer)
	{
		this.m_server = inServer;
		this.m_info = inServer.getConfig();
		this.m_queries = new LinkedBlockingQueue<MySQLQuery>();
	}

	public boolean tryConnect()
	{
		boolean connected = false;
		for(int i = 0; i < 5; i++)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}

			if(this.connect())
			{
				connected = true;
				break;
			}
		}

		if(!connected)
		{
			this.m_shouldRun = false;
			LogManager.getLogger().severe("Unable to connect to database after 5 tries. Shutting down.");
			return false;
		}
		return true;
	}

	public boolean connect()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			this.m_mysqlConnection = DriverManager.getConnection("jdbc:mysql://" + this.m_info.get("host", "localhost:3306") + "/" + this.m_info.get("database", "manchkin"), this.m_info.get("user", "root"), this.m_info.get("pass", ""));
			this.m_mysqlConnection.setAutoCommit(true);
			LogManager.getLogger().config("Connected to database.");
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public boolean isConnected()
	{
		try
		{
			return this.m_mysqlConnection != null && this.m_mysqlConnection.isValid(3) && !this.m_mysqlConnection.isClosed();
		}
		catch(SQLException e)
		{
			return false;
		}
	}

	public void stop()
	{
		this.m_shouldRun = false;
		this.waitForQueries();
		try
		{
			if(this.m_mysqlConnection != null && !this.m_mysqlConnection.isClosed())
				this.m_mysqlConnection.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		this.m_server.getLogger().finer("Stopped MySQL.");
	}

	private void waitForQueries()
	{
		try
		{
			while(this.m_queries.size() > 0)
			{
				Thread.sleep(100);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void enqueueUpdateQuery(String inQuery)
	{
		this.m_queries.offer(new MySQLUpdateQuery(inQuery));
		LogManager.getLogger().finest("Adding query: " + inQuery);
	}

	public void enqueueQuery(MySQLQuery inQuery)
	{
		this.m_queries.offer(inQuery);
		LogManager.getLogger().finest("Adding query: " + inQuery.getQuery());
	}

	public void run()
	{
		while(this.m_shouldRun || this.m_queries.size() > 0)
		{
			if(this.m_queries.size() == 0)
			{
				try
				{
					Thread.sleep(100);
					continue;
				}
				catch(Exception e)
				{
				}
			}

			MySQLQuery query = this.m_queries.poll();
			if(query != null)
			{
				try
				{
					if(query instanceof MySQLUpdateQuery)
					{
					    if(!this.isConnected())
				        {
				            if(!this.connect())
				                continue;
				        }

					    PreparedStatement statement = this.m_mysqlConnection.prepareStatement(query.getQuery(), PreparedStatement.RETURN_GENERATED_KEYS);
						statement.executeUpdate();
						((MySQLUpdateQuery)query).onGeneratedKeys(statement.getGeneratedKeys());
					}
					else
					{
						MySQLSelectQuery select = (MySQLSelectQuery)query;
						ResultSet result = this.doQuery(select);
						if(select.getCallback() != null)
							select.getCallback().run(result);
					}
				}
				catch(Exception e)
				{
					this.m_server.getLogger().warning("Unable to execute query: " + e.getMessage());
					this.m_server.getLogger().fine("Query was: " + query);
				}
			}
		}
		this.m_server.getLogger().finer("Shutting down MySQL");
	}

	public ResultSet doQuery(MySQLQuery inQuery)
	{
		if(!this.isConnected())
		{
			if(!this.connect())
				return null;
		}

		try
		{
			return this.m_mysqlConnection.prepareStatement(inQuery.getQuery()).executeQuery();
		}
		catch(Exception e)
		{
			this.m_server.getLogger().warning("Unable to execute query: " + e.getMessage());
			this.m_server.getLogger().fine("Query was: " + inQuery.getQuery());
			return null;
		}
	}
}