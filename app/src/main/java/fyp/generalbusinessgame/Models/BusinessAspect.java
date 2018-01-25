package fyp.generalbusinessgame.Models;

/**
 * Created by pc on 21/12/2017.
 */

public enum  BusinessAspect {
    MARKETING(0),
    RND(1),
    PRODUCTION(2);

    private final int value;
    private BusinessAspect(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
