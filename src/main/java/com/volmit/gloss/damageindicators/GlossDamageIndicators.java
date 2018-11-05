package com.volmit.gloss.damageindicators;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import com.volmit.gloss.api.GLOSS;
import com.volmit.gloss.api.intent.TemporaryDescriptor;
import com.volmit.volume.bukkit.VolumePlugin;
import com.volmit.volume.bukkit.command.CommandTag;
import com.volmit.volume.bukkit.pawn.Async;
import com.volmit.volume.bukkit.pawn.Start;
import com.volmit.volume.bukkit.task.A;
import com.volmit.volume.bukkit.util.data.Edgy;
import com.volmit.volume.math.M;

@CommandTag("&8[&5GCB&8]:&7 ")
public class GlossDamageIndicators extends VolumePlugin
{
	@Start
	public void loadConf()
	{
		try
		{
			Config.read();
		}

		catch(Exception e)
		{
			System.out.println("Failed to read gloss damage indicators config.");
			e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void on(EntityDamageByEntityEvent e)
	{
		Entity damaged = e.getEntity();
		double dmg = e.getDamage();

		new A()
		{
			@Override
			public void run()
			{
				damaged(dmg, damaged);
			}
		};
	}

	@Edgy
	@Async
	private void damaged(double amt, Entity e)
	{
		Location initial = e.getLocation().clone().add(new Vector(0, 0.7, 0));
		TemporaryDescriptor d = GLOSS.getSourceLibrary().createTemporaryDescriptor("dmg-" + e.getUniqueId() + M.ms() + UUID.randomUUID().toString().split("-")[1], initial, Config.maxTimeAlive);
		d.addLine(Config.indicatorPrefix + ((int) (amt)));
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
}
