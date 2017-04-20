package me.piebridge.brevent.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import me.piebridge.brevent.BuildConfig;
import me.piebridge.brevent.R;

/**
 * Created by thom on 2017/4/7.
 */
public class UnsupportedFragment extends DialogFragment implements DialogInterface.OnKeyListener {

    private int message = R.string.unsupported_owner;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDialog();
    }

    private Dialog createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
        builder.setMessage(getString(message));
        builder.setOnKeyListener(this);
        return builder.create();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            getActivity().finish();
        }
        return false;
    }

    public DialogFragment setMessage(int message) {
        this.message = message;
        return this;
    }

}
