package fyp.generalbusinessgame.Service;

import android.support.v4.util.Pair;

import java.util.ArrayList;


/**
 * Created by Arcstone on 30-Jan-17.
 */

public class HttpRequestModel {
    public ArrayList<Pair<String, String>> headerFields = new ArrayList<Pair<String, String>>();
    public ArrayList<Pair<String, String>> bodyFields = new ArrayList<Pair<String, String>>();
    public ArrayList<Pair<String, String>> urlParameterFields = new ArrayList<Pair<String, String>>();
    public String urlPath = "";
    public String method = "";
}