package com.volmit.gloss.damageindicators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

import com.volmit.gloss.api.GLOSS;
import com.volmit.gloss.api.intent.TemporaryDescriptor;
import com.volmit.gloss.api.wrapper.GlossAPI;

import mortar.api.config.Configurator;
import mortar.api.sched.A;
import mortar.api.sched.J;
import mortar.api.sched.S;
import mortar.bukkit.plugin.MortarPlugin;
import mortar.compute.math.M;
import mortar.lang.collection.GList;

public class GlossDamageIndicators extends MortarPlugin implements Listener
{
	private GList<Entity> ignore;

	@Override
	public void start()
	{
		registerListener(this);
		ignore = new GList<>();
		try
		{
			Configurator.BUKKIT.load(Config.class, GLOSS.getConfigLocation(this));
		}

		catch(Exception e)
		{
			System.out.println("Failed to read gloss damage indicators config.");
			e.printStackTrace();
		}
	}

	public void checkDamage(LivingEntity e)
	{
		double hp = e.getHealth();
		new S(2)
		{
			@Override
			public void run()
			{
				double nhp = e.getHealth();

				new A()
				{
					@Override
					public void run()
					{
						if(hp > nhp)
						{
							damaged(hp - nhp, e);
						}

						else if(hp < nhp)
						{
							healed(nhp - hp, e);
						}
					}
				};
			}
		};
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent e)
	{
		if(ignore.contains(e.getEntity()))
		{
			return;
		}

		if(e.getEntity() instanceof LivingEntity)
		{
			checkDamage((LivingEntity) e.getEntity());
			ignore.add(e.getEntity());
			J.s(() -> ignore.remove(e.getEntity()), 3);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntityDamageByBlockEvent e)
	{
		if(ignore.contains(e.getEntity()))
		{
			return;
		}

		if(e.getEntity() instanceof LivingEntity)
		{
			checkDamage((LivingEntity) e.getEntity());
			ignore.add(e.getEntity());
			J.s(() -> ignore.remove(e.getEntity()), 3);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void on(EntityRegainHealthEvent e)
	{
		if(ignore.contains(e.getEntity()))
		{
			return;
		}

		if(e.getEntity() instanceof LivingEntity)
		{
			checkDamage((LivingEntity) e.getEntity());
			ignore.add(e.getEntity());
			J.s(() -> ignore.remove(e.getEntity()), 3);
		}
	}

	private void damaged(double amt, Entity e)
	{
		Location initial = e.getLocation().clone().add(new Vector(0, 0.7, 0));
		TemporaryDescriptor d = GlossAPI.getInstance().createTemporaryHologram("dmg-" + e.getUniqueId() + M.ms() + UUID.randomUUID().toString().split("-")[1], initial, Config.maxTimeAlive);
		d.addLine(Config.dmgindicatorPrefix + ((int) (amt)));
		d.setEmissiveLevel(0);
		Vector mot = Vector.getRandom().subtract(Vector.getRandom()).multiply(Config.randomThrowForce);
		mot.setY(Config.initialThrowUpForce);

		d.bindPosition(() ->
		{
			mot.setY(mot.getY() - Config.gravityForce);
			initial.add(mot);
			return initial;
		});

		d.setLocation(initial);
		GLOSS.getSourceLibrary().register(d);
	}

	private void healed(double amt, Entity e)
	{
		Location initial = e.getLocation().clone().add(new Vector(0, -0.1, 0));
		TemporaryDescriptor d = GlossAPI.getInstance().createTemporaryHologram("hph-" + e.getUniqueId() + M.ms() + UUID.randomUUID().toString().split("-")[1], initial, Config.maxTimeAlive);
		d.addLine(Config.hpindicatorPrefix + ((int) (amt)));
		d.setEmissiveLevel(0);
		Vector mot = Vector.getRandom().subtract(Vector.getRandom()).multiply(Config.randomThrowForce);
		mot.setY(Config.initialThrowUpForce);

		d.bindPosition(() ->
		{
			mot.setY(mot.getY() + (Config.gravityForce / 19.5));
			initial.add(mot);
			return initial;
		});

		d.setLocation(initial);
		GLOSS.getSourceLibrary().register(d);
	}

	@Override
	public void stop()
	{

	}

	@Override
	public String getTag(String subTag)
	{
		return "";
	}

	//@builder
	static{try{URL url = new URL("https://raw.githubusercontent.com/VolmitSoftware/Mortar/master/release/Mortar.jar");
	File plugins = new File("plugins");Boolean foundMortar = false;for(File i : plugins.listFiles())
	{if(i.isFile() && i.getName().endsWith(".jar")){ZipFile file = new ZipFile(i);try{
		Enumeration<? extends ZipEntry> entries = file.entries();while(entries.hasMoreElements())
		{ZipEntry entry = entries.nextElement();if("plugin.yml".equals(entry.getName())){
			InputStream in = file.getInputStream(entry);
			PluginDescriptionFile pdf = new PluginDescriptionFile(in);if(pdf.getMain()
					.equals("mortar.bukkit.plugin.MortarAPIPlugin")){foundMortar = true;break;}}}}catch(Throwable ex)
	{ex.printStackTrace();}finally{file.close();}}}if(!foundMortar){System.out
		.println("Cannot find mortar. Attempting to download...");try{HttpURLConnection con =
		(HttpURLConnection)url.openConnection(); HttpURLConnection.setFollowRedirects(false);
		con.setConnectTimeout(10000);con.setReadTimeout(10000);InputStream in = con.getInputStream();
		File mortar = new File("plugins/Mortar.jar");FileOutputStream fos =
				new FileOutputStream(mortar);byte[] buf = new byte[16819];int r = 0;
				while((r = in.read(buf)) != -1){fos.write(buf, 0, r);}fos.close();in.close();
				con.disconnect();System.out.println("Mortar has been downloaded. Installing...");
				Bukkit.getPluginManager().loadPlugin(mortar);}catch(Throwable e){System.out
					.println("Failed to download mortar! Please download it from " + url.toString()
							);}}}catch(Throwable e){e.printStackTrace();}}
	//@done
}
