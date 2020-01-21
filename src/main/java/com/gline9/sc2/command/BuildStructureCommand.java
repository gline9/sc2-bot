package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.gline9.sc2.strategy.BuildingPlacementStrategy;
import com.gline9.sc2.units.SCV;

public class BuildStructureCommand implements Command<SCV>
{
    private final S2Agent agent;
    private final BuildingPlacementStrategy buildingPlacementStrategy;
    private final Ability abilityToBuildStructure;

    private boolean building;
    private Runnable completionRunnable;

    public BuildStructureCommand(S2Agent agent, BuildingPlacementStrategy buildingPlacementStrategy, Ability abilityToBuildStructure)
    {
        this.agent = agent;
        this.buildingPlacementStrategy = buildingPlacementStrategy;
        this.abilityToBuildStructure = abilityToBuildStructure;
    }

    @Override
    public boolean handle(SCV unit)
    {
        if (unit.isBuildingStructure())
        {
            return false;
        }

        if (building)
        {
            completionRunnable.run();
            return false;
        }

        unit.buildStructure(agent, abilityToBuildStructure, buildingPlacementStrategy.getLocationForStructure(unit.getUnit(), abilityToBuildStructure, unit.getLocation()));
        building = true;

        return true;
    }

    public void subscribeToCompletion(Runnable completionRunnable)
    {
        this.completionRunnable = completionRunnable;
    }
}
