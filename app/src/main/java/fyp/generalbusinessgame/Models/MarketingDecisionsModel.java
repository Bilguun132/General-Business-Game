package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by pc on 21/12/2017.
 */

public class MarketingDecisionsModel extends BaseModel implements Serializable {
    public int id;
    public String price;
    public String setDateInGame;
    public int firmLinkId;
    public int userLinkId;
    public int periodLinkId;
    public Float marketShareQtyChange;
}
