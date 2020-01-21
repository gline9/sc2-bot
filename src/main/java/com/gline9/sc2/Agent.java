package com.gline9.sc2;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.S2Coordinator;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.game.Race;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.gline9.sc2.strategy.UnitStrategist;
import com.gline9.sc2.units.ControllableUnit;
import com.gline9.sc2.units.IUnit;
import com.gline9.sc2.units.UnitFactory;
import com.gline9.sc2.units.UnitPool;

public class Agent extends S2Agent
{
    private final UnitFactory unitFactory = new UnitFactory();
    private final UnitPool unitPool = new UnitPool(unitFactory);
    private final UnitStrategist unitStrategist = new UnitStrategist(unitPool, this);

    @Override
    public void onGameStart()
    {
        System.out.println("Hello world of Starcraft II bots!");
    }

    @Override
    public void onStep()
    {
        long gameLoop = observation().getGameLoop();

        if (gameLoop % 100 == 0)
        {
            observation().getUnits(Alliance.NEUTRAL).forEach(unitPool::ensureUnit);
            unitStrategist.tick();
            observation().getUnits(Alliance.SELF).forEach(this::handleUnit);
        }
    }

    @Override
    public void onUnitIdle(UnitInPool unitInPool)
    {
        unitPool.onUnitIdle(unitInPool);
    }

    @Override
    public void onUnitDestroyed(UnitInPool unitInPool)
    {
        unitPool.onUnitDestroyed(unitInPool);
    }

    private void handleUnit(UnitInPool unitInPool)
    {
        IUnit unit = unitPool.ensureUnit(unitInPool);

        if (!(unit instanceof ControllableUnit))
        {
            return;
        }

        ControllableUnit controllableUnit = (ControllableUnit) unit;

        controllableUnit.tick(unitInPool);
    }

    public static void main(String[] args) {
        Agent agent = new Agent();

        S2Coordinator s2Coordinator = S2Coordinator.setup()
                .loadLadderSettings(args)
                .setParticipants(S2Coordinator.createParticipant(Race.TERRAN, agent))
                .connectToLadder()
                .joinGame();

        while (s2Coordinator.update())
        {
        }

        s2Coordinator.quit();
    }
}
