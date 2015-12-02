package asf.medieval.net;

import asf.medieval.utility.FileManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by daniel on 11/15/15.
 */
public class GameServerConfig {


	public int tcpPort = 27677;
	public int udpPort = 27677;
	public String serverLogFileLocation;


	public GameServerConfig() {
	}


	public GameServerConfig(int tcpPort, int udpPort, String serverLogFileLocation) {
		this.tcpPort = tcpPort;
		this.udpPort = udpPort;
		this.serverLogFileLocation = serverLogFileLocation;
	}

	public GameServerConfig(String configLoc) {
		if (configLoc == null || configLoc.isEmpty()) {
			configLoc = "GameServerConfig.xml";
		}
		FileHandle configFileHandle = FileManager.relative(configLoc);

		try {
			XmlReader reader = new XmlReader();
			XmlReader.Element root = reader.parse(configFileHandle);

			try {
				tcpPort = Integer.parseInt(root.getChildByName("tcpPort").getText());
			} catch (Exception e) {
				tcpPort = 27677;
			}

			try {
				udpPort = Integer.parseInt(root.getChildByName("udpPort").getText());
			} catch (Exception e) {
				udpPort = 27677;
			}

			serverLogFileLocation = root.getChildByName("serverLogFileLocation").getText().trim();

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Could not load config file, probably didn't specify the right location");
			throw new IllegalArgumentException("could not read config file", e);

		}
	}


	/**
	 * creates an example xml config file. I'll want to incorporate this in to the gradle build
	 * script at some point.
	 */
	public void createExampleConfigFile()
	{
		final String configLoc = "GameServerConfig.xml";
		System.out.println("Creating an example GameServerConfig file: " + configLoc);
		// TODO: may need to handle this degradation better- what if the relative file
		// doesnt exist or is malformed etc?
		FileHandle configFileHandle = Gdx.files.absolute(configLoc);
		boolean local = false;
		if (!configFileHandle.exists() || configFileHandle.isDirectory()) {
			configFileHandle = FileManager.relative(configLoc);
			local = true;
			if (configFileHandle.isDirectory()) {
				throw new IllegalArgumentException("invalid config file: " + configLoc);
			}
		}

		if(local)
		{
			StringWriter stringWriter = new StringWriter();
			XmlWriter xmlWriter = new XmlWriter(stringWriter);
			try {
				xmlWriter
					.element("GameServer")
					.element("tcpPort").text(tcpPort).pop()
					.element("udpPort").text(udpPort).pop()
					.element("serverLogFileLocation").text(serverLogFileLocation).pop()
					.pop();
				configFileHandle.writeString(stringWriter.toString(), false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}


	}
}
