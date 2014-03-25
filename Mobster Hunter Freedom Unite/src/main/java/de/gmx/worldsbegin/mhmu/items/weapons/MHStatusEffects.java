package de.gmx.worldsbegin.mhmu.items.weapons;

import java.util.UUID;

import de.gmx.worldsbegin.mhmu.util.Strings;

public enum MHStatusEffects {
	STUN(Strings.stunDmgIdent, "Status Effect: Stun", UUID.fromString("108E8AB8-CC8F-42A6-A9FC-D2C639741771")),
	POSISON(Strings.posionDmgIdent, "Status Effect: Poison", UUID.fromString("32752E69-6905-4C0A-91D0-44D78F84DEA4"));

	public final String damageType;
	public final String clearName;
	public final UUID uuid;

	private MHStatusEffects(final String damageType, final String clearName, final UUID uuid)
	{
		this.damageType = damageType;
		this.clearName = clearName;
		this.uuid = uuid;
	}
}
