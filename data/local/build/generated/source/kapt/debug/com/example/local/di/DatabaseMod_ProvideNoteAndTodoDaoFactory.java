// Generated by Dagger (https://dagger.dev).
package com.example.local.di;

import com.example.local.Database;
import com.example.local.daos.NoteAndTodoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DatabaseMod_ProvideNoteAndTodoDaoFactory implements Factory<NoteAndTodoDao> {
  private final Provider<Database> databaseProvider;

  public DatabaseMod_ProvideNoteAndTodoDaoFactory(Provider<Database> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public NoteAndTodoDao get() {
    return provideNoteAndTodoDao(databaseProvider.get());
  }

  public static DatabaseMod_ProvideNoteAndTodoDaoFactory create(
      Provider<Database> databaseProvider) {
    return new DatabaseMod_ProvideNoteAndTodoDaoFactory(databaseProvider);
  }

  public static NoteAndTodoDao provideNoteAndTodoDao(Database database) {
    return Preconditions.checkNotNullFromProvides(DatabaseMod.INSTANCE.provideNoteAndTodoDao(database));
  }
}