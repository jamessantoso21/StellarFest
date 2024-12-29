package controllers;

import java.util.List;

import models.Event;
import models.User;
import models.Vendor;
import models.VendorProduct;
import utils.Auth;
import utils.Response;

public class VendorController {
    public Response<Void> acceptInvitation(String eventId) {
        Vendor loggedInVendor = (Vendor) Auth.get();

        if (loggedInVendor == null || !loggedInVendor.getRole().equals("Vendor")) {
            return Response.error("Error accepting invitation: user is not a vendor.");
        }

        return loggedInVendor.acceptInvitation(eventId);
    }

    public static Response<List<Event>> viewAcceptedEvents(String email) {
        return Vendor.viewAcceptedEvents(email);
    }

    public static Response<Void> manageVendor(String description, String product) {
		User currentUser = Auth.get();
		Response<Void> checkResponse = checkManageVendorInput(description, product);
		
		if(!checkResponse.isSuccess()) {
			return Response.error(checkResponse.getMessage());
		}
		if(!currentUser.getRole().equals("Vendor")) {
			return Response.error("Error managing vendor: user is not a vendor");
		}
		
	    return Vendor.manageVendor(currentUser.getUserId(), description, product);
	}
	
	public static Response<VendorProduct> getProduct() {
		User currentUser = Auth.get();
		return Vendor.getProduct(currentUser.getUserId());
	}
	
	public static Response<Void> checkManageVendorInput(String description, String product) {
		return Vendor.checkManageVendorInput(description, product);
	}
}
