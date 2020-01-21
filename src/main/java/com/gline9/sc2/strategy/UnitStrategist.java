package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.gline9.sc2.command.CreateWorkerCommand;
import com.gline9.sc2.command.MineralMiningCommand;
import com.gline9.sc2.units.Building;
import com.gline9.sc2.units.CommandCenter;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

public class UnitStrategist
{
    private final BuildingPlacementStrategy buildingPlacementStrategy;
    private final SupplyCapStrategy supplyCapStrategy;
    private final BuildMarineStrategy buildMarineStrategy;
    private final InsuranceStrategy insuranceStrategy;
    private final UnitPool unitPool;
    private final S2Agent agent;

    public UnitStrategist(UnitPool unitPool, S2Agent agent)
    {
        this.buildingPlacementStrategy = new BuildingPlacementStrategy(agent);
        this.unitPool = unitPool;
        this.agent = agent;
        this.supplyCapStrategy = new SupplyCapStrategy(agent, buildingPlacementStrategy, unitPool);
        this.buildMarineStrategy = new BuildMarineStrategy(agent, buildingPlacementStrategy, unitPool);
        this.insuranceStrategy = new InsuranceStrategy(agent, buildingPlacementStrategy, unitPool);

        unitPool.subscribeToUnitCreation(SCV.class, (unit) -> {
            unit.setCommand(new MineralMiningCommand(agent, unitPool));
        });

        unitPool.subscribeToUnitCreation(CommandCenter.class, (unit) -> {
            unit.setCommand(new CreateWorkerCommand(agent, unitPool));
        });
    }

    public void tick()
    {
        if (this.insuranceStrategy.tick())
        {
            return;
        }
        this.supplyCapStrategy.tick();
        this.buildMarineStrategy.tick();
    }
}
