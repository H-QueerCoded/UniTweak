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

import java.util.List;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.IE.crusher")
@ZenRegister
public class Crusher {
	
	@ZenMethod
	public static void add(String outputKind, String inputKind, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize, @Optional IItemStack secondaryOutput, @Optional double secondaryChance) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, inputKind, energy, outCount, inputSize, secondaryOutput, secondaryChance));
	}
	
	@ZenMethod
	public static void remove(String outputKind, String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(outputKind, inputKind));
	}
	
	
	public static class Add implements IAction{
		
		String inputKind, outputKind;
		int energy, in, out;
		IItemStack secondaryOutput;
		double secondaryChance;
		
		public Add(String outputKind, String inputKind, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize, @Optional IItemStack secondaryOutput, @Optional double secondaryChance) {
			this.outputKind = outputKind;
			this.inputKind = inputKind;
			this.energy = energy;
			this.secondaryOutput = secondaryOutput;
			this.secondaryChance = secondaryChance;
			in = inputSize;
			out = outCount;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int input = Resource.getKindFromName(inputKind);
			int output = Resource.getKindFromName(outputKind);
			List<Resource> inAndOut = uniDictAPI.getResources(input, output);
			
			for(Resource resource : inAndOut) {
				ItemStack outStack = resource.getChild(output).getMainEntry(out);
				IOreDictEntry inputOreDict = ResourceHandling.getOreDictEntry(resource, input);
				CraftTweakerAPI.logInfo("UniTweak: Adding crusher recipe for "+in+" "+inputOreDict.getName()+" to "+outStack.getCount()+" "+outStack.getDisplayName());
				CrusherRecipe r = new CrusherRecipe(outStack, CraftTweakerHelper.toObject(inputOreDict.amount(in)), energy);
				if(secondaryOutput!=null)
					r.addToSecondaryOutput(CraftTweakerHelper.toStack(secondaryOutput), (float)secondaryChance);
				CrusherRecipe.recipeList.add(r);
			}
		}
		
		@Override
		public String describe() {
			return "UniTweak: Trying to create patterned IE Crusher recipe for "+inputKind+" to "+outputKind;
		}
	}
	
	public static class Remove implements IAction {
		String input, output;
		
		public Remove(String outputKind, String inputKind) {
			input = inputKind;
			output = outputKind;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int inputKind = Resource.getKindFromName(input);
			int outputKind = Resource.getKindFromName(output);
			List<Resource> inAndOut = uniDictAPI.getResources(inputKind, outputKind);
			
			for(Resource resource : inAndOut) {
				List<ItemStack> inputList = resource.getChild(inputKind).getEntries();
				for (ItemStack input : inputList) {
					CrusherRecipe r = CrusherRecipe.findRecipe(input);
					if(r != null) {
						List<ItemStack> outputList = resource.getChild(outputKind).getEntries();
						for (ItemStack output : outputList) {
							if(ItemStack.areItemsEqual(r.output, output)) {
								CraftTweakerAPI.logInfo("UniTweak: Removing Crusher recipe for "+input.getDisplayName()+" to "+output.getDisplayName());
								CrusherRecipe.removeRecipesForInput(input);
							}
						}
					}
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing all Crusher recipes with input of "+input+" and output of "+output;
		}
	}
}
