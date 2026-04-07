package com.mikhail.manifestation.ui.viewmodel;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.mikhail.manifestation.data.audio.AudioPlayerManager;
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
public final class MusicViewModel_Factory implements Factory<MusicViewModel> {
  private final Provider<AudioPlayerManager> audioPlayerManagerProvider;

  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public MusicViewModel_Factory(Provider<AudioPlayerManager> audioPlayerManagerProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.audioPlayerManagerProvider = audioPlayerManagerProvider;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public MusicViewModel get() {
    return newInstance(audioPlayerManagerProvider.get(), dataStoreProvider.get());
  }

  public static MusicViewModel_Factory create(
      Provider<AudioPlayerManager> audioPlayerManagerProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new MusicViewModel_Factory(audioPlayerManagerProvider, dataStoreProvider);
  }

  public static MusicViewModel newInstance(AudioPlayerManager audioPlayerManager,
      DataStore<Preferences> dataStore) {
    return new MusicViewModel(audioPlayerManager, dataStore);
  }
}
