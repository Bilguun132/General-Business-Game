package fyp.generalbusinessgame.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GameInformationListModel extends BaseModel implements Serializable  {
    public ArrayList<GameInformationModel> availableGameList = new ArrayList<GameInformationModel>();
    public ArrayList<GameInformationModel> joinedGameList = new ArrayList<GameInformationModel>();
}
