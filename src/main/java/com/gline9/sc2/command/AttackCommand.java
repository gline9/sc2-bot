package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.gline9.sc2.units.Marine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttackCommand implements Command<Marine>
{
    private final List<Point2d> startLocations;
    private S2Agent agent;
    private final PatrolCommand patrolCommand;

    public AttackCommand(S2Agent agent)
    {
        this.agent = agent;
        startLocations = new ArrayList<>(agent.observation().getGameInfo().getStartRaw().get().getStartLocations());
        this.patrolCommand = new PatrolCommand(agent);
    }

    @Override
    public boolean handle(Marine unit)
    {
        Point2d location = unit.getUnit().getPosition().toPoint2d();

        startLocations.removeIf(start -> location.distance(start) < 10);

        if (patrolCommand.handle(unit))
        {
            return true;
        }

        unit.executeAbilityAtLocation(agent, Abilities.ATTACK_ATTACK, startLocations.iterator().next(), false);
        return true;
    }
}
