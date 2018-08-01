package cjminecraft.core.client.render;

import net.minecraft.client.renderer.Tessellator;

public class Vertex {
	public static Vertex	unitX		= new Vertex(1, 0, 0);
	public static Vertex	unitY		= new Vertex(0, 1, 0);
	public static Vertex	unitZ		= new Vertex(0, 0, 1);
	public static Vertex	unitNX		= new Vertex(-1, 0, 0);
	public static Vertex	unitNY		= new Vertex(0, -1, 0);
	public static Vertex	unitNZ		= new Vertex(0, 0, -1);
	public static Vertex	unitPYNZ	= new Vertex(0, 0.707, -0.707);
	public static Vertex	unitPXPY	= new Vertex(0.707, 0.707, 0);
	public static Vertex	unitPYPZ	= new Vertex(0, 0.707, 0.707);
	public static Vertex	unitNXPY	= new Vertex(-0.707, 0.707, 0);

	public float			x, y, z;

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vertex(double x, double y, double z) {
		this((float) x, (float) y, (float) z);
	}

	public Vertex(int x, int y, int z) {
		this((float) x, (float) y, (float) z);
	}

	public Vertex normalize() {
		float sq = (float) Math.sqrt(x * x + y * y + z * z);
		x = x / sq;
		y = y / sq;
		z = z / sq;
		return this;
	}

	public Vertex tessellate(Tessellator tessellator) {
		return this.tessellateWithUV(tessellator, null);
	}

	public Vertex tessellateWithUV(Tessellator tessellator, UV uv) {
		if (uv == null) {
			tessellator.getBuffer().pos(x, y, z);
		} else {
			tessellator.getBuffer().pos(x, y, z);
			tessellator.getBuffer().tex(uv.u, uv.v);
		}
		return this;
	}

	public Vertex add(double x, double y, double z) {
		return new Vertex(this.x + x, this.y + y, this.z + z);
	}

	public Vertex add(Vertex v) {
		return add(v.x, v.y, v.z);
	}

	public Vertex mul(double c) {
		return new Vertex(c * x, c * y, c * z);
	}

	@Override
	public String toString() {
		return String.format("Vertex(%s, %s, %s)", this.x, this.y, this.z);
	}
}