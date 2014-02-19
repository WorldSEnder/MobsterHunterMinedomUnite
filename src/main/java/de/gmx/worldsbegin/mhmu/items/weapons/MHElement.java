package de.gmx.worldsbegin.mhmu.items.weapons;

import java.util.UUID;

import de.gmx.worldsbegin.mhmu.util.Strings;

public enum MHElement {
	Dragon(Strings.dragonDmgIdent, "Elemental Damage: Dragon", UUID.fromString("827CE34A-EC2C-4B37-A31A-39DF102A3AE6")),
	Water(Strings.waterDmgIdent, "Elemental Damage: Water", UUID.fromString("8FA90D47-1B73-4075-9FED-195633556260")),
	Thunder(Strings.thunderDmgIdent, "Elemental Damage: Thunder", UUID.fromString("5B93996A-2E02-4448-B505-EC54A5731E30")),
	Fire(Strings.fireDmgIdent, "Elemental Damage: Fire", UUID.fromString("D8521EB4-C7D0-49B0-A8D8-C25DD0FD6595")),
	Ice(Strings.iceDmgIdent, "Elemental Damage: Ice", UUID.fromString("91808A79-F43E-485D-BBBF-F5F80044656D"));

	public final String damageType;
	public final String clearName;
	public final UUID uuid;

	private MHElement(String damageType, String clearName, UUID uuid)
	{
		this.damageType = damageType;
		this.clearName = clearName;
		this.uuid = uuid;
	}
}
