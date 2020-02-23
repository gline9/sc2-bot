package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.gline9.sc2.conglomerates.BasePoint;

import java.util.concurrent.ThreadLocalRandom;

public class InBasePlacementStrategy implements BuildingPlacementStrategy {
    private final S2Agent agent;

    public InBasePlacementStrategy(S2Agent agent)
    {
        this.agent = agent;
    }

    @Override
    public Point2d getLocationForStructure(Unit unit, Ability abilityForStructure, BasePoint base)
    {
        Point2d attempt;

        do
        {
            attempt = createRandomPoint(base.getExpansionPoint().toPoint2d());
        }
        while (!agent.query().placement(abilityForStructure, attempt) || agent.query().pathingDistance(unit, attempt) > 20 || agent.query().pathingDistance(base.getExpansionPoint().toPoint2d(), attempt) > 20);

        return attempt;
    }

    private Point2d createRandomPoint(Point2d reference)
    {
        return reference.add(Point2d.of(getRandomScalar(), getRandomScalar()).mul(15f));
    }

    private float getRandomScalar()
    {
        return ThreadLocalRandom.current().nextFloat() * 2 - 1;
    }
}
