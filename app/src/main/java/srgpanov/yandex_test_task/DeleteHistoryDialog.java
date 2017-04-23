package srgpanov.yandex_test_task;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import srgpanov.yandex_test_task.Utils.ConstantManager;

/**
 * Created by Пан on 20.04.2017.
 */

public class DeleteHistoryDialog extends DialogFragment {

//диалог подтверждения удаления истории

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getString(R.string.delete_history_dialog_title);
        String message = getString(R.string.are_you_shure);
        String deleteButton = getString(R.string.delete);
        String cancelButton = getString(R.string.cancel);

        final AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(deleteButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //выбор передаём интентом в фрагмент истории
                Intent intent = new Intent();
                intent.putExtra(ConstantManager.TAG_DELETE_HISTORY,true);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,intent);

            }
        });
        builder.setNegativeButton(cancelButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setCancelable(true);


        return builder.create();
    }
}
