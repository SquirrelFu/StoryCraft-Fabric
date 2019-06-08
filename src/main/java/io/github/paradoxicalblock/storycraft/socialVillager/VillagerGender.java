package io.github.paradoxicalblock.storycraft.socialVillager;

import java.util.Random;

//Handles anything related to the gender of the social villager
public class VillagerGender {

    private static Random random = new Random();

    private String gender;

    public VillagerGender() {
        setupGender();
    }

    private void setupGender() {
        String[] genderList = {"Male", "Female"};
        this.gender = genderList[random.nextInt(genderList.length)];
    }

    public String getGender() {
        return gender;
    }

}