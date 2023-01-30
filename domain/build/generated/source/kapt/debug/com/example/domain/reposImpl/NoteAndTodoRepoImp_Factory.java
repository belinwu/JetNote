// Generated by Dagger (https://dagger.dev).
package com.example.domain.reposImpl;

import com.example.local.daos.NoteAndTodoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.inject.Provider;

@ScopeMetadata("dagger.hilt.android.scopes.ViewModelScoped")
@QualifierMetadata
@DaggerGenerated
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class NoteAndTodoRepoImp_Factory implements Factory<NoteAndTodoRepoImp> {
  private final Provider<NoteAndTodoDao> daoProvider;

  public NoteAndTodoRepoImp_Factory(Provider<NoteAndTodoDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public NoteAndTodoRepoImp get() {
    return newInstance(daoProvider.get());
  }

  public static NoteAndTodoRepoImp_Factory create(Provider<NoteAndTodoDao> daoProvider) {
    return new NoteAndTodoRepoImp_Factory(daoProvider);
  }

  public static NoteAndTodoRepoImp newInstance(NoteAndTodoDao dao) {
    return new NoteAndTodoRepoImp(dao);
  }
}