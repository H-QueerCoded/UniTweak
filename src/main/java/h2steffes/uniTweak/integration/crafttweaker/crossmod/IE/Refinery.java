package h2steffes.uniTweak.integration.crafttweaker.crossmod.IE;

import java.util.ArrayList;
import blusunrize.immersiveengineering.api.crafting.RefineryRecipe;
import blusunrize.immersiveengineering.common.IEContent;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;


@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.IE.refinery")
@ZenRegister
public class Refinery {
	
	@ZenMethod
	public static void biodieselIngredients(ILiquidStack[] plantOils, ILiquidStack[] ethanols) {
		CraftTweakerAPI.apply(new Add(plantOils, ethanols));
	}

	public static class Add implements IAction {
		int eAmount = 8;
		int pAmount = 8;
		int bAmount = 16;
		ArrayList<FluidStack> ethanols = new ArrayList<FluidStack>();
		ArrayList<FluidStack> plantOils = new ArrayList<FluidStack>();
		
		public Add(ILiquidStack[] plantOils, ILiquidStack[] ethanols) {
			this.ethanols.add(new FluidStack(IEContent.fluidEthanol, eAmount));
			for(ILiquidStack lE : ethanols) {
				this.ethanols.add(CraftTweakerMC.getLiquidStack(lE));
			}
			
			this.plantOils.add(new FluidStack(IEContent.fluidPlantoil, pAmount));
			for(ILiquidStack lP : plantOils) {
				this.plantOils.add(CraftTweakerMC.getLiquidStack(lP));
			}
		}

		@Override
		public void apply() {
			for(FluidStack p : plantOils) {
				for(FluidStack e : ethanols) {
					if(e.getFluid()==IEContent.fluidEthanol && p.getFluid()==IEContent.fluidPlantoil) {
						continue;
					}
					RefineryRecipe.addRecipe(new FluidStack(IEContent.fluidBiodiesel, bAmount), p, e, 80);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Adding new biodiesel recipes";
		}
	}
	
}
