package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.gline9.sc2.command.AttackCommand;
import com.gline9.sc2.command.BuildStructureCommand;
import com.gline9.sc2.command.Command;
import com.gline9.sc2.command.PatrolCommand;
import com.gline9.sc2.units.*;

import java.util.Collection;
import java.util.Iterator;

public class BuildMarineStrategy
{
    private final S2Agent agent;
    private final BuildingPlacementStrategy buildingPlacementStrategy;
    private final UnitPool unitPool;
    private int barracksCount = 0;
    private boolean buildingBarracks = false;

    public BuildMarineStrategy(S2Agent agent, BuildingPlacementStrategy buildingPlacementStrategy, UnitPool unitPool)
    {
        this.agent = agent;
        this.buildingPlacementStrategy = buildingPlacementStrategy;
        this.unitPool = unitPool;

        unitPool.subscribeToUnitCreation(Marine.class, marine -> {
            System.out.println("Marine created");
            marine.setCommand(new PatrolCommand(agent));
        });
    }

    public void tick()
    {
        Collection<? extends Marine> marines = unitPool.getUnitsOfType(Marine.class);

        if (marines.size() > 20)
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

        int barracksC = unitPool.getUnitsOfType(Barracks.class).size();

        if (barracksC > barracksCount)
        {
            barracksCount = barracksC;
            buildingBarracks = false;
        }

        if (barracksC > 1 || buildingBarracks)
        {
            return;
        }

        Iterator<? extends SCV> scv = unitPool.getUnitsOfType(SCV.class).iterator();

        SCV next = null;
        while (scv.hasNext())
        {
            next = scv.next();

            if (!next.isBuildingStructure())
            {
                break;
            }
        }

        if (null == next)
        {
            return;
        }

        final SCV found = next;
        Command<? super SCV> previousCommand = found.getCommand();

        BuildStructureCommand buildBarracks = new BuildStructureCommand(agent, buildingPlacementStrategy, Abilities.BUILD_BARRACKS);
        buildBarracks.subscribeToCompletion(() -> {
            found.setCommand(previousCommand);
        });

        found.setCommand(buildBarracks);

        buildingBarracks = true;
    }

    private boolean isSupplyDepotRequirementFulfilled()
    {
        return unitPool.getUnitsOfType(SupplyDepot.class).stream().anyMatch(Building::isConstructionCompleted);
    }
}
