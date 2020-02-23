package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.gline9.sc2.command.AttackCommand;
import com.gline9.sc2.command.BuildStructureCommand;
import com.gline9.sc2.command.Command;
import com.gline9.sc2.command.PatrolCommand;
import com.gline9.sc2.conglomerates.BasePoint;
import com.gline9.sc2.units.*;

import java.util.Collection;

public class BuildMarineStrategy implements ArmyConstructionStrategy
{
    private static final int BARRACKS_COUNT = 4;
    private final S2Agent agent;
    private final InBasePlacementStrategy buildingPlacementStrategy;
    private final UnitPool unitPool;
    private int barracksCount = 0;
    private boolean buildingBarracks = false;
    private final BasePoint basePoint;

    public BuildMarineStrategy(S2Agent agent, InBasePlacementStrategy buildingPlacementStrategy, UnitPool unitPool, BasePoint base)
    {
        this.agent = agent;
        this.buildingPlacementStrategy = buildingPlacementStrategy;
        this.unitPool = unitPool;
        this.basePoint = base;
    }

    public void tick()
    {
        Collection<? extends Marine> marines = unitPool.getUnitsOfType(Marine.class);

        if (marines.size() > 30)
        {
            for (Marine marine : marines)
            {
                if (!marine.getCommand().getClass().equals(AttackCommand.class))
                {
                    marine.setCommand(new AttackCommand(agent));
                }
            }
        }

        if (!isSupplyDepotRequirementFulfilled())
        {
            return;
        }

        int minerals = agent.observation().getMinerals();

        if (minerals < 150)
        {
            return;
        }

        for (Barracks barracks : unitPool.getUnitsOfType(Barracks.class))
        {
            if (!barracks.isUnitInQueue())
            {
                barracks.createMarine(agent);
            }
        }

        if (buildingBarracks || barracksCount >= BARRACKS_COUNT)
        {
            return;
        }

        final SCV scv = basePoint.getAvailableWorker();

        if (null == scv)
        {
            return;
        }

        Command<? super SCV> previousCommand = scv.getCommand();

        BuildStructureCommand buildBarracks = new BuildStructureCommand(agent, buildingPlacementStrategy, Abilities.BUILD_BARRACKS, basePoint);
        buildBarracks.subscribeToCompletion(() -> {
            scv.setCommand(previousCommand);
            barracksCount++;
            buildingBarracks = false;
        });

        scv.setCommand(buildBarracks);

        buildingBarracks = true;
    }

    private boolean isSupplyDepotRequirementFulfilled()
    {
        return unitPool.getUnitsOfType(SupplyDepot.class).stream().anyMatch(Building::isConstructionCompleted);
    }
}
