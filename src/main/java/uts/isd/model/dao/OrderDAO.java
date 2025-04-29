package uts.isd.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;

import uts.isd.model.Cart;
import uts.isd.model.CartItem;
import uts.isd.model.Order;

public class OrderDAO {
    private Connection conn; // Connection object to manage the database connection
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;
    private PreparedStatement selectAllStmt;
    private PreparedStatement selectStmt;

        // Constructor that initializes the database connection and prepared statements

    public OrderDAO(Connection connection) throws SQLException {
        this.conn = connection;
        conn.setAutoCommit(true);
        initStatements();
    }

        // Constructor that initializes the database connection and prepared statements

    private void initStatements() throws SQLException {
        String insertQuery = "INSERT INTO `Order` (UserID, Order_Date, Order_Status, Delivery_address, Quantity) VALUES (?, ?, ?, ?, ?)";
        String updateQuery = "UPDATE `Order` SET UserID = ?, Order_Date = ?, Order_Status = ?, Delivery_address = ?, Quantity = ? WHERE OrderID = ?";
        String deleteQuery = "DELETE FROM `Order` WHERE OrderID = ?";
        String selectAllQuery = "SELECT * FROM `Order`";
        String selectQuery = "SELECT * FROM `Order` WHERE OrderID = ?";

        insertStmt = conn.prepareStatement(insertQuery);
        updateStmt = conn.prepareStatement(updateQuery);
        deleteStmt = conn.prepareStatement(deleteQuery);
        selectAllStmt = conn.prepareStatement(selectAllQuery);
        selectStmt = conn.prepareStatement(selectQuery);
    }

        // Inserts a new order into the database

    public boolean insertOrder(Order order) throws SQLException {
        insertStmt.setInt(1, order.getUserId());
        insertStmt.setObject(2, order.getOrderDate());
        insertStmt.setString(3, order.getOrderStatus());
        insertStmt.setString(4, order.getDeliveryAddress());
        insertStmt.setString(5, order.getQuantity());
        return insertStmt.executeUpdate() > 0;
    }

    public boolean insertOrder(Integer userId, Cart cart, String deliveryAddress, String paymentMethod) throws SQLException {
        String query = "INSERT INTO `Order` (UserID, Order_Date, Order_Status, Delivery_Address, TotalPrice, Quantity) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(query);
        if (userId != null) {
            stmt.setInt(1, userId);
        } else {
            stmt.setNull(1, java.sql.Types.INTEGER);
        }
        stmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
        stmt.setString(3, "Pending"); // Assuming status
        stmt.setString(4, deliveryAddress);
        stmt.setDouble(5, calculateTotalPrice(cart)); // Ensure this calculation method exists
        stmt.setInt(6, cart.getTotalQuantity());
    
        int affectedRows = stmt.executeUpdate();
        stmt.close();
        return affectedRows > 0;
    }
    
    // Method to calculate the total price of the cart items

private double calculateTotalPrice(Cart cart) {
    double totalPrice = 0;
    for (CartItem item : cart.getItems()) {
        totalPrice += item.getProduct().getProductprice() * item.getQuantity();
    }
    return totalPrice;
}


    // Lists all orders from the database

    public ArrayList<Order> listAllOrders() throws SQLException {
        ArrayList<Order> listOrder = new ArrayList<>();
        try (ResultSet resultSet = selectAllStmt.executeQuery()) {
            while (resultSet.next()) {
                listOrder.add(extractOrderFromResultSet(resultSet));
            }
        }
        return listOrder;
    }
    
        // Method to list all orders, sorted by the specified column and order

