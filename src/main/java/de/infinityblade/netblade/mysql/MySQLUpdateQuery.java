package de.infinityblade.netblade.mysql;

import java.sql.ResultSet;

public class MySQLUpdateQuery extends MySQLQuery
{
    private MySQLUpdateCallback m_callback;

	public MySQLUpdateQuery(String inQuery)
	{
		super(inQuery);
	}

	public void setCallback(MySQLUpdateCallback inCallback)
	{
	    this.m_callback = inCallback;
	}

	public MySQLUpdateCallback getCallback()
	{
	    return this.m_callback;
	}

	void onGeneratedKeys(ResultSet keys)
	{
	    if(this.m_callback != null)
	        this.m_callback.run(keys);
	}

	public interface MySQLUpdateCallback
	{
	    public void run(ResultSet inKeys);
	}
}