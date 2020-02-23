package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class SCV extends AbsUnit<SCV>
{
    private boolean isMiningMinerals = false;
    private boolean isBuildingStructure = false;

    public SCV(Unit unit)
    {
        super(unit);
    }

    public void mineMinerals(S2Agent agent, Minerals minerals)
    {
        onIdle();
        isMiningMinerals = true;
        executeAbilityOnUnit(agent, Abilities.SMART, minerals.getUnit(), true);
    }

    public void buildStructure(S2Agent agent, Ability structureAbility, Point2d location)
    {
        onIdle();
        isBuildingStructure = true;
        executeAbilityAtLocation(agent, structureAbility, location, false);
    }

    public boolean isMiningMinerals()
    {
        return isMiningMinerals;
    }

    public boolean isBuildingStructure()
    {
        return isBuildingStructure;
    }

    @Override
    public void onIdle()
    {
        isMiningMinerals = false;
        isBuildingStructure = false;
        super.onIdle();
    }
}
