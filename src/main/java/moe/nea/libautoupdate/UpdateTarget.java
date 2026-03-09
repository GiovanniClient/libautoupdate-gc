package moe.nea.libautoupdate;

import moe.nea.libautoupdate.actions.UpdateAction;
import moe.nea.libautoupdate.targets.DeleteAndSaveInSameFolderUpdateTarget;
import moe.nea.libautoupdate.targets.ReplaceJarUpdateTarget;

import java.io.File;
import java.util.List;

/**
 * Interface for generating post download actions for installing the update Jar.
 */
public interface UpdateTarget {
    List<UpdateAction> generateUpdateActions(PotentialUpdate update);

    static UpdateTarget replaceJar(Class<?> containedClass) {
        File file = UpdateUtils.getJarFileContainingClass(containedClass);
        return new ReplaceJarUpdateTarget(file);
    }

    /**
     * Create an update target that deletes the Jar containing the specified class, and saves the new Jar in the same
     */
    static UpdateTarget deleteAndSaveInTheSameFolder(Class<?> containedClass) {
        File file = UpdateUtils.getJarFileContainingClass(containedClass);
        return new DeleteAndSaveInSameFolderUpdateTarget(file);
    }

}
