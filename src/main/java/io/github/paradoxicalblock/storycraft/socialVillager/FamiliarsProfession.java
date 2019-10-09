package io.github.paradoxicalblock.storycraft.socialVillager;

import java.util.Random;

// Handles anything related to the profession of the social villager
public class FamiliarsProfession {

    private static Random random = new Random();

    private String profession;

    public FamiliarsProfession() {
        setupProfession();
    }

    public FamiliarsProfession(String profession) {
        this.profession = profession;
    }

    private void setupProfession() {
        String[] professionList = {
                Professions.LUMBERJACK.name,
                Professions.FARMER.name,
                Professions.ARCHITECT.name,
                Professions.BLACKSMITH.name,
                Professions.ENCHANTER.name,
                Professions.DRUID.name,
                Professions.BUTCHER.name,
                Professions.LIBRARIAN.name,
                Professions.NOMAD.name,
                Professions.BAKER.name,
                Professions.PRIEST.name,
                Professions.MINER.name,
                Professions.GUARD.name,
                Professions.ARCHER.name
        };
        if (random.nextInt(60) == 0)
            this.profession = "Mayor";
        this.profession = professionList[random.nextInt(professionList.length)];
    }

    public String getProfession() {
        return profession;
    }

}