package de.gmx.worldsbegin.mhmu.client.model;

import java.io.IOException;

import net.minecraftforge.client.model.ModelFormatException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.gmx.worldsbegin.mhmu.client.model.exObj.MHModelObject;

@SideOnly(Side.CLIENT)
public class ModelFelyne extends ModelMinedom {

	public ModelFelyne() throws ModelFormatException, IOException {
		super(new MHModelObject(null, null));
	}
}
