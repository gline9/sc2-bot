package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.gline9.sc2.units.AbsUnit;

public class RandomPatrolCommand implements Command<AbsUnit>
{
    private final S2Agent agent;

    public RandomPatrolCommand(S2Agent agent)
    {
        this.agent = agent;
    }

    private Point2d point;

    @Override
    public boolean handle(AbsUnit unit)
    {
        if (null == point)
        {
            point = agent.observation().getGameInfo().findRandomLocation();
            unit.executeAbilityAtLocation(agent, Abilities.ATTACK_ATTACK, point, false);
        }

        return true;
    }

    public void onIdle(AbsUnit unit)
    {
        point = null;
    }
}
