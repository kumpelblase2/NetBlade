package de.infinityblade.netblade.operations;

import java.lang.reflect.Method;
import de.infinityblade.netblade.LogManager;
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