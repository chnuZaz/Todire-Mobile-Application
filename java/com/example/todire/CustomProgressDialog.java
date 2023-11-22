package com.example.todire;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CustomProgressDialog {
    private AlertDialog dialog;
    private Context context;

    public CustomProgressDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String massage, boolean cancellable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.custom_progress_dialog, null);
        builder.setView(dialogView);
        builder.setCancelable(cancellable); // Prevent dialog from being canceled
        TextView messageTextView = dialogView.findViewById(R.id.message);
        messageTextView.setText(massage);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        dialog = builder.create();
        dialog.show();
    }

    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
