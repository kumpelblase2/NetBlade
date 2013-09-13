package de.infinityblade.netblade.sql;

import java.sql.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import de.infinityblade.netblade.*;

public abstract class SQL implements Runnable
{
	protected Connection m_mysqlConnection;
	protected LinkedBlockingQueue<SQLQuery> m_queries;
	protected boolean m_shouldRun = true;
	protected final Logger m_log;

	public SQL()
	{
		this(LogManager.getLogger());
	}

	public SQL(Logger inLogger)
	{
		this.m_log = inLogger;
		this.m_queries = new LinkedBlockingQueue<SQLQuery>();
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

	public abstract boolean connect();

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

		this.m_log.finer("Stopped SQL.");
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
		this.m_queries.offer(new SQLUpdateQuery(inQuery));
		LogManager.getLogger().finest("Adding query: " + inQuery);
	}

	public void enqueueQuery(SQLQuery inQuery)
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

			SQLQuery query = this.m_queries.poll();
			if(query != null)
			{
				try
				{
					if(query instanceof SQLUpdateQuery)
					{
					    if(!this.isConnected())
				        {
				            if(!this.connect())
				                continue;
				        }

					    PreparedStatement statement = this.m_mysqlConnection.prepareStatement(query.getQuery(), PreparedStatement.RETURN_GENERATED_KEYS);
						statement.executeUpdate();
						((SQLUpdateQuery)query).onGeneratedKeys(statement.getGeneratedKeys());
					}
					else
					{
						SQLSelectQuery select = (SQLSelectQuery)query;
						ResultSet result = this.doQuery(select);
						if(select.getCallback() != null)
							select.getCallback().run(result);
					}
				}
				catch(Exception e)
				{
					this.m_log.warning("Unable to execute query: " + e.getMessage());
					this.m_log.fine("Query was: " + query);
				}
			}
		}

		if(this.m_mysqlConnection != null)
		{
			try
			{
				this.m_mysqlConnection.close();
			}
			catch(SQLException e)
			{
			}
		}

		this.m_log.finer("Shutting down SQL");
	}

	public ResultSet doQuery(SQLQuery inQuery)
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
			this.m_log.warning("Unable to execute query: " + e.getMessage());
			this.m_log.fine("Query was: " + inQuery.getQuery());
			return null;
		}
	}
}