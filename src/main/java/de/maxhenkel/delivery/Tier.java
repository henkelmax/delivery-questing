package de.maxhenkel.delivery;

public enum Tier {
    TIER_1(1), TIER_2(2), TIER_3(3), TIER_4(4), TIER_5(5), TIER_6(6);
    private int tier;

    Tier(int tier) {
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }
}
