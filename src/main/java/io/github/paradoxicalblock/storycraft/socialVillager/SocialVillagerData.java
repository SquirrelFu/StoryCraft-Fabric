package io.github.paradoxicalblock.storycraft.socialVillager;

public class SocialVillagerData {

    private final VillagerAspects villagerAspects;
    private final VillagerGender villagerGender;
    private final VillagerProfession villagerProfession;

    public SocialVillagerData(VillagerAspects villagerAspects, VillagerGender villagerGender, VillagerProfession villagerProfession) {
        this.villagerAspects = villagerAspects;
        this.villagerGender = villagerGender;
        this.villagerProfession = villagerProfession;
    }

    public VillagerAspects getVillagerAspects() {
        return villagerAspects;
    }

    public VillagerGender getVillagerGender() {
        return villagerGender;
    }

    public VillagerProfession getVillagerProfession() {
        return villagerProfession;
    }

    public SocialVillagerData withAspects(VillagerAspects villagerAspects) {
        return new SocialVillagerData(villagerAspects, this.villagerGender, this.villagerProfession);
    }

    public SocialVillagerData withGender(VillagerGender villagerGender) {
        return new SocialVillagerData(this.villagerAspects, villagerGender, this.villagerProfession);
    }

    public SocialVillagerData withProfession(VillagerProfession villagerProfession) {
        return new SocialVillagerData(this.villagerAspects, this.villagerGender, villagerProfession);
    }

}