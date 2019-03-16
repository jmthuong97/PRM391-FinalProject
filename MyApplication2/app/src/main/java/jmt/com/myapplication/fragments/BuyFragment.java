package jmt.com.myapplication.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import jmt.com.myapplication.BuildConfig;
import jmt.com.myapplication.R;
import jmt.com.myapplication.activity.LoginActivity;
import jmt.com.myapplication.helpers.Helper;
import jmt.com.myapplication.helpers.IVolleyCallback;
import jmt.com.myapplication.helpers.VolleyRequest;

import static android.app.Activity.RESULT_OK;

public class BuyFragment extends Fragment {
    public View rootView;
    private static final int REQUEST_CODE = 672;
    BraintreeFragment mBraintreeFragment;

    private String clientToken, nonce;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buy, container, false);

        Button btnPayment = rootView.findViewById(R.id.btn_payment);
        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropInRequest dropInRequest = new DropInRequest()
                        .clientToken(clientToken);
                startActivityForResult(dropInRequest.getIntent(getContext()), REQUEST_CODE);
            }
        });

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(getActivity(), BuildConfig.PaypalClientId);
            mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                    String nonce = paymentMethodNonce.getNonce();
                    JSONObject paramsURL = new JSONObject();
                    try {
                        paramsURL.put("amount", 10);
                        paramsURL.put("nonce", nonce);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendPayment(paramsURL);
                }
            });

            // mBraintreeFragment is ready to use!
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
        }

        Map<String, String> paramsURL = new HashMap<>();
        new VolleyRequest(getContext()).GET("/paypal/client-token/", paramsURL, new IVolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                try {
                    JSONObject data = result.getJSONObject("data");
                    clientToken = data.getString("clientToken");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                PaymentMethodNonce nonce = result.getPaymentMethodNonce();
                if (nonce != null) {
                    String strNonce = nonce.getNonce();
                    JSONObject paramsURL = new JSONObject();
                    try {
                        paramsURL.put("amount", 10);
                        paramsURL.put("nonce", strNonce);
                        sendPayment(paramsURL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void sendPayment(JSONObject paramsURL) {
        new VolleyRequest(getContext()).POST("/paypal/execute-payment", paramsURL, new IVolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                // create a dialog alert
                new AlertDialog.Builder(getContext())
                        .setTitle("Purchase Premium Account successfully !")
                        .setMessage("You must logout to activate your Premium Account")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AuthUI.getInstance()
                                        .signOut(getContext())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Helper.makeToastMessage("User Signed Out", getContext());
                                                Intent intent = new Intent(getContext(), LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                            }
                        })
                        .show();
            }
        });
    }
}
