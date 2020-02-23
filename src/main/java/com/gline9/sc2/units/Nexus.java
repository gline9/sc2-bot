package com.gline9.sc2.units;

import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class Nexus extends Base<Nexus>
{
    public Nexus(Unit unit)
    {
        super(unit);
    }

    @Override
    protected Ability getTrainWorkerAbility()
    {
        return Abilities.TRAIN_PROBE;
    }
}
