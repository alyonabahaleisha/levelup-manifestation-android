package com.mikhail.manifestation.ui.viewmodel;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.mikhail.manifestation.data.audio.AudioDownloadManager;
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
public final class MeditationViewModel_Factory implements Factory<MeditationViewModel> {
  private final Provider<AudioPlayerManager> audioPlayerManagerProvider;

  private final Provider<AudioDownloadManager> downloadManagerProvider;

  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public MeditationViewModel_Factory(Provider<AudioPlayerManager> audioPlayerManagerProvider,
      Provider<AudioDownloadManager> downloadManagerProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    this.audioPlayerManagerProvider = audioPlayerManagerProvider;
    this.downloadManagerProvider = downloadManagerProvider;
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public MeditationViewModel get() {
    return newInstance(audioPlayerManagerProvider.get(), downloadManagerProvider.get(), dataStoreProvider.get());
  }

  public static MeditationViewModel_Factory create(
      Provider<AudioPlayerManager> audioPlayerManagerProvider,
      Provider<AudioDownloadManager> downloadManagerProvider,
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new MeditationViewModel_Factory(audioPlayerManagerProvider, downloadManagerProvider, dataStoreProvider);
  }

  public static MeditationViewModel newInstance(AudioPlayerManager audioPlayerManager,
      AudioDownloadManager downloadManager, DataStore<Preferences> dataStore) {
    return new MeditationViewModel(audioPlayerManager, downloadManager, dataStore);
  }
}
