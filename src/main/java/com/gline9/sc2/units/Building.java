package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Unit;

public abstract class Building<B extends Building<B>> extends AbsUnit<B>
{
    public Building(Unit unit)
    {
        super(unit);
    }

    public boolean isConstructionCompleted()
    {
        return getUnit().getBuildProgress() == 1.0f;
    }

    public void takeOff(S2Agent agent)
    {
        executeAbility(agent, Abilities.LIFT, false);
    }
}
