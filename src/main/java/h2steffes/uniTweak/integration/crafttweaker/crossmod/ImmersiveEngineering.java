package h2steffes.uniTweak.integration.crafttweaker.crossmod;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.CraftTweakerHelper;
import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.ie")
@ZenRegister
public class ImmersiveEngineering {
	
	@ZenMethod
	public static void pressRecipe(String outputKind, String inputKind, IItemStack mold, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize) {
		CraftTweaker.LATE_ACTIONS.add(new pressRecipe(outputKind, inputKind, mold, energy, outCount, inputSize));
	}
	
	@ZenMethod
	public static void removePressByOutputKind(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new removePressByOutputKind(outputKind));
	}
	
	@ZenMethod
	public static void crusherTemplate(String outputKind, String inputKind, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize, @Optional IItemStack secondaryOutput, @Optional double secondaryChance) {
		CraftTweaker.LATE_ACTIONS.add(new crusherTemplate(outputKind, inputKind, energy, outCount, inputSize, secondaryOutput, secondaryChance));
	}
	
	@ZenMethod
	public static void removeCrusherByBothKind(String outputKind, String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new removeCrusherByBothKind(outputKind, inputKind));
	}
	
	public static class pressRecipe implements IAction{
		
		int inNum, outNum, energy;
		String inputKind, outputKind;
		ItemStack mold;

		public pressRecipe(String outputKind, String inputKind, IItemStack mold, int energy, int outCount, int inputSize) {
			this.inputKind=inputKind;
			this.outputKind=outputKind;
			this.mold=CraftTweakerMC.getItemStack(mold);
			inNum=inputSize;
			outNum=outCount;
			this.energy=energy;
		}
		
		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int input = Resource.getKindFromName(inputKind);
			int output = Resource.getKindFromName(outputKind);
			List<Resource> inAndOut = uniDictAPI.getResources(input, output);
			
			for(Resource resource : inAndOut) {
				ItemStack outStack = resource.getChild(output).getMainEntry(outNum);
				ItemStack inStack = resource.getChild(input).getMainEntry(inNum);
				CraftTweakerAPI.logInfo("UniTweak: Adding metal press recipe for "+inStack.getCount()+" "+inStack.getDisplayName()+" to "+outStack.getCount()+" "+outStack.getDisplayName());
				MetalPressRecipe.addRecipe(outStack, inStack, mold, energy);
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Trying to create patterned IE Metal Press recipe template for "+inputKind+" to "+outputKind;
		}
		
	}
	
	public static class removePressByOutputKind implements IAction{
		
		String kind;
		
		public removePressByOutputKind(String outputKind) {
			kind=outputKind;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int kindNum = Resource.getKindFromName(kind);
			List<Resource> list = uniDictAPI.getResources(kindNum);
			
			for(Resource resource : list) {
				List<ItemStack> outputList = resource.getChild(kind).getEntries();
				for (ItemStack output : outputList) {
					CraftTweakerAPI.logInfo("UniTweak: Removing Metal Press recipes with output "+output.getDisplayName());
					MetalPressRecipe.removeRecipes(output);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing all Metal Press recipes with output of kind: "+kind;
		}
	}
	
	public static class crusherTemplate implements IAction{
		
		String inputKind, outputKind;
		int energy, in, out;
		IItemStack secondaryOutput;
		double secondaryChance;
		
		public crusherTemplate(String outputKind, String inputKind, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize, @Optional IItemStack secondaryOutput, @Optional double secondaryChance) {
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
				ItemStack inStack = resource.getChild(input).getMainEntry(in);
				CraftTweakerAPI.logInfo("UniTweak: Adding crusher recipe for "+inStack.getCount()+" "+inStack.getDisplayName()+" to "+outStack.getCount()+" "+outStack.getDisplayName());
				CrusherRecipe r = new CrusherRecipe(outStack, inStack, energy);
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
	
	public static class removeCrusherByBothKind implements IAction {
		String input, output;
		
		public removeCrusherByBothKind(String outputKind, String inputKind) {
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
