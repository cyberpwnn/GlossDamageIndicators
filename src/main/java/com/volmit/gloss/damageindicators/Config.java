package com.volmit.gloss.damageindicators;

import mortar.api.config.Key;

public class Config
{
	@Key("damage-indicators.motion.random-throw-force")
	public static double randomThrowForce = 0.08;

	@Key("damage-indicators.max-indicators-per-second")
	public static int maxPerSecond = 10;

	@Key("damage-indicators.motion.initial-up-force")
	public static double initialThrowUpForce = 0.13;

	@Key("damage-indicators.motion.gravity-factor")
	public static double gravityForce = 0.0093;

	@Key("damage-indicators.max-ms-alive")
	public static int maxTimeAlive = 3000;

	@Key("damage-indicators.damage-indicator-prefix")
	public static String dmgindicatorPrefix = "&c&l";

	@Key("damage-indicators.heal-indicator-prefix")
	public static String hpindicatorPrefix = "&a&l";
}
