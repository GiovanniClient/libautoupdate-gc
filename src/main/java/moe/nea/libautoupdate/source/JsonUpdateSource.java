package moe.nea.libautoupdate.sources;

import com.google.gson.Gson;
import moe.nea.libautoupdate.api.UpdateSource;
import moe.nea.libautoupdate.util.HttpClient;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public abstract class JsonUpdateSource implements UpdateSource {
    private static final Gson GSON = new Gson();

    protected Gson getGson() {
        return GSON;
    }

    protected <T> CompletableFuture<T> getJsonFromURL(
            String url, Type clazz
    ) {
        return CompletableFuture.supplyAsync(() -> {
            try (var is = HttpClient.open(
                    URI.create(url).toURL())) {
                return getGson().fromJson(
                        new InputStreamReader(is, StandardCharsets.UTF_8),
                        clazz
                );
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}