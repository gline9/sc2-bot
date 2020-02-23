package com.gline9.sc2.conglomerates;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.debug.Color;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.gline9.sc2.command.MineralMiningCommand;
import com.gline9.sc2.strategy.ArmyConstructionStrategy;
import com.gline9.sc2.units.*;

import java.util.*;
import java.util.stream.Collectors;

public class BasePoint
{
    public static final int MIN_WORKERS = 22;
    public static final int MAX_WORKERS = 30;
    private final Point expansionPoint;
    private final List<SCV> scvs = new ArrayList<>();
    private final S2Agent agent;
    private final UnitPool unitPool;
    private List<Minerals> mineralPatches;
    private Base base;
    private ArmyConstructionStrategy armyConstructionStrategy;
    private int maxWorkers = MIN_WORKERS;

    public BasePoint(S2Agent agent, Point expansionPoint, UnitPool unitPool)
    {
        this.agent = agent;
        this.expansionPoint = expansionPoint;
        this.unitPool = unitPool;
    }

    public void setArmyConstructionStrategy(ArmyConstructionStrategy strategy)
    {
        this.armyConstructionStrategy = strategy;
    }

    public CommandCenter getCommandCenter()
    {
        List<? extends CommandCenter> closest = unitPool.getNClosestUnits(expansionPoint.toPoint2d(), CommandCenter.class, 1);
        if (closest.isEmpty())
        {
            return null;
        }

        CommandCenter cc = closest.get(0);
        if (cc.getLocation().distance(expansionPoint.toPoint2d()) > 2)
        {
            return null;
        }

        return cc;
    }

    public void registerSCV(SCV scv)
    {
        scv.onIdle();
        scv.setCommand(new MineralMiningCommand(agent, this));
        scvs.add(scv);
    }

    public int getWorkerCount()
    {
        return scvs.size();
    }

    public void startExpanding()
    {
        this.maxWorkers = MAX_WORKERS;
    }

    public void stopExpanding()
    {
        this.maxWorkers = MIN_WORKERS;
    }

    public Minerals getRandomMineralPatch()
    {
        if (null == mineralPatches)
        {
            System.out.println("finding minerals");
            mineralPatches = unitPool.getNClosestUnits(expansionPoint.toPoint2d(), Minerals.class, 8);
            for (Minerals minerals : mineralPatches)
            {
                createBox(agent, minerals.getUnit().getPosition(), 1);
            }
            agent.debug().sendDebug();
        }
        Collections.shuffle(mineralPatches);
        return mineralPatches.get(0);
    }

    public List<SCV> removeExtraWorkers()
    {
        long notReady = scvs.stream().filter(scv -> !scv.isMiningMinerals()).count();
        List<SCV> extras = scvs.stream().filter(scv -> !scv.isMiningMinerals()).skip(maxWorkers - notReady).collect(Collectors.toList());
        scvs.removeAll(extras);
        return extras;
    }

    public Point getExpansionPoint()
    {
        return expansionPoint;
    }

    public void tick()
    {
        this.armyConstructionStrategy.tick();

        CommandCenter cc = getCommandCenter();
        if (cc == null)
        {
            return;
        }

        if (scvs.size() >= maxWorkers)
        {
            return;
        }

        if (agent.observation().getMinerals() >= 50 && !cc.isUnitInQueue())
        {
            cc.createWorker(agent);
        }

    }

    public static void createBox(S2Agent agent, Point point, int radius)
    {
        agent.debug().debugBoxOut(point.sub(Point.of(radius, radius)), point.add(Point.of(radius, radius)), Color.RED);
    }

    public SCV getAvailableWorker()
    {
        return scvs.stream().filter(scv -> scv.isMiningMinerals()).findFirst().orElse(null);
    }
}
