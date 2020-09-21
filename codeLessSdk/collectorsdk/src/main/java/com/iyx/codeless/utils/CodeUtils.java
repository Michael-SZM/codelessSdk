package com.iyx.codeless.utils;

import android.app.AlertDialog;

import com.iyx.codeless.CodeLessFacade;

public class CodeUtils {

    public static AlertDialog wrapTest(AlertDialog.Builder builder){
        AlertDialog dialog = builder.create();
        CodeLessFacade.handleDialog(dialog.getWindow());
        return dialog;
    }

}
