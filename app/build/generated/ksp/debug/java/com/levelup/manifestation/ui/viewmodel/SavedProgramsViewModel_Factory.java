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
public final class SavedProgramsViewModel_Factory implements Factory<SavedProgramsViewModel> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public SavedProgramsViewModel_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public SavedProgramsViewModel get() {
    return newInstance(dataStoreProvider.get());
  }

  public static SavedProgramsViewModel_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new SavedProgramsViewModel_Factory(dataStoreProvider);
  }

  public static SavedProgramsViewModel newInstance(DataStore<Preferences> dataStore) {
    return new SavedProgramsViewModel(dataStore);
  }
}
