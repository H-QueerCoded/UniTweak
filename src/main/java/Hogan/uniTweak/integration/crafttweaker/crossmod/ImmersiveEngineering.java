package Hogan.uniTweak.integration.crafttweaker.crossmod;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import java.util.List;
import blusunrize.immersiveengineering.api.crafting.MetalPressRecipe;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.MetalPress;
import wanion.unidict.UniDict;
import wanion.unidict.api.*;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("unitweak.ie")
@ZenRegister
public class ImmersiveEngineering {
	
	static UniDictAPI uniDictAPI = UniDict.getAPI();
	
	@ZenMethod
	public static void pressRecipe(String outputKind, @Optional("1") int outCount, String inputKind, IItemStack mold, int energy, @Optional int inputSize) {
		CraftTweakerAPI.apply(new pressRecipe(outputKind, outCount, inputKind, mold, energy, inputSize));
	}
	
	public static class pressRecipe implements IAction{
		
		int input, output, inNum, outNum, energy;
		IItemStack mold;

		public pressRecipe(String outputKind, int outCount, String inputKind, IItemStack mold, int energy, int inputSize) {
			input = Resource.getKindOfName(inputKind);
			output = Resource.getKindOfName(outputKind);
			this.mold=mold;
			inNum=inputSize;
			outNum=outCount;
		}
		
		@Override
		public void apply() {
			List<Resource> inAndOut = uniDictAPI.getResources(input, output);
			
			for(Resource resource : inAndOut) {
				IItemStack outStack = CraftTweakerMC.getIItemStack(resource.getChild(output).getMainEntry(outNum));
				IItemStack inStack = CraftTweakerMC.getIItemStack(resource.getChild(input).getMainEntry());
				MetalPress.addRecipe(outStack, inStack, mold, energy, inNum);
			}
		}

		@Override
		public String describe() {
			return "Trying to create patterned Metal Press recipe for "+Resource.getNameOfKind(input)+" to "+Resource.getNameOfKind(output);
		}
		
	}
}
