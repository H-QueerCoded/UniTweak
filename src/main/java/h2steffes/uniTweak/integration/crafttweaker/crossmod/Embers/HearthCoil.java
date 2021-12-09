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
	public static void add(String output, int outCount, String input) {
		CraftTweaker.LATE_ACTIONS.add(new Add(output, outCount, input));
	}
	
	@ZenMethod
	public static void remove(String input) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(input));
	}
	
	public static class Add implements IAction{
		
		String inputKind, outputKind;
		int outCount;

		Add(String o, int oC, String i){
			inputKind = i;
			outputKind = o;
			outCount = oC;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int input = Resource.getKindFromName(inputKind);
			int output = Resource.getKindFromName(outputKind);
			List<Resource> inAndOut = uniDictAPI.getResources(input, output);
			
			for(Resource resource : inAndOut) {
				ItemStack outStack = resource.getChild(output).getMainEntry(outCount);
				IOreDictEntry in = ResourceHandling.getOreDictEntry(resource, input);
				CraftTweakerAPI.logInfo("UniTweak: Adding Embers Hearth Coil recipe for "+in.getName()+" to "+outStack.getCount()+" "+outStack.getDisplayName());
				RecipeRegistry.heatCoilRecipes.add(new HeatCoilRecipe(outStack, CraftTweakerMC.getIngredient(in)));
			}
			
		}

		@Override
		public String describe() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class Remove implements IAction{
		
		String input;
		
		public Remove(String input) {
			this.input = input;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int kind = Resource.getKindFromName(input);
			List<Resource> list = uniDictAPI.getResources(input);
			
			for(Resource resource : list) {
				List<ItemStack> inputList = resource.getChild(kind).getEntries();
				for (ItemStack input : inputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Embers Hearth Coil recipes with input "+input.getDisplayName());
					RecipeRegistry.heatCoilRecipes.removeAll(getRecipesByInput(input));
				}
			}
		}

		@Override
		public String describe() {
			// TODO Auto-generated method stub
			return null;
		}
		
		private static List<HeatCoilRecipe> getRecipesByInput(ItemStack stack) {
	        return RecipeRegistry.heatCoilRecipes.stream().filter(recipe -> recipe.matches(stack)).collect(Collectors.toCollection(ArrayList::new));
	    }
		
	}
}
