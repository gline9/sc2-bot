package com.gline9.sc2.command;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.*;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.gline9.sc2.units.AbsUnit;

import java.util.*;

public class PatrolCommand implements Command<AbsUnit>
{
    private final S2Agent agent;
    private final Map<UnitType, UnitTypeData> unitTypeData;
    private final Set<UnitType> unitTypePreferences = new HashSet<>();
    private Long lastUnit;

    public PatrolCommand(S2Agent agent)
    {
        this.agent = agent;
        this.unitTypeData = agent.observation().getUnitTypeData(false);
    }

    public void addPreferredType(UnitType type)
    {
        this.unitTypePreferences.add(type);
    }

    @Override
    public boolean handle(AbsUnit unit)
    {
        List<UnitInPool> unitPool = agent.observation().getUnits(Alliance.ENEMY);
        if (unitPool.isEmpty())
        {
            lastUnit = null;
            return false;
        }

        UnitInPool maxScoreUnit = null;
        double maxScore = Double.NEGATIVE_INFINITY;

        for (UnitInPool unitInPool : unitPool)
        {
            double score = scoreUnit(unit, unitInPool);
            if (score > maxScore)
            {
                maxScore = score;
                maxScoreUnit = unitInPool;
            }
        }

        if (null == maxScoreUnit)
        {
            lastUnit = null;
            return false;
        }

        lastUnit = maxScoreUnit.unit().getTag().getValue();
        unit.executeAbilityOnUnit(agent, Abilities.ATTACK_ATTACK, maxScoreUnit.unit(), false);
        return true;
    }

    private double scoreUnit(AbsUnit unit, UnitInPool pool)
    {
        Unit enemy = pool.unit();
        UnitTypeData enemyType = unitTypeData.get(enemy.getType());

        if (enemy.getType().getUnitTypeId() == Units.ZERG_EGG.getUnitTypeId() || enemy.getType().getUnitTypeId() == Units.ZERG_LARVA.getUnitTypeId())
        {
            return Double.NEGATIVE_INFINITY;
        }

        if (enemy.getType().getUnitTypeId() == Units.ZERG_HATCHERY.getUnitTypeId())
        {
            return -1e100;
        }

        Unit us = unit.getUnit();
        UnitTypeData usType = unitTypeData.get(us.getType());
        boolean melee = usType.getWeapons().stream().allMatch(weapon -> weapon.getRange() < 0.2);
        boolean isBuilding = enemyType.getFoodRequired().isEmpty();
        double attack = enemyType.getWeapons().stream().mapToDouble(Weapon::getDamage).max().orElse(0d);
        double percentHealth = enemy.getHealth().orElse(1f) / enemy.getHealthMax().orElse(1f);
        double remainingHealth = enemy.getHealth().orElse(1f);
        double armor = enemyType.getArmor().orElse(0f);

        //double distance = melee ? agent.query().pathingDistance(us.getPosition().toPoint2d(), enemy.getPosition().toPoint2d()) : us.getPosition().toPoint2d().distance(enemy.getPosition().toPoint2d());
        double distance = us.getPosition().toPoint2d().distance(enemy.getPosition().toPoint2d());
        //double score = unitTypePreferences.contains(enemyType.getUnitType()) ? 0 : -1e50;
        double score = 0;

        score -= distance * 10;
        score += attack * 5;
        if (attack < 1)
        {
            score -= 10;
        }
        score -= armor * 3;
        score -= percentHealth;

        return score;
    }

}
