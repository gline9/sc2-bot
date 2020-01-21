package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.gline9.sc2.units.CommandCenter;

public class HideInCornerCommand implements Command<CommandCenter>
{
    private final S2Agent agent;

    public HideInCornerCommand(S2Agent agent)
    {
        this.agent = agent;
    }

    @Override
    public boolean handle(CommandCenter unit)
    {
        if (unit.getUnit().getFlying().orElse(false))
        {
            unit.executeAbilityAtLocation(agent, Abilities.SMART, Point2d.of(0, 0), false);
        }
        else
        {
            unit.takeOff(agent);
        }

        return true;
    }
}
