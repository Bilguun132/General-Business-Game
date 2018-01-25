package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GameFirmInfoModel extends BaseModel implements Serializable  {
    public int id = 0;
    public String firmName;
    public String firmDescription;
    public String maxNumberOfPlayers;
    public String productionPrice = "0";
    public String productionQuality = "0";
    public Double marketShare = 0.00;
    public Double marketSharePercentage = 0.00;
    public int gameLinkId = 0;
}
