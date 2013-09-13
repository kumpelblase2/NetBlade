package de.infinityblade.netblade.sql;

import java.sql.ResultSet;

public class SQLUpdateQuery extends SQLQuery
{
    private MySQLUpdateCallback m_callback;

	public SQLUpdateQuery(String inQuery)
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