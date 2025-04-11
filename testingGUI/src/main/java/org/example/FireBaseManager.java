package org.example;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
public class FireBaseManager {
    private static FireBaseManager instance = new FireBaseManager();
    private Firestore db;


    


    public FireBaseManager() {
        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirestoreOptions firestoreOptions = FirestoreOptions.newBuilder()
                    .setCredentials(credentials)
                    .build();

            this.db = firestoreOptions.getService();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize Firebase", e);
        }
    }
    Dotenv dotenv = Dotenv.load();
    Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    public static synchronized FireBaseManager getInstance() {
        if (instance == null) {
            instance = new FireBaseManager();
        }
        return instance;
    }

    public Firestore getDb() {
        return db;
    }

    public static void main(String[] args) throws Exception {
        FireBaseManager firebaseManager = new FireBaseManager();
        // Example Usage
        System.out.println("getDb() = " + firebaseManager.getDb());

    }
    Map<String, Object> options = ObjectUtils.asMap(
            "folder", "my_uploads/"

    );

    public List<Item> searchBar(String query) {
        List<Item> matchingItems = new ArrayList<>();

        try {
            ApiFuture<QuerySnapshot> future = db.collection("Items").get();
            QuerySnapshot querySnapshot = future.get();

            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Item item = document.toObject(Item.class);
                if (item != null && item.getItemName() != null) {
                    String itemNameLower = item.getItemName().toLowerCase();
                    String queryLower = query.toLowerCase();

                    if (itemNameLower.contains(queryLower)) {
                        item.setItemID(document.getId()); // Ensure ID is included
                        matchingItems.add(item);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return matchingItems;
    }

    public String uploadimage(String url) {
        String publicurl;
        try {
            Map uploadResult = cloudinary.uploader().unsignedUpload(url, "Ecommerce", options);
            publicurl = (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return publicurl;
    }
    public void addClient(Client user) {
        DocumentReference userRef = db.collection("Clients").document(user.getUserID());
        ApiFuture<WriteResult> result = userRef.set(user);
        try {
            System.out.println("Client added at: " + result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void addVendor(org.example.Vendor user) {
        DocumentReference userRef = db.collection("Vendors").document(user.getUserID());
        ApiFuture<WriteResult> result = userRef.set(user);
        try {
            System.out.println("Vendor added at: " + result.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public org.example.Vendor getVendor(String userID) {
        DocumentReference userRef = db.collection("Vendors").document(userID);
        ApiFuture<DocumentSnapshot> result = userRef.get();
        try {
            if (result.get().exists()) {
                return result.get().toObject(Vendor.class);
            } else {
                return null; // User not found
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
    public User getUser(String userID) {
        DocumentReference userRef = db.collection("Clients").document(userID);
        ApiFuture<DocumentSnapshot> result = userRef.get();
        DocumentReference userRef2 = db.collection("Vendors").document(userID);
        ApiFuture<DocumentSnapshot> result2 = userRef2.get();
        try {
            if (result.get().exists()) {
                return result.get().toObject(Client.class);
            } else if(result2.get().exists()) {
                return result2.get().toObject(Vendor.class); // User not found
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Client getClient(String userID) {
        DocumentReference userRef = db.collection("Clients").document(userID);
        ApiFuture<DocumentSnapshot> result = userRef.get();
        try {
            if (result.get().exists()) {
                return result.get().toObject(Client.class);
            } else {
                return null; // User not found
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Item getItem(String itemID) {
        DocumentReference itemRef = db.collection("Items").document(itemID);
        ApiFuture<DocumentSnapshot> result = itemRef.get();
        try {
            if (result.get().exists()) {
                return result.get().toObject(Item.class);
            } else {
                return null; // User not found
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

    }

    //function changed from void to string
    public String addItem(Item item) {
        String localUrl=item.getImageURL();
        String publicUrl=this.uploadimage(localUrl);
        item.setImageURL(publicUrl);
        DocumentReference postRef = db.collection("Items").document();
        ApiFuture<WriteResult> postResult = postRef.set(item);

        try {
            postResult.get();
            System.out.println("Item added at: " + postResult.get().getUpdateTime());

            String itemId = postRef.getId();

            ApiFuture<WriteResult> updatePostIdResult = postRef.update("ItemID", itemId);
            System.out.println("ItemID updated at: " + updatePostIdResult.get().getUpdateTime());

            DocumentReference userRef = db.collection("Vendors").document(item.getVendor());
            ApiFuture<WriteResult> updateUserResult = userRef.update("ItemsID", FieldValue.arrayUnion(itemId));
            System.out.println("User updated at: " + updateUserResult.get().getUpdateTime());

            return itemId;  //added return here

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

    }
    public void deleteItem(String itemId, String vendorId) {
        // Reference to the item in Firestore
        DocumentReference itemRef = db.collection("Items").document(itemId);

        // Delete the document
        ApiFuture<WriteResult> deleteFuture = itemRef.delete();

        // Remove the item ID from the Vendor's ItemsID array
        DocumentReference vendorRef = db.collection("Vendors").document(vendorId);
        ApiFuture<WriteResult> updateVendorFuture = vendorRef.update("ItemsID", FieldValue.arrayRemove(itemId));

        try {
            deleteFuture.get();
            updateVendorFuture.get();

            System.out.println("Item deleted and removed from vendor list.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        try {
            // Get all item documents from the "Items" collection
            ApiFuture<QuerySnapshot> future = db.collection("Items").get();
            QuerySnapshot querySnapshot = future.get();

            // Iterate through all the item documents
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Item item = document.toObject(Item.class);
                if (item != null) {
                    item.setItemID(document.getId()); // Set the ItemID manually
                    items.add(item);
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return items;
    }


    public List<Item> getItemsForVendor(String vendorId) {
        List<Item> items = new ArrayList<>();

        try {
            // Get the vendor document
            DocumentReference vendorRef = db.collection("Vendors").document(vendorId);
            ApiFuture<DocumentSnapshot> vendorFuture = vendorRef.get();
            DocumentSnapshot vendorDoc = vendorFuture.get();

            if (vendorDoc.exists()) {
                List<String> ItemIds = (List<String>) vendorDoc.get("ItemsID");

                if (ItemIds != null && !ItemIds.isEmpty()) {
                    List<ApiFuture<DocumentSnapshot>> futures = new ArrayList<>();

                    // Fetch all item documents in parallel
                    for (String itemId : ItemIds) {
                        DocumentReference itemRef = db.collection("Items").document(itemId);
                        futures.add(itemRef.get());
                    }

                    // Resolve all futures and convert to Item objects
                    for (int i = 0; i < futures.size(); i++) {
                        DocumentSnapshot itemDoc = futures.get(i).get();
                        if (itemDoc.exists()) {
                            Item item = itemDoc.toObject(Item.class);
                            if (item != null) {
                                item.setItemID(ItemIds.get(i)); // Assuming setItemID exists
                                items.add(item);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Item> getItemsByCategory(String category) {
        List<Item> items = new ArrayList<>();

        try {
            // Create a query on the "Items" collection where ItemCategory equals the given category
            CollectionReference itemsRef = db.collection("Items");
            Query query = itemsRef.whereEqualTo("itemCategory", category);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            // Loop through the query results
            for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                Item item = doc.toObject(Item.class);
                if (item != null) {
                    item.setItemID(doc.getId()); // Set the item ID if needed
                    items.add(item);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return items;
    }

    public void updateStock(String itemID, int quantity) {
        DocumentReference postRef = db.collection("Items").document(itemID);
        ApiFuture<WriteResult> updateStockResult = postRef.update("Stock", quantity);
        try {
            System.out.println("Stock updated at: " + updateStockResult.get().getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public int getStock(String itemID) {
        DocumentReference itemRef = db.collection("Items").document(itemID);
        ApiFuture<DocumentSnapshot> future = itemRef.get();

        try {
            DocumentSnapshot document = future.get(); // Wait for the async result

            if (document.exists() && document.contains("stock")) {
                Long stockLong = document.getLong("stock"); // Firestore stores numbers as Long
                return stockLong != null ? stockLong.intValue() : 0;
            } else {
                System.out.println("Item not found or stock field is missing.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return 0; // Default value if something goes wrong
    }

    public void addReview(Review review) {
        // Create a new review document with auto-generated ID
        DocumentReference reviewRef = db.collection("Reviews").document();
        ApiFuture<WriteResult> reviewResult = reviewRef.set(review);

        try {
            // Wait for the write to complete
            reviewResult.get();
            System.out.println("Review added at: " + reviewResult.get().getUpdateTime());

            // Get the generated Review ID
            String reviewId = reviewRef.getId();

            // Update the review document to include its own ID
            ApiFuture<WriteResult> updateReviewId = reviewRef.update("ReviewID", reviewId);
            System.out.println("ReviewID updated at: " + updateReviewId.get().getUpdateTime());

            // Add review ID to the related Item
            DocumentReference itemRef = db.collection("Items").document(review.getItemID());
            ApiFuture<WriteResult> updateItem = itemRef.update("ReviewsID", FieldValue.arrayUnion(reviewId));
            System.out.println("Item updated with review at: " + updateItem.get().getUpdateTime());

            // Add review ID to the user's review list
            DocumentReference userRef = db.collection("Clients").document(review.getUserID());
            ApiFuture<WriteResult> updateUser = userRef.update("reviewHistory", FieldValue.arrayUnion(reviewId));
            System.out.println("User updated with review at: " + updateUser.get().getUpdateTime());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public List<Review> getReviewsByClient(String clientId) {
        List<Review> reviews = new ArrayList<>();

        try {
            // Get the client document
            DocumentReference clientRef = db.collection("Clients").document(clientId);
            ApiFuture<DocumentSnapshot> clientFuture = clientRef.get();
            DocumentSnapshot clientDoc = clientFuture.get();

            if (clientDoc.exists()) {
                List<String> reviewIds = (List<String>) clientDoc.get("reviewsHistory");

                if (reviewIds != null && !reviewIds.isEmpty()) {
                    List<ApiFuture<DocumentSnapshot>> futures = new ArrayList<>();

                    // Fetch all review documents in parallel
                    for (String reviewId : reviewIds) {
                        DocumentReference reviewRef = db.collection("Reviews").document(reviewId);
                        futures.add(reviewRef.get());
                    }

                    // Resolve all futures and convert to Review objects
                    for (int i = 0; i < futures.size(); i++) {
                        DocumentSnapshot reviewDoc = futures.get(i).get();
                        if (reviewDoc.exists()) {
                            Review review = reviewDoc.toObject(Review.class);
                            if (review != null) {
                                review.setReviewID(reviewIds.get(i)); // Assuming setReviewID exists
                                reviews.add(review);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public List<Review> getReviewsByItem(String ItemID) {
        List<Review> reviews = new ArrayList<>();

        try {
            // Get the client document
            DocumentReference clientRef = db.collection("Items").document(ItemID);
            ApiFuture<DocumentSnapshot> clientFuture = clientRef.get();
            DocumentSnapshot clientDoc = clientFuture.get();

            if (clientDoc.exists()) {
                List<String> reviewIds = (List<String>) clientDoc.get("ReviewsID");

                if (reviewIds != null && !reviewIds.isEmpty()) {
                    List<ApiFuture<DocumentSnapshot>> futures = new ArrayList<>();

                    // Fetch all review documents in parallel
                    for (String reviewId : reviewIds) {
                        DocumentReference reviewRef = db.collection("Reviews").document(reviewId);
                        futures.add(reviewRef.get());
                    }

                    // Resolve all futures and convert to Review objects
                    for (int i = 0; i < futures.size(); i++) {
                        DocumentSnapshot reviewDoc = futures.get(i).get();
                        if (reviewDoc.exists()) {
                            Review review = reviewDoc.toObject(Review.class);
                            if (review != null) {
                                review.setReviewID(reviewIds.get(i)); // Assuming setReviewID exists
                                reviews.add(review);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return reviews;
    }
    public void addOrder(org.example.Order order) {

        // Create a new review document with auto-generated ID
        DocumentReference reviewRef = db.collection("Orders").document();
        ApiFuture<WriteResult> reviewResult = reviewRef.set(order);

        try {
            // Wait for the write to complete
            reviewResult.get();
            System.out.println("Order added at: " + reviewResult.get().getUpdateTime());

            // Get the generated Review ID
            String OrderID = reviewRef.getId();

            // Update the review document to include its own ID
            ApiFuture<WriteResult> updateReviewId = reviewRef.update("orderID", OrderID);
            System.out.println("OrderID updated at: " + updateReviewId.get().getUpdateTime());


            // Add review ID to the user's review list
            DocumentReference userRef = db.collection("Clients").document(order.getUserID());
            ApiFuture<WriteResult> updateUser = userRef.update("CurrentOrders", FieldValue.arrayUnion(OrderID));
            System.out.println("User updated with review at: " + updateUser.get().getUpdateTime());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void makeHistory(String orderID, String userID) {

        // Get references to the Order and Client documents
        DocumentReference orderRef = db.collection("Orders").document(orderID);
        DocumentReference userRef = db.collection("Clients").document(userID);

        // Create update requests
        ApiFuture<WriteResult> updateOrder = orderRef.update("current", 0);
        ApiFuture<WriteResult> removeFromCurrentOrders = userRef.update("CurrentOrders", FieldValue.arrayRemove(orderID));
        ApiFuture<WriteResult> addToHistory = userRef.update("History", FieldValue.arrayUnion(orderID));

        try {
            // Wait for all operations to complete
            updateOrder.get();
            System.out.println("Current field updated to 1 in Order at: " + updateOrder.get().getUpdateTime());

            removeFromCurrentOrders.get();
            System.out.println("OrderID removed from CurrentOrders in Client at: " + removeFromCurrentOrders.get().getUpdateTime());

            addToHistory.get();
            System.out.println("OrderID added to History in Client at: " + addToHistory.get().getUpdateTime());

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public List<org.example.Order> getCurrentOrders() {
        List<org.example.Order> orders = new ArrayList<>();

        try {
            // Query orders where current = 1
            ApiFuture<QuerySnapshot> query = db.collection("Orders").whereEqualTo("current", 1).get();
            QuerySnapshot querySnapshot = query.get();

            if (!querySnapshot.isEmpty()) {
                // Iterate through the results and convert them to Order objects
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    org.example.Order order = document.toObject(org.example.Order.class);
                    if (order != null) {
                        order.setOrderID(document.getId());  // Set OrderID manually
                        orders.add(order);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return orders;
    }


    public List<org.example.Order> getHistoryOrders() {
        List<org.example.Order> orders = new ArrayList<>();

        try {
            // Query orders where current = 0
            ApiFuture<QuerySnapshot> query = db.collection("Orders").whereEqualTo("current", 0).get();
            QuerySnapshot querySnapshot = query.get();

            if (!querySnapshot.isEmpty()) {
                // Iterate through the results and convert them to Order objects
                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                    org.example.Order order = document.toObject(org.example.Order.class);
                    if (order != null) {
                        order.setOrderID(document.getId());  // Set OrderID manually
                        orders.add(order);
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return orders;
    }
    public List<org.example.Order> getCurrentOrdersForClient(String clientId) {
        List<org.example.Order> orders = new ArrayList<>();

        try {
            // Get the client document
            DocumentReference clientRef = db.collection("Clients").document(clientId);
            ApiFuture<DocumentSnapshot> clientFuture = clientRef.get();
            DocumentSnapshot clientDoc = clientFuture.get();

            if (clientDoc.exists()) {
                // Get the list of order IDs for this client
                List<String> orderIds = (List<String>) clientDoc.get("CurrentOrders");

                if (orderIds != null && !orderIds.isEmpty()) {
                    // Fetch the orders where current = 1
                    List<ApiFuture<DocumentSnapshot>> futures = new ArrayList<>();

                    for (String orderId : orderIds) {
                        DocumentReference orderRef = db.collection("Orders").document(orderId);
                        ApiFuture<DocumentSnapshot> orderFuture = orderRef.get();
                        futures.add(orderFuture);
                    }

                    // Resolve all futures and add matching orders to the list
                    for (ApiFuture<DocumentSnapshot> future : futures) {
                        DocumentSnapshot orderDoc = future.get();
                        if (orderDoc.exists() && orderDoc.getBoolean("current") == true) {
                            org.example.Order order = orderDoc.toObject(org.example.Order.class);
                            if (order != null) {
                                order.setOrderID(orderDoc.getId());  // Set OrderID manually
                                orders.add(order);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return orders;
    }
    public List<org.example.Order> getHistoryForClient(String clientId) {
        List<org.example.Order> orders = new ArrayList<>();

        try {
            // Get the client document
            DocumentReference clientRef = db.collection("Clients").document(clientId);
            ApiFuture<DocumentSnapshot> clientFuture = clientRef.get();
            DocumentSnapshot clientDoc = clientFuture.get();

            if (clientDoc.exists()) {
                // Get the list of order IDs for this client
                List<String> orderIds = (List<String>) clientDoc.get("CurrentOrders");

                if (orderIds != null && !orderIds.isEmpty()) {
                    // Fetch the orders where current = 0
                    List<ApiFuture<DocumentSnapshot>> futures = new ArrayList<>();

                    for (String orderId : orderIds) {
                        DocumentReference orderRef = db.collection("Orders").document(orderId);
                        ApiFuture<DocumentSnapshot> orderFuture = orderRef.get();
                        futures.add(orderFuture);
                    }

                    // Resolve all futures and add matching orders to the list
                    for (ApiFuture<DocumentSnapshot> future : futures) {
                        DocumentSnapshot orderDoc = future.get();
                        if (orderDoc.exists() && orderDoc.getBoolean("current") == false) {
                            org.example.Order order = orderDoc.toObject(Order.class);
                            if (order != null) {
                                order.setOrderID(orderDoc.getId());  // Set OrderID manually
                                orders.add(order);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return orders;
    }
    public void addCart(Cart cart) {
        // Get a reference to the "Carts" collection
        DocumentReference cartRef = db.collection("Carts").document(cart.getCartID());

        // Set the Cart document with the data from the Cart object
        ApiFuture<WriteResult> future = cartRef.set(cart);

        try {
            // Wait for the operation to complete
            WriteResult result = future.get();
            System.out.println("Cart added at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public Cart getClientCart(String ClientID) {
        Cart cart = null;

        try {
            // Get the reference to the Cart document using the CartID
            DocumentReference cartRef = db.collection("Carts").document(ClientID);

            // Fetch the document from Firestore
            ApiFuture<DocumentSnapshot> future = cartRef.get();
            DocumentSnapshot document = future.get();

            // Check if the document exists
            if (document.exists()) {
                // Convert the document into a Cart object
                cart = document.toObject(Cart.class);
                // Set the CartID manually in case you need it
                if (cart != null) {
                    cart.setCartID(document.getId());
                }
            } else {
                System.out.println("No cart found with the given ClientID.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return cart;
    }
    public void addItemToWishlist(String userID, String itemID) {
        // Reference to the client document
        DocumentReference clientRef = db.collection("Clients").document(userID);

        // Add the itemID to the wishlist array
        ApiFuture<WriteResult> updateWishlist = clientRef.update("Wishlist", FieldValue.arrayUnion(itemID));

        try {
            // Wait for the operation to complete
            WriteResult result = updateWishlist.get();
            System.out.println("Item added to wishlist at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removeItemFromWishlist(String userID, String itemID) {
        try {
            DocumentReference clientRef = db.collection("Clients").document(userID);

            ApiFuture<WriteResult> updateResult = clientRef.update("Wishlist", FieldValue.arrayRemove(itemID));
            updateResult.get(); // block until the operation completes

            System.out.println("Item removed from wishlist.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public List<String> getWishlist(String userID) {
        List<String> wishlist = new ArrayList<>();

        try {
            // Reference to the client document
            DocumentReference clientRef = db.collection("Clients").document(userID);

            // Get the document snapshot
            ApiFuture<DocumentSnapshot> future = clientRef.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                // Retrieve the wishlist field and cast it to a List
                wishlist = (List<String>) document.get("Wishlist");

                if (wishlist == null) {
                    wishlist = new ArrayList<>(); // Initialize if wishlist is null
                }

                System.out.println("Wishlist retrieved: " + wishlist);
            } else {
                System.out.println("No such document.");
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return wishlist;
    }


    public void addItemToCart(String userID, Item item, int quantity) {
        try {
            // Get a reference to the user's cart document
            DocumentReference cartRef = db.collection("Carts").document(userID);

            // Prepare a list of the item to add (repeating the itemID by quantity)
            List<String> itemsToAdd = new ArrayList<>();
            for (int i = 0; i < quantity; i++) {
                itemsToAdd.add(item.getItemID());
            }

            // Use arrayUnion to add the item(s) to the itemsID array without duplicates
            ApiFuture<WriteResult> updateResult = cartRef.update("itemsID", FieldValue.arrayUnion(itemsToAdd.toArray()));

            // Wait for the update to complete
            updateResult.get(); // This will block until the write is finished
            System.out.println("Item(s) added to cart.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void removeItemFromCart(String userID, Item item, int quantity) {
        try {
            // Get a reference to the user's cart document
            DocumentReference cartRef = db.collection("Carts").document(userID);

            // Loop to remove the specified number of items
            for (int i = 0; i < quantity; i++) {
                // Use arrayRemove to remove the item from the itemsID array
                ApiFuture<WriteResult> updateResult = cartRef.update("itemsID", FieldValue.arrayRemove(item.getItemID()));
                updateResult.get(); // This will block until the write is finished
            }

            System.out.println("Item(s) removed from cart.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void emptyCart(String userID) {
        try {
            // Get a reference to the user's cart document
            DocumentReference cartRef = db.collection("Carts").document(userID);

            // Set the itemsID field to an empty list
            ApiFuture<WriteResult> updateResult = cartRef.update("itemsID", new ArrayList<String>());
            ApiFuture<WriteResult> updateResult1 = cartRef.update("totalPrice", null);
            // Wait for the update to complete
            updateResult.get(); // This will block until the write is finished
            updateResult1.get();
            System.out.println("Cart emptied.");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public List<Item> getCartItems(String userID) {
        List<Item> cartItems = new ArrayList<>();

        try {
            // Get the user's cart document
            DocumentReference cartRef = db.collection("Carts").document(userID);
            DocumentSnapshot cartDoc = cartRef.get().get();

            if (cartDoc.exists()) {
                List<String> itemIds = (List<String>) cartDoc.get("itemsID");

                if (itemIds != null && !itemIds.isEmpty()) {
                    List<String> processedItemIds = new ArrayList<>();

                    for (String itemId : itemIds) {
                        if (processedItemIds.contains(itemId)) {
                            continue;
                        }

                        int quantity = 0;
                        for (String id : itemIds) {
                            if (id.equals(itemId)) {
                                quantity++;
                            }
                        }

                        processedItemIds.add(itemId);

                        DocumentSnapshot itemDoc = db.collection("Items").document(itemId).get().get();

                        if (itemDoc.exists()) {
                            Item item = itemDoc.toObject(Item.class);
                            if (item != null) {
                                item.setItemID(itemId);
                                item.setStock(quantity); // Using stock field to store cart quantity
                                cartItems.add(item);
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return cartItems;
    }

    public void changeVendorPassword(String vendorId, String newPassword) {
        // Reference to the vendor document
        DocumentReference vendorRef = db.collection("Vendors").document(vendorId);

        try {
            // Update the vendor's password field directly
            ApiFuture<WriteResult> future = vendorRef.update("password", newPassword);
            WriteResult result = future.get();
            System.out.println("Vendor password updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeClientPassword(String clientId, String newPassword) {
        // Reference to the client document
        DocumentReference clientRef = db.collection("Clients").document(clientId);

        try {
            // Update the client's password field directly
            ApiFuture<WriteResult> future = clientRef.update("password", newPassword);
            WriteResult result = future.get();
            System.out.println("Client password updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    public void changeVendorEmail(String vendorId, String newEmail) {
        // Reference to the vendor document
        DocumentReference vendorRef = db.collection("Vendors").document(vendorId);

        try {
            // Update the vendor's email field directly
            ApiFuture<WriteResult> future = vendorRef.update("email", newEmail);
            WriteResult result = future.get();
            System.out.println("Vendor email updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeVendorAddress(String vendorId, String newAddress) {
        // Reference to the vendor document
        DocumentReference vendorRef = db.collection("Vendors").document(vendorId);

        try {
            // Update the vendor's Address field directly
            ApiFuture<WriteResult> future = vendorRef.update("address", newAddress);
            WriteResult result = future.get();
            System.out.println("Vendor Address updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeVendorPhoneNumber(String vendorId, String newPhoneNumber) {
        // Reference to the vendor document
        DocumentReference vendorRef = db.collection("Vendors").document(vendorId);

        try {
            // Update the vendor's PhoneNumber field directly
            ApiFuture<WriteResult> future = vendorRef.update("phoneNumber", newPhoneNumber);
            WriteResult result = future.get();
            System.out.println("Vendor PhoneNumber updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeClientEmail(String ClientId, String newEmail) {
        // Reference to the Client document
        DocumentReference vendorRef = db.collection("Clients").document(ClientId);

        try {
            // Update the Client's email field directly
            ApiFuture<WriteResult> future = vendorRef.update("email", newEmail);
            WriteResult result = future.get();
            System.out.println("Client email updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeClientAddress(String ClientId, String newAddress) {
        // Reference to the Client document
        DocumentReference vendorRef = db.collection("Clients").document(ClientId);

        try {
            // Update the Client's Address field directly
            ApiFuture<WriteResult> future = vendorRef.update("address", newAddress);
            WriteResult result = future.get();
            System.out.println("Client address updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeClientPhoneNumber(String ClientId, String newPhoneNumber) {
        // Reference to the Client document
        DocumentReference vendorRef = db.collection("Clients").document(ClientId);

        try {
            // Update the Client's PhoneNumber field directly
            ApiFuture<WriteResult> future = vendorRef.update("phoneNumber", newPhoneNumber);
            WriteResult result = future.get();
            System.out.println("Client phoneNumber updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void changeItemName(String ItemID, String newName) {
        // Reference to the Item document
        DocumentReference vendorRef = db.collection("Items").document(ItemID);

        try {
            // Update the Item's Name field directly
            ApiFuture<WriteResult> future = vendorRef.update("ItemName", newName);
            WriteResult result = future.get();
            System.out.println("Item name updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeItemCategory(String ItemID, String newCategory) {
        // Reference to the Item document
        DocumentReference vendorRef = db.collection("Items").document(ItemID);

        try {
            // Update the Item's Category field directly
            ApiFuture<WriteResult> future = vendorRef.update("ItemCategory", newCategory);
            WriteResult result = future.get();
            System.out.println("Item Category updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    public void changeItemDescription(String ItemID, String newDescription) {
        // Reference to the Item document
        DocumentReference vendorRef = db.collection("Items").document(ItemID);

        try {
            // Update the Item's Description field directly
            ApiFuture<WriteResult> future = vendorRef.update("ItemDescription", newDescription);
            WriteResult result = future.get();
            System.out.println("Item Description updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void changeItemPrice(String ItemID, String newPrice) {
        // Reference to the Item document
        DocumentReference vendorRef = db.collection("Items").document(ItemID);

        try {
            // Update the Item's Price field directly
            ApiFuture<WriteResult> future = vendorRef.update("ItemPrice", newPrice);
            WriteResult result = future.get();
            System.out.println("Item Price updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void changeItemPicture(String ItemID, String newImage) {
        // Reference to the Item document
        String URL=this.uploadimage(newImage);
        Item item=getItem(ItemID);
        item.setImageURL(URL);
        DocumentReference vendorRef = db.collection("Items").document(ItemID);

        try {
            // Update the Item's Image field directly
            ApiFuture<WriteResult> future = vendorRef.update("ImageURL", URL);
            WriteResult result = future.get();
            System.out.println("Item Image updated at: " + result.getUpdateTime());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void close() throws Exception {
        if (db != null) {
            db.close(); // closes the gRPC channel properly
        }
    }

}


