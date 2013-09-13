package de.infinityblade.netblade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class StripClassFormatter extends SimpleFormatter
{
	private final SimpleDateFormat dateFormat;

	public StripClassFormatter()
	{
		super();
		dateFormat = new SimpleDateFormat("HH:mm:ss");
	}

	@Override
	public String format(LogRecord record)
	{
		return "[" + this.dateFormat.format(new Date(record.getMillis())) + "][" + record.getLevel().toString() + "] " + record.getMessage() + "\n";
	}
}