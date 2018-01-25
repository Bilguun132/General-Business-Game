package fyp.generalbusinessgame.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pc on 21/12/2017.
 */

public class CostListModel extends BaseModel implements Serializable {
    public ArrayList<CostModel> costingList = new ArrayList<CostModel>();
}
