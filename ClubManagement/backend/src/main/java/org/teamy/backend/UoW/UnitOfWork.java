package org.teamy.backend.UoW;

public interface UnitOfWork {
    void commit() throws Exception;

    void clear();
}
