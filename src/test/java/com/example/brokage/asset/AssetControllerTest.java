package com.example.brokage.asset;

import com.example.brokage.asset.model.Asset;
import com.example.brokage.asset.model.AssetNames;
import com.example.brokage.asset.model.DepositMoneyDto;
import com.example.brokage.asset.model.WithdrawMoneyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssetControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private AssetController assetController;


    @Test
    public void testContextLoaded() {

    }

    @Test
    public void testAssetControllerWired() {
        assertThat(assetController).isNotNull();
    }

    @Test
    public void testAdminCanListAllAssets() {
        ResponseEntity<Asset[]> response = this.restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset", Asset[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Asset[] assets = response.getBody();
        assertNotNull(assets);
        assertEquals(3, assets.length);
    }

    @Test
    public void testAdminCanGetAssetsOfCustomer1() {
        ResponseEntity<Asset[]> response = this.restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/1", Asset[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Asset[] assets = response.getBody();
        assertNotNull(assets);
        assertEquals(2, assets.length);
        if(assets[0].getId() == 1) {
            assertEqualsTRY_AssetCustomer1(assets[0]);
        }
        if(assets[1].getId() == 1) {
            assertEqualsTRY_AssetCustomer1(assets[1]);
        }
        if(assets[0].getId() == 2) {
            assertEqualsASSET3_AssetCustomer1(assets[0]);
        }
        if(assets[1].getId() == 2) {
            assertEqualsASSET3_AssetCustomer1(assets[1]);
        }
    }

    @Test
    public void testCustomer1CanGetAssetsOfCustomer1() {
        ResponseEntity<Asset[]> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/1", Asset[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Asset[] assets = response.getBody();
        assertNotNull(assets);
        assertEquals(2, assets.length);
        if(assets[0].getId() == 1) {
            assertEqualsTRY_AssetCustomer1(assets[0]);
        }
        if(assets[1].getId() == 1) {
            assertEqualsTRY_AssetCustomer1(assets[1]);
        }
        if(assets[0].getId() == 2) {
            assertEqualsASSET3_AssetCustomer1(assets[0]);
        }
        if(assets[1].getId() == 2) {
            assertEqualsASSET3_AssetCustomer1(assets[1]);
        }
    }

    @Test
    public void testCustomer1ForbiddenOfCustomer2sAssets() {
        ResponseEntity<Asset> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .getForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/2", Asset.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }



    @Test
    public void testAdminCanDepositAndWithdrawMoneyOfCustomer1() {
        DepositMoneyDto deposit = new DepositMoneyDto(1000.0);
        ResponseEntity<Double> response = this.restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/deposit/1", deposit, Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(21000.0, response.getBody());

        WithdrawMoneyDto withdraw = new WithdrawMoneyDto(1000.0, "TR330006100519786457841326");
        ResponseEntity<Double> response2 = this.restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/withdraw/1", withdraw, Double.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(20000.0, response2.getBody());
    }

    @Test
    public void testCustomer1CanDepositAndWithdrawMoneyOfCustomer1() {
        DepositMoneyDto deposit = new DepositMoneyDto(1000.0);
        ResponseEntity<Double> response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .postForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/deposit/1", deposit, Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(21000.0, response.getBody());

        WithdrawMoneyDto withdraw = new WithdrawMoneyDto(1000.0, "TR330006100519786457841326");
        ResponseEntity<Double> response2 = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .postForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/withdraw/1", withdraw, Double.class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals(20000.0, response2.getBody());
    }

    @Test
    public void testCustomer1CannotDepositMoneyOfCustomer2() {
        DepositMoneyDto deposit = new DepositMoneyDto(1000.0);
        ResponseEntity response = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .postForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/deposit/2", deposit, Object.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void testCustomer1CannotWithdrawMoneyOfCustomer2() {
        WithdrawMoneyDto withdraw = new WithdrawMoneyDto(1000.0, "TR330006100519786457841326");
        ResponseEntity response2 = this.restTemplate
                .withBasicAuth("customer1", "Pass1")
                .postForEntity("http://localhost:" + port +
                        "/brokage/api/v1/asset/withdraw/2", withdraw, Object.class);
        assertEquals(HttpStatus.FORBIDDEN, response2.getStatusCode());
    }


    private void assertEqualsTRY_AssetCustomer1(Asset asset) {
        assertEquals(1, asset.getId());
        assertEquals(1, asset.getCustomerId());
        assertEquals(AssetNames.TRY.name(), asset.getAssetName());
        assertEquals(20000, asset.getSize());
        assertEquals(19882.67, asset.getUsableSize());
    }

    private void assertEqualsASSET3_AssetCustomer1(Asset asset) {
        assertEquals(2, asset.getId());
        assertEquals(1, asset.getCustomerId());
        assertEquals("ASSET3", asset.getAssetName());
        assertEquals(5000, asset.getSize());
        assertEquals(5000, asset.getUsableSize());
    }

}