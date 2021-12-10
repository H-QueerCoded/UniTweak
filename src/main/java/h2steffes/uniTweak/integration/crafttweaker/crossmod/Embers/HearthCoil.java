package h2steffes.uniTweak.integration.crafttweaker.crossmod.Embers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.*;
import crafttweaker.mc1120.CraftTweaker;
import h2steffes.uniTweak.util.ResourceHandling;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;
import teamroots.embers.recipe.HeatCoilRecipe;
import teamroots.embers.recipe.RecipeRegistry;

@ModOnly("embers")
@ZenClass("mods.unitweak.embers.hearthCoil")
@ZenRegister
public class HearthCoil {
	
	@ZenMethod
	public static void add(String outputKind, int outCount, String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, outCount, inputKind));
	}
	
	@ZenMethod
	public static void remove(String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(inputKind));
	}
	
	public static class Add implements IAction{
		
		String inputKindString, outputKindString;
		int outputCount;

		Add(String outputKind, int outCount, String inputKind){
			inputKindString = inputKind;
			outputKindString = outputKind;
			outputCount = outCount;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int inputKindInt = Resource.getKindFromName(inputKindString);
			int outputKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(inputKindInt, outputKindInt);
			
			for(Resource resource : matchingResources) {
				ItemStack outputStack = resource.getChild(outputKindInt).getMainEntry(outputCount);
				IOreDictEntry inputOreDictEntry = ResourceHandling.getOreDictEntry(resource, inputKindInt);
				CraftTweakerAPI.logInfo("UniTweak: Adding Embers Hearth Coil recipe for "+inputOreDictEntry.getName()+" to "+outputStack.getCount()+" "+outputStack.getDisplayName());
				RecipeRegistry.heatCoilRecipes.add(new HeatCoilRecipe(outputStack, CraftTweakerMC.getIngredient(inputOreDictEntry)));
			}
			
		}

		@Override
		public String describe() {
			return "UniTweak: Adding recipes for Embers Hearth Coil in pattern "+inputKindString+" to "+outputCount+" "+outputKindString;
		}
		
	}
	
	public static class Remove implements IAction{
		
		String inputKindString;
		
		public Remove(String input) {
			this.inputKindString = input;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int inputKindNum = Resource.getKindFromName(inputKindString);
			List<Resource> mathcingResources = uniDictAPI.getResources(inputKindNum);
			
			for(Resource resource : mathcingResources) {
				List<ItemStack> inputList = resource.getChild(inputKindNum).getEntries();
				for (ItemStack input : inputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Embers Hearth Coil recipes with input "+input.getDisplayName());
					RecipeRegistry.heatCoilRecipes.removeAll(getRecipesByInput(input));
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing recipes for Embers Hearth Coil with input kind "+inputKindString;
		}
		
		private static List<HeatCoilRecipe> getRecipesByInput(ItemStack stack) {
	        return RecipeRegistry.heatCoilRecipes.stream().filter(recipe -> recipe.matches(stack)).collect(Collectors.toCollection(ArrayList::new));
	    }
		
	}
}
