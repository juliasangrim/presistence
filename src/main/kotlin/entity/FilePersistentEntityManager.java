package entity;

import kotlin.NotImplementedError;
import org.example.entity.PersistentEntity;
import org.example.entity.PersistentEntityManager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FilePersistentEntityManager implements PersistentEntityManager {

    @NotNull
    @Override
    public <T extends PersistentEntity> UUID save(@NotNull T entity) {
        throw new NotImplementedError();
    }

    @NotNull
    @Override
    public <T extends PersistentEntity> T get(@NotNull UUID id) {
        throw new NotImplementedError();
    }

    @Override
    public void delete(@NotNull UUID id) {
        throw new NotImplementedError();
    }
}
