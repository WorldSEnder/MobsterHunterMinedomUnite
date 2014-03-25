package de.gmx.worldsbegin.mhmu.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

/**
 * For some looting Sources like Trenya or the field this is somewhat like a
 * generic source with an identifier. And because then there was already some
 * code there, this is now used for everything.
 * 
 * @author Carbon
 * @version 0.0.1a_26.07.2013
 */
public class LootingTarget<T> {
	// protected enum Type {
	// custom("custom"), villageshopfelyne("village.shop.felyne"),
	// villageshopwoman(
	// "village.shop.woman"), villageshoppokke("village.shop.pokké"),
	// villagekitchencat(
	// "village.kitchen.cookingcat"), villagekitchengrill(
	// "village.kitchen.grill"), farmhoney("farm.honey"), farmmushroom(
	// "farm.mushroom"), farmminingSpot1("farm.mining.spot1"), farmminingSpot2(
	// "farm.mining.spot2"), farmminingSpot3("farm.mining.spot3"),
	// farmminingSpot4(
	// "farm.mining.spot4"), farmminingSpot5("farm.mining.spot5"),
	// farmbushSpot1(
	// "farm.bush.spot1"), farmbushSpot2("farm.bush.spot2"), farmbushSpot3(
	// "farm.bush.spot3"), farmbushSpot4("farm.bush.spot4"), farmbushSpot5(
	// "farm.bush.spot5"), farmfishspot1("farm.fish.spot1"), farmfishspot2(
	// "farm.fish.spot2"), farmfishspot3("farm.fish.spot3"), farmfishnet(
	// "farm.fish.net"), farmfieldleft("farm.field.left"), farmfieldmiddle(
	// "farm.field.middle"), farmfieldright("farm.field.right"), farmox(
	// "farm.ox"), farmtree("farm.tree"), farmtrenya("farm.trenya"), farmsword(
	// "farm.blacksword"), questmonsterminion("quest.monster.minion"),
	// questmonstermain(
	// "quest.monster.main"), questmonstercustom(
	// "quest.monster.custom"), questharvestmining(
	// "quest.harvest.mining"), questharvestgathering(
	// "quest.harvest.gathering"), questharvestnet("quest.harvest.net");
	//
	// public final String identifier;
	// private Type(String ident) {
	// this.identifier = ident;
	// }
	//
	// public boolean equals(String identifier) {
	// for (String partialIdent : Splitter.on(" ").omitEmptyStrings()
	// .trimResults().split(identifier)) {
	// if (partialIdent.matches(identifier.replace(".", "\\.")
	// + "(\\..*)?"))
	// return true;
	// }
	// return false;
	// }
	// @Override
	// public String toString() {
	// return this.identifier;
	// }
	// }

	public static <L extends Block> LootingTarget<L> fromBlock(L block) {
		if (block == null)
			return null;
		return new LootingTarget<L>("loot." + block.getUnlocalizedName(), block);
	}

	public static <T extends Entity> LootingTarget<T> fromEntity(T entity) {
		if (entity == null)
			return null;
		return new LootingTarget<T>("loot.entity."
				+ EntityList.getEntityString(entity), entity);
	}

	public static <L> LootingTarget<L> generic(String customIdentifier, L target) {
		// FIXME determine type and automatically return the correct
		// LootingTarget
		return new LootingTarget<L>("loot.generic." + customIdentifier, target);
	}
	public final String type;
	/**
	 * Objects this type requires. E.g. the block, quest, entity it has been
	 * looted from
	 */
	public final T lootTarget;

	protected LootingTarget(String type, T target) {
		this.type = type;
		this.lootTarget = target;
	}
}
