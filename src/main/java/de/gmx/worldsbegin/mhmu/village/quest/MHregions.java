package de.gmx.worldsbegin.mhmu.village.quest;

/**
 * @author Carbon
 * 
 * @version 0.0.1a_26.07.2013
 */
public enum MHregions {
	snowyMountains(""), //TODO unlocalizedNames
	jungle(""),
	swamp(""),
	desert(""),
	volcano(""),
	forestAndHills(""),
	oldJungle(""),
	oldSwamp(""),
	oldDesert(""),
	oldVolcano(""),
	greatForest(""),
	fortress(""),
	town(""),
	castleSherade(""),
	tower(""),
	towerBroken(""),
	towerInTheSky(""),
	battleground(""),
	snowyMountainPeak(""),
	arena(""),
	greatArena(""),
	moatArena("");

	public final String unlocalizedName;

	private MHregions(String unlocName)
	{
		this.unlocalizedName = unlocName;
	}
}
