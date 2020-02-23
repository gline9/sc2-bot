package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.gline9.sc2.command.PatrolCommand;
import com.gline9.sc2.conglomerates.BasePoint;
import com.gline9.sc2.units.Marine;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UnitStrategist implements Strategy
{
    private final InBasePlacementStrategy buildingPlacementStrategy;
    private final SupplyCapStrategy supplyCapStrategy;
    private final ExpansionStrategy expansionStrategy;
    private final UnitPool unitPool;
    private final S2Agent agent;
    private final BasePoint start;
    private final List<BasePoint> expansionBases;

    public UnitStrategist(UnitPool unitPool, S2Agent agent)
    {
        this.buildingPlacementStrategy = new InBasePlacementStrategy(agent);
        this.unitPool = unitPool;
        this.start = new BasePoint(agent, agent.observation()
                .getStartLocation(), unitPool);
        ArmyConstructionStrategy buildMarines = new BuildMarineStrategy(agent, buildingPlacementStrategy, unitPool, start);
        this.start.setArmyConstructionStrategy(buildMarines);

        this.expansionBases = agent.query().calculateExpansionLocations(agent.observation(), agent.debug())
                .stream()
                .map(point -> new BasePoint(agent, point, unitPool))
                .collect(Collectors.toList());

        this.expansionStrategy = new ExpansionStrategy(agent, expansionBases, start, unitPool);

        this.agent = agent;
        this.supplyCapStrategy = new SupplyCapStrategy(agent, buildingPlacementStrategy, unitPool, start);

        unitPool.subscribeToUnitCreation(SCV.class, (scv) -> {
            Point2d location = scv.getLocation();
            Optional<BasePoint> spawnedBasePoint = Stream.concat(Stream.of(start),expansionBases.stream()).sorted(Comparator.comparingDouble(base -> base.getExpansionPoint().toPoint2d().distance(location))).findFirst();
            spawnedBasePoint.ifPresent(basePoint -> basePoint.registerSCV(scv));
        });

        unitPool.subscribeToUnitCreation(Marine.class, marine -> {
            marine.setCommand(new PatrolCommand(agent));
        });
    }

    public void tick()
    {
        //if (this.insuranceStrategy.tick())
        //{
         //   return;
        //}
        this.supplyCapStrategy.tick();
        this.expansionStrategy.tick();
    }
}
