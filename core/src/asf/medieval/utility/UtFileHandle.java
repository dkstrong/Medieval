package asf.medieval.utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by daniel on 12/1/15.
 */
public class UtFileHandle {

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

		try{
			String jarPath = UtFileHandle.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String directoryPath = Gdx.files.absolute(jarPath).parent().path();

			return Gdx.files.absolute(directoryPath +"/" +path);
		}catch(SecurityException ex){
			return Gdx.files.external(".medieval/"+path);
		}

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

	private UtFileHandle(){

	}

}
