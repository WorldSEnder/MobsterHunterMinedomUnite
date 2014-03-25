/**
 * 
 */
package de.gmx.worldsbegin.mhmu.village.quest;

import de.gmx.worldsbegin.mhmu.entity.EntityMinedom;

/**
 * @author Carbon
 */
public class ActiveQuest {
	protected static class UltimateGoal extends QuestGoal {
		private ActiveQuest questReference;

		public UltimateGoal(QuestGoal... initialGoals) {
			super(null);
			for (QuestGoal qG : initialGoals) {
				if (qG != null) {
					this.children.add(qG);
				}
			}
		}

		@Override
		public void initialize(ActiveQuest contextQuest) {
			this.questReference = contextQuest;
		}

		@Override
		protected void onCompletition() {
			this.questReference.onGoalComplete(this);
		}
	}

	public final Quest pattern;
	public final int activeID;
	public final Party party;

	private UltimateGoal finalGoal;

	protected ActiveQuest(Quest quest, int id) {
		this.pattern = quest;
		this.party = new Party();
		this.activeID = id;
	}

	public void forceTerminate() {
		// TODO Auto-generated method stub
	}

	public void onDeathOf(EntityMinedom entityMinedom) {
		// TODO onDeathof()
	}

	public void onGoalComplete(QuestGoal questGoal) {
		if (questGoal == this.finalGoal) {
			this.onQuestComplete();
		}
	}

	protected void onQuestComplete() {

	}

	/**
	 * Every EVERY active quest has an unique identifier. The first part is
	 * based on the quest this active quest is derived from. The second part
	 * uniquely identifies the instance.
	 */
	@Override
	public String toString() {
		return "ActiveQuest:" + this.pattern.toString() + "_" + this.activeID;
	}
}
