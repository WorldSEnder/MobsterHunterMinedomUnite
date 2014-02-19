package de.gmx.worldsbegin.mhmu.util;

/**
 * Holds the {@link String}s for the project. Used to access them from everywhere and
 * keep the possibility to edit them.
 * @author Carbon
 * 
 * @version 0.0.1a_20.07.2013
 */
public final class Strings {

	public static final String modelfolder = "models/";
	public static final String weaponsfolder = "weapons/";
	public static final String longswordIconName = "Longsword_IconBase";

	public static final String felyne_internal = "felyne";
	public static final String armordude_internal = "craftsman";

	private static final String mhDmgIdenPrefix = "generic.MHdamage.";
	private static final String elementPref = "element.";
	private static final String statusEffectPrefix = "status.";
	public static final String rawDmgIden = mhDmgIdenPrefix + "raw";
	public static final String thunderDmgIdent = mhDmgIdenPrefix + elementPref + "thunder";
	public static final String fireDmgIdent = mhDmgIdenPrefix + elementPref + "fire";
	public static final String iceDmgIdent = mhDmgIdenPrefix + elementPref + "ice";
	public static final String waterDmgIdent = mhDmgIdenPrefix + "water";
	public static final String dragonDmgIdent = mhDmgIdenPrefix + "dragon";
	public static final String stunDmgIdent = mhDmgIdenPrefix + statusEffectPrefix + "stun";
	public static final String posionDmgIdent = mhDmgIdenPrefix + statusEffectPrefix + "posion";
}
