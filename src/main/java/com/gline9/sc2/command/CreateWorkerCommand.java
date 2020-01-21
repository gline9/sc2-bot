package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.gline9.sc2.units.CommandCenter;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

public class CreateWorkerCommand implements Command<CommandCenter>
{
    private final S2Agent agent;
    private final UnitPool unitPool;

    public CreateWorkerCommand(S2Agent agent, UnitPool unitPool)
    {
        this.agent = agent;
        this.unitPool = unitPool;
    }

    @Override
    public boolean handle(CommandCenter unit)
    {
        if (agent.observation().getMinerals() >= 50 && !unit.isUnitInQueue() && unitPool.getUnitsOfType(SCV.class).size() < 20)
        {
            unit.createSCV(agent);
        }

        return true;
    }
}
