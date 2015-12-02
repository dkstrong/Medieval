package asf.medieval.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by daniel on 12/1/15.
 */
public class FileManager {

	private static String relativePath;

	private FileManager(){

	}

	public static void setRelativePath(String path){
		if(path == null || path.trim().isEmpty())
			detectRelativePath();
		else
			relativePath = path;
	}

	public static void setRelativePathToLocal(){
		relativePath = Gdx.files.local("local").parent().path();
	}

	public static void detectRelativePath(){
		try{
			String jarPath = FileManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			relativePath = Gdx.files.absolute(jarPath).parent().path();
		}catch(SecurityException ex){
			relativePath = Gdx.files.external(".medieval").file().getPath();
		}
	}

	/**
	 * file located in the assets directory
	 * @param path
	 * @return
	 */
	public static FileHandle internal(String path){
		return Gdx.files.internal(path);
	}

	/**
	 * file located in the jar directory,
	 *
	 * If the JVM security permissions do not allow finding the jar directory,
	 * then a "dot directory" int he users home will be used instead
	 * @param path
	 * @return
	 */
	public static FileHandle relative(String path){
		return Gdx.files.absolute(relativePath +"/" +path);
	}

	/**
	 * check the "relative" path first, if a file does not exist then use internal instead.
	 *
	 * This allows the user to drop in their own maps, models, and otherassets
	 * @param path
	 * @return
	 */
	public static FileHandle moddable(String path){
		FileHandle relative = relative(path);
		if(relative.exists())
			return relative;
		return Gdx.files.internal(path);
	}



}
