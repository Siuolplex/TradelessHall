package io.siuolplex.tradelesshall;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class Tradelesshall implements ModInitializer {

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(Commands.literal("tlh:check").then(Commands.argument("targets", EntityArgument.entity()).executes((commandContext) -> {

            Entity villagerIHope = EntityArgument.getEntity(commandContext, "targets");
            if (villagerIHope instanceof HelpMeBeEthical ethicalEntity) {
                boolean vehicle = ethicalEntity.tradelessHall$vehicleCheck();
                boolean sleep = ethicalEntity.tradelessHall$sleepCheck();
                boolean box = ethicalEntity.tradelessHall$boxCheck();
                commandContext.getSource().sendSuccess(() -> Component.literal("Following Checks Passed: \n" + "Vehicle: " + ((vehicle) ? "PASS" : "FAIL") + "\n" + "Sleep: " + ((sleep) ? "PASS" : "FAIL") + "\n" + "Box: " + ((box) ? "PASS" : "FAIL") + "\n"), true);
                return 1;
            } else {
                commandContext.getSource().sendFailure(Component.literal("Ruh roh, someone put in the wrong entity."));
                return 0;
            }
        }))));
    }
}
