package de.infinityblade.netblade.sql;

import java.sql.*;

public class SQLSelectQuery extends SQLQuery
{
	private MySQLSelectCallback m_callback;

	public SQLSelectQuery(String inQuery)
	{
		super(inQuery);
	}

	public SQLSelectQuery(String inQuery, MySQLSelectCallback inCallback)
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