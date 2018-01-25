package fyp.generalbusinessgame.Models;

import java.io.Serializable;

/**
 * Created by Bilguun on 31/10/17.
 */

public class GameBalanceSheetModel extends BaseModel implements Serializable  {
    public int id;
    public String totalAsset;
    public String totalLiability ;
    public String totalEquity ;
}
