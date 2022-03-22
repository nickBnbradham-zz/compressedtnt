package dialup.compressedtnt;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

final class EntityCompressedTNTPrimed extends EntityTNTPrimed {

	private static final DataParameter<Integer> FUSE = EntityDataManager
			.<Integer>createKey(EntityCompressedTNTPrimed.class, DataSerializers.VARINT);

	private int fuse;

	public EntityCompressedTNTPrimed(World worldIn, double x, double y, double z, EntityLivingBase igniter) {
		super(worldIn, x, y, z, igniter);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(FUSE, Integer.valueOf(80));
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (!this.hasNoGravity()) {
			this.motionY -= 0.03999999910593033D;
		}

		this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.9800000190734863D;
		this.motionY *= 0.9800000190734863D;
		this.motionZ *= 0.9800000190734863D;

		if (this.onGround) {
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
			this.motionY *= -0.5D;
		}

		--this.fuse;

		if (this.fuse <= 0) {
			this.setDead();

			if (!this.world.isRemote) {
				this.world.createExplosion(this, this.posX, this.posY + (double) (this.height / 16.0F), this.posZ, 8.0F,
						true);
			}
		} else {
			this.handleWaterMovement();
			this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D,
					0.0D);
		}
	}

	@Override
	public void setFuse(int fuseIn) {
		this.dataManager.set(FUSE, Integer.valueOf(fuseIn));
		this.fuse = fuseIn;
	}
}