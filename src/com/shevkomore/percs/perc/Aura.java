package com.shevkomore.percs.perc;

import org.bukkit.Particle;

/**
 * {@code Aura} class stores some parameters that are required to use a {@linkplain org.bukkit.World.spawnParticle spawnParicle} function.
 * 
 * </p><b>Stored parameters:</b>
 * <ul>
 * <li><b>particle</b>: a {@linkplain org.bukkit.Particle Particle} enum option.</li>
 * <li><b>intensity</b>: amount of {@linkplain org.bukkit.Particle particles} generated at once.</li>
 * <li><b>options</b>: additional {@linkplain org.bukkit.Particle.DustOptions options} required. <i>({@linkplain org.bukkit.Particle#REDSTONE REDSTONE} particles only)</i></li>
 * </ul>
 * 
 * <i>other particles that require additional options are not supported.</i>
 * */
public class Aura {
	public Particle particle;
	public int intensity;
	public Particle.DustOptions options;
	/**
	 * Creates a {@linkplain Aura} instance.
	 * 
	 * @param   particle   a {@linkplain org.bukkit.Particle Particle} enum option.
	 * @param   intensity   amount of {@linkplain org.bukkit.Particle particles} generated at once.
	 * */
	public Aura(Particle particle, int intensity) {
		this.particle = particle;
		this.intensity = intensity;
	}
	/**
	 * Creates a {@linkplain Aura} instance with {@linkplain org.bukkit.Particle.DustOptions options} defined.
	 * 
	 * @param   particle   a {@linkplain org.bukkit.Particle Particle} enum option.
	 * @param   intensity   amount of {@linkplain org.bukkit.Particle particles} generated at once.
	 * @param   options   additional {@linkplain org.bukkit.Particle.DustOptions options} required. <i>({@linkplain org.bukkit.Particle#REDSTONE REDSTONE} particles only)</i>
	 * */
	public Aura(Particle particle, int intensity, Particle.DustOptions options) {
		this(particle, intensity);
		this.options = options;
	}
	/**
	 * use this {@linkplain Aura} instance in {@linkplain Perc.getAura getAura()} when no particles should appear.
	 * */
	public static Aura noAura = new Aura(null, 0);
}
