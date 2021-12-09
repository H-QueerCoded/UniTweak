package h2steffes.uniTweak.integration.crafttweaker.crossmod.Embers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;
import teamroots.embers.compat.crafttweaker.CTUtil;
import teamroots.embers.recipe.ItemMeltingRecipe;
import teamroots.embers.recipe.RecipeRegistry;

@ModOnly("embers")
@ZenClass("mods.unitweak.embers.melter")
@ZenRegister
public class Melter {
	
	@ZenMethod
	public static void add(int liquidAmount, String input) {
		CraftTweaker.LATE_ACTIONS.add(new Add(liquidAmount, input));
	}
	
	@ZenMethod
	public static void addSecondOutput(ILiquidStack bonus, IItemStack input) {
		CraftTweaker.LATE_ACTIONS.add(new AddSecondary(bonus, input));
	}
	
	public static class Add implements IAction{
		String input;
		int liquidAmount;
		
		public Add(int liquidAmount, String input) {
			this.input=input;
			this.liquidAmount=liquidAmount;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int kind = Resource.getKindFromName(input);
			List<Resource> list = uniDictAPI.getResources(kind);
			
			for(Resource resource : list) {
				ItemStack inStack = resource.getChild(kind).getMainEntry();
				if(!FluidRegistry.isFluidRegistered(resource.getName().toLowerCase())){
					CraftTweakerAPI.logInfo("UniTweak: No molten version of "+resource.getName()+", skipping melter recipe");
					continue;
				}
				Fluid fluid = FluidRegistry.getFluid(resource.getName().toLowerCase());
				
				RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe(CraftingHelper.getIngredient(inStack), new FluidStack(fluid, liquidAmount)));
			}
		}

		@Override
		public String describe() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class AddSecondary implements IAction{
		ILiquidStack bonus;
		ItemStack input;
		
		public AddSecondary(ILiquidStack outputFluid, IItemStack input) {
			this.bonus = outputFluid;
			this.input = CraftTweakerMC.getItemStack(input);
		}

		@Override
		public void apply() {
			for(ItemMeltingRecipe recipe: getRecipesByInput(input)) {
				recipe.addBonusOutput(CraftTweakerMC.getLiquidStack(bonus));
			}
		}

		@Override
		public String describe() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private static List<ItemMeltingRecipe> getRecipesByInput(ItemStack stack)
    {
        return RecipeRegistry.meltingRecipes.stream().filter(recipe -> recipe.input.apply(stack)).collect(Collectors.toCollection(ArrayList::new));
    }
}
