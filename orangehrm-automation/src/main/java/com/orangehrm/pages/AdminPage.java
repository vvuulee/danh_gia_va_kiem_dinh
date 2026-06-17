package com.orangehrm.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * AdminPage - Page Object cho trang quản lý người dùng hệ thống
 * URL: /web/index.php/admin/viewSystemUsers
 */
public class AdminPage extends BasePage {

    @FindBy(css = "button.oxd-button--secondary[class*='add']")
    private WebElement addUserButton;

    @FindBy(css = "input.oxd-input--active")
    private WebElement searchUsernameField;

    @FindBy(xpath = "//button[normalize-space()='Search']")
    private WebElement searchButton;

    @FindBy(xpath = "//button[normalize-space()='Reset']")
    private WebElement resetButton;

    public boolean isAdminPageDisplayed() {
        return getCurrentUrl().contains("viewSystemUsers");
    }

    public AdminPage searchByUsername(String username) {
        type(searchUsernameField, username);
        click(searchButton);
        waitForLoadingSpinner();
        try {
            Thread.sleep(8000); // Vue.js lazy render cần thêm thời gian
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("🔍 Tìm kiếm user: {}", username);
        return this;
    }

    public AdminPage clickReset() {
    click(resetButton);
    waitForLoadingSpinner();
    try {
        Thread.sleep(4000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
    return this;
}

    public int getUserCount() {
        // Đợi table body render xong, sau đó đếm rows
        try {
            waitForPresence(By.cssSelector(".oxd-table-body"));
            Thread.sleep(2000); // Thêm 2s để đảm bảo lazy render
        } catch (Exception e) {
            logger.debug("Table body not present, returning 0");
        }
        return driver.findElements(
            By.cssSelector(".oxd-table-body .oxd-table-row")
        ).size();
    }

    public int getUserCountAfterSearch() {
        // Thêm 8s sleep để đảm bảo Vue.js lazy render xong
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return getUserCount();
    }

    public boolean isUserPresent(String username) {
        return driver.findElements(By.cssSelector(".oxd-table-body .oxd-table-row"))
            .stream().anyMatch(row -> row.getText().contains(username));
    }

    public boolean isNoRecordFound() {
        // OrangeHRM demo không hiển thị "No Records Found" text
        // Chỉ đơn giản trả về 0 rows
        int rowCount = getUserCount();
        boolean hasNoRecords = rowCount == 0;
        logger.info(hasNoRecords ? "ℹ️ Không có kết quả tìm kiếm" : "ℹ️ Tìm thấy {} kết quả", rowCount);
        return hasNoRecords;
    }
}