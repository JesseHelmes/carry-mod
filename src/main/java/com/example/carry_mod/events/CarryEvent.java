package com.example.carry_mod.events;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CarryEvent {
	protected final RandomSource random = RandomSource.create();

	@SubscribeEvent
	public void preventVillagerTrade(PlayerInteractEvent.EntityInteract event) {
		if (event.isCanceled()) return;
		Player player = event.getEntity();

		if(!player.isVehicle()) return;

		if (player.getFirstPassenger() instanceof AbstractVillager && !player.isShiftKeyDown()) event.setCanceled(true);
	}

	@SubscribeEvent
	public void throwOrPickupPassenger(PlayerInteractEvent.EntityInteract event) {
		if (event.isCanceled() || event.getHand() != InteractionHand.MAIN_HAND) return;
		Player player = event.getEntity();

		ItemStack itemstack = player.getMainHandItem();
		if (!itemstack.isEmpty()) return;

		if (player.isVehicle()) {
			LivingEntity rider = (LivingEntity) player.getFirstPassenger();
			if (rider instanceof AbstractVillager && !player.isShiftKeyDown()) return;

			rider.stopRiding();
			this.shootFromRotation(rider, player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
			return;
		} else {
			if (!(event.getTarget() instanceof LivingEntity) || player.isPassenger()) return;
			if (event.getTarget() instanceof AbstractVillager && !player.isShiftKeyDown()) return;

			LivingEntity target = (LivingEntity) event.getTarget();
			target.startRiding(player, true);
		}
	}

	// System.out.println("ride");

	// TODO give the parameters better names
	public void shoot(Entity entity, double p_37266_, double p_37267_, double p_37268_, float throwStrength, float p_37270_) {
		Vec3 vec3 = (new Vec3(p_37266_, p_37267_, p_37268_)).normalize().add(this.random.triangle(0.0D, 0.0172275D * (double)p_37270_), this.random.triangle(0.0D, 0.0172275D * (double)p_37270_), this.random.triangle(0.0D, 0.0172275D * (double)p_37270_)).scale((double)throwStrength);
		entity.setDeltaMovement(vec3);
		double d0 = vec3.horizontalDistance();
		entity.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
		entity.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
		entity.yRotO = entity.getYRot();
		entity.xRotO = entity.getXRot();
	}

	// TODO give the parameters better names
	public void shootFromRotation(Entity target, Entity player, float p_37253_, float p_37254_, float p_37255_, float throwStrength, float p_37257_) {
		float f = -Mth.sin(p_37254_ * ((float)Math.PI / 180F)) * Mth.cos(p_37253_ * ((float)Math.PI / 180F));
		float f1 = -Mth.sin((p_37253_ + p_37255_) * ((float)Math.PI / 180F));
		float f2 = Mth.cos(p_37254_ * ((float)Math.PI / 180F)) * Mth.cos(p_37253_ * ((float)Math.PI / 180F));
		this.shoot(target, (double)f, (double)f1, (double)f2, throwStrength, p_37257_);
		Vec3 vec3 = player.getDeltaMovement();
		target.setDeltaMovement(target.getDeltaMovement().add(vec3.x, player.isOnGround() ? 0.0D : vec3.y, vec3.z));
	}

	// drop passengers on logout, not counting server stop
	@SubscribeEvent
	public void logout(ClientPlayerNetworkEvent.LoggingOut event) {
		Player player = event.getPlayer();
		if (player == null || !player.isVehicle()) return;
		LivingEntity rider = (LivingEntity) player.getFirstPassenger();
		rider.stopRiding();
	}

	@SubscribeEvent
	public void preventMobFromAttacking(LivingChangeTargetEvent event) {
		if (!(event.getEntity() instanceof Mob)) return;

		Mob entity = (Mob) event.getEntity();

		if (entity.getTarget() == null || !entity.isPassenger() || !(entity.getVehicle() instanceof Player)) return;

		entity.setTarget(null);
	}

	// TODO make passenger sit higher so player can see something
	// check lowerst entity hitbox and set the offset based on that

	// .Pre or .Post
//	@SubscribeEvent
	public void raiseArms(RenderPlayerEvent.Pre event) {
//		if (!event.getEntity().isVehicle()) return;

		// TODO raise hands
		// TODO don't show arms in first person
		// TODO prevent arms from moving when walking
//		event.getRenderer().getModel().rightArm.zRot = 30F; //  = ArmPose.THROW_SPEAR;
//		event.getRenderer().getModel().leftArmPose = ArmPose.THROW_SPEAR;
		
//		event.getPoseStack().pushPose();
		event.getRenderer().getModel().rightArm.zRot = 30F;
		event.getRenderer().getModel().rightArm.yRot = 90F;
		
	}

}
