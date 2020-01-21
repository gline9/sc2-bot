package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.gline9.sc2.units.Minerals;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

import java.util.Comparator;
import java.util.Set;

public class MineralMiningCommand implements Command<SCV>
{
    private final S2Agent agent;
    private final UnitPool unitPool;

    public MineralMiningCommand(S2Agent agent, UnitPool unitPool)
    {
        this.agent = agent;
        this.unitPool = unitPool;
    }

    @Override
    public boolean handle(SCV scv)
    {
        if (scv.isMiningMinerals())
        {
            return true;
        }

        Minerals minerals = getBestMineralPatch(unitPool.getUnitsOfType(Minerals.class), scv.getUnit().getPosition());
        scv.mineMinerals(agent, minerals);
        return true;
    }

    private Minerals getBestMineralPatch(Set<? extends Minerals> minerals, Point point)
    {
        return minerals.stream().min(Comparator.comparing(a -> a.getUnit().getPosition().distance(point))).orElseThrow(RuntimeException::new);
    }
}
