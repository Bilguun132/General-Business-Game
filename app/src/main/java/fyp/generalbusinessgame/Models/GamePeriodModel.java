package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GamePeriodModel extends BaseModel implements Serializable  {
    public int id = 0;
    public int previousPeriodId = 0;
    public String periodName;
    public String periodNumber ;
    public int status = 0;
    public String startTime ;
    public String endTime ;
    public String expectedStart ;
    public String expectedEnd ;
    public int gameLinkId = 0;
}
