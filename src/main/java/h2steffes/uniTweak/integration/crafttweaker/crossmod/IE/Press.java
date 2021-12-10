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
	public static void add(String outputKind, String inputKind, IItemStack mold, int energy, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, inputKind, mold, energy, outCount, inputSize));
	}
	
	@ZenMethod
	public static void remove(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(outputKind));
	}
	
	public static class Add implements IAction{
		
		int inNum, outNum, energy;
		String inputKind, outputKind;
		ItemStack mold;

		public Add(String outputKind, String inputKind, IItemStack mold, int energy, int outCount, int inputSize) {
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
				IOreDictEntry inputOreDict = ResourceHandling.getOreDictEntry(resource, input);
				CraftTweakerAPI.logInfo("UniTweak: Adding metal press recipe for "+inNum+" "+inputOreDict.getName()+" to "+outStack.getCount()+" "+outStack.getDisplayName());
				MetalPressRecipe.addRecipe(outStack, CraftTweakerHelper.toObject(inputOreDict.amount(inNum)), mold, energy);
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Trying to create patterned IE Metal Press recipe template for "+inputKind+" to "+outputKind;
		}
		
	}
	
	public static class Remove implements IAction{
		
		String kind;
		
		public Remove(String outputKind) {
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
}
