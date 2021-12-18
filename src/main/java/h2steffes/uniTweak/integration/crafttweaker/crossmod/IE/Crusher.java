package h2steffes.uniTweak.integration.crafttweaker.crossmod.IE;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.CraftTweaker;
import h2steffes.uniTweak.util.ResourceHandling;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.CraftTweakerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.IE.crusher")
@ZenRegister
public class Crusher {
	
	static List<String> resourcesToIgnore =  new ArrayList<String>();
	
	@ZenMethod
	public static void ignore(String[] resources) {
		resourcesToIgnore.clear();
		Collections.addAll(resourcesToIgnore, resources);
	}
	
	@ZenMethod
	public static void add(String outputKind, String inputKind, int energy, @Optional(valueLong = 1) int outputCount, @Optional(valueLong = 1) int inputCount, @Optional IItemStack secondaryOutput, @Optional double secondaryChance) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, inputKind, energy, outputCount, inputCount, secondaryOutput, secondaryChance));
	}
	
	@ZenMethod
	public static void removeForInput(String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new RemoveForInput(inputKind));
	}
	
	@ZenMethod
	public static void removeForOutput(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new RemoveForOutput(outputKind));
	}
	
	public static class Add implements IAction{
		
		String inputKindString, outputKindString;
		int energy, inputCount, outputCount;
		IItemStack secondaryOutput;
		double secondaryChance;
		
		public Add(String outputKind, String inputKind, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize, @Optional IItemStack secondaryOutput, @Optional double secondaryChance) {
			this.outputKindString = outputKind;
			this.inputKindString = inputKind;
			this.energy = energy;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			inputCount = inputSize;
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
				CraftTweakerAPI.logInfo("UniTweak: Adding crusher recipe for "+inputCount+" "+inputOreDictEntry.getName()+" to "+outputStack.getCount()+" "+outputStack.getDisplayName());
				CrusherRecipe recipe = new CrusherRecipe(outputStack, CraftTweakerHelper.toObject(inputOreDictEntry.amount(inputCount)), energy);
				if(secondaryOutput!=null)
					recipe.addToSecondaryOutput(CraftTweakerHelper.toStack(secondaryOutput), (float)secondaryChance);
				CrusherRecipe.recipeList.add(recipe);
			}
		}
		
		@Override
		public String describe() {
			return "UniTweak: Trying to create patterned IE Crusher recipe for "+inputKindString+" to "+outputKindString;
		}
	}
	
	public static class RemoveForInput implements IAction {
		String inputKindString;
		
		public RemoveForInput(String inputKind) {
			inputKindString = inputKind;
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
					CrusherRecipe.removeRecipesForInput(input);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing all Crusher recipes with input of "+inputKindString;
		}
	}
	
	public static class RemoveForOutput implements IAction {
		String outputKindString;
		
		public RemoveForOutput(String outputKind) {
			outputKindString = outputKind;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outputKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outputKindInt);
			
			for(Resource resource : matchingResources) {
				List<ItemStack> outputList = resource.getChild(outputKindInt).getEntries();
				for (ItemStack output : outputList) {
					CrusherRecipe.removeRecipesForOutput(output);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing all Crusher recipes with output of "+outputKindString;
		}
	}
}
