package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GameTypeInformationModel extends BaseModel implements Serializable  {
    public int id;
    public String gameTypeName;
    public String gameTypeDescription;
    public String gameTypeImageString;
}
