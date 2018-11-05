package com.volmit.gloss.damageindicators;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.volmit.gloss.api.GLOSS;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.util.data.YAMLClusterPort;
import com.volmit.volume.cluster.DataCluster;

public class Config
{
	public static double randomThrowForce = 0.08;
	public static double initialThrowUpForce = 0.13;
	public static double gravityForce = 0.0093;
	public static int maxTimeAlive = 3000;
	public static String indicatorPrefix = "&c&l";

	public static void read() throws IOException, Exception
	{
		File f = GLOSS.getConfigLocation(VolumePlugin.vpi);

		if(!f.exists())
		{
			f.getParentFile().mkdirs();
			new YAMLClusterPort().fromCluster(peel()).save(f);
		}

		FileConfiguration fc = new YamlConfiguration();
		fc.load(f);
		stick(new YAMLClusterPort().toCluster(fc));
	}

	public static void stick(DataCluster cc)
	{
		for(Field i : Config.class.getDeclaredFields())
		{
			try
			{
				if(cc.has(i.getName()))
				{
					i.set(null, cc.get(i.getName()));
				}
			}

			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static DataCluster peel()
	{
		DataCluster cc = new DataCluster();

		for(Field i : Config.class.getDeclaredFields())
		{
			try
			{
				cc.set(i.getName(), i.get(null));
			}

			catch(IllegalArgumentException | IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		return cc;
	}
}
