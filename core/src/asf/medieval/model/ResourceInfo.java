package asf.medieval.model;

/**
 * Created by daniel on 12/14/15.
 */
public strictfp class ResourceInfo {


	public static ResourceInfo[] standardConfiguration()
	{
		ResourceInfo[] ri = new ResourceInfo[ResourceId.values().length];
		ri[ResourceId.Wood.ordinal()] = new ResourceInfo();

		return ri;
	}
}
