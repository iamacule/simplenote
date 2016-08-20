package vn.mran.simplenote.view.toast;

import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import vn.mran.simplenote.R;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class CustomEditText {
    public LinearLayout lnMain;
    public TextInputLayout txtMain;
    public EditText editText;

    public CustomEditText(View view, int id) {
        lnMain = (LinearLayout) view.findViewById(id);
        txtMain = (TextInputLayout) lnMain.findViewById(R.id.hint);
        editText = (EditText) lnMain.findViewById(R.id.et);
    }

    public void clearText(){
        editText.setText("");
    }

    public boolean isEmpty(){
        if(editText.getText().toString().equals(""))
            return true;
        return false;
    }
}
