package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.gline9.sc2.units.Marine;

import java.util.List;

public class PatrolCommand implements Command<Marine>
{
    private final S2Agent agent;

    public PatrolCommand(S2Agent agent)
    {
        this.agent = agent;
    }

    @Override
    public boolean handle(Marine unit)
    {
        List<UnitInPool> unitPool = agent.observation().getUnits(Alliance.ENEMY);
        if (unitPool.isEmpty())
        {
            return false;
        }

        unit.executeAbilityAtLocation(agent, Abilities.ATTACK_ATTACK, unitPool.get(0).unit().getPosition().toPoint2d(), false);
        return true;
    }
}
