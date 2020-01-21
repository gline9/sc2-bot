package com.gline9.sc2.units;

import com.github.ocraft.s2client.protocol.unit.Unit;

public class AbsDumbUnit implements IUnit
{
    private Unit unit;

    public AbsDumbUnit(Unit unit)
    {
        this.unit = unit;
    }

    @Override
    public Unit getUnit()
    {
        return unit;
    }

    @Override
    public void setUnit(Unit unit)
    {
        this.unit = unit;
    }
}
