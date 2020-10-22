package Hogan.uniTweak;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = UniTweak.MODID, name = UniTweak.NAME, useMetadata = true)
public class UniTweak
{
    public static final String MODID = "unitweak";
    public static final String NAME = "UniTweak";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
