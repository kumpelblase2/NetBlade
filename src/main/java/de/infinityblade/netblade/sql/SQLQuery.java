package de.infinityblade.netblade.sql;

public abstract class SQLQuery
{
	private final String m_query;

	public SQLQuery(String inQuery)
	{
		this.m_query = inQuery;
	}

	public String getQuery()
	{
		return this.m_query;
	}
}