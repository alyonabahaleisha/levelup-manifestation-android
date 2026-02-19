package com.levelup.manifestation.ui.viewmodel;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
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
public final class ThemeViewModel_Factory implements Factory<ThemeViewModel> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public ThemeViewModel_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public ThemeViewModel get() {
    return newInstance(dataStoreProvider.get());
  }

  public static ThemeViewModel_Factory create(Provider<DataStore<Preferences>> dataStoreProvider) {
    return new ThemeViewModel_Factory(dataStoreProvider);
  }

  public static ThemeViewModel newInstance(DataStore<Preferences> dataStore) {
    return new ThemeViewModel(dataStore);
  }
}
