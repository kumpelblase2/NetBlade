package de.infinityblade.netblade;

import java.util.logging.ConsoleHandler;
import jline.console.ConsoleReader;

public class JLineConsoleHandler extends ConsoleHandler
{
	private final ConsoleReader m_reader;
	private boolean useJLine = true;

	public JLineConsoleHandler(ConsoleReader inReader)
	{
		super();
		this.m_reader = inReader;
		this.useJLine = !("jline.UnsupportedTerminal").equals(System.getProperty("jline.terminal"));
	}

	@Override
	public synchronized void flush()
	{
		try
		{
			if(this.useJLine)
			{
				this.m_reader.print(ConsoleReader.RESET_LINE + "");
				this.m_reader.flush();
				super.flush();
				try
				{
					this.m_reader.drawLine();
	            }
				catch (Exception ex)
				{
	            }

				this.m_reader.flush();
			}
			else
			{
				super.flush();
			}
		}
		catch(Exception e)
		{
			LogManager.getLogger().warning("Error while handling log entry: " + e.getMessage());
		}
	}
}