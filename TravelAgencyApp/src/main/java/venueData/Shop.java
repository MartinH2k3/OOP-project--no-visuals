package venueData;

import java.util.ArrayList;

public class Shop extends Venue {
    public Shop(Universal.Address address, Long id, String name, String type) {
        super(address, id, name, type);
    }

    private class ShopItem<C> {
        private String name;
        private C content;

        public ShopItem(String name, C content) {
            this.name = name;
            this.content = content;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public C getContent() {
            return content;
        }

        public void setContent(C content) {
            this.content = content;
        }
    }

    private ArrayList<ShopItem<?>> shopItems;
    private String productCategory;

    public void setProductCategory(String productCategory){ this.productCategory = productCategory; }

    public String getProductCategory(){ return productCategory; }

    public void addShopItem(ShopItem<?> item) {
        shopItems.add(item);
    }
}
