package cjminecraft.core.client.render.world;

public interface IClimateProvider {
	public ICloudProvider getCloudProvider();

	public IStormProvider getStormProvider();
}