package de.gmx.worldsbegin.mhmu.village.quest;

import java.util.ArrayList;
import java.util.List;

public abstract class QuestGoal {
	protected final QuestGoal parent;
	protected List<QuestGoal> children;

	public QuestGoal(QuestGoal parentGoal) {
		this.parent = parentGoal;
		this.children = new ArrayList<QuestGoal>();
	}

	// TODO QuestGoal
	public abstract void initialize(ActiveQuest contextQuest);

	public final boolean isCompleted() {
		for (QuestGoal child : this.children) {
			if (!child.isOptional())
				return false;
		}
		return this.isGoalComplete();
	}

	protected boolean isGoalComplete() {
		return true;
	}

	public boolean isOptional() {
		return true;
	}

	protected void onChildGoalComplete(QuestGoal questGoal) {
		this.children.remove(questGoal);
		if (this.isCompleted()) {
			this.setCompleted();
		}
	}

	protected void onCompletition() {
		if (this.parent != null) {
			this.parent.onChildGoalComplete(this);
		}
	}

	protected void setCompleted() {
		this.onCompletition();
	}
}
