package com.mikhail.manifestation.data.audio;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AudioPlayerManager_Factory implements Factory<AudioPlayerManager> {
  private final Provider<Context> contextProvider;

  private final Provider<AudioDownloadManager> downloadManagerProvider;

  public AudioPlayerManager_Factory(Provider<Context> contextProvider,
      Provider<AudioDownloadManager> downloadManagerProvider) {
    this.contextProvider = contextProvider;
    this.downloadManagerProvider = downloadManagerProvider;
  }

  @Override
  public AudioPlayerManager get() {
    return newInstance(contextProvider.get(), downloadManagerProvider.get());
  }

  public static AudioPlayerManager_Factory create(Provider<Context> contextProvider,
      Provider<AudioDownloadManager> downloadManagerProvider) {
    return new AudioPlayerManager_Factory(contextProvider, downloadManagerProvider);
  }

  public static AudioPlayerManager newInstance(Context context,
      AudioDownloadManager downloadManager) {
    return new AudioPlayerManager(context, downloadManager);
  }
}
