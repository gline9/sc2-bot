package com.gline9.sc2.units;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.unit.Unit;

public class UnitFactory
{

    private static final int SCV = 45;
    private static final int MINERAL_FIELD = 341;
    private static final int MINERAL_FIELD750 = 483;
    private static final int COMMAND_CENTER = 18;
    private static final int SUPPLY_DEPOT = 19;
    private static final int BARRACKS = 21;
    private static final int MARINE = 48;

    private static final int PROBE = 84;
    private static final int NEXUS = 59;

    private static final int DRONE = 104;
    private static final int HATCHERY = 86;
    private static final int LARVA = 151;

    public IUnit createUnit(UnitInPool unitInPool, UnitPool unitPool)
    {
        Unit unit = unitInPool.unit();
        switch(unitInPool.unit().getType().getUnitTypeId())
        {
            case SCV:
                return new SCV(unit);
            case MINERAL_FIELD:
            case MINERAL_FIELD750:
                return new Minerals(unit);
            case COMMAND_CENTER:
                return new CommandCenter(unit);
            case SUPPLY_DEPOT:
                return new SupplyDepot(unit);
            case BARRACKS:
                return new Barracks(unit);
            case MARINE:
                return new Marine(unit);
            case PROBE:
                return new Probe(unit);
            case NEXUS:
                return new Nexus(unit);
            case DRONE:
                return new Drone(unit);
            case HATCHERY:
                return new Hatchery(unit, unitPool);
            case LARVA:
                return new Larva(unit);
            default:
                return null;
        }
    }
}
