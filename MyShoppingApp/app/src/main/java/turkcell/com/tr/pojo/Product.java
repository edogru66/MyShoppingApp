package turkcell.com.tr.pojo;
/**
 * Created by emre on 24.12.2017.
 */

public class Product {
    private String id;
    private String name;
    private int price;
    private String imageUrl;
    private String description;

    public Product (String aid,
                    String aname,
                    int aprice,
                    String aimageUrl) {
        id = aid;
        name = aname;
        price = aprice;
        imageUrl = aimageUrl;
        description = "";
    }

    public Product(String product_id, String name, int price, String image, String adescription) {
        this(product_id, name, price, image);
        description = adescription;
    }

    @Override
    public String toString() {
        return id + " " + name + " " + price + " " +imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
