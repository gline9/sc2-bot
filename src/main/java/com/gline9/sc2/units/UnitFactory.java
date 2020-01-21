package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class UnitFactory
{

    public IUnit createUnit(UnitInPool unitInPool)
    {
        Unit unit = unitInPool.unit();
        switch((Units)Units.from(unitInPool.unit().getType().getUnitTypeId()))
        {
            case TERRAN_SCV:
                return new SCV(unit);
            case NEUTRAL_MINERAL_FIELD:
                return new Minerals(unit);
            case TERRAN_COMMAND_CENTER:
                return new CommandCenter(unit);
            case TERRAN_SUPPLY_DEPOT:
                return new SupplyDepot(unit);
            case TERRAN_BARRACKS:
                return new Barracks(unit);
            case TERRAN_MARINE:
                return new Marine(unit);
            default:
                return null;
        }
    }
}
