package ru.tensor.sbis.appdesign;

import android.content.Intent;
import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.tensor.sbis.appdesign.appbar.AppBarActivity;
import ru.tensor.sbis.appdesign.appbar.AppBarDemoData;
import ru.tensor.sbis.appdesign.cloudview.CloudViewActivity;
import ru.tensor.sbis.appdesign.combined_multiselection.ShareMultiSelectorActivity;
import ru.tensor.sbis.appdesign.container.ContainerActivity;
import ru.tensor.sbis.appdesign.context_menu.ContextMenuActivity;
import ru.tensor.sbis.appdesign.folders.FoldersActivity;
import ru.tensor.sbis.appdesign.folders.FoldersViewModelActivity;
import ru.tensor.sbis.appdesign.folderview.CurrentFolderViewActivity;
import ru.tensor.sbis.appdesign.hallscheme.HallSchemeActivity;
import ru.tensor.sbis.appdesign.input_view.InputViewDemoActivity;
import ru.tensor.sbis.appdesign.inputtextbox.InputTextBoxActivity;
import ru.tensor.sbis.appdesign.listheader.DateHeaderDemoActivity;
import ru.tensor.sbis.appdesign.menu.DesignItem;
import ru.tensor.sbis.appdesign.menu.SimpleAdapter;
import ru.tensor.sbis.appdesign.navigation.NavigationActivity;
import ru.tensor.sbis.appdesign.pincode.PinCodeHostActivity;
import ru.tensor.sbis.appdesign.scrolltotop.ScrollToTopActivity;
import ru.tensor.sbis.appdesign.selection.RecipientMultiSelectorActivity;
import ru.tensor.sbis.appdesign.selection.RecipientMultiSelectorActivityKt;
import ru.tensor.sbis.appdesign.selection.RecipientSingleSelectorActivity;
import ru.tensor.sbis.appdesign.selection.SelectorActivity;
import ru.tensor.sbis.appdesign.selection.SingleSelectorActivity;
import ru.tensor.sbis.appdesign.selection.selectionpreview.SelectionPreviewActivity;
import ru.tensor.sbis.appdesign.skeletonview.SkeletonViewActivity;
import ru.tensor.sbis.appdesign.stubview.NestedStubViewActivity;
import ru.tensor.sbis.appdesign.stubview.StubViewActivity;
import ru.tensor.sbis.appdesign.title_view.TitleViewActivity;
import ru.tensor.sbis.design.toolbar.appbar.transition.SbisAppBarTransitionUtil;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleAdapter = new SimpleAdapter();
        simpleAdapter.setOnClickListener(onItemClickListener);
        simpleAdapter.setItems(Arrays.asList(DesignItem.values()));

        recyclerView = findViewById(R.id.rv_designed_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(simpleAdapter);

        if (!Fresco.hasBeenInitialized()) {
            Fresco.initialize(getApplicationContext());
        }
    }

    private SimpleAdapter.OnItemClickListener onItemClickListener = item -> {
        Intent intent;
        switch (item) {
            case ITEM_FOLDERS:
                startActivity(new Intent(MainActivity.this, FoldersActivity.class));
                break;
            case ITEM_FOLDERS_VIEWMODEL:
                startActivity(new Intent(MainActivity.this, FoldersViewModelActivity.class));
                break;
            case ITEM_SCROLL_TO_TOP:
                startActivity(new Intent(MainActivity.this, ScrollToTopActivity.class));
                break;
            case ITEM_CONTEXT_MENU:
                startActivity(new Intent(MainActivity.this, ContextMenuActivity.class));
                break;
            case ITEM_CONTAINER:
                startActivity(new Intent(MainActivity.this, ContainerActivity.class));
                break;
            case ITEM_HALL_SCHEME:
                startActivity(new Intent(MainActivity.this, HallSchemeActivity.class));
                break;
            case STUB_VIEW:
                startActivity(new Intent(MainActivity.this, StubViewActivity.class));
                break;
            case STUB_VIEW_NESTED:
                startActivity(new Intent(MainActivity.this, NestedStubViewActivity.class));
                break;
            case ITEM_INPUT_TEXT_BOX:
                startActivity(new Intent(MainActivity.this, InputTextBoxActivity.class));
                break;
            case ITEM_SINGLE_SELECTION:
                startActivity(new Intent(MainActivity.this, SingleSelectorActivity.class));
                break;
            case ITEM_MULTI_SELECTION:
                startActivity(new Intent(MainActivity.this, SelectorActivity.class));
                break;
            case ITEM_SINGLE_RECIPIENT_SELECTION:
                startActivity(new Intent(MainActivity.this, RecipientSingleSelectorActivity.class));
                break;
            case ITEM_MULTI_RECIPIENT_SELECTION:
                startActivity(new Intent(MainActivity.this, RecipientMultiSelectorActivity.class));
                break;
            case ITEM_MULTI_RECIPIENT_SELECTION_COMMON_API:
                intent = new Intent(MainActivity.this, RecipientMultiSelectorActivity.class);
                intent.putExtra(RecipientMultiSelectorActivityKt.IS_COMMON_RECIPIENT_API, true);
                startActivity(intent);
                break;
            case ITEM_SELECTION_PREVIEW:
                startActivity(new Intent(MainActivity.this, SelectionPreviewActivity.class));
                break;
            case ITEM_NAVIGATION:
                startActivity(new Intent(MainActivity.this, NavigationActivity.class));
                break;
            case ITEM_APP_BAR:
                intent = new Intent(MainActivity.this, AppBarActivity.class);
                SbisAppBarTransitionUtil.saveState(AppBarDemoData.MODEL_LIGHT, intent);
                startActivity(intent);
                break;
            case ITEM_FOLDER_VIEW:
                startActivity(new Intent(MainActivity.this, CurrentFolderViewActivity.class));
                break;
            case ITEM_CLOUD_VIEW:
                startActivity(new Intent(MainActivity.this, CloudViewActivity.class));
                break;
            case ITEM_SHARE_MULTISELECTION:
                startActivity(new Intent(MainActivity.this, ShareMultiSelectorActivity.class));
                break;
            case ITEM_PIN_CODE:
                startActivity(new Intent(MainActivity.this, PinCodeHostActivity.class));
                break;
            case ITEM_SKELETON_VIEW:
                startActivity(new Intent(MainActivity.this, SkeletonViewActivity.class));
                break;
            case ITEM_TITLE_VIEW:
                startActivity(new Intent(MainActivity.this, TitleViewActivity.class));
                break;
            case ITEM_DATE_HEADER:
                startActivity(new Intent(MainActivity.this, DateHeaderDemoActivity.class));
                break;
            case ITEM_INPUT_VIEW:
                startActivity(new Intent(MainActivity.this, InputViewDemoActivity.class));
                break;
            default:
                break;
        }
    };
}