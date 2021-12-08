package h2steffes.uniTweak;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.SidedProxy;

@Mod(modid = UniTweak.MODID, name = UniTweak.NAME, useMetadata = true)
public class UniTweak
{
    public static final String MODID = "unitweak";
    public static final String NAME = "UniTweak";
    
    @Mod.Instance
    public static UniTweak instance;
    
    @SidedProxy(clientSide = "h2steffes.uniTweak.clientProxy", serverSide = "h2steffes.uniTweak.commonProxy")
    public static commonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit();
    }
}
