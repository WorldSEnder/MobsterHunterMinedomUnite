package de.gmx.worldsbegin.mhmu.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;

import com.google.common.base.Splitter;

/**
 * For some looting Sources like Trenya or the field this is somewhat
 * like a generic source with an identifier. And because then there was already
 * some code there, this is now used for everything.
 * @author Carbon
 * @version 0.0.1a_26.07.2013
 */
public class LootingSource {
	public static class SourceFarm
	{
		public static class SourceBush
		{
			public final Type spot1 = Type.farmbushSpot1;
			public final Type spot2 = Type.farmbushSpot2;
			public final Type spot3 = Type.farmbushSpot3;
			public final Type spot4 = Type.farmbushSpot4;
			public final Type spot5 = Type.farmbushSpot5;
			private SourceBush()
			{

			}
		}
		public static class SourceField
		{
			public final Type rightRow = Type.farmfieldright;
			public final Type middleRow = Type.farmfieldmiddle;
			public final Type leftRow = Type.farmfieldleft;
			private SourceField()
			{

			}
		}
		public static class SourceFish
		{
			public final Type spot1 = Type.farmfishspot1;
			public final Type spot2 = Type.farmfishspot2;
			public final Type spot3 = Type.farmfishspot3;
			public final Type netMachine = Type.farmfishnet;
			private SourceFish()
			{

			}
		}
		public static class SourceMining
		{
			public final Type spot1 = Type.farmminingSpot1;
			public final Type spot2 = Type.farmminingSpot2;
			public final Type spot3 = Type.farmminingSpot3;
			public final Type spot4 = Type.farmminingSpot4;
			public final Type spot5 = Type.farmminingSpot5;
			private SourceMining()
			{

			}
		}

		public final Type honey = Type.farmhoney;
		public final Type mushroom = Type.farmmushroom;
		public final Type tree = Type.farmtree;
		public final Type trenya = Type.farmtrenya;
		public final Type blackSword = Type.farmsword;
		public final Type ox = Type.farmox;

		public final SourceMining mining = new SourceMining();
		public final SourceBush bush = new SourceBush();
		public final SourceFish fish = new SourceFish();
		public final SourceField field = new SourceField();

		private SourceFarm()
		{

		}
	}
	public static class SourceQuest
	{
		public static class SourceMonster
		{
			public final Type mininon = Type.questmonsterminion;
			public final Type boss = Type.questmonstermain;
			public final Type custom = Type.questmonstercustom;

			private SourceMonster()
			{

			}
		}

		public final SourceMonster monster = new SourceMonster();

		private SourceQuest()
		{

		}
	}
	public static class SourceVillage
	{
		private SourceVillage()
		{

		}
	}
	protected enum Type
	{
		custom("custom"),
		villageshopfelyne("village.shop.felyne"),
		villageshopwoman("village.shop.woman"),
		villageshoppokke("village.shop.pokké"),
		villagekitchencat("village.kitchen.cookingcat"),
		villagekitchengrill("village.kitchen.grill"),
		farmhoney("farm.honey"),
		farmmushroom("farm.mushroom"),
		farmminingSpot1("farm.mining.spot1"),
		farmminingSpot2("farm.mining.spot2"),
		farmminingSpot3("farm.mining.spot3"),
		farmminingSpot4("farm.mining.spot4"),
		farmminingSpot5("farm.mining.spot5"),
		farmbushSpot1("farm.bush.spot1"),
		farmbushSpot2("farm.bush.spot2"),
		farmbushSpot3("farm.bush.spot3"),
		farmbushSpot4("farm.bush.spot4"),
		farmbushSpot5("farm.bush.spot5"),
		farmfishspot1("farm.fish.spot1"),
		farmfishspot2("farm.fish.spot2"),
		farmfishspot3("farm.fish.spot3"),
		farmfishnet("farm.fish.net"),
		farmfieldleft("farm.field.left"),
		farmfieldmiddle("farm.field.middle"),
		farmfieldright("farm.field.right"),
		farmox("farm.ox"),
		farmtree("farm.tree"),
		farmtrenya("farm.trenya"),
		farmsword("farm.blacksword"),
		questmonsterminion("quest.monster.minion"),
		questmonstermain("quest.monster.main"),
		questmonstercustom("quest.monster.custom"),
		questharvestmining("quest.harvest.mining"),
		questharvestgathering("quest.harvest.gathering"),
		questharvestnet("quest.harvest.net");

		public final String identifier;
		private Type(String ident)
		{
			this.identifier = ident;
		}
		public boolean equals(String identifier)
		{
			for(String partialIdent : Splitter.on(" ").omitEmptyStrings().trimResults().split(identifier))
			{
				if(partialIdent.matches(identifier.replace(".", "\\.") + "(\\..*)?"))
				{
					return true;
				}
			}
			return false;
		}
		@Override
		public String toString()
		{
			return this.identifier;
		}
	}

	public static final SourceFarm farm;
	public static final SourceQuest quest;
	public static final SourceVillage village;
	static //old fragment, why change?
	{
		farm = new SourceFarm();
		quest = new SourceQuest();
		village = new SourceVillage();
	}
	public static LootingSource fromBlock(Block block)
	{
		return null; //TODO fromBlock()
	}
	public static LootingSource fromEntity(Entity entity)
	{
		return null; //TODO fromEntity()
	}
	public static LootingSource generic(String customIdentifier, Object[] objects)
	{
		return new LootingSource(Type.custom + "." + customIdentifier, objects);
	}

	public final String type;
	/**
	 * Objects this type requires. E.g. the block, quest, entity it has been looted from
	 */
	public final Object[] objects;

	private LootingSource(){this.type="null";this.objects=null;}

	protected LootingSource(String type, Object[] objects)
	{
		this.type = type;
		this.objects = objects;
	}
}
