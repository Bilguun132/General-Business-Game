package fyp.generalbusinessgame.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pc on 21/12/2017.
 */

public class MarketingDecisionsListModel extends BaseModel implements Serializable {
    public ArrayList<MarketingDecisionsModel> costingList = new ArrayList<MarketingDecisionsModel>();
}
