package de.infinityblade.netblade.mysql;

import java.sql.*;

public class MySQLSelectQuery extends MySQLQuery
{
	private MySQLSelectCallback m_callback;

	public MySQLSelectQuery(String inQuery)
	{
		super(inQuery);
	}

	public MySQLSelectQuery(String inQuery, MySQLSelectCallback inCallback)
	{
		super(inQuery);
		this.m_callback = inCallback;
	}

	public MySQLSelectCallback getCallback()
	{
		return this.m_callback;
	}

	public interface MySQLSelectCallback
	{
		public void run(ResultSet inResult);
	}
}