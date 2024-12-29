package models;

import utils.Connect;
import utils.Response;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Vendor extends User {
    private final static Connect db = Connect.getInstance();

    public Vendor(String userId, String userEmail, String username, String userPassword) {
        super(userId, userEmail, username, userPassword, "Vendor");
    }

    // mengubah status invitation menjadi 1 yang artinya accept
    public Response<Void> acceptInvitation(String eventId) {
        String query = "UPDATE invitations SET invitation_status = 1 WHERE user_id = ? AND event_id = ?";
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, this.getUserId());
            ps.setString(2, eventId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return Response.success("Invitation accepted successfully.", null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Failed to accept invitation: " + e.getMessage());
        }
        return Response.error("Failed to accept invitation. No matching record found.");
    }

    // melihat daftar acara yang telah diterima oleh vendor berdasarkan email.
    public static Response<List<Event>> viewAcceptedEvents(String email) {
        String query = "SELECT e.event_id, e.event_name, e.event_date, e.event_location, e.event_description, e.organizer_id " +
                "FROM events e JOIN invitations i ON e.event_id = i.event_id " +
                "JOIN users u ON u.user_id = i.user_id " +
                "WHERE u.user_email = ? AND i.invitation_role = 'Vendor' AND i.invitation_status = 1";

        List<Event> events = new ArrayList<>();
        try (PreparedStatement ps = db.preparedStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String eventId = rs.getString("event_id");
                String eventName = rs.getString("event_name");
                String eventDate = rs.getString("event_date");
                String eventLocation = rs.getString("event_location");
                String eventDescription = rs.getString("event_description");
                String organizerId = rs.getString("organizer_id");

                events.add(new Event(eventId, eventName, eventDate, eventLocation, eventDescription, organizerId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching accepted events: " + e.getMessage());
        }

        return Response.success("Fetched accepted events successfully", events);
    }

	 // Metode untuk mengelola data vendor (nama produk dan deskripsi) dalam tabel `vendors`.
	 // Jika vendor sudah ada, data akan bisa diperbarui. Jika tidak, data baru akan ditambahkan.
    // asumsi 1 vendor memiliki 1 product
    public static Response<Void> manageVendor(String vendorId, String description, String product) {
        PreparedStatement ps = db.preparedStatement(
            "INSERT INTO vendors (vendor_id, product_name, product_description) "
            + "VALUES (?, ?, ?) "
            + "ON DUPLICATE KEY UPDATE "
            + "product_name = VALUES(product_name), "
            + "product_description = VALUES(product_description)"
        );

        try {
            ps.setString(1, vendorId);
            ps.setString(2, product);
            ps.setString(3, description);
            ps.executeUpdate();

            return Response.success("Manage vendor success", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Manage vendor failed: " + e.getMessage());
        }
    }
    
    // mengambil data produk vendor berdasarkan vendorId.
    public static Response<VendorProduct> getProduct(String vendorId) {
        ResultSet rs = db.execQuery(
            String.format(
                "SELECT product_name, product_description "
                + "FROM vendors "
                + "WHERE vendor_id = '%s'",
                vendorId
            )
        );

        try {
            if (rs.next()) {
                String name = rs.getString("product_name");
                String description = rs.getString("product_description");

                return Response.success("Fetch product success", new VendorProduct(name, description));
            }
            return Response.success("No product", null);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.error("Error fetching product: " + e.getMessage());
        }
    }

    // validasi input untuk data nama dan deskripsi product
    public static Response<Void> checkManageVendorInput(String description, String product) {
        if (description.isEmpty()) {
            return Response.error("Product description must be filled");
        }
        if (product.isEmpty()) {
            return Response.error("Product name must be filled");
        }
        if (description.length() > 200) {
            return Response.error("Product description is maximum 200 characters long");
        }

        return Response.success("", null);
    }

}
