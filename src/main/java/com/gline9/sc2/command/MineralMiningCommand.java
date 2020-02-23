package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.gline9.sc2.conglomerates.BasePoint;
import com.gline9.sc2.units.Minerals;
import com.gline9.sc2.units.SCV;

public class MineralMiningCommand implements Command<SCV>
{
    private final S2Agent agent;
    private final BasePoint base;

    public MineralMiningCommand(S2Agent agent, BasePoint base)
    {
        this.agent = agent;
        this.base = base;
    }

    @Override
    public boolean handle(SCV scv)
    {
        if (scv.isMiningMinerals())
        {
            return true;
        }

        Minerals minerals = base.getRandomMineralPatch();
        scv.mineMinerals(agent, minerals);
        return true;
    }
}
