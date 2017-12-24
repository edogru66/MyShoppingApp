package turkcell.com.tr.myshoppingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import turkcell.com.tr.NetworkUtils;
import turkcell.com.tr.database.DatabaseHandler;
import turkcell.com.tr.pojo.Product;

public class MainActivity extends AppCompatActivity implements ProductFragment.OnListFragmentInteractionListener {

    public String TAG = this.getClass().getName();

    List<Product> listOfProducts;
    ProgressBar pbIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbIndicator = findViewById(R.id.pb_loading);

        DatabaseHandler dbhandler = new DatabaseHandler(this);
        if (NetworkUtils.isConnected(this)) {
            try {
                String listOfProductsJson = new ProductListAsyncTask().execute(getString(R.string.product_list_url) + "list").get();
                listOfProducts = getListOfProducts(listOfProductsJson);
                dbhandler.addProducts(listOfProducts);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            listOfProducts = dbhandler.getAllProducts();
        }

        if (listOfProducts != null && !listOfProducts.isEmpty()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.list_fragment, ProductFragment.newInstance(2, listOfProducts))
                    .commit();
        } else {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListFragmentInteraction(Product item) {
        if (NetworkUtils.isConnected(this)) {
            try {
                String detail = new ProductListAsyncTask().execute(getString(R.string.product_list_url) + item.getId() + "/detail").get();
                Product product = getProductDetail(detail);
                DatabaseHandler dbhandler = new DatabaseHandler(this);
                dbhandler.addProduct(product);
            } catch (InterruptedException | ExecutionException | JSONException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, item.getId());
        startActivity(intent);
    }

    private Product getProductDetail(String json) throws JSONException {
        JSONObject productJson = new JSONObject(json);
        return new Product(
                productJson.getString("product_id"),
                productJson.getString("name"),
                productJson.getInt("price"),
                productJson.getString("image"),
                productJson.getString("description"));

    }

    public List<Product> getListOfProducts(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONArray products = (JSONArray) jsonObject.get("products");
        List<Product> listOfProducts = new ArrayList<>(products.length());
        for (int i=0; i<products.length(); i++) {
            JSONObject productJson = products.getJSONObject(i);
            Product p = new Product(
                    productJson.getString("product_id"),
                    productJson.getString("name"),
                    productJson.getInt("price"),
                    productJson.getString("image"));
            listOfProducts.add(p);
        }
        return listOfProducts;
    }

    class ProductListAsyncTask extends AsyncTask<String, Void, String> {


        String TAG = this.getClass().getName();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL (strings[0]);
                Log.e(TAG, "doInBackground: " + url.toString());
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                return stringBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pbIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
