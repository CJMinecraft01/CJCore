package cjminecraft.core.client.particle;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cjminecraft.core.client.GameResources;
import cjminecraft.core.client.render.OpenGL;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityFXElectricArc extends Particle {
	private static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");

	private Random rand;
	private int color;
	private int tessellation;
	private float rotYaw;
	private float rotPitch;
	private float density;
	private double targetX;
	private double targetY;
	private double targetZ;
	private double displacement;
	private double complexity;

	public EntityFXElectricArc(World world, double x, double y, double z, double targetX, double targetY, double targetZ, int age) {
		this(world, x, y, z, targetX, targetY, targetZ, age, 1.6D, 0.1D, 0.1F, 0xFFAA99FF);
	}

	public EntityFXElectricArc(World world, double x, double y, double z, double targetX, double targetY, double targetZ, int age, int color) {
		this(world, x, y, z, targetX, targetY, targetZ, age, 1.6D, 0.1D, 0.1F, color);
	}

	public EntityFXElectricArc(World world, double x, double y, double z, double targetX, double targetY, double targetZ, int age, double displacement, double complexity, float density, int color) {
		super(world, x, y, z);
		this.rand = new Random();
		this.tessellation = 2;
		this.particleMaxAge = age;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.displacement = displacement;
		this.complexity = complexity;
		this.density = density;
		this.color = color;
		this.changeDirection((float) (this.posX - this.targetX), (float) (this.posY - this.targetY), (float) (this.posZ - this.targetZ));
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entity, float partialTicks, float rX, float rZ, float rYZ, float rXY, float rXZ) {
		GameResources.BLANK.bind();
		this.drawArc(buffer, posX, posY, posZ, targetX, targetY, targetZ, displacement, complexity, density);
	}

	public EntityFXElectricArc setTessellation(int tessellation) {
		this.tessellation = tessellation;
		return this;
	}

	private void changeDirection(float x, float y, float z) {
		double variance = MathHelper.sqrt(x * x + z * z);
		this.rotYaw = ((float) (Math.atan2(x, z) * 180.0D / Math.PI));
		this.rotPitch = ((float) (Math.atan2(y, variance) * 180.0D / Math.PI));
	}

	private void drawArc(BufferBuilder buffer, double x, double y, double z, double targetX, double targetY, double targetZ, double displacement, double complexity, float density) {
		if (displacement < complexity) {
			float rx = (float) (x - targetX);
			float ry = (float) (y - targetY);
			float rz = (float) (z - targetZ);

			this.changeDirection(rx, ry, rz);

			OpenGL.pushMatrix();
			OpenGL.translate((float) (x - interpPosX), (float) (y - interpPosY), (float) (z - interpPosZ));
			OpenGL.enableBlend();
			OpenGL.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_CURRENT_BIT);
			OpenGL.disableCullFace();
			OpenGL.rotate(90.0F, 1.0F, 0.0F, 0.0F);
			OpenGL.rotate(180.0F + this.rotYaw, 0.0F, 0.0F, -1.0F);
			OpenGL.rotate(this.rotPitch, 1.0F, 0.0F, 0.0F);
			OpenGL.disableLightMapping();
			OpenGL.disableLight();

			double vX1 = density * -0.15;
			double vX2 = density * -0.15 * 1.0;
			double vY2 = MathHelper.sqrt(rx * rx + ry * ry + rz * rz);
			double vY1 = 0.0D;

			int a = (color >> 24 & 255);
			int r = (color >> 16 & 255);
			int g = (color >> 8 & 255);
			int b = (color & 255);

			for (int i2 = 0; i2 < tessellation; i2++) {
				GlStateManager.rotate((360F / tessellation) / 2, 0.0F, 1.0F, 0.0F);
				OpenGL.color(r / 255F, g / 255F, b / 255F, a / 255F);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(vX2, vY2, 0).color(255, 255, 255, 255).endVertex();
				buffer.pos(vX1, vY1, 0).color(255, 255, 255, 255).endVertex();
				buffer.pos(-vX1, vY1, 0).color(255, 255, 255, 255).endVertex();
				buffer.pos(-vX2, vY2, 0).color(255, 255, 255, 255).endVertex();
				Tessellator.getInstance().draw();
			}

			OpenGL.enableLight();
			OpenGL.color(1.0F, 1.0F, 1.0F, 1.0F);
			OpenGL.enableCullFace();
			OpenGL.disableBlend();
			OpenGL.popMatrix();
		} else {
			double splitX = (targetX + x) / 2;
			double splitY = (targetY + y) / 2;
			double splitZ = (targetZ + z) / 2;
			splitX += (rand.nextFloat() - 0.5) * displacement;
			splitY += (rand.nextFloat() - 0.5) * displacement;
			splitZ += (rand.nextFloat() - 0.5) * displacement;
			drawArc(buffer, x, y, z, splitX, splitY, splitZ, displacement / 2, complexity, density);
			drawArc(buffer, targetX, targetY, targetZ, splitX, splitY, splitZ, displacement / 2, complexity, density);
		}
	}

	@Override
	public void onUpdate() {
		if (this.particleAge++ > this.particleMaxAge) {
			this.setExpired();
		}
	}

	@Override
	public int getFXLayer() {
		return 3;
	}
}