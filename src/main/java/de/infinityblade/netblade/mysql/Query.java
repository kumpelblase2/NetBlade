package de.infinityblade.netblade.mysql;

public class Query
{
	private final String m_query;

	public Query(String inQuery)
	{
		this.m_query = inQuery;
	}

	public String get()
	{
		return this.m_query;
	}

	public String get(String inParameter)
	{
		return this.get(new String[] { inParameter });
	}

	public String get(String... inParameters)
	{
		String result = this.get();
		for(int i = 0; i < inParameters.length; i++)
		{
			result = result.replace("@" + i +"@", inParameters[i]);
		}

		return result;
	}
}