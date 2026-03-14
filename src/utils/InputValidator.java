package utils;

import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^(?=.*[a-zA-Zа-яА-ЯёЁ])[a-zA-Zа-яА-ЯёЁ\\s-]+$"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]+$"
    );

    private static final Pattern VIN_PATTERN = Pattern.compile(
            "^[A-HJ-NPR-Z0-9]{17}$"
    );

    private static final Pattern LICENSE_PLATE_PATTERN = Pattern.compile(
            "^[АВЕКМНОРСТУХABEKMHOPCTYX]\\d{3}[АВЕКМНОРСТУХABEKMHOPCTYX]{2}\\d{2,3}$"
    );

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        return NAME_PATTERN.matcher(name).matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidVin(String vin) {
        if (vin == null || vin.trim().isEmpty()) return false;
        return VIN_PATTERN.matcher(vin.toUpperCase()).matches();
    }

    public static boolean isValidLicensePlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) return false;
        return LICENSE_PLATE_PATTERN.matcher(plate.toUpperCase()).matches();
    }

    public static String validateName(String name) {
        if (!isValidName(name)) {
            return "ФИО должно содержать хотя бы одну букву. Можно использовать буквы, пробелы и дефисы";
        }

        if (name.replaceAll("[\\s-]", "").isEmpty()) {
            return "ФИО не может состоять только из пробелов и дефисов";
        }

        return null;
    }

    public static String validateEmail(String email) {
        if (!isValidEmail(email)) {
            return "Введите корректный email (пример: user@mail.ru)";
        }
        return null;
    }

    public static String validatePhone(String phone) {
        if (!isValidPhone(phone)) {
            return "Телефон может содержать только цифры и знак + в начале";
        }
        return null;
    }
}