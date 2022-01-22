package h2steffes.uniTweak.integration.crafttweaker.crossmod.NuclearCraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.common.collect.Lists;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ModOnly;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.CraftTweaker;
import h2steffes.uniTweak.util.ResourceHandling;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import wanion.unidict.UniDict;
import wanion.unidict.api.UniDictAPI;
import wanion.unidict.resource.Resource;
import nc.integration.crafttweaker.AddProcessorRecipe;
import nc.integration.crafttweaker.RemoveProcessorRecipe;
import nc.recipe.IngredientSorption;
import nc.recipe.NCRecipes;


@ModOnly("nuclearcraft")
@ZenClass("mods.unitweak.nuclearcraft.melter")
@ZenRegister
public class Melter {

	static List<String> resourcesToIgnore =  new ArrayList<String>();
	
	@ZenMethod
	public static void ignore(String[] resources) {
		resourcesToIgnore.clear();
		Collections.addAll(resourcesToIgnore, resources);
	}
	
	@ZenMethod
	public static void addTemplate(String inputKind, int liquidAmount, @Optional(valueDouble=1) double timeMultiplier, @Optional(valueDouble=1) double powerMultiplier, @Optional double processRadiation) {
		CraftTweaker.LATE_ACTIONS.add(new AddTemplate(inputKind, liquidAmount, timeMultiplier, powerMultiplier, processRadiation));
	}
	
	
	@ZenMethod
	public static void removeTemplate(String inputKind) {
		CraftTweaker.LATE_ACTIONS.add(new RemoveForInput(inputKind));
	}
	
	public static class AddTemplate implements IAction {
		
		String inputKind;
		int liquidAmount;
		double timeMultiplier, powerMultiplier, processRadiation;

		public AddTemplate(String inputKind, int liquidAmount,  @Optional double timeMultiplier,  @Optional double powerMultiplier, @Optional double processRadiation) {
			this.inputKind = inputKind;
			this.liquidAmount = liquidAmount;
			this.timeMultiplier = timeMultiplier;
			this.powerMultiplier = powerMultiplier;
			this.processRadiation = processRadiation;
		}

		@Override
		public void apply() {
			final UniDictAPI uniDictAPI = UniDict.getAPI();
			int inputKindInt = Resource.getKindFromName(inputKind);
			List<Resource> mathcingResources = uniDictAPI.getResources(inputKindInt);
			
			for(Resource resource : mathcingResources) {
				if(resourcesToIgnore.contains(resource.getName())) {
					CraftTweakerAPI.logInfo("UniTweak: Skipping resource "+resource.getName());
					continue;
				}
				IOreDictEntry inputOreDictEntry = ResourceHandling.getOreDictEntry(resource, inputKindInt);
				if(!FluidRegistry.isFluidRegistered(resource.getName().toLowerCase())){
					CraftTweakerAPI.logInfo("UniTweak: No molten version of "+resource.getName()+", skipping melter recipe");
					continue;
				}
				Fluid fluid = FluidRegistry.getFluid(resource.getName().toLowerCase());
				FluidStack fluidStack = new FluidStack(fluid, liquidAmount);
				
				AddProcessorRecipe recipe = new AddProcessorRecipe(NCRecipes.melter, Lists.newArrayList(inputOreDictEntry, CraftTweakerMC.getILiquidStack(fluidStack), timeMultiplier, powerMultiplier, processRadiation));
				recipe.apply();
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Adding nuclearcraft melter recipes for "+inputKind+" to "+liquidAmount+"mb";
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

			for (Resource resource : matchingResources) {
				if(resourcesToIgnore.contains(resource.getName())) {
					CraftTweakerAPI.logInfo("UniTweak: Skipping resource "+resource.getName());
					continue;
				}
				List<ItemStack> inputList = resource.getChild(inputKindInt).getEntries();
				for (ItemStack input : inputList) {
					RemoveProcessorRecipe recipe = new RemoveProcessorRecipe(NCRecipes.melter, IngredientSorption.INPUT, Lists.newArrayList(CraftTweakerMC.getIItemStacks(input)));
					recipe.apply();
				}
			}
		}

		@Override
		public String describe() {
			return "UniTweak: Removing nuclearcraft melter recipes with input "+inputKindString;
		}
	}
}
