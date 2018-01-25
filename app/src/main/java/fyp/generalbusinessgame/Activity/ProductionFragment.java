package fyp.generalbusinessgame.Activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

import fyp.generalbusinessgame.Activity.dummy.DummyContent;
import fyp.generalbusinessgame.Models.BusinessAspect;
import fyp.generalbusinessgame.Models.CostListModel;
import fyp.generalbusinessgame.Models.GameFirmInfoModel;
import fyp.generalbusinessgame.Models.GamePeriodModel;
import fyp.generalbusinessgame.Models.IncomeStatementModel;
import fyp.generalbusinessgame.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public GamePeriodModel gamePeriodModel;

    public ProductionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductionFragment newInstance(String param1, String param2) {
        ProductionFragment fragment = new ProductionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public int firmId;
    public int gameId;
    public int userId;
    CostListModel costListModel;
    GameFirmInfoModel gameFirmInfoModel;
    public ListFragment listFragment = new ListFragment();
    public String domainName = "";
    public String subdomainName = "";
    public View mainview;
    public IncomeStatementModel incomeStatementModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview = inflater.inflate(R.layout.fragment_production, container, false);

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        if (sharedPref != null) {
            userId = sharedPref.getInt(getString(R.string.user_id), 0);
            domainName = sharedPref.getString(getString(R.string.domain_name_key), null);
            if (domainName == null || domainName.isEmpty()) {
                domainName = getString(R.string.domain_name);
            }
            subdomainName = sharedPref.getString(getString(R.string.subdomain_name_key), null);
            if (subdomainName == null || subdomainName.isEmpty()) {
                subdomainName = getString(R.string.subdomain_name);
            }
        }

        getActivity().setTitle("Production Summary. Period: " + gamePeriodModel.periodNumber);

      //  getIncomeStatement();

        double productionCost = 0;
        for (int i = 0; i < costListModel.costingList.size(); i++) {
            if (costListModel.costingList.get(i).businessAspect == BusinessAspect.PRODUCTION.getValue()) {
                productionCost += costListModel.costingList.get(i).paymentAmount;
            }
        }

        Button changeProductionQuantity = mainview.findViewById(R.id.production_change_production_quantity_button);
        changeProductionQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePriceClicked(view);
            }
        });

        return mainview;
    }

    private void getIncomeStatement() {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        int periodId;
        if (gamePeriodModel.endTime == null) periodId = gamePeriodModel.previousPeriodId;
        else periodId = gamePeriodModel.id;
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id) + firmId + "/incomeStatement/" + periodId;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Gson gson = new GsonBuilder().create();
                        incomeStatementModel = gson.fromJson(response, IncomeStatementModel.class);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        ListFragment newList = new ListFragment();
                        DummyContent newDummyContent = new DummyContent();
                        newDummyContent.ITEMS.clear();
                        for (int i = 0; i < incomeStatementModel.productionDecisions.size(); i++) {
                            newDummyContent.ITEMS.add(new DummyContent.DummyItem("Production Change", "Changed", "Market Change"));
                        }
                        newList.dummyContent = newDummyContent;
                        transaction.replace(R.id.marketing_history_layout, newList);
                        transaction.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void changePriceClicked(final View view) {

        final EditText productionQuantity = (EditText) mainview.findViewById(R.id.production_quantity);
        final String qty = productionQuantity.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id)  + firmId + "/productionDecision";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Snackbar.make(view, "Production Qty changed",
                                Snackbar.LENGTH_LONG).show();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String asd = error.toString();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", Integer.toString(userId));
                params.put("productionQuantity", qty);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
