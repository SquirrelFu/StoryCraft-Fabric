package io.github.paradoxicalblock.storycraft.socialVillager;

import io.github.paradoxicalblock.storycraft.entity.FamiliarsEntity;

import java.util.Random;

//Handles anything related to the appearance of the social villager
public class FamiliarsAspects {

    private static Random random = new Random();

    private String hairColor;
    private String eyeColor;
    private String skinColor;
    private String sexuality;
    private int hairStyle = 0;

    private FamiliarsEntity familiarsEntity;

    public FamiliarsAspects(FamiliarsEntity familiarsEntity) {
        this.familiarsEntity = familiarsEntity;
        setupHair();
        setupSkin();
        setupEyes();
        setupOrientation();
    }

    public FamiliarsAspects(String hairColor, String eyeColor, String skinColor, String sexuality, int hairStyle) {
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
        this.skinColor = skinColor;
        this.sexuality = sexuality;
        this.hairStyle = hairStyle;
    }

    private void setupHair() {
        String[] maleHairList = {"Red", "Brown", "Black", "Blonde"};
        String[] femaleHairStyle = {"Red", "Brown", "Black", "Blonde", "Pink", "Ginger"};
        int[] styleList = {1, 2, 3, 4};
        this.hairStyle = styleList[random.nextInt(styleList.length)];
        if (familiarsEntity.get(FamiliarsEntity.genderUnified).equals("Female")) {
            this.hairColor = femaleHairStyle[random.nextInt(femaleHairStyle.length)];
        } else {
            this.hairColor = maleHairList[random.nextInt(maleHairList.length)];
        }
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
                if (familiarsEntity.get(FamiliarsEntity.genderUnified).equals("Female")) {
                    this.sexuality = "Lesbian";
                } else {
                    this.sexuality = "Gay";
                }
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