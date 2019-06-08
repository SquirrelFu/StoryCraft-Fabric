package io.github.paradoxicalblock.storycraft.socialVillager;

import java.util.Random;

// Handles anything related to the profession of the social villager
public class VillagerProfession {

    private static Random random = new Random();

    private String profession;

    public VillagerProfession() {
        setupProfession();
    }

    private void setupProfession() {
        String[] professionList = {"Lumberjack", "Farmer", "Architect", "Blacksmith", "Enchanter", "Druid", "Butcher",
                "Librarian", "Nomad", "Baker", "Priest", "Miner", "Guard"};
        if(random.nextInt(100) == 0)
            this.profession = "Mayor";
        this.profession = professionList[random.nextInt(professionList.length)];
    }

    public String getProfession() {
        return profession;
    }

}