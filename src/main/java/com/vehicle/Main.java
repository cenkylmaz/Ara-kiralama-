package com.vehicle;





import com.vehicle.model.User;
import com.vehicle.model.Vehicle;
import com.vehicle.service.RentalService;
import com.vehicle.service.UserService;
import com.vehicle.service.VehicleService;
import com.vehicle.exception.AuthException;
import com.vehicle.exception.RentalException;
import com.vehicle.exception.VehicleNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);
    private static UserService userService = new UserService();
    private static VehicleService vehicleService = new VehicleService();
    private static RentalService rentalService = new RentalService();

    public static void main(String[] args) {
        System.out.println("Araç Kiralama Sistemine Hoş Geldiniz!");

        while (true) {
            if (currentUser == null) {
                showMainMenu();
            } else {
                if (currentUser.getUserType() == UserType.ADMIN) {
                    showAdminMenu();
                } else {
                    showUserMenu();
                }
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n1. Giriş Yap");
        System.out.println("2. Kayıt Ol");
        System.out.println("3. Çıkış");
        System.out.print("Seçiminiz: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Çıkış yapılıyor...");
                System.exit(0);
                break;
            default:
                System.out.println("Geçersiz seçim!");
        }
    }

    private static void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Şifre: ");
        String password = scanner.nextLine();

        try {
            currentUser = userService.login(email, password);
            System.out.println("Başarıyla giriş yapıldı. Hoş geldiniz, " + currentUser.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Giriş hatası: " + e.getMessage());
        }
    }

    private static void register() {
        System.out.print("Ad Soyad: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Şifre: ");
        String password = scanner.nextLine();
        System.out.print("Kullanıcı Türü (INDIVIDUAL/CORPORATE): ");
        String userType = scanner.nextLine();
        System.out.print("Doğum Tarihi (YYYY-MM-DD): ");
        LocalDate birthDate = LocalDate.parse(scanner.nextLine());

        try {
            currentUser = userService.register(name, email, password, userType, birthDate);
            System.out.println("Kayıt başarılı. Hoş geldiniz, " + currentUser.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Kayıt hatası: " + e.getMessage());
        }
    }

    private static void showAdminMenu() {
        System.out.println("\nADMIN MENÜ");
        System.out.println("1. Araç Ekle");
        System.out.println("2. Araçları Listele");
        System.out.println("3. Çıkış Yap");
        System.out.print("Seçiminiz: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                addVehicle();
                break;
            case 2:
                listVehicles(1);
                break;
            case 3:
                currentUser = null;
                System.out.println("Çıkış yapıldı.");
                break;
            default:
                System.out.println("Geçersiz seçim!");
        }
    }

    private static void addVehicle() {
        System.out.print("Plaka: ");
        String plateNumber = scanner.nextLine();
        System.out.print("Marka: ");
        String brand = scanner.nextLine();
        System.out.print("Model: ");
        String model = scanner.nextLine();
        System.out.print("Yıl: ");
        int year = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Araç Türü (CAR/MOTORCYCLE/HELICOPTER): ");
        VehicleType type = VehicleType.valueOf(scanner.nextLine());
        System.out.print("Fiyat: ");
        BigDecimal price = scanner.nextBigDecimal();
        scanner.nextLine();

        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(plateNumber);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setVehicleType(type);
        vehicle.setPrice(price);
        vehicle.setAvailable(true);
        vehicle.setCreatedBy(currentUser.getId());

        try {
            if (vehicleService.addVehicle(vehicle)) {
                System.out.println("Araç başarıyla eklendi.");
            } else {
                System.out.println("Araç eklenirken hata oluştu.");
            }
        } catch (VehicleNotFoundException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    private static void showUserMenu() {
        System.out.println("\nKULLANICI MENÜ");
        System.out.println("1. Araçları Listele");
        System.out.println("2. Araç Kirala");
        System.out.println("3. Kiralama Geçmişim");
        System.out.println("4. Çıkış Yap");
        System.out.print("Seçiminiz: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                listVehicles(1);
                break;
            case 2:
                rentVehicle();
                break;
            case 3:
                showRentalHistory();
                break;
            case 4:
                currentUser = null;
                System.out.println("Çıkış yapıldı.");
                break;
            default:
                System.out.println("Geçersiz seçim!");
        }
    }

    private static void listVehicles(int page) {
        System.out.println("\nAraç Listesi (Sayfa " + page + ")");
        System.out.println("1. Tüm Araçlar");
        System.out.println("2. Otomobiller");
        System.out.println("3. Motosikletler");
        System.out.println("4. Helikopterler");
        System.out.println("5. Önceki Sayfa");
        System.out.println("6. Sonraki Sayfa");
        System.out.println("7. Ana Menü");
        System.out.print("Seçiminiz: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        List<Vehicle> vehicles;
        int pageSize = 5;

        try {
            switch (choice) {
                case 1:
                    vehicles = vehicleService.getAllVehicles(page, pageSize);
                    displayVehicles(vehicles);
                    break;
                case 2:
                    vehicles = vehicleService.getVehiclesByType(VehicleType.CAR, page, pageSize);
                    displayVehicles(vehicles);
                    break;
                case 3:
                    vehicles = vehicleService.getVehiclesByType(VehicleType.MOTORCYCLE, page, pageSize);
                    displayVehicles(vehicles);
                    break;
                case 4:
                    vehicles = vehicleService.getVehiclesByType(VehicleType.HELICOPTER, page, pageSize);
                    displayVehicles(vehicles);
                    break;
                case 5:
                    if (page > 1) {
                        listVehicles(page - 1);
                    } else {
                        System.out.println("Zaten ilk sayfadasınız.");
                        listVehicles(page);
                    }
                    return;
                case 6:
                    listVehicles(page + 1);
                    return;
                case 7:
                    return;
                default:
                    System.out.println("Geçersiz seçim!");
            }

            System.out.println("\nDevam etmek için bir tuşa basın...");
            scanner.nextLine();
            listVehicles(page);
        } catch (VehicleNotFoundException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    private static void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("Gösterilecek araç bulunamadı.");
            return;
        }

        System.out.println("\nID  | Plaka      | Marka-Model         | Yıl | Tür        | Fiyat      | Durum");
        System.out.println("----|------------|---------------------|-----|------------|------------|--------");
        for (Vehicle vehicle : vehicles) {
            System.out.printf("%-3d | %-10s | %-15s %-4d | %-10s | %-10.2f | %s%n",
                    vehicle.getId(),
                    vehicle.getPlateNumber(),
                    vehicle.getBrand() + " " + vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getVehicleType(),
                    vehicle.getPrice(),
                    vehicle.isAvailable() ? "Müsait" : "Kiralandı");
        }
    }

    private static void rentVehicle() {
        System.out.print("Kiralanacak araç ID: ");
        int vehicleId = scanner.nextInt();
        scanner.nextLine();

        try {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (!vehicle.isAvailable()) {
                System.out.println("Bu araç şu anda müsait değil.");
                return;
            }

            System.out.println("\nSeçilen Araç:");
            System.out.println(vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlateNumber() + ")");
            System.out.println("Günlük Kira Ücreti: " + vehicleService.calculateRentalPrice(vehicle, RentalPeriod.DAILY));

            System.out.print("\nKiralama Periyodu (HOURLY/DAILY/WEEKLY/MONTHLY): ");
            RentalPeriod period = RentalPeriod.valueOf(scanner.nextLine());

            System.out.print("Kiralama Başlangıç Tarihi (YYYY-MM-DDTHH:MM): ");
            LocalDateTime startDate = LocalDateTime.parse(scanner.nextLine());

            Rental rental = rentalService.createRental(currentUser, vehicle, period, startDate);

            System.out.println("\nKiralama Başarılı!");
            System.out.println("Toplam Ücret: " + rental.getTotalPrice());
            if (rental.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Depozito: " + rental.getDepositAmount());
            }
            System.out.println("Kiralama Periyodu: " + rental.getRentalPeriod());
            System.out.println("Başlangıç: " + rental.getStartDate());
            System.out.println("Bitiş: " + rental.getEndDate());

        } catch (VehicleNotFoundException | RentalException e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    private static void showRentalHistory() {
        try {
            List<Rental> rentals = rentalService.getUserRentals(currentUser.getId());

            if (rentals.isEmpty()) {
                System.out.println("Henüz kiralama geçmişiniz bulunmamaktadır.");
                return;
            }

            System.out.println("\nKiralama Geçmişiniz:");
            System.out.println("ID  | Araç               | Başlangıç          | Bitiş              | Periyod | Ücret     | Durum");
            System.out.println("----|--------------------|--------------------|--------------------|---------|-----------|--------");

            for (Rental rental : rentals) {
                System.out.printf("%-3d | %-18s | %-19s | %-19s | %-7s | %-9.2f | %s%n",
                        rental.getId(),
                        rental.getVehicleInfo(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getRentalPeriod(),
                        rental.getTotalPrice(),
                        rental.getStatus());
            }

            System.out.println("\nDevam etmek için bir tuşa basın...");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }
}