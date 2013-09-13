package de.infinityblade.netblade;

import java.util.logging.Level;

public class NetBladeLogLevel extends Level
{
    public static final NetBladeLogLevel NOTICE = new NetBladeLogLevel("NOTICE", 850);

    protected NetBladeLogLevel(String name, int value)
    {
        super(name, value);
    }
}