package de.infinityblade.netblade.mysql;

public abstract class MySQLQuery
{
	private final String m_query;

	public MySQLQuery(String inQuery)
	{
		this.m_query = inQuery;
	}

	public String getQuery()
	{
		return this.m_query;
	}
}