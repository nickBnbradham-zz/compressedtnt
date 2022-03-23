package dialup.compressedtnt;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import dialup.compressedtnt.proxies.CommonProxy;

@Mod(modid = CompressedTNTMod.MODID, name = "Compressed TNT", version = "a1.1")
public class CompressedTNTMod {
	public static final String MODID = "compressedtnt";

	@SidedProxy(clientSide = "dialup.compressedtnt.proxies.ClientProxy", serverSide = "dialup.compressedtnt.proxies.CommonProxy")
	public static CommonProxy prox;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(prox);
	}
}