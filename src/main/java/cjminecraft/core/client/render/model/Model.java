package cjminecraft.core.client.render.model;

import cjminecraft.core.access.AccessHandler;
import cjminecraft.core.util.MathUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Model extends ModelBase {
	public static final float DEFAULT_SCALE = 1F / 16F;

	/**
	 * Set the width and height of this ModelBaseExtension's texture.
	 * 
	 * @param textureWidth
	 *            - The texture width in pixels
	 * @param textureHeight
	 *            - The texture height in pixels
	 */
	public void setTextureDimensions(int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	/**
	 * Set the rotation angles of the specified ModelRenderer instance.
	 * 
	 * @param model
	 *            - The model rotations are being set for.
	 * @param rotateAngleX
	 *            - Angle by which model will rotate in the X direction.
	 * @param rotateAngleY
	 *            - Angle by which the model will rotate in the Y direction.
	 * @param rotateAngleZ
	 *            - Angle by which the model will rotate in the Z direction.
	 */
	public void setRotation(ModelRenderer model, float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
		model.rotateAngleX = rotateAngleX;
		model.rotateAngleY = rotateAngleY;
		model.rotateAngleZ = rotateAngleZ;
	}

	/**
	 * Renders a model.
	 * 
	 * @param modelRenderer
	 *            - The ModelRenderer being used.
	 */
	public static void draw(ModelRenderer modelRenderer) {
		modelRenderer.render(DEFAULT_SCALE);
	}

	/**
	 * Renders a group of models.
	 * 
	 * @param group
	 *            - A group of models for which to be rendered.
	 */
	public static void draw(ModelRenderer[] group) {
		for (ModelRenderer child : group) {
			draw(child);
		}
	}

	public void render() {
		this.render(null);
	}

	/**
	 * The entity render method from ModelBase with correct parameter mappings.
	 * Calls the base render method.
	 * 
	 * @param entity
	 *            - The Entity instance being rendered.
	 * @param swing
	 *            - The arm swing progress of the Entity being rendered.
	 * @param swingPrev
	 *            - The previous tick's arm swing progress of the Entity being
	 *            rendered.
	 * @param idle
	 *            - The idle arm swing progress of the Entity being rendered.
	 * @param headYaw
	 *            - The head rotation yaw of the Entity being rendered.
	 * @param headPitch
	 *            - The head rotation pitch of the Entity being rendered.
	 * @param scale
	 *            - The scale this model will render at.
	 */
	public void render(Object obj) {
		;
	}

	/**
	 * The entity render method from ModelBase with correct parameter mappings.
	 * Calls the base render method.
	 *
	 * @param entity
	 *            - The Entity instance being rendered.
	 * @param swing
	 *            - The arm swing progress of the Entity being rendered.
	 * @param swingPrev
	 *            - The previous tick's arm swing progress of the Entity being
	 *            rendered.
	 * @param idle
	 *            - The idle arm swing progress of the Entity being rendered.
	 * @param headYaw
	 *            - The head rotation yaw of the Entity being rendered.
	 * @param headPitch
	 *            - The head rotation pitch of the Entity being rendered.
	 * @param scale
	 *            - The scale this model will render at.
	 */
	@Override
	public void render(Entity entity, float swing, float swingPrev, float idle, float headYaw, float headPitch, float scale) {
		this.render(entity);
	}

	/**
	 * The standard setRotationAngles method from ModelBase with correct parameter
	 * mappings. Calls the superclass method.
	 *
	 * @param swing
	 *            - The arm swing progress of the Entity being rendered.
	 * @param swingPrev
	 *            - The previous tick's arm swing progress of the Entity being
	 *            rendered.
	 * @param idle
	 *            - The idle arm swing progress of the Entity being rendered.
	 * @param headYaw
	 *            - The head rotation yaw of the Entity being rendered.
	 * @param headPitch
	 *            - The head rotation pitch of the Entity being rendered.
	 * @param scale
	 *            - The scale this model will render at.
	 * @param entity
	 *            - The Entity instance being rendered.
	 */
	@Override
	public void setRotationAngles(float swing, float swingPrev, float idle, float headYaw, float headPitch, float scale, Entity entity) {
		;
	}

	/**
	 * The standard setLivingAnimations method from ModelBase with correct parameter
	 * mappings. Calls the superclass method.
	 *
	 * @param entityLiving
	 *            - The EntityLiving instance currently being rendered.
	 * @param swingProgress
	 *            - The arm swing progress of the Entity being rendered.
	 * @param swingProgressPrev
	 *            - The previous tick's arm swing progress of the Entity being
	 *            rendered.
	 * @param renderPartialTicks
	 *            - Render partial ticks
	 */
	@Override
	public void setLivingAnimations(EntityLivingBase entityLiving, float swingProgress, float swingProgressPrev, float renderPartialTicks) {
		;
	}

	/**
	 * Creates an array or group of ModelRenderers.
	 * 
	 * @param children
	 *            - The ModelRenderer instances we're adding to this group.
	 * @return The array or group created.
	 */
	public static ModelRenderer[] group(ModelRenderer... children) {
		return children;
	}

	/**
	 * Constructs a standard ModelBase instance from the specified class.
	 * 
	 * @param modelClass
	 *            - A class extending ModelBase which will be instantaniated.
	 * @return Instance of the class specified in the modelClass parameter.
	 */
	public static ModelBase createModelBase(Class<? extends ModelBase> modelClass) {
		try {
			return (modelClass.getConstructor()).newInstance(new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Constructs a ModelBaseExtension instance from the specified class.
	 * 
	 * @param modelClass
	 *            - A class extending ModelBaseExtension which will be
	 *            instantiated.
	 * @return Instance of the class specified in the modelClass parameter.
	 */
	public static Model createExtendedModelBase(Class<? extends Model> modelClass) {
		try {
			return (modelClass.getConstructor()).newInstance(new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the number of ticks the entity has existed for, plus the time since the
	 * game last ticked. This allows for precise timing and smooth movement.
	 * 
	 * @param base
	 *            - The EntityLivingBase class for which to check the ticks existed.
	 * @return TicksExisted + partialTicks of the entity.
	 */
	public static float getIdleProgress(EntityLivingBase base) {
		return base.ticksExisted + AccessHandler.getPartialTicks();
	}

	/**
	 * Gets the limb swing progress of an entity. Includes partial ticks for
	 * precision.
	 * 
	 * @param base
	 *            - The EntityLivingBase class for which to get the limb swing
	 *            progress from.
	 * @return How far along the entity is from completing its swing.
	 */
	public static float getSwingProgress(EntityLivingBase base) {
		return base.limbSwing - base.limbSwingAmount * (1.0F - AccessHandler.getPartialTicks());
	}

	/**
	 * Gets the previous limb swing progress of an entity. Includes partial ticks
	 * for precision. Basically float-precise timing of ticksExisted, instead of an
	 * integer value.
	 * 
	 * @param base
	 *            - The EntityLivingBase class for which to get the previous limb
	 *            swing progress from.
	 * @return The time since the last limb swing of the entity was completed.
	 */
	public static float getSwingProgressPrev(EntityLivingBase base) {
		return base.prevLimbSwingAmount + (base.limbSwingAmount - base.prevLimbSwingAmount) * AccessHandler.getPartialTicks();
	}

	/**
	 * Gets the yaw rotation of the entity's head. Includes partial ticks for
	 * precision.
	 * 
	 * @param base
	 *            - The entity from which to get the head yaw rotation from.
	 * @return The value of the yaw rotation the head is at.
	 */
	public static float getHeadYaw(EntityLivingBase base) {
		float yawOffset = MathUtils.interpolateRotation(base.prevRenderYawOffset, base.renderYawOffset, AccessHandler.getPartialTicks());
		float yawHead = MathUtils.interpolateRotation(base.prevRotationYawHead, base.rotationYawHead, AccessHandler.getPartialTicks());
		return yawHead - yawOffset;
	}

	/**
	 * Gets the pitch rotation of the entity's head. Includes partial ticks for
	 * precision.
	 * 
	 * @param base
	 *            - The entity from which to get the head pitch rotation from.
	 * @return The value of the pitch rotation the head is at.
	 */
	public static float getHeadPitch(EntityLivingBase base) {
		return (base.prevRotationPitch + (base.rotationPitch - base.prevRotationPitch) * AccessHandler.getPartialTicks());
	}

	/**
	 * Gets the idle progress of a generic Object. Uses partial ticks for precision.
	 * Basically float-precise timing of ticksExisted, instead of an integer value.
	 * 
	 * @param o
	 *            - The object for which to get the idle progress from. Should be an
	 *            instance of EntityLivingBase.
	 * @return ticksExisted + partialTicks of the object.
	 */
	public static float idleProgress(Object o) {
		if (o != null && o instanceof EntityLivingBase) {
			return getIdleProgress((EntityLivingBase) o);
		}

		return 0F;
	}

	/**
	 * Gets the swing process of a generic Object. Uses partial ticks for precision.
	 * 
	 * @param o
	 *            - The object to get the swing progress of. Should be an instance
	 *            of EntityLivingBase.
	 * @return How far along the object is from completing its swing.
	 */
	public static float swingProgress(Object o) {
		if (o != null && o instanceof EntityLivingBase) {
			return getSwingProgress((EntityLivingBase) o);
		}

		return 0F;
	}

	/**
	 * Gets the previous swing progress of a generic Object.
	 * 
	 * @param o
	 *            - The object to get the previous swing progress of. Should be an
	 *            instance of EntityLivingBase.
	 * @return The time since the object's last swing was completed.
	 */
	public static float swingProgressPrev(Object o) {
		if (o != null && o instanceof EntityLivingBase) {
			return getSwingProgressPrev((EntityLivingBase) o);
		}

		return 0F;
	}

	/**
	 * Gets the yaw rotation of a generic Object.
	 * 
	 * @param o
	 *            - The object from which to get the yaw of. Should be an instance
	 *            of EntityLivingBase.
	 * @return The yaw rotation of the object.
	 */
	public static float headYaw(Object o) {
		if (o != null && o instanceof EntityLivingBase) {
			return getHeadYaw((EntityLivingBase) o);
		}

		return 0F;
	}

	/**
	 * Gets the pitch rotation of a generic Object.
	 * 
	 * @param o
	 *            - The object from which to get the pitch of. Should be an instance
	 *            of EntityLivingBase.
	 * @return The pitch rotation of the object.
	 */
	public static float headPitch(Object o) {
		if (o != null && o instanceof EntityLivingBase) {
			return getHeadPitch((EntityLivingBase) o);
		}

		return 0F;
	}
}