package com.gline9.sc2;

import com.github.ocraft.s2client.bot.S2Coordinator;
import com.github.ocraft.s2client.protocol.game.BattlenetMap;
import com.github.ocraft.s2client.protocol.game.Difficulty;
import com.github.ocraft.s2client.protocol.game.Race;

public class Main
{
    public static void main(String[] args)
    {
        Agent agent = new Agent();
        S2Coordinator coordinator = S2Coordinator.setup()
                .loadSettings(args)
                .setParticipants(
                        S2Coordinator.createParticipant(Race.TERRAN, agent),
                        S2Coordinator.createComputer(Race.ZERG, Difficulty.MEDIUM)
                ).launchStarcraft()
                .startGame(BattlenetMap.of("Lava Flow"));

        while (coordinator.update()) {}

        coordinator.quit();
    }

}
