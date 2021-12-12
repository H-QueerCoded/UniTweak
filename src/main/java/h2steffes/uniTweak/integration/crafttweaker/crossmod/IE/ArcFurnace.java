package h2steffes.uniTweak.integration.crafttweaker.crossmod.IE;

import java.util.List;

import blusunrize.immersiveengineering.api.crafting.ArcFurnaceRecipe;
import blusunrize.immersiveengineering.common.util.compat.crafttweaker.CraftTweakerHelper;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.CraftTweaker;
import h2steffes.uniTweak.util.ResourceHandling;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;

@ModOnly("immersiveengineering")
@ZenClass("mods.unitweak.IE.arcFurnace")
@ZenRegister
public class ArcFurnace {

	@ZenMethod
	public static void add(String outputKind, String inputKind, IItemStack slag, int time, int energyPerTick, @Optional(valueLong = 1) int outCount, @Optional(valueLong = 1) int inputSize, @Optional IIngredient[] additives, @Optional String specialRecipeType) {
		CraftTweaker.LATE_ACTIONS.add(new Add(outputKind, inputKind, slag, time, energyPerTick, outCount, inputSize, additives, specialRecipeType));
	}
	
	@ZenMethod
	public static void remove(String outputKind) {
		CraftTweaker.LATE_ACTIONS.add(new Remove(outputKind));
	}

	public static class Add implements IAction {
		
		String inputKindString, outputKindString, specialRecipeType;
		int energyPerTick, time, inputCount, outputCount;
		IItemStack slag;
		Object[] adds = null;

		public Add(String outputKind, String inputKind, IItemStack slag, int time, int energyPerTick, int outputCount, int inputCount, IIngredient[] additives, String specialRecipeType) {
			outputKindString = outputKind;
			inputKindString = inputKind;
			this.slag = slag;
			this.time = time;
			this.energyPerTick = energyPerTick;
			this.specialRecipeType = specialRecipeType;
			this.inputCount = inputCount;
			this.outputCount = outputCount;
			
			if(additives!=null)
			{
				adds = new Object[additives.length];
				for(int i = 0; i < additives.length; i++)
					adds[i] = CraftTweakerHelper.toObject(additives[i]);
			}
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outputKindInt = Resource.getKindFromName(outputKindString);
			int inputKindInt = Resource.getKindFromName(inputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outputKindInt, inputKindInt);

			for (Resource resource : matchingResources) {
				ItemStack outputStack = resource.getChild(outputKindInt).getMainEntry(outputCount);
				IOreDictEntry inputOreDictEntry = ResourceHandling.getOreDictEntry(resource, inputKindInt);
				ArcFurnaceRecipe recipe = new ArcFurnaceRecipe(outputStack, CraftTweakerHelper.toObject(inputOreDictEntry.amount(inputCount)), CraftTweakerHelper.toStack(slag), time, energyPerTick, adds);
				if(specialRecipeType!=null)
					recipe.setSpecialRecipeType(specialRecipeType);
				ArcFurnaceRecipe.recipeList.add(recipe);
			}
		}

		@Override
		public String describe() {
			return "UniTweak: ";
		}
	}

	public static class Remove implements IAction {
		
		String outputKindString;

		public Remove(String outputKind) {
			outputKindString=outputKind;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int outputKindInt = Resource.getKindFromName(outputKindString);
			List<Resource> matchingResources = uniDictAPI.getResources(outputKindInt);

			for (Resource resource : matchingResources) {
				List<ItemStack> outputList = resource.getChild(outputKindInt).getEntries();
				for (ItemStack output : outputList) {
					ArcFurnaceRecipe.removeRecipes(output);
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: removing Arc Furnace recipes that output "+outputKindString;
		}
	}
}
