package sb.firefds.pie.firefdskit.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import androidx.appcompat.app.AlertDialog;
import sb.firefds.pie.firefdskit.MainApplication;
import sb.firefds.pie.firefdskit.R;
import sb.firefds.pie.firefdskit.utils.Constants;
import sb.firefds.pie.firefdskit.utils.Utils;

public class SaveDialog {

    private AlertDialog dialog;

    public SaveDialog() {
    }

    public void showDialog(Context context, View contentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText editText = new EditText(context);
        editText.setHint(R.string.backup_name);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialog = builder.setCancelable(true).setTitle(R.string.save).setView(editText)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    if (savePreferencesToSdCard(editText.getText().toString())) {
                        Utils.createSnackbar(contentView,
                                R.string.save_successful,
                                context).show();
                    } else {
                        Utils.createSnackbar(contentView,
                                R.string.save_unsuccessful,
                                context).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();
        dialog.show();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private boolean savePreferencesToSdCard(String string) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + Constants.BACKUP_DIR;
        File dir = new File(path);
        dir.mkdirs();

        File file = new File(dir, string + ".xt");

        boolean res = false;
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(MainApplication.getSharedPreferences().getAll());

            res = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return res;
    }
}
