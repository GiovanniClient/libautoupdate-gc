package moe.nea.libautoupdate.targets;

import moe.nea.libautoupdate.PotentialUpdate;
import moe.nea.libautoupdate.api.UpdateTarget;

import java.io.File;
import java.util.List;

public class ReplaceJarUpdateTarget implements UpdateTarget {
    private final File file;

    public ReplaceJarUpdateTarget(File file) {
        this.file = file;
    }

    @Override
    public List<UpdateAction> generateUpdateActions(
            PotentialUpdate update
    ) {
        return List.of(
                new UpdateAction.DeleteFile(file),
                new UpdateAction.MoveFile(update.getUpdateJarStorage(), file)
        );
    }
}