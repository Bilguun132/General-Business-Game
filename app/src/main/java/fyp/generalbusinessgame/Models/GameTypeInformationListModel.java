package fyp.generalbusinessgame.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GameTypeInformationListModel extends BaseModel implements Serializable  {
    public ArrayList<GameTypeInformationModel> gameTypeList = new ArrayList<GameTypeInformationModel>();
}
