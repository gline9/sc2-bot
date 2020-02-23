package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.unit.Unit;

public abstract class Base<T extends Base<T>> extends Building<T>
{
    private boolean isCreatingUnit = false;

    public Base(Unit unit)
    {
        super(unit);
    }

    protected abstract Ability getTrainWorkerAbility();

    public void createWorker(S2Agent agent)
    {
        isCreatingUnit = true;
        executeAbility(agent, getTrainWorkerAbility(), true);
    }

    public boolean isUnitInQueue()
    {
        return isCreatingUnit;
    }

    @Override
    public void onIdle()
    {
        isCreatingUnit = false;
        super.onIdle();
    }
}
