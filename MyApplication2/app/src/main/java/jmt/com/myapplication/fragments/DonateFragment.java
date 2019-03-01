package jmt.com.myapplication.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import jmt.com.myapplication.BuildConfig;
import jmt.com.myapplication.R;
import jmt.com.myapplication.helpers.Helper;

import static android.app.Activity.RESULT_OK;

public class DonateFragment extends Fragment {
    public View rootView;
    public static final int PAYPAL_REQUEST_CODE = 35;
    EditText edtMount;
    Button btnPayNow;

    private PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(BuildConfig.PaypalClientId);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_donate, container, false);
        setupPaypal();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        showResultDonate(paymentDetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                Helper.makeToastMessage("Cancel", getContext());
            }
        } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Helper.makeToastMessage("Invalid", getContext());
        }
    }

    private void setupPaypal() {
        // Start  Paypal service
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        edtMount = rootView.findViewById(R.id.edtMount);

        btnPayNow = rootView.findViewById(R.id.btnPayNow);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = edtMount.getText().toString().trim();
                if (!amountStr.equals("") && Integer.parseInt(amountStr) > 0)
                    processPayment(amountStr);
            }
        });
    }

    private void processPayment(String amount) {
        PayPalPayment payPalPayment = new PayPalPayment(
                new BigDecimal(String.valueOf(amount)),
                "USD",
                "Donate for app ",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    private void showResultDonate(String paymentDetails) {
        new AlertDialog.Builder(getContext())
                .setTitle("Donate success")
                .setMessage("Thank you for your donate <3\n " + paymentDetails)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edtMount.setText("");
                    }
                })
                .show();
    }
}
