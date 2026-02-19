package com.levelup.manifestation.ui.viewmodel;

import android.content.Context;
import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NotificationViewModel_Factory implements Factory<NotificationViewModel> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<Context> contextProvider;

  public NotificationViewModel_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<Context> contextProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public NotificationViewModel get() {
    return newInstance(dataStoreProvider.get(), contextProvider.get());
  }

  public static NotificationViewModel_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider, Provider<Context> contextProvider) {
    return new NotificationViewModel_Factory(dataStoreProvider, contextProvider);
  }

  public static NotificationViewModel newInstance(DataStore<Preferences> dataStore,
      Context context) {
    return new NotificationViewModel(dataStore, context);
  }
}
