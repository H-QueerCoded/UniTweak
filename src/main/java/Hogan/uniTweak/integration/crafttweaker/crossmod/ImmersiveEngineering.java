package Hogan.uniTweak.integration.crafttweaker.crossmod;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import java.util.List;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.ie")
@ZenRegister
public class ImmersiveEngineering {
	
	@ZenMethod
	public static void pressRecipe(String outputKind, String inputKind, IItemStack mold, int energy, @Optional int inputSize, @Optional("1") int outCount) {
		CraftTweakerAPI.apply(new pressRecipe(outputKind, outCount, inputKind, mold, energy, inputSize));
	}
	
	public static class pressRecipe implements IAction{
		
		int inNum, outNum, energy;
		String inputKind, outputKind;
		ItemStack mold;

		public pressRecipe(String outputKind, int outCount, String inputKind, IItemStack mold, int energy, int inputSize) {
			this.inputKind=inputKind;
			this.outputKind=outputKind;
			this.mold=CraftTweakerMC.getItemStack(mold);
			inNum=inputSize;
			outNum=outCount;
			this.energy=energy;
		}
		
		@Override
		public void apply() {
			UniDictAPI uniDictAPI = UniDict.getAPI();
			CraftTweakerAPI.logInfo("inputKind String: "+inputKind);
			CraftTweakerAPI.logInfo("does inputKind exist: "+Resource.kindExists(inputKind));
			int input = Resource.getKindFromName(inputKind);
			int output = Resource.getKindFromName(outputKind);
			List<Resource> inAndOut = uniDictAPI.getResources(input, output);
			CraftTweakerAPI.logInfo("List length: "+inAndOut.size());
			
			for(Resource resource : inAndOut) {
				ItemStack outStack = resource.getChild(output).getMainEntry(outNum);
				ItemStack inStack = resource.getChild(input).getMainEntry(inNum);
				//MetalPress.addRecipe(outStack, inStack, mold, energy, inNum);
				MetalPressRecipe.addRecipe(outStack, inStack, mold, energy);
			}
		}

		@Override
		public String describe() {
			return "Trying to create patterned Metal Press recipe for "+inputKind+" to "+outputKind;
		}
		
	}
}
