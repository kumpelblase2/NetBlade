package de.infinityblade.netblade.operations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PacketOperation
{
	public int opcode();
}