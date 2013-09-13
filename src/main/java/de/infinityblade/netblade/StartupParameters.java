package de.infinityblade.netblade;

public enum StartupParameters
{
	PORT("port"),
	MAX_CLIENTS("max_clients"),
	FILE_LEVEL("file_level"),
	LOG_LEVEL("log_level");

	private final String m_name;

	private StartupParameters(String inParameterName)
	{
		this.m_name = inParameterName;
	}

	public static StartupParameters getByName(String inName)
	{
		for(StartupParameters parameter : values())
		{
			if(parameter.getName().equalsIgnoreCase(inName))
				return parameter;
		}

		return null;
	}

	public String getName()
	{
		return this.m_name;
	}
}