package moe.nea.libautoupdate.targets;

import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.UpdateException;
import moe.nea.libautoupdate.api.UpdateTarget;
import moe.nea.libautoupdate.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class DeleteAndSaveInSameFolderUpdateTarget
        implements UpdateTarget {
    private final File file;

    public DeleteAndSaveInSameFolderUpdateTarget(File file) {
        this.file = file;
    }

    @Override
    public List<UpdateAction> generateUpdateActions(
            PotentialUpdate update
    ) {
        try {
            File parentFolder = file.getParentFile();
            String fileName = update.getFileName();
            File targetFile =
                    FileUtils.resolveContained(parentFolder, fileName);

            return List.of(
                    new UpdateAction.DeleteFile(file),
                    new UpdateAction.MoveFile(
                            update.getUpdateJarStorage(), targetFile
                    )
            );
        } catch (URISyntaxException | IOException e) {
            throw new UpdateException(
                    "Failed to resolve update target path", e
            );
        }
    }
}