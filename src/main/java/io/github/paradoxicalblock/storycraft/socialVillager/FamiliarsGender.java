package io.github.paradoxicalblock.storycraft.socialVillager;

import java.util.Random;

//Handles anything related to the gender of the social villager
public class FamiliarsGender {

    private static Random random = new Random();

    private String gender;

    public FamiliarsGender() {
        setupGender();
    }

    public FamiliarsGender(String gender) {
        this.gender = gender;
    }

    private void setupGender() {
        String[] genderList = {"Male", "Female"};
        this.gender = genderList[random.nextInt(genderList.length)];
    }

    public String getGender() {
        return gender;
    }

}