package controller;

public interface BaseController {
	// Khoi tao view ban dau
	public void constructorView();

	// Set cac event
	public void setOnAction();

	// set du lieu trong truong hop la TableView
	public void loadData();

	// RELOAD LAI DU LIEU BAN DAU CUA INIT
	public void refresh();

//	public void checkPermission();
}
