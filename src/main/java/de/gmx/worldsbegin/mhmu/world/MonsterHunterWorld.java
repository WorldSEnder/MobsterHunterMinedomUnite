package de.gmx.worldsbegin.mhmu.world;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

public class MonsterHunterWorld extends WorldProvider {

	@Override
	public boolean canRespawnHere() {
		return false;
	}

	@Override
	public IChunkProvider createChunkGenerator() {
		return super.createChunkGenerator();
	}

	@Override
	public String getDepartMessage() {
		return "Returning to the village.";
	}

	@Override
	public String getDimensionName() {
		return "Mobster Hunter";
	}

	@Override
	public String getSaveFolder() {
		return "MHWorld";
	}

	@Override
	public String getWelcomeMessage() {
		return "Quest is being prepared.";
	}

	@Override
	protected void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.beach, 0.1F);
		this.isHellWorld = false;
	}

}
