package turkcell.com.tr.myshoppingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import turkcell.com.tr.database.DatabaseHandler;
import turkcell.com.tr.pojo.Product;

public class DetailActivity extends AppCompatActivity {

    ImageView mProductImage;
    TextView mProductName;
    TextView mPrice;
    TextView mDescription;
    Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DatabaseHandler dbhandler = new DatabaseHandler(this);
        String id = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        product = dbhandler.getProductById(id);

        mProductImage = findViewById(R.id.image);
        mProductName = findViewById(R.id.product_name);
        mPrice = findViewById(R.id.price);
        mDescription = findViewById(R.id.description);

        Glide.with(this).asBitmap().load(product.getImageUrl()).into(mProductImage);
        mProductName.setText(product.getName());
        mPrice.setText(String.valueOf(product.getPrice()));
        mDescription.setText(product.getDescription());
    }



}
