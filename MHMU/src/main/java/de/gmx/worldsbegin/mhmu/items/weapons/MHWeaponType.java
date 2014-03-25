package de.gmx.worldsbegin.mhmu.items.weapons;

public enum MHWeaponType {
	LONGSWORD(1/4.8F), 
	GREATSWORD(1/4.8F), 
	HAMMER(1/5.2F), 
	HUNTINGHORN(1/5.2F), 
	LANCE(1/2.3F), 
	GUNLANCE(1/2.3F), 
	SWORDANDSHIELD(1/1.4F), 
	DUALSWORDS(1/1.4F), 
	BOW(1/1.2F), 
	LIGHTCROSSBOW(0F), 
	HEAVYCROSSBOW(0F);
	
	public final float typeMultiplier;
	
	private MHWeaponType(final float typeMultplier)
	{
		this.typeMultiplier = typeMultplier;
	}
}
