package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Units;
import com.gline9.sc2.command.AttackCommand;
import com.gline9.sc2.command.CreateWorkerCommand;
import com.gline9.sc2.units.*;

public class WorkerRushStrategy implements Strategy
{
    public WorkerRushStrategy(UnitPool unitPool, S2Agent agent)
    {
        unitPool.subscribeToUnitCreation(Drone.class, scv -> {
            AttackCommand attackCommand = new AttackCommand(agent);
            attackCommand.addPreferredType(Units.TERRAN_SCV);
            attackCommand.addPreferredType(Units.PROTOSS_PROBE);
            attackCommand.addPreferredType(Units.ZERG_DRONE);
            scv.setCommand(attackCommand);
        });

        unitPool.subscribeToUnitCreation(Hatchery.class, cc -> {
            cc.setCommand(new CreateWorkerCommand(agent, unitPool));
        });
    }

    @Override
    public void tick() {

    }
}
