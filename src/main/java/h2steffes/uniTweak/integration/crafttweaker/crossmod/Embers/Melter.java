package h2steffes.uniTweak.integration.crafttweaker.crossmod.Embers;

import java.util.ArrayList;
import java.util.Collections;
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
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.CraftTweaker;
import h2steffes.uniTweak.util.ResourceHandling;
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
	
	static List<String> resourcesToIgnore =  new ArrayList<String>();
	
	@ZenMethod
	public static void ignore(String[] resources) {
		resourcesToIgnore.clear();
		Collections.addAll(resourcesToIgnore, resources);
	}
	
	@ZenMethod
	public static void add(int liquidAmount, String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Add(liquidAmount, inputKind));
	}
	
	@ZenMethod
	public static void remove(String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(inputKind));
	}
	
	public static class Add implements IAction{
		String inputKindString;
		int liquidAmount;
		
		public Add(int liquidAmount, String input) {
			this.inputKindString=input;
			this.liquidAmount=liquidAmount;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int inputKindInt = Resource.getKindFromName(inputKindString);
			List<Resource> mathcingResources = uniDictAPI.getResources(inputKindInt);
			
			for(Resource resource : mathcingResources) {
				if(resourcesToIgnore.contains(resource.getName())) {
					CraftTweakerAPI.logInfo("UniTweak: Skipping resource "+resource.getName());
					continue;
				}
				IOreDictEntry inputOreDictEntry = ResourceHandling.getOreDictEntry(resource, inputKindInt);
				if(!FluidRegistry.isFluidRegistered(resource.getName().toLowerCase())){
					CraftTweakerAPI.logInfo("UniTweak: No molten version of "+resource.getName()+", skipping melter recipe");
					continue;
				}
				Fluid fluid = FluidRegistry.getFluid(resource.getName().toLowerCase());
				
				RecipeRegistry.meltingRecipes.add(new ItemMeltingRecipe( CraftTweakerMC.getIngredient(inputOreDictEntry), new FluidStack(fluid, liquidAmount)));
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Creating Embers Melter recipes for "+inputKindString+" to "+liquidAmount+"mb";
		}
		
	}
	
	public static class Remove implements IAction{
		String inputKindString;
		
		public Remove(String input) {
			inputKindString = input;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int inputKindInt = Resource.getKindFromName(inputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(inputKindInt);
			
			for(Resource resource : matchingResources) {
				if(resourcesToIgnore.contains(resource.getName())) {
					CraftTweakerAPI.logInfo("UniTweak: Skipping resource "+resource.getName());
					continue;
				}
				List<ItemStack> inputList = resource.getChild(inputKindInt).getEntries();
				for (ItemStack input : inputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Embers Melter recipes with input "+input.getDisplayName());
					RecipeRegistry.meltingRecipes.removeAll(getRecipesByInput(input));
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing Embers Melter recipes for input kind "+inputKindString;
		}
	}
	
	public static List<ItemMeltingRecipe> getRecipesByInput(ItemStack stack)
    {
        return RecipeRegistry.meltingRecipes.stream().filter(recipe -> recipe.input.apply(stack)).collect(Collectors.toCollection(ArrayList::new));
    }
}
