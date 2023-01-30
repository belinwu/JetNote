// Generated by Dagger (https://dagger.dev).
package com.example.local.di;

import com.example.local.Database;
import com.example.local.daos.LabelDao;
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
public final class DatabaseMod_ProvideLabelDaoFactory implements Factory<LabelDao> {
  private final Provider<Database> databaseProvider;

  public DatabaseMod_ProvideLabelDaoFactory(Provider<Database> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public LabelDao get() {
    return provideLabelDao(databaseProvider.get());
  }

  public static DatabaseMod_ProvideLabelDaoFactory create(Provider<Database> databaseProvider) {
    return new DatabaseMod_ProvideLabelDaoFactory(databaseProvider);
  }

  public static LabelDao provideLabelDao(Database database) {
    return Preconditions.checkNotNullFromProvides(DatabaseMod.INSTANCE.provideLabelDao(database));
  }
}