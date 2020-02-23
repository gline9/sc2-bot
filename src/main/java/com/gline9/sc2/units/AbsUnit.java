package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.gline9.sc2.command.Command;

public abstract class AbsUnit<U extends AbsUnit<U>> implements ControllableUnit
{
    private Unit unit;
    private Command<? super U> command;

    public AbsUnit(Unit unit)
    {
        this.unit = unit;
    }

    @Override
    public Unit getUnit()
    {
        return unit;
    }

    public void setCommand(Command<? super U> value)
    {
        this.command = value;
    }

    public void executeAbility(S2Agent agent, Ability ability, boolean queued)
    {
        agent.actions().unitCommand(unit, ability, queued);
    }

    public void executeAbilityOnUnit(S2Agent agent, Ability ability, Unit unit, boolean queued)
    {
        agent.actions().unitCommand(this.unit, ability, unit, queued);
    }

    public void executeAbilityAtLocation(S2Agent agent, Ability ability, Point2d location, boolean queued)
    {
        agent.actions().unitCommand(unit, ability, location, queued);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void tick(UnitInPool unitInPool)
    {
        if (null == command)
        {
            return;
        }

        command.handle((U)this);
    }

    @Override
    public void onIdle()
    {
        if (null == command)
        {
            return;
        }

        command.onIdle((U)this);
    }

    public Command<? super U> getCommand()
    {
        return command;
    }

    @Override
    public void setUnit(Unit unit)
    {
        this.unit = unit;
    }
}
