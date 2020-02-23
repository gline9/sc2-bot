package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.gline9.sc2.command.BuildStructureCommand;
import com.gline9.sc2.command.Command;
import com.gline9.sc2.conglomerates.BasePoint;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

import java.util.ArrayList;
import java.util.List;

public class ExpansionStrategy
{
    private final S2Agent agent;
    private final UnitPool unitPool;
    private final List<BasePoint> possibleBases;
    private final List<BasePoint> builtBases = new ArrayList<>();
    private boolean expanding = false;

    public ExpansionStrategy(S2Agent agent, List<BasePoint> possibleBases, BasePoint startingBase, UnitPool unitPool)
    {
        this.agent = agent;
        this.unitPool = unitPool;
        this.possibleBases = possibleBases;
        this.builtBases.add(startingBase);
    }

    public void tick()
    {
        List<BasePoint> readyToExpand = new ArrayList<>();
        for (BasePoint created : builtBases)
        {
            created.tick();
            if (created.getWorkerCount() >= BasePoint.MIN_WORKERS)
            {
                readyToExpand.add(created);
            }
        }

        if (expanding)
        {
            return;
        }

        if (!readyToExpand.isEmpty() && agent.observation().getMinerals() >= 400)
        {
            expanding = true;
            readyToExpand.forEach(BasePoint::startExpanding);
            BasePoint toExpand = getClosestUnocupiedBase(readyToExpand, possibleBases);
            possibleBases.remove(toExpand);

            SCV scv = readyToExpand.get(0).getAvailableWorker();

            Command<? super SCV> previousCommand = scv.getCommand();

            BuildStructureCommand buildCC = new BuildStructureCommand(agent,
                    (unit, abilityForStructure, base) -> toExpand.getExpansionPoint().toPoint2d(),
                    Abilities.BUILD_COMMAND_CENTER, readyToExpand.get(0));
            buildCC.subscribeToCompletion(() -> {
                scv.setCommand(previousCommand);
                expanding = false;
                readyToExpand.forEach(BasePoint::stopExpanding);

                List<SCV> workers = new ArrayList<>();
                for (BasePoint basePoint : readyToExpand)
                {
                    workers.addAll(basePoint.removeExtraWorkers());
                }

                workers.forEach(toExpand::registerSCV);
                builtBases.add(toExpand);
                ArmyConstructionStrategy armyConstructionStrategy = new BuildMarineStrategy(agent, new InBasePlacementStrategy(agent), unitPool, toExpand);
                toExpand.setArmyConstructionStrategy(armyConstructionStrategy);
            });
            scv.setCommand(buildCC);
        }
    }

    private BasePoint getClosestUnocupiedBase(List<BasePoint> readyToExpand, List<BasePoint> toExpandTo)
    {
        double bestScore = Double.POSITIVE_INFINITY;
        BasePoint winner = null;

        for (BasePoint expansionPoint : toExpandTo)
        {
            double score = getDistanceScore(readyToExpand, expansionPoint);
            if (score < bestScore)
            {
                bestScore = score;
                winner = expansionPoint;
            }
        }

        return winner;
    }

    private double getDistanceScore(List<BasePoint> readyToExpand, BasePoint toExpandTo)
    {
        double score = 0;

        for (BasePoint ready : readyToExpand)
        {
            score += ready.getExpansionPoint().distance(toExpandTo.getExpansionPoint());
        }

        return score;
    }
}
