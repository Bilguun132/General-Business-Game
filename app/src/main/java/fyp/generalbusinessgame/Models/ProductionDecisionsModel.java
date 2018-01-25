package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by pc on 21/12/2017.
 */

public class ProductionDecisionsModel extends BaseModel implements Serializable {
    public int id;
    public String  productionQuantity;
    public String setDateInGame;
    public int firmLinkId = 0;
    public int userLinkId = 0;
    public int periodLinkId = 0;
    public int revenueAndCostLinkId = 0;
}
