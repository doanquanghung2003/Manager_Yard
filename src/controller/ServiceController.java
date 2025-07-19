package controller;

import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import bean.ServicesModel;
import singleton.DuLieu;

public class ServiceController implements BaseController {

	private String serviceId;
	private String serviceName;
	private String serviceDescription;
	private double servicePrice;
	private String unit;

	private final Scanner sc = new Scanner(System.in);

	public void initialize() {
		while (true) {
			constructorView();
			setOnAction();
		}
	}

	@Override
	public void constructorView() {
		System.out.println("\n=== Quản lý dịch vụ sân ===");
		System.out.println("1. Thêm dịch vụ");
		System.out.println("2. In danh sách dịch vụ");
		System.out.println("0. Thoát");
		System.out.print("Chọn chức năng: ");
	}

	@Override
	public void setOnAction() {
		int choose;
		try {
			choose = Integer.parseInt(sc.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Vui lòng nhập một số hợp lệ.");
			return;
		}

		switch (choose) {
			case 1:
				handleAddService();
				break;
			case 2:
				showServices();
				break;
			case 0:
				System.out.println("Đã thoát chương trình quản lý dịch vụ.");
				System.exit(0);
				break;
			default:
				System.out.println("Lựa chọn không hợp lệ. Vui lòng thử lại.");
		}
	}

	public void handleAddService() {
		serviceId = UUID.randomUUID().toString();

		System.out.print("Nhập tên dịch vụ: ");
		serviceName = sc.nextLine();

		System.out.print("Mô tả dịch vụ: ");
		serviceDescription = sc.nextLine();

		System.out.print("Nhập giá dịch vụ: ");
		try {
			servicePrice = Double.parseDouble(sc.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Giá không hợp lệ. Thêm dịch vụ thất bại.");
			return;
		}

		ServicesModel service = new ServicesModel(serviceId, serviceName, serviceDescription, servicePrice);
		DuLieu.getInstance().addService(service);
		DuLieu.getInstance().saveServiceToFile("data/services.json");

		System.out.println("Thêm dịch vụ thành công!");
	}

	public void showServices() {
		DuLieu.getInstance().loadServicesFromFile("data/services.json");
		List<ServicesModel> services = DuLieu.getInstance().getServices();

		if (services == null || services.isEmpty()) {
			System.out.println(" Không có dịch vụ nào.");
			return;
		}

		System.out.println("\n=== DANH SÁCH DỊCH VỤ ===");
		for (ServicesModel service : services) {
			System.out.println(service);
		}
	}

	@Override
	public void loadData() {
		DuLieu.getInstance().loadServicesFromFile("data/services.json");
	}

	@Override
	public void refresh() {
		loadData();
	}
}
