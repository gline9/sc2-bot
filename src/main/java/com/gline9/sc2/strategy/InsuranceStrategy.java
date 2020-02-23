package com.gline9.sc2.strategy;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.gline9.sc2.command.BuildStructureCommand;
import com.gline9.sc2.command.Command;
import com.gline9.sc2.command.HideInCornerCommand;
import com.gline9.sc2.units.CommandCenter;
import com.gline9.sc2.units.SCV;
import com.gline9.sc2.units.UnitPool;

import java.util.Iterator;

public class InsuranceStrategy
{
    private final S2Agent agent;
    private final InBasePlacementStrategy buildingPlacementStrategy;
    private final UnitPool unitPool;
    private int ccCount = 1;
    private boolean buildingCC = false;

    public InsuranceStrategy(S2Agent agent, InBasePlacementStrategy buildingPlacementStrategy, UnitPool unitPool)
    {
        this.agent = agent;
        this.buildingPlacementStrategy = buildingPlacementStrategy;
        this.unitPool = unitPool;
    }

    public boolean tick()
    {
        int minerals = agent.observation().getMinerals();

        if (minerals < 400)
        {
            return false;
        }

        int ccs = unitPool.getUnitsOfType(CommandCenter.class).size();
        if (ccs > ccCount)
        {
            ccCount = ccs;
            buildingCC = false;
        }

        if (ccs > 1 || buildingCC)
        {
            return false;
        }

        Iterator<? extends SCV> scv = unitPool.getUnitsOfType(SCV.class).iterator();

        SCV next = null;
        while (scv.hasNext())
        {
            next = scv.next();

            if (!next.isBuildingStructure())
            {
                break;
            }
        }

        if (null == next)
        {
            return false;
        }

        final SCV found = next;
        Command<? super SCV> previousCommand = found.getCommand();

        BuildStructureCommand buildCC = new BuildStructureCommand(agent, buildingPlacementStrategy, Abilities.BUILD_COMMAND_CENTER, null);
        buildCC.subscribeToCompletion(() -> {
            found.setCommand(previousCommand);
        });

        found.setCommand(buildCC);
        buildingCC = true;

        unitPool.subscribeToUnitCreation(CommandCenter.class, this::verifyInsurance);

        return true;
    }

    private void verifyInsurance(CommandCenter cc)
    {
        cc.setCommand(new HideInCornerCommand(agent));
    }
}
