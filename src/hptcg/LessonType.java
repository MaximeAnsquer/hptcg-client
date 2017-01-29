package hptcg;

public enum LessonType {
  CARE_OF_MAGICAL_CREATURES,
  CHARMS,
  POTIONS,
  QUIDDITCH,
  TRANSFIGURATION;

  public String toString() {
    switch (this) {
      case CARE_OF_MAGICAL_CREATURES:
        return "CareOfMagicalCreatures";
      case CHARMS:
        return "Charms";
      case POTIONS:
        return "Potions";
      case QUIDDITCH:
        return "Quidditch";
      case TRANSFIGURATION:
        return "Transfiguration";
      default:
        return "";
    }
  }
}
