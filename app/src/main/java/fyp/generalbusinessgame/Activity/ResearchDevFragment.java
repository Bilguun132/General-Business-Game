package fyp.generalbusinessgame.Activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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

import fyp.generalbusinessgame.Models.CostListModel;
import fyp.generalbusinessgame.Models.GamePeriodModel;
import fyp.generalbusinessgame.Models.IncomeStatementModel;
import fyp.generalbusinessgame.R;
import fyp.generalbusinessgame.Service.DownloadCallback;
import fyp.generalbusinessgame.Service.HttpResult;
import fyp.generalbusinessgame.Service.HttpServiceFragment;


public class ResearchDevFragment extends Fragment implements DownloadCallback<HttpResult> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ResearchDevFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResearchDevFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResearchDevFragment newInstance(String param1, String param2) {
        ResearchDevFragment fragment = new ResearchDevFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public String domainName = "";
    public String subdomainName = "";

    public int userId;
    public int firmId;
    public int gameId;
    public int periodId;
    CostListModel costListModel;
    GamePeriodModel gamePeriodModel = new GamePeriodModel();
    IncomeStatementModel incomeStatementModel = new IncomeStatementModel();
    private int mDownloadTarget;
    private HttpServiceFragment mHttpFragment = null;
    private boolean mDownloading = false;
    private String investAmount;
    private View mainview;
    public ListFragment listFragment = new ListFragment();
    private  EditText investAmtEditText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mHttpFragment = HttpServiceFragment.getInstance(getActivity().getSupportFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mainview = inflater.inflate(R.layout.fragment_research_dev, container, false);

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

        getActivity().setTitle("R&D Summary. Period: " + gamePeriodModel.periodNumber);

        getIncomeStatement();


            investAmtEditText = (EditText) mainview.findViewById(R.id.rnd_invest_value);
            Button investButton = mainview.findViewById(R.id.rnd_invest_button);
            investButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    investButtonClicked(view);
                }
            });

        LinearLayout rnd_information_layout = mainview.findViewById(R.id.rnd_information_layout);
        LinearLayout rnd_decisions_layout = mainview.findViewById(R.id.rnd_decisions_layout);

//        switch (gamePeriodModel.status) {
//            case 0:
//                rnd_information_layout.setVisibility(View.VISIBLE);
//                rnd_decisions_layout.setVisibility(View.GONE);
//                break;
//            case 1:
//                break;
//            case 2:
//                rnd_information_layout.setVisibility(View.VISIBLE);
//                rnd_decisions_layout.setVisibility(View.GONE);
//                break;
//        }
        return mainview;
        }

    private void getIncomeStatement() {
// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        if (gamePeriodModel.endTime == null) periodId = gamePeriodModel.previousPeriodId;
        else periodId = gamePeriodModel.id;
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id) + firmId + "/incomeStatement/" + periodId;

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String test = response;
                        Gson gson = new GsonBuilder().create();
                        incomeStatementModel = gson.fromJson(response, IncomeStatementModel.class);
                        EditText rndCostEditText = (EditText) mainview.findViewById(R.id.rnd_cost);
                        rndCostEditText.setText(String.valueOf(incomeStatementModel.rndCost));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void investButtonClicked(final View view) {
        final EditText rndInvestment = (EditText) mainview.findViewById(R.id.rnd_invest_value);
        final String investValue = rndInvestment.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = domainName + subdomainName + getString(R.string.get_firm_info_by_firm_id)  + firmId + "/rndDecision";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Snackbar.make(view, "Investment Amount changed",
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
                params.put("annualExpenditure", investValue);
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


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void updateFromDownload(HttpResult result) {
        if (result == null) {
            Toast.makeText(getActivity().getApplicationContext(), "No internet", Toast.LENGTH_LONG).show();
        } else {
            if (result.exception != null) {
                Toast.makeText(getActivity().getApplicationContext(), result.exception.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                if (mDownloadTarget == 1) {
                    switch (result.responseCode) {
                        case 200:
                            Toast.makeText(getActivity().getApplicationContext(), "INVESTMENT MADE", Toast.LENGTH_LONG).show();
                            super.getActivity().onBackPressed();
                            break;
                        default:
                            Toast.makeText(getActivity().getApplicationContext(), result.responseBody, Toast.LENGTH_LONG).show();
                            break;
                    }

                }

            }
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;

        if (mHttpFragment != null) {
            mHttpFragment.cancelDownload();
        }
    }

    @Override
    public void finishInitializing() {

    }

    private void startDownload(int target) {

        if (!mDownloading && mHttpFragment != null) {
            // Execute the async download.
            mDownloadTarget = target;
            mHttpFragment.startDownload();
            mDownloading = true;
            super.getActivity().onBackPressed();
        }
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
