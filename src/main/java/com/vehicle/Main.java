package com.vehicle;
import com.vehicle.model.User;
import com.vehicle.model.Vehicle;
import com.vehicle.model.enums.UserType;
import com.vehicle.model.enums.VehicleType;
import com.vehicle.model.enums.RentalPeriod;
import com.vehicle.model.Rental;
import com.vehicle.service.RentalService;
import com.vehicle.service.UserService;
import com.vehicle.service.VehicleService;
import com.vehicle.exception.AuthException;
import com.vehicle.exception.RentalException;
import com.vehicle.exception.VehicleNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    private static User currentUser = null;
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final VehicleService vehicleService = new VehicleService();
    private static final RentalService rentalService = new RentalService();

    public static void main(String[] args) {
        System.out.println("Araç Kiralama Sistemine Hoş Geldiniz!");

        while (true) {
            try {
                if (currentUser == null) {
                    showMainMenu();
                } else {
                    if (UserType.ADMIN.equals(currentUser.getUserType())) {
                        showAdminMenu();
                    } else {
                        showUserMenu();
                    }
                }
            } catch (Exception e) {
                System.out.println("Beklenmeyen bir hata oluştu: " + e.getMessage());
                scanner.nextLine(); // Hata sonrası input buffer'ı temizle
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n1. Giriş Yap");
        System.out.println("2. Kayıt Ol");
        System.out.println("3. Çıkış");
        System.out.print("Seçiminiz: ");

        try {
            int choice = Integer.parseInt(scanner.nextLine());

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
                    System.out.println("Geçersiz seçim! Lütfen 1-3 arası bir sayı girin.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Hata: Geçerli bir sayı giriniz!");
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    private static void login() {
        try {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                throw new AuthException("Email boş olamaz");
            }

            System.out.print("Şifre: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                throw new AuthException("Şifre boş olamaz");
            }

            currentUser = userService.login(email, password);
            if (currentUser == null) {
                throw new AuthException("Giriş bilgileri hatalı");
            }
            System.out.println("Başarıyla giriş yapıldı. Hoş geldiniz, " + currentUser.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Giriş hatası: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }

    private static void register() {
        try {
            System.out.print("Ad Soyad: ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                throw new AuthException("Ad soyad boş olamaz");
            }

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) {
                throw new AuthException("Email boş olamaz");
            }

            System.out.print("Şifre: ");
            String password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                throw new AuthException("Şifre boş olamaz");
            }

            System.out.print("Kullanıcı Türü (ADMIN/CUSTOMER): ");
            UserType userType;
            try {
                userType = UserType.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AuthException("Geçerli bir kullanıcı türü girin (ADMIN/CUSTOMER)");
            }

            System.out.print("Doğum Tarihi (YYYY-MM-DD): ");
            LocalDate birthDate;
            try {
                birthDate = LocalDate.parse(scanner.nextLine().trim());
                if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
                    throw new AuthException("18 yaşından küçükler kayıt olamaz");
                }
            } catch (DateTimeParseException e) {
                throw new AuthException("Geçerli bir tarih formatı girin (YYYY-MM-DD)");
            }

            currentUser = userService.register(name, email, password, userType, birthDate);
            if (currentUser == null) {
                throw new AuthException("Kayıt işlemi başarısız oldu");
            }
            System.out.println("Kayıt başarılı. Hoş geldiniz, " + currentUser.getName() + "!");
        } catch (AuthException e) {
            System.out.println("Kayıt hatası: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }

    private static void showAdminMenu() {
        while (currentUser != null && UserType.ADMIN.equals(currentUser.getUserType())) {
            System.out.println("\nADMIN MENÜ");
            System.out.println("1. Araç Ekle");
            System.out.println("2. Araçları Listele");
            System.out.println("3. Çıkış Yap");
            System.out.print("Seçiminiz: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

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
                        return;
                    default:
                        System.out.println("Geçersiz seçim! Lütfen 1-3 arası bir sayı girin.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Hata: Geçerli bir sayı giriniz!");
            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }
    }

    private static void addVehicle() {
        try {
            System.out.print("Plaka: ");
            String plateNumber = scanner.nextLine().trim();
            if (plateNumber.isEmpty()) {
                throw new IllegalArgumentException("Plaka boş olamaz");
            }

            System.out.print("Marka: ");
            String brand = scanner.nextLine().trim();
            if (brand.isEmpty()) {
                throw new IllegalArgumentException("Marka boş olamaz");
            }

            System.out.print("Model: ");
            String model = scanner.nextLine().trim();
            if (model.isEmpty()) {
                throw new IllegalArgumentException("Model boş olamaz");
            }

            System.out.print("Yıl: ");
            int year;
            try {
                year = Integer.parseInt(scanner.nextLine().trim());
                if (year < 1900 || year > LocalDate.now().getYear() + 1) {
                    throw new IllegalArgumentException("Geçersiz yıl (1900-" + (LocalDate.now().getYear() + 1) + " arasında olmalı)");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Yıl sayı olmalıdır");
            }

            System.out.print("Araç Türü (CAR/MOTORCYCLE/HELICOPTER): ");
            VehicleType type;
            try {
                type = VehicleType.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Geçerli bir araç türü girin (CAR/MOTORCYCLE/HELICOPTER)");
            }

            System.out.print("Fiyat: ");
            BigDecimal price;
            try {
                price = new BigDecimal(scanner.nextLine().trim());
                if (price.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Fiyat pozitif olmalıdır");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Geçerli bir fiyat girin");
            }

            Vehicle vehicle = new Vehicle();
            vehicle.setPlateNumber(plateNumber);
            vehicle.setBrand(brand);
            vehicle.setModel(model);
            vehicle.setYear(year);
            vehicle.setVehicleType(type);
            vehicle.setPrice(price);
            vehicle.setAvailable(true);

            boolean result = vehicleService.addVehicle(vehicle);
            if (!result) {
                throw new Exception("Araç eklenirken hata oluştu");
            }
            System.out.println("Araç başarıyla eklendi.");
        } catch (IllegalArgumentException e) {
            System.out.println("Hata: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }

    private static void showUserMenu() {
        while (currentUser != null && UserType.CUSTOMER.equals(currentUser.getUserType())) {
            System.out.println("\nKULLANICI MENÜ");
            System.out.println("1. Araçları Listele");
            System.out.println("2. Araç Kirala");
            System.out.println("3. Kiralama Geçmişim");
            System.out.println("4. Çıkış Yap");
            System.out.print("Seçiminiz: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

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
                        return;
                    default:
                        System.out.println("Geçersiz seçim! Lütfen 1-4 arası bir sayı girin.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Hata: Geçerli bir sayı giriniz!");
            } catch (Exception e) {
                System.out.println("Hata: " + e.getMessage());
            }
        }
    }

    private static void listVehicles(int page) {
        if (page < 1) page = 1;

        try {
            System.out.println("\nAraç Listesi (Sayfa " + page + ")");
            System.out.println("1. Tüm Araçlar");
            System.out.println("2. Otomobiller");
            System.out.println("3. Motosikletler");
            System.out.println("4. Helikopterler");
            System.out.println("5. Önceki Sayfa");
            System.out.println("6. Sonraki Sayfa");
            System.out.println("7. Ana Menü");
            System.out.print("Seçiminiz: ");

            int choice = Integer.parseInt(scanner.nextLine());
            int pageSize = 5;
            List<Vehicle> vehicles;

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
                        return;
                    }
                    System.out.println("Zaten ilk sayfadasınız.");
                    break;
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
        } catch (NumberFormatException e) {
            System.out.println("Hata: Geçerli bir sayı giriniz!");
            listVehicles(page);
        } catch (VehicleNotFoundException e) {
            System.out.println("Hata: " + e.getMessage());
            listVehicles(page);
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
            listVehicles(page);
        }
    }

    private static void displayVehicles(List<Vehicle> vehicles) {
        if (vehicles == null || vehicles.isEmpty()) {
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
        try {
            System.out.print("Kiralanacak araç ID: ");
            int vehicleId;
            try {
                vehicleId = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Geçerli bir araç ID giriniz");
            }

            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            if (vehicle == null) {
                throw new VehicleNotFoundException("Araç bulunamadı");
            }
            if (!vehicle.isAvailable()) {
                System.out.println("Bu araç şu anda müsait değil.");
                return;
            }

            System.out.println("\nSeçilen Araç:");
            System.out.println(vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getPlateNumber() + ")");
            System.out.println("Günlük Kira Ücreti: " + vehicleService.calculateRentalPrice(vehicle, RentalPeriod.DAILY));

            System.out.print("\nKiralama Periyodu (HOURLY/DAILY/WEEKLY/MONTHLY): ");
            RentalPeriod period;
            try {
                period = RentalPeriod.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Geçerli bir kiralama periyodu girin (HOURLY/DAILY/WEEKLY/MONTHLY)");
            }

            System.out.print("Kiralama Başlangıç Tarihi (YYYY-MM-DDTHH:MM): ");
            LocalDateTime startDate;
            try {
                startDate = LocalDateTime.parse(scanner.nextLine().trim());
                if (startDate.isBefore(LocalDateTime.now())) {
                    throw new IllegalArgumentException("Başlangıç tarihi geçmiş bir tarih olamaz");
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Geçerli bir tarih formatı girin (YYYY-MM-DDTHH:MM)");
            }

            Rental rental = rentalService.createRental(currentUser, vehicle, period, startDate);
            if (rental == null) {
                throw new RentalException("Kiralama oluşturulamadı");
            }

            System.out.println("\nKiralama Başarılı!");
            System.out.println("Toplam Ücret: " + rental.getTotalPrice());
            if (rental.getDepositAmount() != null && rental.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Depozito: " + rental.getDepositAmount());
            }
            System.out.println("Kiralama Periyodu: " + rental.getRentalPeriod());
            System.out.println("Başlangıç: " + rental.getStartDate());
            System.out.println("Bitiş: " + rental.getEndDate());

        } catch (IllegalArgumentException | VehicleNotFoundException | RentalException e) {
            System.out.println("Hata: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Beklenmeyen bir hata oluştu: " + e.getMessage());
        }
    }

    private static void showRentalHistory() {
        try {
            List<Rental> rentals = rentalService.getUserRentals(currentUser.getId());
            if (rentals == null || rentals.isEmpty()) {
                System.out.println("Henüz kiralama geçmişiniz bulunmamaktadır.");
                return;
            }

            System.out.println("\nKiralama Geçmişiniz:");
            System.out.println("ID  | Araç               | Başlangıç          | Bitiş              | Periyod | Ücret     | Durum");
            System.out.println("----|--------------------|--------------------|--------------------|---------|-----------|--------");

            for (Rental rental : rentals) {
                System.out.printf("%-3d | %-18s | %-19s | %-19s | %-7s | %-9.2f | %s%n",
                        rental.getId(),
                        rental.getVehicleInfo() != null ? rental.getVehicleInfo() : "Bilinmiyor",
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getRentalPeriod(),
                        rental.getTotalPrice(),
                        rental.getStatus() != null ? rental.getStatus() : "Bilinmiyor");
            }

            System.out.println("\nDevam etmek için bir tuşa basın...");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Hata: " + e.getMessage());
        }
    }
}