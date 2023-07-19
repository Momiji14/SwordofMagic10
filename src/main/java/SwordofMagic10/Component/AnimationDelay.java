package SwordofMagic10.Component;

public record AnimationDelay(int time) implements AnimationFlame {

    @Override
    public AnimationFlame clone() {
        return new AnimationDelay(time);
    }
}
