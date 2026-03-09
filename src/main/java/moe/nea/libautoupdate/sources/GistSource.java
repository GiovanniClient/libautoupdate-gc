package moe.nea.libautoupdate.sources;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import moe.nea.libautoupdate.UpdateData;

import java.util.concurrent.CompletableFuture;

@Value
@EqualsAndHashCode(callSuper = false)
public class GistSource extends JsonUpdateSource {
    @NonNull
    String owner;
    @NonNull
    String gistId;
    private static final String GIST_RAW_URL = "https://gist.githubusercontent.com/%s/%s/raw/%s.json";
    
    public GistSource(String owner, String gistId) {
        this.owner = owner;
        this.gistId = gistId;
    }

    @Override
    public CompletableFuture<UpdateData> checkUpdate(String updateStream) {
        return getJsonFromURL(String.format(GIST_RAW_URL, owner, gistId, updateStream), UpdateData.class);
    }
}
