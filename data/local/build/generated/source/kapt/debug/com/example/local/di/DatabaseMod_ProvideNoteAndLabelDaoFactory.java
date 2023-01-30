// Generated by Dagger (https://dagger.dev).
package com.example.local.di;

import com.example.local.Database;
import com.example.local.daos.NoteAndLabelDao;
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
public final class DatabaseMod_ProvideNoteAndLabelDaoFactory implements Factory<NoteAndLabelDao> {
  private final Provider<Database> databaseProvider;

  public DatabaseMod_ProvideNoteAndLabelDaoFactory(Provider<Database> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public NoteAndLabelDao get() {
    return provideNoteAndLabelDao(databaseProvider.get());
  }

  public static DatabaseMod_ProvideNoteAndLabelDaoFactory create(
      Provider<Database> databaseProvider) {
    return new DatabaseMod_ProvideNoteAndLabelDaoFactory(databaseProvider);
  }

  public static NoteAndLabelDao provideNoteAndLabelDao(Database database) {
    return Preconditions.checkNotNullFromProvides(DatabaseMod.INSTANCE.provideNoteAndLabelDao(database));
  }
}