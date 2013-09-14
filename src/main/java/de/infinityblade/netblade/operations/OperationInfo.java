package de.infinityblade.netblade.operations;

import java.lang.reflect.Method;
import de.infinityblade.netblade.LogManager;
import de.infinityblade.netblade.NetBladeLogLevel;
import de.infinityblade.netblade.network.Client;
import de.infinityblade.netblade.network.packets.Packet;

class OperationInfo
{
	private final PacketOperation m_operation;
	private final OperationExecutor m_executor;
	private final Method m_method;

	public OperationInfo(PacketOperation inOperation, OperationExecutor inExecutor, Method inMethod)
	{
		this.m_operation = inOperation;
		this.m_executor = inExecutor;
		this.m_method = inMethod;
	}

	int getOpCode()
	{
		return this.m_operation.opcode();
	}

	public void execute(Packet inPacket, Client inClient)
	{
		if(!this.m_method.getParameterTypes()[0].isAssignableFrom(inPacket.getClass()))
		{
			LogManager.getLogger().log(NetBladeLogLevel.NOTICE, "Wrong packet type provided for method " + this.m_method.getName() + ".");
			LogManager.getLogger().log(NetBladeLogLevel.NOTICE, "Type was: " + inPacket.getClass().getName() + " ; Expected: " + this.m_method.getParameterTypes()[0].getName());
			return;
		}

		try
		{

			this.m_method.invoke(this.m_executor, inPacket, inClient);
		}
		catch(Exception e)
		{
			LogManager.getLogger().warning("Unable to execute method for packet " + this.getOpCode() + ": " + e.getMessage());
			e.printStackTrace();
		}
	}
}