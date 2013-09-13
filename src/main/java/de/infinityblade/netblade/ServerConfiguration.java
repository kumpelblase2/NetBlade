package de.infinityblade.netblade;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public final class ServerConfiguration
{
	private HashMap<String, String> m_data = new HashMap<String, String>();
	private final String m_file;

	public ServerConfiguration()
	{
		this("config.txt");
	}

	public ServerConfiguration(String inFile)
	{
		this.m_file = inFile;
		this.load();
	}

	public void load()
	{
		try
		{
			File f = new File(this.m_file);
			if(!f.exists())
			{
				if(!f.createNewFile())
					LogManager.getLogger().info("Unable to create config file because none existed.");
				else
					LogManager.getLogger().info("Created new config file because none existed.");

				return;
			}

			FileInputStream file = new FileInputStream(f);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(file)));
			String settingsLine;
			while((settingsLine = reader.readLine()) != null)
			{
				if(settingsLine.contains("="))
				{
					String[] split = settingsLine.split("=");
					this.m_data.put(split[0], split[1]);
				}
			}

			reader.close();
		}
		catch(Exception e)
		{
			LogManager.getLogger().warning("Unable to read config: " + e.getMessage());
		}
	}

	public String get(String inKey, String inDefault)
	{
		if(!this.m_data.containsKey(inKey))
			return inDefault;

		return this.m_data.get(inKey);
	}
}