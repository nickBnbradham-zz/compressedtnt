package dialup.compressedtnt;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

final class EntityCompressedTNTPrimed extends EntityTNTPrimed {

	private static final DataParameter<Integer> FUSE = EntityDataManager
			.<Integer>createKey(EntityCompressedTNTPrimed.class, DataSerializers.VARINT);
	private static final IBlockState AIR_STATE = Blocks.AIR.getDefaultState();

	private final double explSize;
	private final int dropD;

	private int fuse, startX, stopX, startY, stopY, startZ, stopZ;

	public EntityCompressedTNTPrimed(World worldIn, double x, double y, double z, EntityLivingBase igniter,
			double explosionSize, int dropDenom) {
		super(worldIn, x, y, z, igniter);
		explSize = explosionSize;
		dropD = dropDenom;
	}

	@Override
	protected void entityInit() {
		dataManager.register(FUSE, Integer.valueOf(80));
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (!hasNoGravity()) {
			motionY -= 0.03999999910593033D;
		}

		move(MoverType.SELF, motionX, motionY, motionZ);
		motionX *= 0.9800000190734863D;
		motionY *= 0.9800000190734863D;
		motionZ *= 0.9800000190734863D;

		if (onGround) {
			motionX *= 0.699999988079071D;
			motionZ *= 0.699999988079071D;
			motionY *= -0.5D;
		}

		if (--fuse == 0) {
			if (!world.isRemote) {
				stopX = (int) (posX + explSize);
				startY = (int) Math.max(posY - explSize, 1);
				stopY = (int) Math.min(posY + explSize, 255);
				startZ = (int) (posZ - explSize);
				stopZ = (int) (posZ + explSize);
				world.playSound((EntityPlayer) null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_EXPLODE,
						SoundCategory.BLOCKS, 4.0F,
						(1.0F + (this.world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
				startX = (int) (posX - explSize);
			}
		} else if (fuse > 0) {
			handleWaterMovement();
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, posX, posY + 0.5D, posZ, 0.0D, 0.0D, 0.0D);
		} else {
			int explX = startX - fuse;
			for (byte i = 0; i < 8 && explX <= stopX; i++) {
				explX = startX - fuse--;
				for (int y = startY; y < stopY; y++)
					for (int z = startZ; z < stopZ; z++) {
						BlockPos bp = new BlockPos(explX, y, z);
						if (!world.getBlockState(bp).equals(AIR_STATE))
							if (world.rand.nextInt(dropD) == 0) {
								IBlockState state = world.getBlockState(bp);
								state.getBlock().dropBlockAsItem(world, bp, state, 0);
								world.setBlockState(bp, AIR_STATE);
							} else
								world.setBlockState(bp, AIR_STATE);
					}
			}
			fuse++;
			if (explX >= stopX)
				setDead();
		}
	}

	@Override
	public void setFuse(int fuseIn) {
		dataManager.set(FUSE, Integer.valueOf(fuseIn));
		fuse = fuseIn;
	}
}