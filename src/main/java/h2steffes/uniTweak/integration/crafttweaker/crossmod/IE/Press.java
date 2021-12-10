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

import java.util.List;

import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.CraftTweakerHelper;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.IE.press")
@ZenRegister
public class Press {
	
	@ZenMethod
	public static void add(String outputKind, String inputKind, IItemStack mold, int energy, @Optional(valueLong = 1) int outputCount, @Optional(valueLong = 1) int inputCount) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, inputKind, mold, energy, outputCount, inputCount));
	}
	
	@ZenMethod
	public static void remove(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(outputKind));
	}
	
	public static class Add implements IAction{
		
		int inputCount, outputCount, energy;
		String inputKindString, outputKindString;
		ItemStack mold;

		public Add(String outputKind, String inputKind, IItemStack mold, int energy, int outCount, int inputSize) {
			this.inputKindString=inputKind;
			this.outputKindString=outputKind;
			this.mold=CraftTweakerMC.getItemStack(mold);
			inputCount=inputSize;
			outputCount=outCount;
			this.energy=energy;
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
				CraftTweakerAPI.logInfo("UniTweak: Adding metal press recipe for "+inputCount+" "+inputOreDictEntry.getName()+" to "+outputStack.getCount()+" "+outputStack.getDisplayName());
				MetalPressRecipe.addRecipe(outputStack, CraftTweakerHelper.toObject(inputOreDictEntry.amount(inputCount)), mold, energy);
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Trying to create patterned IE Metal Press recipe template for "+inputKindString+" to "+outputKindString;
		}
		
	}
	
	public static class Remove implements IAction{
		
		String outputKindString;
		
		public Remove(String outputKind) {
			outputKindString=outputKind;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outputKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outputKindInt);
			
			for(Resource resource : matchingResources) {
				List<ItemStack> outputList = resource.getChild(outputKindString).getEntries();
				for (ItemStack output : outputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Metal Press recipes with output "+output.getDisplayName());
					MetalPressRecipe.removeRecipes(output);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing all Metal Press recipes with output of kind: "+outputKindString;
		}
	}
}
