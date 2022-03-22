package dialup.compressedtnt.proxies;

import dialup.compressedtnt.BlockCompressedTNT;
import dialup.compressedtnt.CompressedTNTMod;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CommonProxy {

	public static final CreativeTabs CREATIVE_TAB = new CreativeTabs(CompressedTNTMod.MODID) {

		@Override
		public final ItemStack getTabIconItem() {
			return new ItemStack(Item.getByNameOrId(CompressedTNTMod.MODID + ":compressedtnt"));
		}
	};

	private static final BlockCompressedTNT block = new BlockCompressedTNT();
	protected static final Item item = new ItemBlock(block).setRegistryName(block.getRegistryName())
			.setCreativeTab(CREATIVE_TAB);

	@SubscribeEvent
	public final void registerBlocks(RegistryEvent.Register<Block> ev) {
		ev.getRegistry().register(block);
	}

	@SubscribeEvent
	public final void registerItems(RegistryEvent.Register<Item> ev) {
		ev.getRegistry().register(item);
	}
}