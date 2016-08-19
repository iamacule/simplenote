package vn.mran.simplenote.activity;

import vn.mran.simplenote.R;
import vn.mran.simplenote.view.Header;

/**
 * Created by MrAn on 19-Aug-16.
 */
public class AddNoteActivity extends BaseActivity {
    private Header header;

    @Override
    public int getView() {
        return R.layout.activity_add_note;
    }

    @Override
    public void initView() {
        header = new Header(getWindow().getDecorView().getRootView());
        header.title.setText(getString(R.string.add_note));
        header.setDefaultBtnBack();
    }

    @Override
    public void initValue() {

    }

    @Override
    public void initAction() {
    }
}
