package com.blocklynukkit.loader.other.AddonsAPI;

public final class CustomItemInfo {
    private int id;
    private int type;
    private boolean isDisplayAsTool;
    private boolean canOnOffhand;
    private boolean isTool;
    private int toolType;
    private int toolTier;
    private int durability;
    private int attackDamage;
    private boolean isFood;
    private boolean isDrink;
    private int eatTick;
    private int nutrition;
    private boolean isArmor;
    private boolean isHelmet;
    private boolean isChest;
    private boolean isLeggings;
    private boolean isBoots;
    private boolean forceScale;
    private float zoom;

    public CustomItemInfo(int id, int type, boolean isDisplayAsTool, boolean canOnOffhand) {
        this.id = id;
        this.type = type;
        this.isDisplayAsTool = isDisplayAsTool;
        this.canOnOffhand = canOnOffhand;
        this.isTool = false;
    }

    public CustomItemInfo(int id, boolean canOnOffhand, int toolType, int toolTier, int durability, int attackDamage) {
        this.id = id;
        this.canOnOffhand = canOnOffhand;
        this.toolType = toolType;
        this.toolTier = toolTier;
        this.durability = durability;
        this.attackDamage = attackDamage;
        this.isTool = true;
        this.type = 3;
        this.isDisplayAsTool = true;
    }

    public CustomItemInfo(int id, int armorType, boolean canOnOffhand, int durability){
        this.id = id;
        switch (armorType){
            case 0: this.isHelmet = true;break;
            case 1: this.isChest = true;break;
            case 2: this.isLeggings = true;break;
            case 3: this.isBoots = true;break;
        }
        this.canOnOffhand = canOnOffhand;
        this.durability = durability;
        this.type = 3;
        this.isArmor = true;
    }

    public CustomItemInfo(int id, boolean foodOrDrink, boolean canOnOffhand, int eatTick, int nutrition){
        this.id = id;
        if(foodOrDrink){
            this.isFood = true;
        }else {
            this.isDrink = true;
        }
        this.type = 4;
        this.eatTick = eatTick;
        this.canOnOffhand = canOnOffhand;
        this.nutrition = nutrition;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDisplayAsTool() {
        return isDisplayAsTool;
    }

    public void setDisplayAsTool(boolean displayAsTool) {
        isDisplayAsTool = displayAsTool;
    }

    public boolean isCanOnOffhand() {
        return canOnOffhand;
    }

    public void setCanOnOffhand(boolean canOnOffhand) {
        this.canOnOffhand = canOnOffhand;
    }

    public boolean isTool() {
        return isTool;
    }

    public void setTool(boolean tool) {
        isTool = tool;
    }

    public int getToolType() {
        return toolType;
    }

    public void setToolType(int toolType) {
        this.toolType = toolType;
    }

    public int getToolTier() {
        return toolTier;
    }

    public void setToolTier(int toolTier) {
        this.toolTier = toolTier;
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public boolean isFood() {
        return isFood;
    }

    public void setFood(boolean food) {
        isFood = food;
    }

    public boolean isDrink() {
        return isDrink;
    }

    public void setDrink(boolean drink) {
        isDrink = drink;
    }

    public int getEatTick() {
        return eatTick;
    }

    public void setEatTick(int eatTick) {
        this.eatTick = eatTick;
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    public boolean isHelmet() {
        return isHelmet;
    }

    public void setHelmet(boolean helmet) {
        isHelmet = helmet;
    }

    public boolean isChest() {
        return isChest;
    }

    public void setChest(boolean chest) {
        isChest = chest;
    }

    public boolean isLeggings() {
        return isLeggings;
    }

    public void setLeggings(boolean leggings) {
        isLeggings = leggings;
    }

    public boolean isBoots() {
        return isBoots;
    }

    public void setBoots(boolean boots) {
        isBoots = boots;
    }

    public boolean isArmor() {
        return isArmor;
    }

    public void setArmor(boolean armor) {
        isArmor = armor;
    }

    public boolean isForceScale() {
        return forceScale;
    }

    public void setForceScale(boolean forceScale) {
        this.forceScale = forceScale;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
}
