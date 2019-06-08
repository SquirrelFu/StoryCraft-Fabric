package io.github.paradoxicalblock.storycraft.socialVillager;

import java.util.Random;

//Handles anything related to the appearance of the social villager
public class VillagerAspects {

    private static Random random = new Random();

    private String hairColor;
    private String eyeColor;
    private String skinColor;
    private String sexuality;
    private int hairStyle = 0;

    public VillagerAspects() {
        setupHair();
        setupSkin();
        setupEyes();
        setupOrientation();
    }

    private void setupHair() {
        String[] hairList = {"Red", "Brown", "Black", "Blonde"};
        int[] styleList = {1, 2, 3, 4};
        this.hairStyle = styleList[random.nextInt(styleList.length)];
        this.hairColor = hairList[random.nextInt(hairList.length)];
    }

    private void setupEyes() {
        String[] eyeList = {"Black", "Blue", "Brown", "Green", "Lime", "Pink", "Yellow"};
        this.eyeColor = eyeList[random.nextInt(eyeList.length)];
    }

    private void setupSkin() {
        String[] skinList = {"Light", "Medium", "Dark"};
        this.skinColor = skinList[random.nextInt(skinList.length)];
    }

    private void setupOrientation() {
        int orientationInt = random.nextInt(10);
        if (orientationInt == 9) {
            boolean orientationBool = random.nextBoolean();
            if (orientationBool) {
                this.sexuality = "Bisexual";
            } else {
                this.sexuality = "Gay";
            }
        } else {
            this.sexuality = "Straight";
        }
    }

    public String getHairColor() {
        return hairColor;
    }

    public String getEyeColor() {
        return eyeColor;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public int getHairStyle() {
        return hairStyle;
    }

    public String getSexuality() {
        return sexuality;
    }

}