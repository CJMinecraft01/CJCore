package cjminecraft.core.proxy;

/**
 * Any proxy should look like this
 * @author CJMinecraft
 *
 */
public interface IProxy {
	
	void preInit();
	
	void init();
	
	void postInit();

}
