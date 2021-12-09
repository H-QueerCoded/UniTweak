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
	public static void remove(String input) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(input));
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
			return "UniTweak: Creating Embers Melter recipes for "+input+" to "+liquidAmount+"mb";
		}
		
	}
	
	public static class Remove implements IAction{
		String inputKind;
		
		public Remove(String input) {
			inputKind = input;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int kind = Resource.getKindFromName(inputKind);
			List<Resource> list = uniDictAPI.getResources(kind);
			
			for(Resource resource : list) {
				List<ItemStack> inputList = resource.getChild(kind).getEntries();
				for (ItemStack input : inputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Embers Melter recipes with input "+input.getDisplayName());
					RecipeRegistry.meltingRecipes.removeAll(getRecipesByInput(input));
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing Embers Melter recipes for input kind "+inputKind;
		}
	}
	
	public static List<ItemMeltingRecipe> getRecipesByInput(ItemStack stack)
    {
        return RecipeRegistry.meltingRecipes.stream().filter(recipe -> recipe.input.apply(stack)).collect(Collectors.toCollection(ArrayList::new));
    }
}
