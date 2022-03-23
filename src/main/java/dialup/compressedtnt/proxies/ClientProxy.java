package dialup.compressedtnt.proxies;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public final class ClientProxy extends CommonProxy {

	@SubscribeEvent
	public final void registerRenderers(ModelRegistryEvent ev) {
		for (Item i : ITEMS)
			ModelLoader.setCustomModelResourceLocation(i, 0,
					new ModelResourceLocation(i.getRegistryName(), "inventory"));
	}
}