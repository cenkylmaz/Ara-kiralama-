📌 Proje Hakkında
Bu proje, bir araç kiralama sistemi için geliştirilmiş Java tabanlı bir konsol uygulamasıdır. Sistem, müşterilerin ve yöneticilerin araç kiralama işlemlerini yönetmelerine olanak tanır.

✨ Özellikler
Kullanıcı Yönetimi:

Kullanıcı kaydı ve girişi

Admin ve müşteri rolleri

Profil yönetimi

Araç Yönetimi:

Araç ekleme/güncelleme/silme (admin)

Araç listeleme ve filtreleme

Araç detay görüntüleme

Kiralama İşlemleri:

Araç kiralama

Kiralama geçmişi görüntüleme

Farklı kiralama periyotları (saatlik, günlük, haftalık, aylık)

🛠 Teknoloji Yığını
Programlama Dili: Java 11+

Veritabanı: (Projede kullanılıyorsa belirtin)

Bağımlılıklar:

Java Time API (Tarih işlemleri için)

BigDecimal (Finansal hesaplamalar için)

🚀 Kurulum
Projeyi klonlayın:

bash
git clone https://github.com/kullaniciadi/arac-kiralama-sistemi.git
Proje dizinine gidin:

bash
cd arac-kiralama-sistemi
Projeyi derleyin ve çalıştırın:

bash
javac Main.java
java Main
📋 Kullanım
Admin Giriş Bilgileri
Email: admin@admin.com
Şifre: admin123
Müşteri Giriş Bilgileri
Email: musteri@musteri.com
Şifre: musteri123
📂 Proje Yapısı
arac-kiralama-sistemi/
├── src/
│   ├── com/vehicle/
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Vehicle.java
│   │   │   ├── Rental.java
│   │   │   └── enums/
│   │   │       ├── UserType.java
│   │   │       ├── VehicleType.java
│   │   │       └── RentalPeriod.java
│   │   ├── service/
│   │   │   ├── UserService.java
│   │   │   ├── VehicleService.java
│   │   │   └── RentalService.java
│   │   └── exception/
│   │       ├── AuthException.java
│   │       ├── RentalException.java
│   │       └── VehicleNotFoundException.java
├── Main.java
└── README.md
📝 Lisans
Bu proje MIT lisansı altında lisanslanmıştır. Daha fazla bilgi için LICENSE dosyasına bakın.

🤝 Katkıda Bulunma
Katkıda bulunmak isterseniz:

Forklayın (https://github.com/kullaniciadi/arac-kiralama-sistemi/fork)

Yeni bir branch oluşturun (git checkout -b feature/fooBar)

Değişikliklerinizi commit edin (git commit -am 'Add some fooBar')

Push yapın (git push origin feature/fooBar)

Yeni bir Pull Request oluşturun

✉️ İletişim
Proje sahibi: Cenk 
Proje Linki: https://github.com/cenkylmz/arac-kiralama-sistemi
