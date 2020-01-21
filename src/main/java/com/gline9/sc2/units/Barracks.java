package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class Barracks extends AbsUnit<Barracks>
{
    private boolean isCreatingUnit = false;

    public Barracks(Unit unit)
    {
        super(unit);
    }

    public void createMarine(S2Agent agent)
    {
        isCreatingUnit = true;
        executeAbility(agent, Abilities.TRAIN_MARINE, true);
    }

    public boolean isUnitInQueue()
    {
        return isCreatingUnit;
    }

    @Override
    public void onIdle()
    {
        isCreatingUnit = false;
    }
}
