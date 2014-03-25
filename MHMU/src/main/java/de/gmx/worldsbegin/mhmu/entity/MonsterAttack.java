package de.gmx.worldsbegin.mhmu.entity;


public abstract class MonsterAttack{
	private static int attackId = 0;
	/** A (generic) name for this attack*/
	private String name;
	//methods
	public MonsterAttack()
	{
		this(""+(attackId+1));
	}
	public MonsterAttack(String name)
	{
		attackId++;
		this.name = name;
	}
	/**
	 *  Set up for the attack
	 */
	public abstract void execute();
	/**
	 * @return the animation is stored in the model-file of an entity.
	 * Each animation you have in there is referred to as a String
	 * (which is internally an int). Anyways, the String is the same
	 * you used to name the animation so if you want to play the animation
	 * you referred to as "chomp" than you should return "chomp" here.
	 * This is case-sensitive so be careful.
	 */
	public abstract String getAnimationId();
	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * Returns the weight of this attack in comparison to the aóther attacks the entity
	 * can perform
	 */
	public abstract int getWeight();
	/**
	 * @return - Whether this attack is finished and a new one should
	 * 			be selected by the AI.
	 * 			Eventually after the cycle of your attack is finished.
	 */
	public abstract boolean isFinished();
	/** Maybe something about distances, rage, rank?
	 *  Called AFTER an attack was selected
	 * @return - Whether this attack should be executed by the mob
	 * 			if not the attack will be regenerated
	 */
	public boolean shouldBeExecuted()
	{
		return true;
	}
	/**
	 * Do everything necessary like spawning entities like bullets at the right frames...
	 * Normally you get the current frame by calling {@link entity.getCurrentAttackFrame()}.
	 * <br>YOU DO NOT HAVE TO SET LOCATION AND ROTATION.  (I think you even can't ;? )
	 * <br>You should have access to the monster you need to update over as MonsterAttacks
	 * should be declared in {@code setUpAttacks()}
	 */
	public abstract void updateExecutionalProcess();
}
