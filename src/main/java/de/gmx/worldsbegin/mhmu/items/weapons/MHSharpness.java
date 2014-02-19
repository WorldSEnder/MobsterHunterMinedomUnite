package de.gmx.worldsbegin.mhmu.items.weapons;

public enum MHSharpness {
	RED(0.5F, 0.25F, 0xFF0000), 
	ORANGE(0.75F, 0.5F, 0xFF8800), 
	YELLOW(1F, 0.75F, 0xFFFF00), 
	GREEN(1.125F, 1F, 0x00FF00), //TODO richtige farben
	BLUE(1.25F, 1.0625F, 0x0000FF), 
	WHITE(1.3F, 1.125F, 0xFFFFFF), 
	PURPLE(1.5F, 1.25F, 0xFF00FF);
	
	public final float attackMultiplier;
	public final float elementMultiplier;
	public final int color;
	
	private MHSharpness(final float attackMultiplier, final float elementMultiplier, final int color)
	{
		this.attackMultiplier = attackMultiplier;
		this.elementMultiplier = elementMultiplier;
		this.color = color;
	}
}
