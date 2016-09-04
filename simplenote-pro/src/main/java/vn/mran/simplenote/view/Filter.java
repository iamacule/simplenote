package vn.mran.simplenote.view;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import vn.mran.simplenote.R;
import vn.mran.simplenote.util.EventUtil;
import vn.mran.simplenote.view.effect.TouchEffect;

/**
 * Created by MrAn on 18-Aug-16.
 */
public class Filter {
    public LinearLayout parent;
    public LinearLayout btnSort;
    public LinearLayout btnFolder;
    public TextView txtSortStatus;
    public TextView txtFolderName;
    public EditText etSearch;
    public ImageButton btnSearch;
    private boolean isShowSearch = false;
    private EventUtil.KeyBoard keyBoard;

    public Filter(View view) {
        keyBoard = new EventUtil.KeyBoard(view.getContext());
        parent = (LinearLayout) view.findViewById(R.id.parent);
        btnSort = (LinearLayout) view.findViewById(R.id.btnSort);
        btnFolder = (LinearLayout) view.findViewById(R.id.btnFolder);
        txtSortStatus = (TextView) view.findViewById(R.id.txtSortStatus);
        txtFolderName = (TextView) view.findViewById(R.id.txtFolderName);
        etSearch = (EditText) view.findViewById(R.id.etSearch);
        btnSearch = (ImageButton) view.findViewById(R.id.btnSearch);
        TouchEffect.addAlpha(btnSort);
        TouchEffect.addAlpha(btnFolder);
        TouchEffect.addAlpha(btnSearch);
    }

    public void showHideSearch() {
        if(!isShowSearch){
            etSearch.setVisibility(View.VISIBLE);
            etSearch.requestFocus();
            keyBoard.show(etSearch);
            btnSearch.setBackgroundResource((R.drawable.ic_cancel_white_24dp));
        }else {
            etSearch.setVisibility(View.GONE);
            etSearch.setText("");
            keyBoard.hide(etSearch);
            btnSearch.setBackgroundResource((R.drawable.ic_search_white_24dp));
        }
        isShowSearch = !isShowSearch;
    }
}