    public ArrayList<Order> listAllOrders(String sortBy, String sortOrder) throws SQLException {
        String query = "SELECT * FROM `Order` ORDER BY " + sortBy + " " + sortOrder;
        PreparedStatement stmt = conn.prepareStatement(query);
        ArrayList<Order> listOrder = new ArrayList<>();
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            listOrder.add(extractOrderFromResultSet(resultSet));
        }
        resultSet.close();
        return listOrder;
    }

        // Method to list orders by a specific user, sorted by the specified column and order

    public ArrayList<Order> listOrdersByUserId(int userId, String sortBy, String sortOrder) throws SQLException {
        String query = "SELECT * FROM `Order` WHERE UserID = ? ORDER BY " + sortBy + " " + sortOrder;
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ArrayList<Order> listOrder = new ArrayList<>();
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            listOrder.add(new Order(
                resultSet.getInt("OrderID"),
                resultSet.getInt("UserID"),
                resultSet.getObject("Order_Date", LocalDateTime.class),
                resultSet.getString("Order_Status"),
                resultSet.getString("Delivery_address"),
                resultSet.getString("Quantity")
            ));
        }
        resultSet.close();
        stmt.close();
        return listOrder;
    }
    
    // Method to search for orders by user ID and search criteria, sorted by the specified column and order

    public List<Order> searchOrdersByUserId(int userId, String searchType, String searchTerm, String sortBy,
            String sortOrder) throws SQLException {
        String query = "SELECT * FROM `Order` WHERE UserID = ? AND " + searchType + " LIKE ? ORDER BY " + sortBy + " "
                + sortOrder;
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        stmt.setString(2, "%" + searchTerm + "%");
        List<Order> listOrder = new ArrayList<>();
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            listOrder.add(extractOrderFromResultSet(resultSet));
        }
        resultSet.close();
        stmt.close();
        return listOrder;
    }

        // Helper method to extract Order object from ResultSet

    public List<Order> getOrderListByUserId(int userId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM `Order` WHERE UserID = ?"; // Adjust the query to match your table structure
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            orders.add(extractOrderFromResultSet(rs));
        }
        rs.close();
        stmt.close();
        return orders;
    }
    
    
    // Updates an existing order in the database

    public boolean updateOrder(Order order) throws SQLException {
        updateStmt.setInt(1, order.getUserId());
        updateStmt.setObject(2, order.getOrderDate());
        updateStmt.setString(3, order.getOrderStatus());
        updateStmt.setString(4, order.getDeliveryAddress());
        updateStmt.setString(5, order.getQuantity());
        updateStmt.setInt(6, order.getOrderId());
        return updateStmt.executeUpdate() > 0;
    }

    public boolean deleteOrder(int orderId) throws SQLException {
        deleteStmt.setInt(1, orderId);
        return deleteStmt.executeUpdate() > 0;
    }

        // Retrieves a specific order by its ID

    public Order getOrder(int id) throws SQLException {
        selectStmt.setInt(1, id);
        try (ResultSet resultSet = selectStmt.executeQuery()) {
            if (resultSet.next()) {
                return extractOrderFromResultSet(resultSet);
            }
        }
        return null;
    }

    
    // Extracts order data from the ResultSet and returns an Order object

    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        return new Order(
            rs.getInt("OrderID"),
            rs.getInt("UserID"),
            rs.getObject("Order_Date", LocalDateTime.class),
            rs.getString("Order_Status"),
            rs.getString("Delivery_address"),
            rs.getString("Quantity")
        );
    }

        // Method to save the current state of the cart for a specific user

    public void saveCart(int userId, Cart cart) throws SQLException {
        String query = "INSERT INTO SavedCarts (userId, productId, quantity, savedDate) VALUES (?, ?, ?, NOW())";
        PreparedStatement stmt = conn.prepareStatement(query);

                // Iterate through each item in the cart and set the parameters for the prepared statement

        for (CartItem item : cart.getItems()) {
            stmt.setInt(1, userId);
            stmt.setInt(2, item.getProduct().getProductid());
            stmt.setInt(3, item.getQuantity());
            stmt.executeUpdate();
        }

        stmt.close();
    }
    
    
    
}