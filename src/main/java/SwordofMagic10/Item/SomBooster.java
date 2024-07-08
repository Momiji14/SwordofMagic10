package SwordofMagic10.Item;

public class SomBooster {


    public enum Type {
        ClassExp("クラス経験値"),
        Gathering("ギャザリング"),
        ;

        private final String display;
        Type(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }
}
