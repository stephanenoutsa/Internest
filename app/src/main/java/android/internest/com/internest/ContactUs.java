package android.internest.com.internest;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ContactUs extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Amsterdrum_Grotesk.ttf");
        toolbarTitle.setTypeface(typeface);
        setSupportActionBar(toolbar);
    }

    ////////////Intents for menu items////////////
    public void onClickScan() {
        Intent i = new Intent(this, ScanCode.class);
        startActivity(i);
    }

    public void onClickHistory() {
        Intent i = new Intent(this, History.class);
        startActivity(i);
    }

    public void onClickShare() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link));
        i.setType("text/plain");
        startActivity(Intent.createChooser(i, getResources().getText(R.string.share_text)));
    }

    public void onClickContact() {
        Intent i = new Intent(this, ContactUs.class);
        startActivity(i);
    }
    //////////////End of intents//////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_blog, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.go_to_scan) {
            onClickScan();
            return true;
        }

        if (id == R.id.go_to_history) {
            onClickHistory();
            return true;
        }

        if (id == R.id.menu_item_share) {
            onClickShare();
            return true;
        }

        if (id == R.id.go_to_contact) {
            onClickContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
