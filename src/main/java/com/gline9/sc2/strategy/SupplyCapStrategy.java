package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.gline9.sc2.command.BuildStructureCommand;
import com.gline9.sc2.command.Command;
import com.gline9.sc2.conglomerates.BasePoint;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

public class SupplyCapStrategy
{
    private final S2Agent agent;
    private final InBasePlacementStrategy buildingPlacementStrategy;
    private final UnitPool unitPool;
    private final BasePoint mainBase;

    private boolean isBuildingSupply = false;

    public SupplyCapStrategy(S2Agent agent, InBasePlacementStrategy buildingPlacementStrategy, UnitPool unitPool, BasePoint mainBase)
    {
        this.agent = agent;
        this.buildingPlacementStrategy = buildingPlacementStrategy;
        this.unitPool = unitPool;
        this.mainBase = mainBase;
    }

    public void tick()
    {
        int supplyCap = agent.observation().getFoodCap();
        int foodUsed = agent.observation().getFoodUsed();

        if (supplyCap - foodUsed < 5)
        {
            queueSupplyDepotBuild();
        }

    }

    public void queueSupplyDepotBuild()
    {
        if (isBuildingSupply || agent.observation().getMinerals() < 100)
        {
            return;
        }

        SCV scv = mainBase.getAvailableWorker();

        Command<? super SCV> previousCommand = scv.getCommand();

        BuildStructureCommand buildSupplyDepotCommand = new BuildStructureCommand(agent, buildingPlacementStrategy, Abilities.BUILD_SUPPLY_DEPOT, mainBase);
        buildSupplyDepotCommand.subscribeToCompletion(() -> {
            scv.setCommand(previousCommand);
            isBuildingSupply = false;
        });

        scv.setCommand(buildSupplyDepotCommand);

        isBuildingSupply = true;
    }
}
