package de.infinityblade.netblade.operations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import de.infinityblade.netblade.LogManager;
import de.infinityblade.netblade.NetBlade;
import de.infinityblade.netblade.network.Client;
import de.infinityblade.netblade.network.packets.Packet;

public class OperationHandler
{
	private Map<Integer, OperationInfo> m_methods = new HashMap<Integer, OperationInfo>();
	private final String m_name;

	public OperationHandler(String inName)
	{
		this.m_name = inName;
	}

	public void register(OperationExecutor executor)
	{
		int countBefore = this.m_methods.size();
		for(Method method : executor.getClass().getMethods())
		{
			if(method.getParameterTypes().length == 2)
			{
				if(!Packet.class.isAssignableFrom(method.getParameterTypes()[0]) || !method.getParameterTypes()[1].isAssignableFrom(Client.class))
					continue;

				for(Annotation ann : method.getAnnotations())
				{
					if(ann instanceof PacketOperation)
					{
						PacketOperation op = (PacketOperation)ann;
						this.m_methods.put(op.opcode(), new OperationInfo(op, executor, method));
						break;
					}
				}
			}
		}

		LogManager.getLogger().config("[" + this.m_name + "] Added " + (m_methods.size() - countBefore) + " new packet handling method(s). " + this.m_methods.size() + " in total.");
	}

	public void handle(Client inClient, Packet inPacket)
	{
		OperationInfo info = this.m_methods.get(inPacket.getOpCode());
		if(info != null)
			info.execute(inPacket, inClient);
		else
			NetBlade.getServer().getLogger().fine("[" + this.m_name + "] No method exist for opcode " + inPacket.getOpCode());
	}

	public String getName()
	{
		return this.m_name;
	}
}