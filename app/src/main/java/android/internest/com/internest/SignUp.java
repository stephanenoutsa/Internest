package android.internest.com.internest;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUp extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;

    MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
    Context context = this;
    User newUser;
    TextView numText;
    EditText numInput;
    TextView dobText;
    EditText dobInput;
    String num, dob, gender;
    TextView genderText;
    RadioButton male, female, other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbarTitle);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Amsterdrum_Grotesk.ttf");
        toolbarTitle.setTypeface(typeface);
        setSupportActionBar(toolbar);

        // Set navigation icon
        toolbar.setNavigationIcon(R.drawable.icon);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/bariol_regular_italic-webfont.ttf");
        numText = (TextView) findViewById(R.id.numText);
        numText.setTypeface(font);
        numInput = (EditText) findViewById(R.id.numInput);
        dobText = (TextView) findViewById(R.id.dobText);
        dobText.setTypeface(font);
        dobInput = (EditText) findViewById(R.id.dobInput);

        genderText = (TextView) findViewById(R.id.genderText);
        genderText.setTypeface(font);
        male = (RadioButton) findViewById(R.id.maleGender);
        female = (RadioButton) findViewById(R.id.femaleGender);
        other = (RadioButton) findViewById(R.id.otherGender);

    }

    // Check radio buttons clicks
    public void radioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.maleGender:
                if (checked)
                    gender = "male";
                break;
            case R.id.femaleGender:
                if (checked)
                    gender = "female";
                break;
            case R.id.otherGender:
                if (checked)
                    gender = "other";
                break;
        }
    }

    // Create user account
    public void onClickCreate(View view) {
        num = numInput.getText().toString();
        dob = dobInput.getText().toString();

        if (validate(num, dob, gender)) {
            // Check if user is connected
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                User user = new User(num, dob, gender, 0);

                // Add user to app database
                dbHandler.addUser(user);

                /**
                 * Add new user to server
                 */
                // Trailing slash is needed
                final String BASE_URL = "http://10.0.2.2:8080/tracker/webapi/"; // Localhost value is 10.0.2.2

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                InternestService internestService = retrofit.create(InternestService.class);

                Call<User> call = internestService.addUser(user);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        int statusCode = response.code();
                        if (statusCode == 200) {
                            newUser = response.body();

                            Toast.makeText(context, getString(R.string.user_created), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, getString(R.string.user_not_created), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                Intent i = new Intent(this, PointsPromo.class);
                i.putExtra("back", "signup");
                startActivity(i);
            } else {
                Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Validate the form
    public boolean validate(String num, String dob, String gender) {
        boolean valid = true;

        // Email validation
        /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError(getString(R.string.email_incorrect));
            valid = false;
        } else {
            emailInput.setError(null);
        }*/

        // Number validation
        if (num.isEmpty() || num.length() < 9) {
            numInput.setError(getString(R.string.num_incorrect));
            valid = false;
        } else {
            numInput.setError(null);
        }

        // DOB validation
        boolean a = dob.matches("\\d\\d/\\d\\d/\\d\\d");
        boolean b = dob.matches("\\d\\d/\\d\\d/\\d\\d\\d\\d");
        boolean c = dob.matches("\\d\\d\\.\\d\\d\\.\\d\\d");
        boolean d = dob.matches("\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d");
        boolean e = dob.matches("\\d\\d-\\d\\d-\\d\\d");
        boolean f = dob.matches("\\d\\d-\\d\\d-\\d\\d\\d\\d");
        if (a || b || c || d || e || f ) {
            dobInput.setError(null);
        } else {
            dobInput.setError(getString(R.string.dob_incorrect));
            valid = false;
        }

        // Gender validation
        if (gender == null) {
            male.setError(getString(R.string.gender_error));
            valid = false;
        } else {
            male.setError(null);
        }

        return valid;
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

    public void onClickPoints() {
        MyDBHandler dbHandler = new MyDBHandler(this, null, null, 1);
        User user = dbHandler.getUser();
        String num = user.getNum();
        String dob = user.getDob();
        Intent i;
        if (num.equals("null") || dob.equals("null")) {
            i = new Intent(this, SignUp.class);
        }
        else {
            i = new Intent(this, PointsPromo.class);
            i.putExtra("back", "signup");
        }

        startActivity(i);
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

        if (id == R.id.go_to_points) {
            onClickPoints();
            return true;
        }

        if (id == R.id.go_to_contact) {
            onClickContact();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
