package com.gline9.sc2.command;

import com.gline9.sc2.units.ControllableUnit;

import java.util.List;

public class CompositeCommand implements Command
{
    private final List<Command> substrategies;

    private CompositeCommand(List<Command> strategies)
    {
        this.substrategies = strategies;
    }

    public static CompositeCommand forStrategies(List<Command> strategies)
    {
        return new CompositeCommand(strategies);
    }

    @Override
    public boolean handle(ControllableUnit unit)
    {
        for (Command command : substrategies)
        {
            if (command.handle(unit))
            {
                return true;
            }
        }

        return false;
    }
}
