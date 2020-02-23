package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.Set;

public class Hatchery extends Base<Hatchery>
{
    private final UnitPool unitPool;

    public Hatchery(Unit unit, UnitPool unitPool)
    {
        super(unit);
        this.unitPool = unitPool;
    }

    @Override
    protected Ability getTrainWorkerAbility()
    {
        return Abilities.TRAIN_DRONE;
    }

    public void createWorker(S2Agent agent)
    {
        Set<? extends Larva> larva = unitPool.getUnitsOfType(Larva.class);
        if (larva.isEmpty())
        {
            return;
        }

        larva.iterator().next().executeAbility(agent, getTrainWorkerAbility(), true);
    }
}
