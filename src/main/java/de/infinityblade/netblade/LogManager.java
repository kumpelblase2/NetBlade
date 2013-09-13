package de.infinityblade.netblade;

import java.util.logging.Logger;

public final class LogManager
{
	private static Logger s_log;

	public static void registerLogger(Logger inLogger)
	{
		s_log = inLogger;
		s_log.config("Logger registered.");
	}

	public static Logger getLogger()
	{
		return s_log;
	}
}