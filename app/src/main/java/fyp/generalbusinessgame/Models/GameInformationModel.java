package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GameInformationModel extends BaseModel implements Serializable  {
    public int id;
    public String gameName;
    public String gameType;
    public String gameDescription;
    public String gameRecommendedPlayers;
    public String gameImageString;
    public String drawableUrl;
    public int joinedFirmId;
}
