package org.openhab.binding.thekeys.internal.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.thekeys.internal.gateway.TheKeysGatewayConfiguration;
import org.openhab.core.io.net.http.HttpUtil;
import org.openhab.core.library.types.OnOffType;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@NonNullByDefault
public class GatewayApi {
    private final Gson gson;
    private final TheKeysGatewayConfiguration configuration;
    private static final int DEFAULT_API_TIMEOUT_MS = 15000;

    public GatewayApi(TheKeysGatewayConfiguration configuration) {
        this.configuration = configuration;
        this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    }

    public GatewayInfo getGwInfos() throws IOException {
        return get("/", GatewayInfo.class);
    }

    public Optional<Locker> getLock(int lockId) throws IOException {
        return getLocks().stream().filter(locker -> lockId == locker.getIdentifier()).findFirst();
    }

    public List<Locker> getLocks() throws IOException {
        return get("/lockers", Lockers.class).getDevices();
    }

    public LockerStatus getLockStatus(int lockId) throws IOException {
        return post("/locker_status", LockerStatus.class, lockId);
    }

    public <T> T get(String path, Class<T> responseType) throws IOException {
        String url = "http://" + configuration.hostname + path;
        String jsonResponse = HttpUtil.executeUrl("GET", url, DEFAULT_API_TIMEOUT_MS);
        return gson.fromJson(jsonResponse, responseType);
    }

    public <T> T post(String path, Class<T> responseType, int lockId) throws IOException {
        String url = "http://" + configuration.hostname + path;
        String jsonResponse = HttpUtil.executeUrl("POST", url, getRequestBody(lockId),
                "application/x-www-form-urlencoded", DEFAULT_API_TIMEOUT_MS);
        return gson.fromJson(jsonResponse, responseType);
    }

    public InputStream getRequestBody(int lockId) {
        try {
            String timestamp = String.valueOf(Instant.now().toEpochMilli() / 1000);
            String hash = hmacSha256(timestamp, configuration.code);
            String data = String.format("identifier=%s&ts=%s&hash=%s", lockId, timestamp, hash);
            return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new TheKeysError("Failed to compute the auth data", e);
        }
    }

    public OpenCloseResponse sendLockCommand(int lockId, OnOffType open) throws IOException {
        String url = open == OnOffType.ON ? "/open" : "/close";
        return post(url, OpenCloseResponse.class, lockId);
    }

    public static String hmacSha256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        String algorithm = "HmacSHA256";
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKeySpec);
        return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes()));
    }
}
