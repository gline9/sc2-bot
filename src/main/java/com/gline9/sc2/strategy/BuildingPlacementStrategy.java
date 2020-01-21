package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;

import java.util.concurrent.ThreadLocalRandom;

public class BuildingPlacementStrategy
{
    private final S2Agent agent;

    public BuildingPlacementStrategy(S2Agent agent)
    {
        this.agent = agent;
    }

    public Point2d getLocationForStructure(Unit unit, Ability abilityForStructure, Point2d startingLocation)
    {
        Point2d attempt;

        do
        {
            attempt = createRandomPoint(startingLocation);
        }
        while (!agent.query().placement(abilityForStructure, attempt) || agent.query().pathingDistance(unit, attempt) > 15);

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
