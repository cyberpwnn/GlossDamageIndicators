package com.volmit.gloss.damageindicators;

import primal.bukkit.config.Key;

public class Config
{
	@Key("damage-indicators.motion.random-throw-force")
	public static double randomThrowForce = 0.08;

	@Key("damage-indicators.motion.initial-up-force")
	public static double initialThrowUpForce = 0.13;

	@Key("damage-indicators.motion.gravity-factor")
	public static double gravityForce = 0.0093;

	@Key("damage-indicators.max-ms-alive")
	public static int maxTimeAlive = 3000;

	@Key("damage-indicators.indicator-prefix")
	public static String indicatorPrefix = "&c&l";
}
