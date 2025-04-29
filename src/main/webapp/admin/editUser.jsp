<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="uts.isd.model.User"%>
<%
    String role = (String) session.getAttribute("role");
    if (role == null || (!role.equals("Admin"))) {
        response.sendRedirect("/unauthorized.jsp"); 
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit User</title>
    <link rel="stylesheet" href="/css/admin.css">

    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 0;
        }

        .content {
            max-width: 600px;
            margin: 50px auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }

        .user-form {
            display: flex;
            flex-direction: column;
        }

        .user-form label {
            margin: 10px 0 5px 0;
            color: #34495e;
        }

        .user-form input,
        .user-form select {
            padding: 10px;
            border: 1px solid #bdc3c7;
            border-radius: 5px;
            margin-bottom: 10px;
        }

        .btn {
            padding: 10px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        .btn-primary {
            background-color: #3498db;
            color: white;
        }

        .btn-primary:hover {
            background-color: #2980b9;
        }

    </style>
</head>
<body>
    <nav>
        <ul>
            <li><a href="/admin.jsp">AdminPage</a></li>
            <li><a href="/admin/createUser.jsp">Create User</a></li>
            <li><a href="/admin/viewUsers.jsp">View Users</a></li>
            <li><a href="/admin/searchUsers.jsp">Search Users</a></li>
            <li><a href="/productlistadmin">View Products</a></li>
            <li><a href="/admin/addProduct.jsp">Add Products</a></li>
            <li><a href="/LogoutServlet">Logout</a></li>
        </ul>
    </nav>

     <% 
        String emailErr = (String) session.getAttribute("emailErr");
        String nametypeErr = (String) session.getAttribute("nametypeErr");
        String nullErr = (String) session.getAttribute("nullErr");
        String phoneErr = (String) session.getAttribute("phoneErr");
        String userexistsErr = (String) session.getAttribute("userexistsErr");
    %>

    <div class="content">
        <h1>Edit User</h1>
        <p>Update the user details below:</p>

        <% if (nullErr != null) { %>
            <div class="alert alert-danger"><%= nullErr %></div>
        <% } %>
        <% if(userexistsErr != null) { %>
            <div class="alert alert-danger"><%= userexistsErr
            %></div>
        <% } %>

        <% User user = (User) request.getAttribute("user"); %>
        <form action="/AdminEditUserServlet" method="post" class="user-form">
            <input type="hidden" name="userId" value="<%= user.getUserID() %>">

            <label for="first-name">Email:</label>
            <input type="text" id="email" name="email" value="<%= user.getEmail() %>" required>
            <% if(emailErr != null) { %>
                <div class="alert alert-danger"><%= emailErr %></div>
            <% } %>

            <label for="first-name">First Name:</label>
            <input type="text" id="first-name" name="firstName" value="<%= user.getfirstName() %>" required>

            <label for="last-name">Last Name:</label>
            <input type="text" id="last-name" name="lastName" value="<%= user.getlastname() %>" required>
            <% if(nametypeErr != null) { %>
                <div class="alert alert-danger"><%= nametypeErr %></div>
            <% } %>

            <label for="phone-number">Phone Number:</label>
            <input type="number" id="phone-number" name="phone" value="<%= user.getPhone() %>" required>
            <% if(phoneErr != null) { %>
                <div class="alert alert-danger"><%= phoneErr %></div>
            <% } %>

            <label for="gender">Gender:</label>
            <select id="gender" name="gender" required>
                <option value="male" <%= user.getGender().equals("male") ? "selected" : "" %>>Male</option>
                <option value="female" <%= user.getGender().equals("female") ? "selected" : "" %>>Female</option>
                <option value="other" <%= user.getGender().equals("other") ? "selected" : "" %>>Other</option>
            </select>

            <label for="role">Role:</label>
            <% if (!user.getRole().equals("Admin")) { %>
                <select id="role" name="role" required>
                    <option value="Customer" <%= user.getRole().equals("Customer") ? "selected" : "" %>>Customer</option>
                    <option value="Staff" <%= user.getRole().equals("Staff") ? "selected" : "" %>>Staff</option>
                </select>
            <% } 
            else {%>
                <select id="role" name="role" required>
                    <option value="Admin" <%= user.getRole().equals("Admin") ? "selected" : "" %>>Admin</option>
                </select>
            <% } %>

            <label for="isActivated">Activated:</label>
            <select id="isActivated" name="isActivated" required>
                <option value="true" <%= user.getIsActivated() ? "selected" : "" %>>True</option>
                <option value="false" <%= !user.getIsActivated() ? "selected" : "" %>>False</option>
            </select>

            <button type="submit" class="btn btn-primary">Update User</button>
        </form>
    </div>

</body>
</html>
