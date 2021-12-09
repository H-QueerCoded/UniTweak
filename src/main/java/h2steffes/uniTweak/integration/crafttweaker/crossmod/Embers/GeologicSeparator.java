package h2steffes.uniTweak.integration.crafttweaker.crossmod.Embers;

import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import teamroots.embers.recipe.ItemMeltingRecipe;

@ModOnly("embers")
@ZenClass("mods.unitweak.embers.geologicSeparator")
@ZenRegister
public class GeologicSeparator {

	@ZenMethod
	public static void add(ILiquidStack bonus, IItemStack input) {
		CraftTweaker.LATE_ACTIONS.add(new Add(bonus, input));
	}

	public static class Add implements IAction{
		ILiquidStack bonus;
		ItemStack input;
		
		public Add(ILiquidStack outputFluid, IItemStack input) {
			this.bonus = outputFluid;
			this.input = CraftTweakerMC.getItemStack(input);
		}

		@Override
		public void apply() {
			for(ItemMeltingRecipe recipe: Melter.getRecipesByInput(input)) {
				recipe.addBonusOutput(CraftTweakerMC.getLiquidStack(bonus));
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Adding bonus liquid "+bonus.getName()+" to Embers Geological Separator for input "+input.getDisplayName();
		}
	}
}
