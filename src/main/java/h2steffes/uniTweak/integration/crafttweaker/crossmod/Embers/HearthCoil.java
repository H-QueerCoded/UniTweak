package h2steffes.uniTweak.integration.crafttweaker.crossmod.Embers;

import java.util.ArrayList;
import java.util.Collections;
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
	
	static List<String> resourcesToIgnore =  new ArrayList<String>();
	
	@ZenMethod
	public static void ignore(String[] resources) {
		resourcesToIgnore.clear();
		Collections.addAll(resourcesToIgnore, resources);
	}
	
	@ZenMethod
	public static void addTemplate(String outputKind, int outCount, String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, outCount, inputKind));
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
				if(resourcesToIgnore.contains(resource.getName())) {
					CraftTweakerAPI.logInfo("UniTweak: Skipping resource "+resource.getName());
					continue;
				}
				ItemStack outputStack = resource.getChild(outputKindInt).getMainEntry(outputCount);
				IOreDictEntry inputOreDictEntry = ResourceHandling.getOreDictEntry(resource, inputKindInt);
				RecipeRegistry.heatCoilRecipes.add(new HeatCoilRecipe(outputStack, CraftTweakerMC.getIngredient(inputOreDictEntry)));
			}
			
		}

		@Override
		public String describe() {
			return "UniTweak: Adding recipes for Embers Hearth Coil in pattern "+inputKindString+" to "+outputCount+" "+outputKindString;
		}
		
	}
}
