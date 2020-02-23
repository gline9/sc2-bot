package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.gline9.sc2.units.AbsUnit;
import com.gline9.sc2.units.ControllableUnit;
import com.gline9.sc2.units.Marine;

import java.util.*;

public class AttackCommand implements Command<AbsUnit>
{
    private static List<Point2d> startLocations;
    private S2Agent agent;
    private final PatrolCommand patrolCommand;
    private final RandomPatrolCommand randomPatrolCommand;

    public AttackCommand(S2Agent agent)
    {
        this.agent = agent;
        if (null == startLocations)
        {
            startLocations = new ArrayList<>(agent.observation().getGameInfo().getStartRaw().get().getStartLocations());
        }
        this.patrolCommand = new PatrolCommand(agent);
        this.randomPatrolCommand = new RandomPatrolCommand(agent);
    }

    public void addPreferredType(UnitType type)
    {
        this.patrolCommand.addPreferredType(type);
    }

    @Override
    public boolean handle(AbsUnit unit)
    {
        Point2d location = unit.getUnit().getPosition().toPoint2d();

        startLocations.removeIf(start -> location.distance(start) < 10);

        if (patrolCommand.handle(unit))
        {
            return true;
        }

        if (startLocations.isEmpty())
        {
            randomPatrolCommand.handle(unit);
            return true;
        }

        unit.executeAbilityAtLocation(agent, Abilities.ATTACK_ATTACK, startLocations.iterator().next(), false);
        return true;
    }

    @Override
    public void onIdle(AbsUnit unit)
    {
        if (startLocations.isEmpty())
        {
            this.randomPatrolCommand.onIdle(unit);
        }
    }
}
