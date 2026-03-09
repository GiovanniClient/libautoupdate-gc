package moe.nea.libautoupdate.sources;

import moe.nea.libautoupdate.UpdateData;

import java.util.concurrent.CompletableFuture;

public class GistSource extends JsonUpdateSource {
    String owner;
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
