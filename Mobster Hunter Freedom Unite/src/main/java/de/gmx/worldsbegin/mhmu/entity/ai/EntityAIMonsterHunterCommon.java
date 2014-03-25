package de.gmx.worldsbegin.mhmu.entity.ai;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;
import de.gmx.worldsbegin.mhmu.entity.MonsterAttack;

/**
 * I recommend putting this task directly after {@link EntityAISwimming}
 * and all the other survival things
 * @author Carbon
 *
 */
public class EntityAIMonsterHunterCommon extends EntityAIBase{
	private static final boolean active = true;
	public int currentAttackFrame = 0;
	private EntityMinedom entity;
	private Random rand;

	private MonsterAttack currentAttack;

	public EntityAIMonsterHunterCommon(EntityMinedom entity, Random rand)
	{
		this.entity = entity;
		this.rand = rand;
		this.setMutexBits(3);
		this.selectNewAttack();
	}
	@Override
	public boolean continueExecuting() {
		return this.currentAttack==null||!this.currentAttack.isFinished()||this.shouldExecute();
	}

	/**
	 * The entity should be allowed to swim, basic movement, etc.
	 */
	@Override
	public boolean isInterruptible() {
		return true;
	}
	private void selectNewAttack()
	{
		if(this.entity.performableAttacks==null)
		{
			this.currentAttack = null;
			return;
		}
		int overallAttackRarity = 0, counter = 0;
		ArrayList<MonsterAttack> executableMonsterAttacks = new ArrayList<MonsterAttack>(0);
		boolean isListFilled = false;
		for(MonsterAttack ma : this.entity.performableAttacks){
			isListFilled |= ma.shouldBeExecuted();
			if(ma.shouldBeExecuted()) {
				executableMonsterAttacks.add(ma);
			}
		}
		if(isListFilled)
		{
			for(MonsterAttack ma: executableMonsterAttacks) {
				overallAttackRarity += ma.getWeight();
			}
			int selected = this.rand.nextInt(overallAttackRarity);
			MonsterAttack selectedAttack = null;
			for(MonsterAttack ma : executableMonsterAttacks)
			{
				counter += ma.getWeight();
				if(selected<counter)
				{
					selectedAttack = ma;
					break;
				}
			}
			this.currentAttackFrame = 0;
			this.currentAttack = selectedAttack;
			this.entity.getDataWatcher().updateObject(20, Integer.valueOf(this.currentAttackFrame));
			int i;
			for(i = 0; i<this.entity.performableAttacks.length; i++)
			{
				if(this.currentAttack==this.entity.performableAttacks[i]) {
					break;
				}
			}
			this.entity.getDataWatcher().updateObject(19, Integer.valueOf(i));
			if(selectedAttack!=null) {
				selectedAttack.execute();
			}
		}else{
			this.currentAttack = null;
		}
	}

	@Override
	public boolean shouldExecute() {
		boolean shouldExecute = false;
		for(MonsterAttack attack : this.entity.performableAttacks)
		{
			shouldExecute = shouldExecute||attack.shouldBeExecuted();
		}
		return shouldExecute;
	}

	@Override
	public void startExecuting()
	{
		this.selectNewAttack();
	}

	@Override
	public void updateTask() {
		if(!active) {
			return;
		}
		this.currentAttackFrame++;
		if(this.entity.forcedAttack!=null)
		{
			this.currentAttackFrame = 0;
			this.currentAttack = this.entity.forcedAttack;
			this.entity.forcedAttack = null;
			int i;
			for(i = 0; i<this.entity.performableAttacks.length; i++)
			{
				if(this.currentAttack==this.entity.performableAttacks[i]) {
					break;
				}
			}
			this.entity.getDataWatcher().updateObject(19, Integer.valueOf(i));
		}
		this.entity.getDataWatcher().updateObject(20, Integer.valueOf(this.currentAttackFrame));
		if(!this.currentAttack.isFinished())
		{
			this.currentAttack.updateExecutionalProcess();
		}else {
			this.selectNewAttack();
		}
	}

}
