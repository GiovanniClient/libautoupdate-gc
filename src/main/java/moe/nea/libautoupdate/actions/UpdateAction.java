package moe.nea.libautoupdate.actions;

import java.io.File;
import java.util.List;
import java.util.Objects;

public abstract class UpdateAction {
    protected UpdateAction() {
    }

    public abstract void encode(List<String> arguments);

    public static class DeleteFile extends UpdateAction {
        private final File toDelete;

        public DeleteFile(File toDelete) {
            this.toDelete = Objects.requireNonNull(toDelete);
        }

        @Override
        public void encode(List<String> arguments) {
            arguments.add("delete");
            arguments.add(toDelete.getAbsolutePath());
        }
    }

    public static class MoveDownloadedFile extends UpdateAction {
        private final File whereFrom;
        private final File whereTo;

        public MoveDownloadedFile(File whereFrom, File whereTo) {
            this.whereFrom = Objects.requireNonNull(whereFrom);
            this.whereTo = Objects.requireNonNull(whereTo);
        }

        @Override
        public void encode(List<String> arguments) {
            arguments.add("move");
            arguments.add(whereFrom.getAbsolutePath());
            arguments.add(whereTo.getAbsolutePath());
        }
    }
}