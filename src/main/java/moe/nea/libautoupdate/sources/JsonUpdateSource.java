package moe.nea.libautoupdate.sources;

import com.google.gson.Gson;
import moe.nea.libautoupdate.UpdateSource;
import moe.nea.libautoupdate.UpdateUtils;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;


public abstract class JsonUpdateSource implements UpdateSource {

    protected Gson getGson() {
        return UpdateUtils.gson;
    }

    protected <T> CompletableFuture<T> getJsonFromURL(String url, Type clazz) {
        return UpdateUtils.httpGet(url, getGson(), clazz);
    }
}
