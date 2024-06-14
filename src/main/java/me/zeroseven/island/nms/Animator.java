package me.zeroseven.island.nms;

import me.zeroseven.island.IslandPlugin;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Animator {

    private IslandPlugin islandPlugin;

    public Animator(IslandPlugin islandPlugin) {
        this.islandPlugin = islandPlugin;
    }

    private Random random = new Random();


    public void animateArmorStand(Player player, ArmorStand armorStand) {
        new BukkitRunnable() {
            double t = 0;
            boolean up = true;

            @Override
            public void run() {
                if (t > 1) {
                    up = false;
                } else if (t < -1) {
                    up = true;
                }

                t += up ? 0.05 : -0.05;

                updateArmorStandArmPose(armorStand, t);
            }
        }.runTaskTimer(islandPlugin, 0L, 1L);


        updateArmorStandHeadPose(armorStand, 0.3);



        new BukkitRunnable() {
            double t = 0;
            boolean up = true;
            boolean clockwise = true;

            @Override
            public void run() {
                if (t >= 1) {
                    up = false;
                } else if (t <= 0) {
                    up = true;
                    clockwise = !clockwise; // Reverse direction on each cycle
                }

                t += up ? 0.01 : -0.01;

                spawnParticleInFrontOfArmorStand(armorStand, Particle.VILLAGER_HAPPY, 1, 1); // Adjust offset and count as needed

                updateArmorStandBodyPose(armorStand, t, clockwise);

            }
        }.runTaskTimer(islandPlugin, 0L, 1L);
    }

    private void spawnParticleInFrontOfArmorStand(ArmorStand armorStand, Particle particle, double offset, int count) {
        Location location = armorStand.getLocation();
        Vector direction = location.getDirection().normalize().multiply(offset);
        Location particleLocation = location.add(direction);
        armorStand.getWorld().spawnParticle(particle, particleLocation, count);
    }

    private void updateArmorStandBodyPose(ArmorStand armorStand, double angle, boolean clockwise) {
        if (armorStand == null) {
            return;
        }

        // Rotate the body
        double bodyYaw = clockwise ? angle * Math.PI * 1 : -angle * Math.PI * 1;
        armorStand.setRotation(armorStand.getLocation().getYaw() + (float) bodyYaw, armorStand.getLocation().getPitch());
    }


    private void updateArmorStandHeadPose(ArmorStand armorStand, double angle) {
        if (armorStand == null) {
            return;
        }

        // Move the head up and down
        double headPitch = Math.sin(angle * Math.PI) * 0.5; // Adjust the multiplier for desired head movement range
        armorStand.setHeadPose(new EulerAngle(headPitch, 0, 0));
    }

    private void updateArmorStandArmPose(ArmorStand armorStand, double angle) {
        if (armorStand == null) {
            return;
        }

        armorStand.setRightArmPose(new EulerAngle(angle, 0, 0));
    }
}
