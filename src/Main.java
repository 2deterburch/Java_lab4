
import java.time.LocalDate;

interface Expirable {
    boolean isValid();
}

// Абстрактний клас знижки
abstract class Discount {
    public abstract double apply(double price);
    public abstract String getName();
}

// Сезонна знижка 10%
class SeasonalDiscount extends Discount {
    @Override
    public double apply(double price) {
        return price * 0.90;
    }

    @Override
    public String getName() {
        return "Сезонна знижка 10%";
    }
}

// VIP-знижка 20%
class VipDiscount extends Discount {
    @Override
    public double apply(double price) {
        return price * 0.80;
    }

    @Override
    public String getName() {
        return "VIP-знижка 20%";
    }
}

// Купон зі строком дії
class CouponDiscount extends Discount implements Expirable {
    private final double amount;
    private final LocalDate expiryDate;

    public CouponDiscount(double amount, LocalDate expiryDate) {
        this.amount = amount;
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean isValid() {
        return !LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public double apply(double price) {
        return Math.max(0, price - amount);
    }

    @Override
    public String getName() {
        return "Купон на " + amount + " грн";
    }
}

// Інтерфейс програми лояльності
interface LoyaltyProgram {
    double applyBonus(double price);
    String getName();
}

// Без додаткового бонусу
class BasicLoyalty implements LoyaltyProgram {
    @Override
    public double applyBonus(double price) {
        return price;
    }

    @Override
    public String getName() {
        return "Базова програма";
    }
}

// Додатковий бонус 5%
class PremiumLoyalty implements LoyaltyProgram {
    @Override
    public double applyBonus(double price) {
        return price * 0.95;
    }

    @Override
    public String getName() {
        return "Преміум-програма 5%";
    }
}

// Центральний сервіс
class DiscountService {
    private final Discount discount;
    private final LoyaltyProgram loyalty;

    public DiscountService(Discount discount, LoyaltyProgram loyalty) {
        this.discount = discount;
        this.loyalty = loyalty;
    }

    public void calculate(double price) {
        System.out.println("Тип: " + discount.getName());
        System.out.println("Лояльність: " + loyalty.getName());
        System.out.printf("Початкова ціна: %.2f грн%n", price);

        double result = price;

        if (discount instanceof Expirable) {
            Expirable expirable = (Expirable) discount;

            if (!expirable.isValid()) {
                System.out.println("Термін дії купона закінчився!");
            } else {
                result = discount.apply(price);
            }
        } else {
            result = discount.apply(price);
        }

        result = loyalty.applyBonus(result);

        System.out.printf("До сплати: %.2f грн%n%n", result);
    }
}

public class Main {
    public static void main(String[] args) {

        double price = 1000;

        // Конфігурація 1
        System.out.println("=== СЕЗОННА ЗНИЖКА ===");
        DiscountService service1 = new DiscountService(
                new SeasonalDiscount(),
                new BasicLoyalty()
        );
        service1.calculate(price);

        // Конфігурація 2
        System.out.println("=== VIP-ЗНИЖКА ===");
        DiscountService service2 = new DiscountService(
                new VipDiscount(),
                new PremiumLoyalty()
        );
        service2.calculate(price);

        // Конфігурація 3
        System.out.println("=== КУПОН ===");
        DiscountService service3 = new DiscountService(
                new CouponDiscount(150, LocalDate.now().plusDays(10)),
                new BasicLoyalty()
        );
        service3.calculate(price);

        // Конфігурація 4
        System.out.println("=== ПРОСТРОЧЕНИЙ КУПОН ===");
        DiscountService service4 = new DiscountService(
                new CouponDiscount(150, LocalDate.now().minusDays(1)),
                new BasicLoyalty()
        );
        service4.calculate(price);

        // Конфігурація 5
        System.out.println("=== КУПОН + ПРЕМІУМ ===");
        DiscountService service5 = new DiscountService(
                new CouponDiscount(150, LocalDate.now().plusDays(10)),
                new PremiumLoyalty()
        );
        service5.calculate(price);
    }
}
