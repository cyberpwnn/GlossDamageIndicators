package com.volmit.gloss.damageindicators;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
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
	private int limit;

	@Override
	public void start()
	{
		limit = 0;
		registerListener(this);
		ignore = new GList<>();

		J.a(() ->
		{
			if(limit > 0)
			{
				limit--;
			}
		}, 20);

		try
		{
			Configurator.BUKKIT.load(Config.class, getDataFile("config.yml"));
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
		if(limit >= Config.maxPerSecond)
		{
			return;
		}

		limit++;
		Location initial = e.getLocation().clone().add(new Vector(0, 0.7, 0));
		TemporaryDescriptor d = GlossAPI.getInstance().createTemporaryHologram("dmg-" + e.getUniqueId() + M.ms() + UUID.randomUUID().toString().split("-")[1], initial, Config.maxTimeAlive);
		d.addLine(Config.dmgindicatorPrefix + ((int) (amt)));
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
		if(limit >= Config.maxPerSecond)
		{
			return;
		}

		limit++;

		Location initial = e.getLocation().clone().add(new Vector(0, -0.1, 0));
		TemporaryDescriptor d = GlossAPI.getInstance().createTemporaryHologram("hph-" + e.getUniqueId() + M.ms() + UUID.randomUUID().toString().split("-")[1], initial, Config.maxTimeAlive);
		d.addLine(Config.hpindicatorPrefix + ((int) (amt)));
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
}
