package h2steffes.uniTweak.util;

import crafttweaker.api.oredict.IOreDictEntry;
import crafttweaker.mc1120.oredict.MCOreDict;
import wanion.unidict.resource.Resource;

public class ResourceHandling {
	public static IOreDictEntry getOreDictEntry(Resource resource, int kind) {
		String inName = resource.getChild(kind).name;
		MCOreDict oreDict = new MCOreDict();
		return oreDict.get(inName);
	}
}
