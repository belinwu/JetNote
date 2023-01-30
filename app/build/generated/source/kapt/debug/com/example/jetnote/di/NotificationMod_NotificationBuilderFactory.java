package com.example.jetnote.di;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class NotificationMod_NotificationBuilderFactory implements Factory<NotificationCompat.Builder> {
  private final Provider<Context> contextProvider;

  public NotificationMod_NotificationBuilderFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public NotificationCompat.Builder get() {
    return notificationBuilder(contextProvider.get());
  }

  public static NotificationMod_NotificationBuilderFactory create(
      Provider<Context> contextProvider) {
    return new NotificationMod_NotificationBuilderFactory(contextProvider);
  }

  public static NotificationCompat.Builder notificationBuilder(Context context) {
    return Preconditions.checkNotNullFromProvides(NotificationMod.INSTANCE.notificationBuilder(context));
  }
}