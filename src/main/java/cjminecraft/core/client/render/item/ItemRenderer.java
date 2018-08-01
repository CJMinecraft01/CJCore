package cjminecraft.core.client.render.item;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import cjminecraft.core.client.render.Draw;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * A neat class that removes the need for JSON in item rendering.
 * 
 * @author Hypeirochus
*/
public abstract class ItemRenderer implements IBakedModel {

	public static class ItemRenderList extends ItemOverrideList {

		public ItemRenderList() {
			super(Lists.<ItemOverride>newArrayList());
		}

		@Override
		public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
			if (originalModel instanceof ItemRenderer) {
				ItemRenderer model = (ItemRenderer) originalModel;
				model.setItemstack(stack);
				model.setEntity(entity);
			}

			return super.handleItemState(originalModel, stack, world, entity);
		}
	}

	protected static final Minecraft mc = Minecraft.getMinecraft();
	private static List<BakedQuad> quads = Collections.emptyList();
	protected EntityLivingBase entity;
	protected ModelBase model;
	private ItemRenderList overrides;
	protected ResourceLocation resource;
	private final Pair<? extends IBakedModel, Matrix4f> selfPair;

	protected ItemStack stack;

	public ItemRenderer(ModelBase model, ResourceLocation resource) {
		this.overrides = new ItemRenderList();
		this.selfPair = Pair.of(this, null);
		this.model = model;
		this.resource = resource;
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return ItemCameraTransforms.DEFAULT;
	}

	public ModelBase getModel() {
		return model;
	}

	@Override
	public ItemOverrideList getOverrides() {
		return overrides;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return null;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return quads;
	}

	public ResourceLocation getResourceLocation() {
		return resource;
	}

	protected void bindTexture() {
		Draw.bindTexture(resource);
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType type) {
		renderPre(stack, entity, type);

		switch (type) {
		case FIRST_PERSON_LEFT_HAND: {
			renderFirstPersonLeft(stack, entity, type);
		}
			break;
		case FIRST_PERSON_RIGHT_HAND: {
			renderFirstPersonRight(stack, entity, type);
		}
			break;
		case GUI: {
			renderInInventory(stack, entity, type);
		}
			break;
		case THIRD_PERSON_LEFT_HAND: {
			renderThirdPersonLeft(stack, entity, type);
		}
			break;
		case THIRD_PERSON_RIGHT_HAND: {
			renderThirdPersonRight(stack, entity, type);
		}
			break;
		case GROUND: {
			renderInWorld(stack, entity, type);
		}
			break;
		case FIXED: {
			renderFixed(stack, entity, type);
		}
			break;
		case HEAD: {
			renderHead(stack, entity, type);
		}
			break;

		default:
			break;
		}

		renderPost(stack, entity, type);

		GlStateManager.bindTexture(Minecraft.getMinecraft().getTextureMapBlocks().getGlTextureId());

		return selfPair;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}

	public abstract void renderFirstPersonLeft(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public abstract void renderFirstPersonRight(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public abstract void renderInInventory(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public abstract void renderInWorld(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public void renderPost(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType) {
	}

	public void renderPre(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType) {
	}

	public abstract void renderThirdPersonLeft(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public abstract void renderThirdPersonRight(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public abstract void renderFixed(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	public abstract void renderHead(ItemStack itemstack, EntityLivingBase entity, TransformType cameraTransformType);

	private void setEntity(EntityLivingBase entity) {
		this.entity = entity;
	}

	private void setItemstack(ItemStack stack) {
		this.stack = stack;
	}

	public void setResourceLocation(ResourceLocation resource) {
		this.resource = resource;
	}
}