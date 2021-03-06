package dialup.compressedtnt;

import dialup.compressedtnt.proxies.CommonProxy;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public final class BlockCompressedTNT extends BlockTNT {

	public static final String[] TUPLES = { "", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple",
			"octuple", "nonuple", "decuple", "undecuple", "duodecuple", "tredecuple", "quattuordecuple", "quindecuple",
			"sexdecuple", "septendecuple", "octodecuple", "novemdecuple", "vigintuple", "unvigintuple", "duovigintuple",
			"trevigintuple", "quattuorvigintuple", "quinvigintuple", "sexvigintuple", "septenvigintuple",
			"octovigintuple", "novemvigintuple", "trigintuple", "untrigintuple", "duotrigintuple" };

	private final double explSize;
	private final int dropDenom;

	public BlockCompressedTNT(int level) {
		super();
		String id = TUPLES[level] + "compressedtnt";
		setUnlocalizedName(id);
		setRegistryName(id);
		setCreativeTab(CommonProxy.CREATIVE_TAB);
		dropDenom = 9 * level * level + 1;
		explSize = 9 * (level + 1);
	}

	@Override
	public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn) {
		if (!worldIn.isRemote) {
			EntityCompressedTNTPrimed entitytntprimed = new EntityCompressedTNTPrimed(worldIn,
					(double) ((float) pos.getX() + 0.5F), (double) pos.getY(), (double) ((float) pos.getZ() + 0.5F),
					explosionIn.getExplosivePlacedBy(), explSize, dropDenom);
			entitytntprimed.setFuse(
					(short) (worldIn.rand.nextInt(entitytntprimed.getFuse() / 4) + entitytntprimed.getFuse() / 8));
			worldIn.spawnEntity(entitytntprimed);
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
		explode(worldIn, pos, state, (EntityLivingBase) null);
	}

	@Override
	public void explode(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase igniter) {
		if (!worldIn.isRemote) {
			if (((Boolean) state.getValue(EXPLODE)).booleanValue()) {
				EntityCompressedTNTPrimed entitytntprimed = new EntityCompressedTNTPrimed(worldIn,
						(double) ((float) pos.getX() + 0.5F), (double) pos.getY(), (double) ((float) pos.getZ() + 0.5F),
						igniter, explSize, dropDenom);
				worldIn.spawnEntity(entitytntprimed);
				worldIn.playSound((EntityPlayer) null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ,
						SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = playerIn.getHeldItem(hand);

		if (!itemstack.isEmpty()
				&& (itemstack.getItem() == Items.FLINT_AND_STEEL || itemstack.getItem() == Items.FIRE_CHARGE)) {
			explode(worldIn, pos, state.withProperty(EXPLODE, Boolean.valueOf(true)), playerIn);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 11);

			if (itemstack.getItem() == Items.FLINT_AND_STEEL) {
				itemstack.damageItem(1, playerIn);
			} else if (!playerIn.capabilities.isCreativeMode) {
				itemstack.shrink(1);
			}

			return true;
		} else {
			return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote && entityIn instanceof EntityArrow) {
			EntityArrow entityarrow = (EntityArrow) entityIn;

			if (entityarrow.isBurning()) {
				explode(worldIn, pos, worldIn.getBlockState(pos).withProperty(EXPLODE, Boolean.valueOf(true)),
						entityarrow.shootingEntity instanceof EntityLivingBase
								? (EntityLivingBase) entityarrow.shootingEntity
								: null);
				worldIn.setBlockToAir(pos);
			}
		}
	}
}