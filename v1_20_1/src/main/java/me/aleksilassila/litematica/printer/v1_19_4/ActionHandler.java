package me.aleksilassila.litematica.printer.v1_19_4;

import me.aleksilassila.litematica.printer.v1_19_4.actions.Action;
import me.aleksilassila.litematica.printer.v1_19_4.actions.PrepareAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ActionHandler {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;

    private final Queue<Action> actionQueue = new LinkedList<>();
    public PrepareAction lookAction = null;

    public ActionHandler(MinecraftClient client, ClientPlayerEntity player) {
        this.client = client;
        this.player = player;
    }

    private int tick = 0;

    public void onGameTick() {
        int tickRate = LitematicaMixinMod.PRINTING_INTERVAL.getIntegerValue();

        tick = tick % tickRate == tickRate - 1 ? 0 : tick + 1;
        if (tick % tickRate != 0) {
            return;
        }

        Action nextAction = actionQueue.poll();
        if (nextAction != null) {
            if (LitematicaMixinMod.DEBUG) System.out.println("Sending action " + nextAction);
            nextAction.send(client, player);
        } else {
            lookAction = null;
        }
    }

    public boolean acceptsActions() {
        return actionQueue.isEmpty();
    }

    public void addActions(Action... actions) {
        if (!acceptsActions()) return;

        for (Action action : actions) {
            if (action instanceof PrepareAction)
                lookAction = (PrepareAction) action;
        }

        actionQueue.addAll(List.of(actions));
    }
}
