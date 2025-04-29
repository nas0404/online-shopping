package uts.isd.Controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uts.isd.model.Product;
import uts.isd.model.dao.DBConnector;
import uts.isd.model.dao.ProductDAO;

@WebServlet("/createproduct")
public class CreateProductServlet extends HttpServlet {
    private DBConnector db; 
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            db = new DBConnector();
            Connection conn = db.openConnection(); 
            productDAO = new ProductDAO(conn);
        } catch (ClassNotFoundException | SQLException e) {
            throw new ServletException("DBConnector initialization failed.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve form parameters
        String name = request.getParameter("name");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price").trim();
        String stockStr = request.getParameter("stock").trim();

        HttpSession session = request.getSession();
        StringBuilder errorMessage = new StringBuilder();

        double price = 0; 
        int stock = 0;  

        // Validate form parameters
        if (name.isEmpty() || category.isEmpty() || description.isEmpty() ||
            priceStr.isEmpty() || stockStr.isEmpty()) {
            errorMessage.append("Please fill in all the fields given. ");
        } else {
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    errorMessage.append("Price cannot be negative. ");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Price must be a valid number. ");
            }

            try {
                stock = Integer.parseInt(stockStr);
                if (stock < 0) {
                    errorMessage.append("Stock cannot be negative. ");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Stock must be a valid number. ");
            }
        }

        // Check for validation errors
        if (errorMessage.length() > 0) {
            session.setAttribute("createProductError", errorMessage.toString());
            response.sendRedirect("/admin/addProduct.jsp");
            return;
        }

        // Create and add new product
        Product product = new Product(0, name, category, description, price, stock);
        try {
            productDAO.addProduct(product);
            session.removeAttribute("createProductError");
            response.sendRedirect("/productlistadmin");
        } catch (SQLException e) {
            throw new ServletException("Error creating product", e);
        }
    }

    @Override
    public void destroy() {
        try {
            if (db != null) {
                db.closeConnection(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
