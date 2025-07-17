package bean;

import com.google.gson.JsonObject;

public class ServicesModel implements Bean {
    private String serviceId;
    private String serviceName;
    private String serviceDescription;
    private double servicePrice;
   

  
    public ServicesModel() {
		
	}

    public ServicesModel(String serviceId, String serviceName, String serviceDescription, double servicePrice) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;
        this.servicePrice = servicePrice;
    }


	// Getters and setters
    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return serviceDescription;
    }

    public void setDescription(String description) {
        this.serviceDescription = description;
    }

    public double getPrice() {
        return servicePrice;
    }

    public void setPrice(double price) {
        this.servicePrice = price;
    }

 

    @Override
    public String toString() {
        return serviceName + " - " + (int) servicePrice + " VNĐ";
    }


	@Override
    public JsonObject toJsonObject() {
        JsonObject obj = new JsonObject();
        obj.addProperty("serviceId", serviceId);
        obj.addProperty("serviceName", serviceName);
        obj.addProperty("description", serviceDescription);
        obj.addProperty("price", servicePrice);
        
        return obj;
    }

    @Override
    public void parse(JsonObject obj) throws Exception {
        if (obj.has("serviceId")) serviceId = obj.get("serviceId").getAsString();
        if (obj.has("serviceName")) serviceName = obj.get("serviceName").getAsString();
        if (obj.has("description")) serviceDescription = obj.get("description").getAsString();
        if (obj.has("price")) servicePrice = obj.get("price").getAsDouble();
      
    }
}
